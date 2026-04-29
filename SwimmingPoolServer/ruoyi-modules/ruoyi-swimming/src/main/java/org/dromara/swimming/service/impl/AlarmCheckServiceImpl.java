package org.dromara.swimming.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.swimming.domain.SpIotAlarmRule;
import org.dromara.swimming.domain.SpIotDeviceAlarm;
import org.dromara.swimming.domain.bo.SpIotDeviceAlarmBo;
import org.dromara.swimming.service.IAlarmCheckService;
import org.dromara.swimming.service.IAlarmNotifyService;
import org.dromara.swimming.service.ISpIotAlarmRuleService;
import org.dromara.swimming.service.ISpIotDeviceAlarmService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警检查服务实现
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmCheckServiceImpl implements IAlarmCheckService {

    private final ISpIotAlarmRuleService alarmRuleService;
    private final ISpIotDeviceAlarmService deviceAlarmService;
    private final IAlarmNotifyService alarmNotifyService;

    @Override
    public void checkAndTriggerAlarm(String deviceKey, Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            log.debug("设备数据为空，跳过告警检查 - DeviceKey: {}", deviceKey);
            return;
        }

        // 获取该设备的所有启用的告警规则
        List<SpIotAlarmRule> rules = alarmRuleService.getEnabledRulesByDeviceKey(deviceKey);
        if (rules == null || rules.isEmpty()) {
            log.debug("设备无启用的告警规则 - DeviceKey: {}", deviceKey);
            return;
        }

        log.info("🔍 开始检查告警规则 - DeviceKey: {}, 规则数量: {}, 数据: {}", deviceKey, rules.size(), data);

        // 遍历所有规则进行检查
        for (SpIotAlarmRule rule : rules) {
            checkRule(rule, data, deviceKey);
        }
    }

    /**
     * 检查单个告警规则
     */
    private void checkRule(SpIotAlarmRule rule, Map<String, Object> data, String deviceKey) {
        try {
            String metricName = rule.getMetricName();
            Object valueObj = data.get(metricName);

            if (valueObj == null) {
                log.debug("规则指标不在数据中 - 规则: {}, 指标: {}", rule.getRuleName(), metricName);
                return;
            }

            // 转换数值
            BigDecimal value;
            if (valueObj instanceof Number) {
                value = new BigDecimal(valueObj.toString());
            } else {
                try {
                    value = new BigDecimal(valueObj.toString());
                } catch (NumberFormatException e) {
                    log.warn("规则指标值格式错误 - 规则: {}, 指标: {}, 值: {}", rule.getRuleName(), metricName, valueObj);
                    return;
                }
            }

            // 检查阈值
            boolean trigger = false;
            String condition = "";

            if (rule.getThresholdMin() != null && value.compareTo(rule.getThresholdMin()) < 0) {
                trigger = true;
                condition = "低于最小阈值";
            } else if (rule.getThresholdMax() != null && value.compareTo(rule.getThresholdMax()) > 0) {
                trigger = true;
                condition = "高于最大阈值";
            }

            if (trigger) {
                // 触发告警
                createAlarm(rule, deviceKey, metricName, value, condition);
            } else {
                log.debug("规则检查通过 - 规则: {}, 值: {}", rule.getRuleName(), value);
            }

        } catch (Exception e) {
            log.error("检查告警规则失败 - 规则: {}, Error: {}", rule.getRuleName(), e.getMessage(), e);
        }
    }

    /**
     * 创建告警记录
     */
    private void createAlarm(SpIotAlarmRule rule, String deviceKey, String metricName,
                            BigDecimal value, String condition) {
        try {
            SpIotDeviceAlarmBo alarmBo = new SpIotDeviceAlarmBo();
            alarmBo.setDeviceKey(deviceKey);
            alarmBo.setAlarmType(rule.getMetricName());
            alarmBo.setAlarmLevel(rule.getAlarmLevel());

            // 构建告警标题
            String alarmTitle = StrUtil.isNotBlank(rule.getAlarmTitle())
                ? rule.getAlarmTitle()
                : metricName + "异常";

            alarmBo.setAlarmTitle(alarmTitle);

            // 构建告警消息
            String alarmMessage = buildAlarmMessage(rule, metricName, value, condition);
            alarmBo.setAlarmMessage(alarmMessage);

            // 构建告警详细数据
            Map<String, Object> alarmData = new HashMap<>();
            alarmData.put("ruleId", rule.getId());
            alarmData.put("ruleName", rule.getRuleName());
            alarmData.put("metricName", metricName);
            alarmData.put("metricValue", value);
            alarmData.put("thresholdMin", rule.getThresholdMin());
            alarmData.put("thresholdMax", rule.getThresholdMax());
            alarmData.put("condition", condition);
            alarmData.put("triggerTime", LocalDateTime.now().toString());

            // 将alarmData转换为JSON字符串存储
            alarmBo.setAlarmData(JSONUtil.toJsonStr(alarmData));

            // 保存告警记录
            boolean success = deviceAlarmService.insertByBo(alarmBo);

            if (success) {
                log.warn("🚨 告警触发 - DeviceKey: {}, 规则: {}, 指标: {}, 值: {}, 原因: {}",
                    deviceKey, rule.getRuleName(), metricName, value, condition);

                // 发送告警通知（SSE + 邮件）
                try {
                    SpIotDeviceAlarm alarm = new SpIotDeviceAlarm();
                    alarm.setDeviceKey(deviceKey);
                    alarm.setAlarmType(rule.getMetricName());
                    alarm.setAlarmLevel(rule.getAlarmLevel());
                    alarm.setAlarmTitle(alarmTitle);
                    alarm.setAlarmMessage(alarmMessage);
                    alarm.setCreateTime(LocalDateTime.now());

                    alarmNotifyService.sendAlarmNotification(alarm);
                } catch (Exception ex) {
                    log.error("发送告警通知失败 - {}", ex.getMessage());
                }
            } else {
                log.error("告警记录保存失败 - 规则: {}", rule.getRuleName());
            }

        } catch (Exception e) {
            log.error("创建告警记录失败 - Error: {}", e.getMessage(), e);
        }
    }

    /**
     * 构建告警消息
     */
    private String buildAlarmMessage(SpIotAlarmRule rule, String metricName,
                                    BigDecimal value, String condition) {
        String template = rule.getAlarmMessageTemplate();

        if (StrUtil.isNotBlank(template)) {
            // 替换模板变量
            return template
                .replace("${value}", value.toString())
                .replace("${min}", rule.getThresholdMin() != null ? rule.getThresholdMin().toString() : "无")
                .replace("${max}", rule.getThresholdMax() != null ? rule.getThresholdMax().toString() : "无")
                .replace("${metric}", metricName)
                .replace("${condition}", condition);
        }

        // 默认消息格式
        return String.format("%s告警: %s当前值为%s，%s阈值",
            rule.getAlarmLevel(),
            metricName,
            value.toString(),
            condition);
    }
}
