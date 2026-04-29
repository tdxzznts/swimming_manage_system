package org.dromara.swimming.config.mqtt;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.service.ISpIotDeviceService;

/**
 * MQTT发布消息拦截器
 * 拦截设备发布的所有消息
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@RequiredArgsConstructor
public class MqttPublishInterceptor implements InterceptHandler {

    private final MqttInternalSubscriber mqttInternalSubscriber;
    private final ISpIotDeviceService iotDeviceService;

    @Override
    public String getID() {
        return "SwimmingMqttPublishInterceptor";
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        try {
            String topic = msg.getTopicName();
            String clientId = msg.getClientID();
            byte[] payload = new byte[msg.getPayload().readableBytes()];
            msg.getPayload().readBytes(payload);

            log.info("📨 拦截到MQTT消息 - ClientId: {}, Topic: {}", clientId, topic);
            mqttInternalSubscriber.onMessage(topic, payload, clientId);
        } catch (Exception e) {
            log.error("处理MQTT消息失败", e);
        }
    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
        // 可选实现 - 处理消息确认
    }

    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
        // 可选实现 - 处理订阅
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
        // 可选实现 - 处理取消订阅
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        String clientId = msg.getClientID();
        log.info("✅ 设备连接 - ClientId: {}", clientId);

        // 标记设备在线
        markDeviceOnline(clientId, true);
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        String clientId = msg.getClientID();
        log.info("❌ 设备断开 - ClientId: {}", clientId);

        // 标记设备离线
        markDeviceOnline(clientId, false);
    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
        String clientId = msg.getClientID();
        log.warn("⚠️ 设备连接丢失 - ClientId: {}", clientId);

        // 标记设备离线
        markDeviceOnline(clientId, false);
    }

    /**
     * 标记设备在线/离线状态
     */
    private void markDeviceOnline(String clientId, boolean online) {
        try {
            // 尝试从clientId提取deviceKey
            // clientId格式可能是: "test-device-01" 或 "swimming_pool.device_01"
            String deviceKey = extractDeviceKey(clientId);

            if (deviceKey != null) {
                boolean success = iotDeviceService.updateOnlineStatus(deviceKey, online);
                if (success) {
                    log.info("📍 设备状态已更新 - DeviceKey: {}, 在线: {}", deviceKey, online);
                } else {
                    log.debug("设备状态更新失败 - DeviceKey: {}", deviceKey);
                }
            } else {
                log.debug("无法从ClientId提取DeviceKey - ClientId: {}", clientId);
            }
        } catch (Exception e) {
            log.error("更新设备在线状态失败 - ClientId: {}, Error: {}", clientId, e.getMessage());
        }
    }

    /**
     * 从ClientId中提取DeviceKey
     */
    private String extractDeviceKey(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return null;
        }

        // 优先使用clientId本身查找设备（因为很多情况下clientId就是deviceKey）
        var device = iotDeviceService.getByDeviceKey(clientId);
        if (device != null) {
            return clientId;
        }

        // 如果clientId本身找不到，尝试从clientId中提取
        // 如果clientId本身就是deviceKey（包含点号），直接返回
        if (clientId.contains(".")) {
            return clientId;
        }

        // 尝试匹配测试格式: test-{deviceKey}
        // 对于test-device-01，提取device-01
        if (clientId.startsWith("test-")) {
            String possibleKey = clientId.substring(5);
            // 再次尝试查找
            device = iotDeviceService.getByDeviceKey(possibleKey);
            if (device != null) {
                return possibleKey;
            }
        }

        // 如果都找不到，返回clientId，让updateOnlineStatus尝试
        log.debug("使用ClientId作为DeviceKey - ClientId: {}", clientId);
        return clientId;
    }

    @Override
    public Class<?>[] getInterceptedMessageTypes() {
        return new Class[]{
            InterceptPublishMessage.class,
            InterceptConnectMessage.class,
            InterceptDisconnectMessage.class,
            InterceptConnectionLostMessage.class
        };
    }
}
