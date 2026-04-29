package org.dromara.swimming.domain.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5预约记录展示对象
 *
 * @author swimming
 * @date 2026-02-03
 */
@Data
public class SpH5ReservationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 预约日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reservationDate;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm")
    private Date endTime;

    /**
     * 泳道编号
     */
    private String laneNo;

    /**
     * 泳道类型名称
     */
    private String laneTypeName;

    /**
     * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 金额（元）
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
     * 支付方式（1微信支付 2余额支付 3免费次数）
     */
    private String payType;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 签到时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date checkInTime;

    /**
     * 签退时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date checkOutTime;

    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date cancelTime;

    /**
     * 是否可取消
     */
    private Boolean canCancel;
}
