package org.dromara.swimming.domain.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * H5订单记录VO
 *
 * @author W
 * @date 2026-02-04
 */
@Data
public class H5OrderRecordVo {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单类型（1预约 2充值 3积分）
     */
    private String orderType;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 订单金额（元）
     */
    private Long amount;

    /**
     * 优惠金额（元）
     */
    private Long discountAmount;

    /**
     * 实际支付金额（元）
     */
    private Long actualAmount;

    /**
     * 支付方式（1支付宝 2余额）
     */
    private String payType;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 支付状态（0待支付 1已支付 2支付失败 3已退款）
     */
    private String payStatus;

    /**
     * 支付状态名称
     */
    private String payStatusName;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 图标名称
     */
    private String iconName;

    /**
     * 金额文本（用于显示）
     */
    private String amountText;

    /**
     * 金额样式（increase/decrease）
     */
    private String amountClass;
}
