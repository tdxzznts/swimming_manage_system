package org.dromara.swimming.config.mqtt;

import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.broker.subscriptions.Topic;
import lombok.extern.slf4j.Slf4j;

/**
 * MQTT授权策略
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
public class MqttAuthorizator implements IAuthorizatorPolicy {

    @Override
    public boolean canRead(Topic topic, String username, String client) {
        log.debug("授权检查 - 读取: topic={}, username={}, client={}", topic, username, client);
        return true; // 允许读取
    }

    @Override
    public boolean canWrite(Topic topic, String username, String client) {
        log.debug("授权检查 - 写入: topic={}, username={}, client={}", topic, username, client);
        return true; // 允许写入
    }
}
