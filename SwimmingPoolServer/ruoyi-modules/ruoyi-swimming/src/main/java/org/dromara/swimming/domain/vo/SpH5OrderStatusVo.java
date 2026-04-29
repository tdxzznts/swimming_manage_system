package org.dromara.swimming.domain.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5订单状态查询返回对象
 *
 * @author swimming
 * @date 2026-02-04
 */
@Data
public class SpH5OrderStatusVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付状态（0待支付 1已支付 2支付失败 3已退款）
     */
    private String payStatus;

    /**
     * 支付状态描述
     */
    private String payStatusDesc;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /**
     * 订单状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    private String status;

    /**
     * 订单状态描述
     */
    private String statusDesc;
}
