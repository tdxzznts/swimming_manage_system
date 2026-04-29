package org.dromara.swimming.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IoT设备告警规则对象 sp_iot_alarm_rule
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@TableName("sp_iot_alarm_rule")
public class SpIotAlarmRule {

    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备key
     */
    private String deviceKey;

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
     * 删除标志(0存在 2删除)
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private Long updateBy;

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
