package org.dromara.swimming.config.mqtt;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.domain.SpIotDeviceData;
import org.dromara.swimming.service.IAlarmCheckService;
import org.dromara.swimming.service.ISpIotDeviceDataService;
import org.dromara.swimming.service.ISpIotDeviceService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MQTT内部消息订阅器
 * 处理所有设备发布的消息
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttInternalSubscriber {

    private final ISpIotDeviceService iotDeviceService;
    private final IAlarmCheckService alarmCheckService;
    private final ISpIotDeviceDataService deviceDataService;

    /**
     * 处理接收到的MQTT PUBLISH消息
     * 此方法由MqttMessageInterceptor调用
     */
    public void onMessage(String topic, byte[] payload, String clientId) {
        try {
            String message = new String(payload, StandardCharsets.UTF_8);
            log.info("📨 收到设备消息 - ClientId: {}, Topic: {}, Message: {}", clientId, topic, message);

            // 解析主题格式: /sys/{productKey}/{deviceKey}/thing/event/property/post
            String[] topicParts = topic.split("/");
            if (topicParts.length >= 3) {
                String productKey = topicParts[1];
                String deviceKey = topicParts[2];

                log.info("📦 设备信息 - ProductKey: {}, DeviceKey: {}", productKey, deviceKey);

                // 更新设备在线状态
                iotDeviceService.updateOnlineStatus(deviceKey, true);

                // 解析消息内容、保存数据并检查告警
                parseAndCheckAlarm(deviceKey, message, topic);
            }
        } catch (Exception e) {
            log.error("处理MQTT消息失败 - Error: {}", e.getMessage(), e);
        }
    }

    /**
     * 解析消息内容并检查告警
     */
    private void parseAndCheckAlarm(String deviceKey, String message, String topic) {
        try {
            // 解析JSON消息
            JSONObject jsonObject = JSONUtil.parseObj(message);

            // 提取params字段
            if (jsonObject.containsKey("params")) {
                JSONObject params = jsonObject.getJSONObject("params");
                Map<String, Object> data = new HashMap<>();

                // 转换为Map
                for (String key : params.keySet()) {
                    Object value = params.get(key);
                    data.put(key, value);
                }

                log.debug("📊 解析设备数据 - DeviceKey: {}, Data: {}", deviceKey, data);

                // 1. 先保存设备数据到数据库
                saveDeviceData(deviceKey, topic, params);

                // 2. 检查告警规则
                alarmCheckService.checkAndTriggerAlarm(deviceKey, data);
            } else {
                log.debug("消息中无params字段 - Message: {}", message);
            }

        } catch (Exception e) {
            log.warn("解析设备数据失败 - DeviceKey: {}, Error: {}", deviceKey, e.getMessage());
        }
    }

    /**
     * 保存设备数据到数据库
     */
    private void saveDeviceData(String deviceKey, String topic, JSONObject params) {
        try {
            SpIotDeviceData deviceData = new SpIotDeviceData();
            deviceData.setDeviceKey(deviceKey);
            deviceData.setTopic(topic);
            deviceData.setMethod("thing/event/property/post");
            deviceData.setParams(params.toString());
            deviceData.setCollectTime(LocalDateTime.now());
            deviceData.setTimestamp(System.currentTimeMillis());

            Boolean success = deviceDataService.save(deviceData);
            if (success) {
                log.debug("💾 设备数据已保存 - DeviceKey: {}", deviceKey);
            } else {
                log.warn("设备数据保存失败 - DeviceKey: {}", deviceKey);
            }
        } catch (Exception e) {
            log.error("保存设备数据失败 - DeviceKey: {}, Error: {}", deviceKey, e.getMessage(), e);
        }
    }
}
