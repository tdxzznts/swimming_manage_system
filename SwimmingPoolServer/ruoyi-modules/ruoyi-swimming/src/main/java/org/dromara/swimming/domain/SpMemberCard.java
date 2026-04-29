package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 会员卡对象 sp_member_card
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_member_card")
public class SpMemberCard extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    private Long userId;

    /**
     * 用户名（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 会员卡号
     */
    private String cardNo;

    /**
     * 会员等级（0普通 1银卡 2金卡 3钻石）
     */
    private String cardLevel;

    /**
     * 余额
     */
    private Long balance;

    /**
     * 累计充值金额
     */
    private Long totalRecharge;

    /**
     * 累计消费金额
     */
    private Long totalConsume;

    /**
     * 积分余额
     */
    private Long totalPoint;

    /**
     * 免费次数
     */
    private Long freeTimes;

    /**
     * 发卡日期
     */
    private Date issueDate;

    /**
     * 到期日期
     */
    private Date expiryDate;

    /**
     * 状态（0正常 1挂失 2冻结）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


}
