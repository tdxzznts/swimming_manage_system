package org.dromara.swimming.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import org.dromara.swimming.domain.SpIotDevice;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * IoT设备视图对象 sp_iot_device
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@AutoMapper(target = SpIotDevice.class)
public class SpIotDeviceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long id;

    /**
     * 产品标识
     */
    private String productKey;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备密钥
     */
    private String deviceSecret;

    /**
     * 设备唯一标识
     */
    private String deviceKey;

    /**
     * MAC地址
     */
    private String mac;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 状态(0离线 1在线 3禁用)
     */
    private String status;

    /**
     * 最后在线时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOnlineTime;

    /**
     * 激活时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activeTime;

    /**
     * 父设备key
     */
    private String parentDeviceKey;

    /**
     * 设备影子
     */
    private String shadow;

    /**
     * 设备标签
     */
    private String tags;

    /**
     * 安装位置
     */
    private String location;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 产品名称（关联查询）
     */
    private String productName;

    /**
     * 在线时长（秒）
     */
    private Long onlineDuration;

    /**
     * 最新数据（JSON格式）
     */
    private String latestData;
}
