package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员信息返回对象
 *
 * @author W
 * @date 2026-02-04
 */
@Data
public class MemberInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前等级值
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 英文名称
     */
    private String levelEn;

    /**
     * 会员卡号
     */
    private String cardNo;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 余额（单位：分）
     */
    private Long balance;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 有效期
     */
    private String validThru;

    /**
     * 可提前预约天数（权益）
     */
    private Integer priorityBookingDays;
}
