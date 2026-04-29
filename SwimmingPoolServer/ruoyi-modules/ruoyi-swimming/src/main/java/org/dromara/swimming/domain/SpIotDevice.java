package org.dromara.swimming.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.time.LocalDateTime;

/**
 * IoT设备对象 sp_iot_device
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_iot_device")
public class SpIotDevice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 产品标识
     */
    private String productKey;

    /**
     * 设备名称(产品内唯一)
     */
    private String deviceName;

    /**
     * 设备密钥
     */
    private String deviceSecret;

    /**
     * 设备唯一标识(product_key&device_name)
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
     * 父设备key(网关子设备时使用)
     */
    private String parentDeviceKey;

    /**
     * 设备影子(JSON格式)
     */
    private String shadow;

    /**
     * 设备标签(JSON格式)
     */
    private String tags;

    /**
     * 安装位置
     */
    private String location;

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
     * 备注
     */
    private String remark;
}
