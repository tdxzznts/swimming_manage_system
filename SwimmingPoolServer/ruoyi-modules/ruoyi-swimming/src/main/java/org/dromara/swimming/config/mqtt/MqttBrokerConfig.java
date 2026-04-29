package org.dromara.swimming.config.mqtt;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import io.moquette.broker.security.IAuthenticator;
import io.moquette.interception.InterceptHandler;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.service.ISpIotDeviceService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * MQTT Broker配置类 - 嵌入式MQTT服务端
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttBrokerConfig {

    private final MqttProperties mqttProperties;
    private final MqttInternalSubscriber mqttInternalSubscriber;
    private final ISpIotDeviceService iotDeviceService;
    private Server mqttBroker;

    /**
     * 启动嵌入式MQTT Broker
     */
    @Bean
    public Server mqttBroker() throws IOException {
        if (!mqttProperties.isEnabled()) {
            log.info("MQTT Broker未启用");
            return null;
        }

        log.info("正在启动嵌入式MQTT Broker...");

        // 创建Broker配置
        Properties props = new Properties();
        String host = mqttProperties.getHost();
        int port = 1883; // 默认端口

        // 解析host,提取端口
        if (host != null && host.contains(":")) {
            String[] parts = host.split(":");
            if (parts.length == 2) {
                try {
                    port = Integer.parseInt(parts[1].replace("/", ""));
                } catch (NumberFormatException e) {
                    log.warn("无法解析MQTT端口,使用默认端口1883");
                }
            }
        }

        // Moquette 0.15使用简单的属性配置
        props.setProperty("port", String.valueOf(port));
        props.setProperty("host", "0.0.0.0"); // 监听所有网卡
        props.setProperty("allow_anonymous", "true"); // 允许匿名连接

        IConfig config = new MemoryConfig(props);

        // 创建认证器(使用统一用户名密码)
        IAuthenticator authenticator = new IAuthenticator() {
            @Override
            public boolean checkValid(String clientId, String username, byte[] password) {
                // 允许所有连接(也可以添加用户名密码验证)
                if (mqttProperties.getUsername() != null && !mqttProperties.getUsername().isEmpty()) {
                    String pwd = new String(password);
                    return mqttProperties.getUsername().equals(username) &&
                           mqttProperties.getPassword().equals(pwd);
                }
                return true;
            }
        };

        // 创建拦截器列表
        List<InterceptHandler> handlers = new ArrayList<>();
        handlers.add(new MqttPublishInterceptor(mqttInternalSubscriber, iotDeviceService));

        // 创建授权策略
        MqttAuthorizator authorizator = new MqttAuthorizator();

        mqttBroker = new Server();
        mqttBroker.startServer(config, handlers, null, authenticator, authorizator);

        log.info("✅ MQTT Broker启动成功! 监听端口: {}", port);
        log.info("📡 设备连接地址: tcp://localhost:{}", port);
        log.info("🔑 统一认证 - 用户名: {}, 密码: {}",
            mqttProperties.getUsername(),
            mqttProperties.getPassword().replaceAll(".", "*"));

        return mqttBroker;
    }

    /**
     * 应用关闭时停止MQTT Broker
     */
    @PreDestroy
    public void stopBroker() {
        if (mqttBroker != null) {
            log.info("正在停止MQTT Broker...");
            mqttBroker.stopServer();
            log.info("MQTT Broker已停止");
        }
    }
}
