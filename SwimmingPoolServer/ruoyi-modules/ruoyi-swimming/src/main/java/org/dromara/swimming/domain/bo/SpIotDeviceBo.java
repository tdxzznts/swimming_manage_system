package org.dromara.swimming.domain.bo;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.swimming.domain.SpIotDevice;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * IoT设备业务对象 sp_iot_device
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpIotDevice.class, reverseConvertGenerate = false)
public class SpIotDeviceBo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 产品标识
     */
    @NotBlank(message = "产品标识不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 64, message = "产品标识长度不能超过64个字符")
    private String productKey;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 64, message = "设备名称长度不能超过64个字符")
    private String deviceName;

    /**
     * 设备密钥
     */
    @Size(max = 255, message = "设备密钥长度不能超过255个字符")
    private String deviceSecret;

    /**
     * 设备唯一标识
     */
    private String deviceKey;

    /**
     * MAC地址
     */
    @Size(max = 17, message = "MAC地址长度不能超过17个字符")
    private String mac;

    /**
     * 设备类型
     */
    @Size(max = 50, message = "设备类型长度不能超过50个字符")
    private String deviceType;

    /**
     * 固件版本
     */
    @Size(max = 50, message = "固件版本长度不能超过50个字符")
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
    @Size(max = 128, message = "父设备key长度不能超过128个字符")
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
    @Size(max = 200, message = "安装位置长度不能超过200个字符")
    private String location;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
