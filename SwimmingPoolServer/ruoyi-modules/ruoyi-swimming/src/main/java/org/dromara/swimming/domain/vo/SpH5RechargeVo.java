package org.dromara.swimming.domain.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5充值返回对象
 *
 * @author swimming
 * @date 2026-02-04
 */
@Data
public class SpH5RechargeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付二维码URL
     */
    private String qrCodeUrl;

    /**
     * 订单过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 充值金额（单位：分）
     */
    private Long amount;

    /**
     * 赠送金额（单位：分）
     */
    private Long bonusAmount;

    /**
     * 实际支付金额（单位：分）
     */
    private Long actualAmount;
}
