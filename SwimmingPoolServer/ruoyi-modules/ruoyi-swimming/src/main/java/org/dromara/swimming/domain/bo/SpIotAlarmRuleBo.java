package org.dromara.swimming.domain.bo;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.swimming.domain.SpIotAlarmRule;

import java.math.BigDecimal;

/**
 * IoT设备告警规则业务对象 sp_iot_alarm_rule
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpIotAlarmRule.class, reverseConvertGenerate = false)
public class SpIotAlarmRuleBo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备key
     */
    @NotBlank(message = "设备key不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 128, message = "设备key长度不能超过128个字符")
    private String deviceKey;

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 100, message = "规则名称长度不能超过100个字符")
    private String ruleName;

    /**
     * 指标名称
     */
    @NotBlank(message = "指标名称不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 50, message = "指标名称长度不能超过50个字符")
    private String metricName;

    /**
     * 指标单位
     */
    @Size(max = 20, message = "指标单位长度不能超过20个字符")
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
     * 告警级别
     */
    @NotBlank(message = "告警级别不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 20, message = "告警级别长度不能超过20个字符")
    private String alarmLevel;

    /**
     * 告警标题
     */
    @Size(max = 200, message = "告警标题长度不能超过200个字符")
    private String alarmTitle;

    /**
     * 告警消息模板
     */
    @Size(max = 500, message = "告警消息模板长度不能超过500个字符")
    private String alarmMessageTemplate;

    /**
     * 是否启用
     */
    private String status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
