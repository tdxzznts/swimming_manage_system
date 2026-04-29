package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 会员卡业务对象 sp_member_card
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpMemberCard.class, reverseConvertGenerate = false)
public class SpMemberCardBo extends BaseEntity {

    /**
     * 会员卡ID
     */
    @NotNull(message = "会员卡ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @NotNull(message = "用户ID（关联sys_user.user_id）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 用户名（非数据库字段，仅用于查询）
     */
    private String userName;

    /**
     * 会员卡号
     */
    @NotBlank(message = "会员卡号不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "发卡日期不能为空", groups = { AddGroup.class, EditGroup.class })
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


}
