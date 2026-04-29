package org.dromara.swimming.controller;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.SpIotDevice;
import org.dromara.swimming.domain.SpIotDeviceData;
import org.dromara.swimming.domain.bo.SpIotDeviceBo;
import org.dromara.swimming.domain.vo.SpIotDeviceVo;
import org.dromara.swimming.service.ISpIotDeviceService;
import org.dromara.swimming.service.ISpIotDeviceDataService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * IoT设备API控制器
 * 提供给硬件设备调用的接口
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/iot/api")
public class SpIotDeviceApiController extends BaseController {

    private final ISpIotDeviceService deviceService;
    private final ISpIotDeviceDataService deviceDataService;

    /**
     * 设备激活
     * 设备首次联网时调用，获取设备密钥
     *
     * @param activateRequest 激活请求
     * @return 设备密钥和连接信息
     */
    @PostMapping("/device/activate")
    public R<Map<String, Object>> activateDevice(@Validated @RequestBody ActivateRequest activateRequest) {
        log.info("设备激活请求: productKey={}, deviceName={}, mac={}",
            activateRequest.getProductKey(),
            activateRequest.getDeviceName(),
            activateRequest.getMac());

        // 1. 验证产品是否存在
        SpIotDeviceVo product = deviceService.getByProductKey(activateRequest.getProductKey());
        if (product == null) {
            return R.fail("产品不存在");
        }

        // 2. 生成设备密钥
        String deviceKey = activateRequest.getProductKey() + "&" + activateRequest.getDeviceName();
        String deviceSecret = DigestUtil.md5Hex(deviceKey + System.currentTimeMillis());

        // 3. 创建或更新设备
        SpIotDeviceBo deviceBo = new SpIotDeviceBo();
        deviceBo.setProductKey(activateRequest.getProductKey());
        deviceBo.setDeviceName(activateRequest.getDeviceName());
        deviceBo.setDeviceKey(deviceKey);
        deviceBo.setDeviceSecret(deviceSecret);
        deviceBo.setMac(activateRequest.getMac());
        deviceBo.setFirmwareVersion(activateRequest.getFirmwareVersion());
        deviceBo.setStatus("0"); // 初始离线
        deviceBo.setActiveTime(LocalDateTime.now());

        deviceBo = deviceService.registerOrUpdateDevice(deviceBo);

        // 4. 返回设备信息和MQTT连接参数
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceBo.getId());
        result.put("deviceKey", deviceKey);
        result.put("deviceSecret", deviceSecret);
        result.put("mqtt", Map.of(
            "host", "mqtt.example.com",
            "port", 1883,
            "clientId", deviceKey,
            "username", activateRequest.getDeviceName() + "&" + activateRequest.getProductKey(),
            "password", "hmacsha256:" + System.currentTimeMillis() + ":" + deviceSecret,
            "topics", Map.of(
                "upload", "/device/upload/" + deviceKey,
                "command", "/device/command/" + deviceKey,
                "heartbeat", "/device/heartbeat/" + deviceKey
            )
        ));

