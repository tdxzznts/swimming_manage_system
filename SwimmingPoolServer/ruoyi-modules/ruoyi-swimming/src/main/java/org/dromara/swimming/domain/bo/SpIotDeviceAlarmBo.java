package org.dromara.swimming.domain.bo;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.swimming.domain.SpIotDeviceAlarm;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * IoT设备告警业务对象 sp_iot_device_alarm
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpIotDeviceAlarm.class, reverseConvertGenerate = false)
public class SpIotDeviceAlarmBo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 告警ID
     */
    @NotNull(message = "告警ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备key
     */
    @NotBlank(message = "设备key不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 128, message = "设备key长度不能超过128个字符")
    private String deviceKey;

    /**
     * 告警类型
     */
    @Size(max = 50, message = "告警类型长度不能超过50个字符")
    private String alarmType;

    /**
     * 告警级别
     */
    @Size(max = 20, message = "告警级别长度不能超过20个字符")
    private String alarmLevel;

    /**
     * 告警标题
     */
    @Size(max = 200, message = "告警标题长度不能超过200个字符")
    private String alarmTitle;

    /**
     * 告警内容
     */
    @Size(max = 500, message = "告警内容长度不能超过500个字符")
    private String alarmMessage;

    /**
     * 告警详细数据
     */
    private String alarmData;

    /**
     * 是否已处理
     */
    private String isHandled;

    /**
     * 处理人ID
     */
    private Long handleUserId;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /**
     * 处理结果
     */
    @Size(max = 500, message = "处理结果长度不能超过500个字符")
    private String handleResult;
}
