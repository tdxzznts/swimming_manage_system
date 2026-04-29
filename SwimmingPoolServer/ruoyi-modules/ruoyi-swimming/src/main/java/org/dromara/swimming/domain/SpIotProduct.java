package org.dromara.swimming.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * IoT产品对象 sp_iot_product
 *
 * @author swimming
 * @date 2026-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_iot_product")
public class SpIotProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 产品标识(全局唯一,8位大写字母)
     */
    private String productKey;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品类型(水处理设备/环境监测/智能控制等)
     */
    private String productType;

    /**
     * 节点类型(0直连设备 1网关 2子设备)
     */
    private String nodeType;

    /**
     * 设备分类(传感器/控制器/网关等)
     */
    private String category;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 数据模板(物模型定义)
     */
    private String dataTemplate;

    /**
     * 认证方式(secret密钥/certificate证书)
     */
    private String authType;

    /**
     * 状态(0停用 1启用 2已删除)
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
     * 备注
     */
    private String remark;
}
