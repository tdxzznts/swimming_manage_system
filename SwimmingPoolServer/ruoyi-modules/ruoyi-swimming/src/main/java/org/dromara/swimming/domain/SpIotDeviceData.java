package org.dromara.swimming.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IoT设备数据对象 sp_iot_device_data
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@TableName("sp_iot_device_data")
public class SpIotDeviceData {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备key
     */
    private String deviceKey;

    /**
     * MQTT主题
     */
    private String topic;

    /**
     * 方法名
     */
    private String method;

    /**
     * 数据参数(OneJSON格式)
     */
    private String params;

    /**
     * 数据时间戳(毫秒)
     */
    private Long timestamp;

    /**
     * 采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collectTime;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