        log.info("设备激活成功: deviceKey={}", deviceKey);
        return R.ok(result);
    }

    /**
     * 设备数据上报
     * 设备通过HTTPS方式上报实时数据
     *
     * @param request 数据上报请求
     * @return 处理结果
     */
    @PostMapping("/data/upload")
    public R<Map<String, Object>> uploadData(@RequestBody DataUploadRequest request) {
        log.info("收到设备数据: deviceKey={}, method={}", request.getDeviceKey(), request.getMethod());

        // 1. 验证设备签名
        if (!validateDeviceSignature(request.getDeviceKey(), request.getSignature(), request.getTimestamp())) {
            return R.fail("设备签名验证失败");
        }

        // 2. 检查设备是否存在且在线
        SpIotDeviceVo device = deviceService.getByDeviceKey(request.getDeviceKey());
        if (device == null) {
            return R.fail("设备不存在");
        }

        // 3. 保存设备数据
        SpIotDeviceData deviceData = new SpIotDeviceData();
        deviceData.setDeviceKey(request.getDeviceKey());
        deviceData.setMethod(request.getMethod());
        deviceData.setParams(JSONUtil.toJsonStr(request.getParams()));
        deviceData.setTimestamp(request.getTimestamp());
        deviceData.setCollectTime(LocalDateTime.now());

        deviceDataService.save(deviceData);

        // 4. 更新设备在线状态
        deviceService.updateOnlineStatus(request.getDeviceKey(), true);

        // 5. 检查告警规则（异步）
        // TODO: 实现告警规则检查

        Map<String, Object> result = new HashMap<>();
        result.put("msgId", request.getMsgId());
        result.put("processedAt", System.currentTimeMillis());

        return R.ok(result);
    }

    /**
     * 批量数据上报
     * 设备离线后批量补传数据
     *
     * @param request 批量数据请求
     * @return 处理结果
     */
    @PostMapping("/data/batch")
    public R<Map<String, Object>> uploadBatchData(@RequestBody BatchDataRequest request) {
        log.info("收到批量数据: deviceKey={}, count={}", request.getDeviceKey(), request.getRecords().size());

        // 1. 验证设备签名
        if (!validateDeviceSignature(request.getDeviceKey(), request.getSignature(), request.getTimestamp())) {
            return R.fail("设备签名验证失败");
        }

        // 2. 批量保存数据
        deviceDataService.batchSave(request.getDeviceKey(), request.getRecords());

        Map<String, Object> result = new HashMap<>();
        result.put("batchId", request.getBatchId());
        result.put("successCount", request.getRecords().size());
        result.put("failedCount", 0);

        return R.ok(result);
    }

    /**
     * 设备心跳
     * 设备定时发送心跳保持在线状态
     *
     * @param request 心跳请求
     * @return 心跳响应
     */
    @PostMapping("/device/heartbeat")
    public R<Map<String, Object>> heartbeat(@RequestBody HeartbeatRequest request) {
        log.debug("收到设备心跳: deviceKey={}", request.getDeviceKey());

        // 1. 验证设备签名
        if (!validateDeviceSignature(request.getDeviceKey(), request.getSignature(), request.getTimestamp())) {
            return R.fail("设备签名验证失败");
        }

        // 2. 更新设备在线状态
        deviceService.updateOnlineStatus(request.getDeviceKey(), true);

        // 3. 返回待处理的命令（如果有）
        // TODO: 从设备影子中获取待处理的命令

        Map<String, Object> result = new HashMap<>();
        result.put("serverTime", System.currentTimeMillis());
        result.put("status", "online");

        return R.ok(result);
    }

    /**
     * 获取设备配置
     * 设备请求获取云端配置
     *
     * @param deviceKey 设备key
     * @return 设备配置
     */
    @GetMapping("/device/config/{deviceKey}")
    public R<Map<String, Object>> getDeviceConfig(@PathVariable String deviceKey) {
        SpIotDeviceVo device = deviceService.getByDeviceKey(deviceKey);
        if (device == null) {
            return R.fail("设备不存在");
        }

        // 从设备影子中获取配置
        String shadow = device.getShadow();
        Map<String, Object> config = JSONUtil.toBean(shadow, Map.class);

        return R.ok(config);
    }

    /**
     * 验证设备签名
     *
     * @param deviceKey 设备key
     * @param signature 签名
     * @param timestamp 时间戳
     * @return 是否验证通过
     */
    private boolean validateDeviceSignature(String deviceKey, String signature, Long timestamp) {
        // TODO: 实现签名验证逻辑
        // 1. 获取设备密钥
        // 2. 计算期望签名 = HMAC-SHA256(deviceSecret + timestamp)
        // 3. 对比签名是否一致
        // 4. 检查时间戳是否在允许范围内（5分钟内）

        // 临时实现：总是返回true
        return true;
    }

    // ==================== 请求对象 ====================

    /**
     * 设备激活请求
     */
    public static class ActivateRequest {
        private String productKey;
        private String deviceName;
        private String mac;
        private String firmwareVersion;

        // getters and setters
        public String getProductKey() { return productKey; }
        public void setProductKey(String productKey) { this.productKey = productKey; }
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public String getMac() { return mac; }
        public void setMac(String mac) { this.mac = mac; }
        public String getFirmwareVersion() { return firmwareVersion; }
        public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
    }

    /**
     * 数据上报请求
     */
    public static class DataUploadRequest {
        private String msgId;
        private String deviceKey;
        private Long timestamp;
        private String signature;
        private String method;
        private Map<String, Object> params;

        // getters and setters
        public String getMsgId() { return msgId; }
        public void setMsgId(String msgId) { this.msgId = msgId; }
        public String getDeviceKey() { return deviceKey; }
        public void setDeviceKey(String deviceKey) { this.deviceKey = deviceKey; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    /**
     * 批量数据请求
     */
    public static class BatchDataRequest {
        private String batchId;
        private String deviceKey;
        private Long timestamp;
        private String signature;
        private java.util.List<Map<String, Object>> records;

        // getters and setters
        public String getBatchId() { return batchId; }
        public void setBatchId(String batchId) { this.batchId = batchId; }
        public String getDeviceKey() { return deviceKey; }
        public void setDeviceKey(String deviceKey) { this.deviceKey = deviceKey; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
        public java.util.List<Map<String, Object>> getRecords() { return records; }
        public void setRecords(java.util.List<Map<String, Object>> records) { this.records = records; }
    }

    /**
     * 心跳请求
     */
    public static class HeartbeatRequest {
        private String deviceKey;
        private Long timestamp;
        private String signature;
        private String status;
        private Map<String, Object> metrics;

        // getters and setters
        public String getDeviceKey() { return deviceKey; }
        public void setDeviceKey(String deviceKey) { this.deviceKey = deviceKey; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }
}
