package org.dromara.swimming.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IoT设备告警对象 sp_iot_device_alarm
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@TableName("sp_iot_device_alarm")
public class SpIotDeviceAlarm {

    private static final long serialVersionUID = 1L;

    /**
     * 告警ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备key
     */
    private String deviceKey;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 告警级别(info/warning/critical)
     */
    private String alarmLevel;

    /**
     * 告警标题
     */
    private String alarmTitle;

    /**
     * 告警内容
     */
    private String alarmMessage;

    /**
     * 告警详细数据(当前值、阈值等)
     */
    private String alarmData;

    /**
     * 是否已处理(0未处理 1已处理)
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
    private LocalDateTime handleTime;

    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 告警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
