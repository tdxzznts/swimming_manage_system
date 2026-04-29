package org.dromara.swimming.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * IoT OTA升级对象 sp_iot_ota_upgrade
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@TableName("sp_iot_ota_upgrade")
public class SpIotOtaUpgrade {

    private static final long serialVersionUID = 1L;

    /**
     * 升级ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备key
     */
    private String deviceKey;

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 当前版本
     */
    private String fromVersion;

    /**
     * 目标版本
     */
    private String toVersion;

    /**
     * 升级类型(full完整包/diff差分)
     */
    private String upgradeType;

    /**
     * 固件文件URL
     */
    private String fileUrl;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 升级状态(pending待升级/downloading下载中/upgrading升级中/success成功/failed失败)
     */
    private String upgradeStatus;

    /**
     * 升级进度(0-100)
     */
    private Integer progress;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 回滚版本
     */
    private String rollbackVersion;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
