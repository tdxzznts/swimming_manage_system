package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 会员等级配置对象 sp_member_level
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_member_level")
public class SpMemberLevel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 等级编码(normal/silver/gold/diamond)
     */
    private String levelCode;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 英文名称
     */
    private String levelEn;

    /**
     * 等级值(0-3)
     */
    private Long levelValue;

    /**
     * 折扣比例(如95表示95折)
     */
    private Long discount;

    /**
     * 升级阈值
     */
    private Long price;

    /**
     * 卡片渐变起始色
     */
    private String cardColorStart;

    /**
     * 卡片渐变结束色
     */
    private String cardColorEnd;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

    /**
     * 排序
     */
    private Long sortOrder;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;


}
