package org.dromara.swimming.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.swimming.domain.SpIotDeviceData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * IoT设备数据视图对象 sp_iot_device_data
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@AutoMapper(target = SpIotDeviceData.class)
public class SpIotDeviceDataVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
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

    /**
     * 设备名称（关联查询）
     */
    private String deviceName;

    /**
     * 产品名称（关联查询）
     */
    private String productName;
}
