package org.dromara.swimming.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.swimming.domain.SpIotAlarmRule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IoT设备告警规则视图对象 sp_iot_alarm_rule
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@AutoMapper(target = SpIotAlarmRule.class)
public class SpIotAlarmRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    private Long id;

    /**
     * 设备key
     */
    private String deviceKey;

    /**
     * 设备名称（关联查询）
     */
    private String deviceName;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 指标单位
     */
    private String metricUnit;

    /**
     * 最小阈值
     */
    private BigDecimal thresholdMin;

    /**
     * 最大阈值
     */
    private BigDecimal thresholdMax;

    /**
     * 告警级别(info/warning/critical)
     */
    private String alarmLevel;

    /**
     * 告警标题
     */
    private String alarmTitle;

    /**
     * 告警消息模板
     */
    private String alarmMessageTemplate;

    /**
     * 是否启用(0禁用 1启用)
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}
