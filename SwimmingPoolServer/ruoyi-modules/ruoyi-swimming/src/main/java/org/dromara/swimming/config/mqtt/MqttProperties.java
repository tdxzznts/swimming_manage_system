package org.dromara.swimming.config.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MQTT配置属性
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    /**
     * MQTT服务器地址
     */
    private String host = "tcp://localhost:1883";

    /**
     * MQTT客户端ID前缀
     */
    private String clientId = "swimming-server";

    /**
     * 用户名
     */
    private String username = "";

    /**
     * 密码
     */
    private String password = "";

    /**
     * 超时时间（秒）
     */
    private int timeout = 10;

    /**
     * 保持连接时间（秒）
     */
    private int keepAlive = 60;

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 订阅主题前缀
     */
    private String topicPrefix = "/sys/#";
}
