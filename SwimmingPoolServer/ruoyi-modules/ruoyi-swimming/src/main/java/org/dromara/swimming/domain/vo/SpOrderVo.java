package org.dromara.swimming.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.swimming.domain.SpOrder;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 订单视图对象 sp_order
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpOrder.class)
public class SpOrderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @ExcelProperty(value = "订单ID")
    private Long id;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private String orderNo;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @ExcelProperty(value = "用户ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sys_user.user_id")
    private Long userId;

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名")
    private String userName;

    /**
     * 预约ID（关联sp_reservation.id）
     */
    @ExcelProperty(value = "预约ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sp_reservation.id")
    private Long reservationId;

    /**
     * 订单类型（1预约 2充值 3积分）
     */
    @ExcelProperty(value = "订单类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "order_type")
    private String orderType;

    /**
     * 订单金额（元）
     */
    @ExcelProperty(value = "订单金额", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "元=")
    private Long amount;

    /**
     * 优惠金额（元）
     */
    @ExcelProperty(value = "优惠金额", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "元=")
    private Long discountAmount;

    /**
     * 实际支付金额（元）
     */
    @ExcelProperty(value = "实际支付金额", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "元=")
    private Long actualAmount;

    /**
     * 获得积分（元）
     */
    @ExcelProperty(value = "获得积分", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "元=")
    private Long getPoint;

    /**
     * 支付方式（1支付宝 2余额）
     */
    @ExcelProperty(value = "支付方式", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_pay_type")
    private String payType;

    /**
     * 支付状态（0待支付 1已支付 2支付失败 3已退款）
     */
    @ExcelProperty(value = "支付状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_pay_status")
    private String payStatus;

    /**
     * 支付时间
     */
    @ExcelProperty(value = "支付时间")
    private Date payTime;

    /**
     * 第三方交易号
     */
    @ExcelProperty(value = "第三方交易号")
    private String tradeNo;

    /**
     * 第三方订单号
     */
    @ExcelProperty(value = "第三方订单号")
    private String thirdPartyNo;

    /**
     * 退款金额（元）
     */
    @ExcelProperty(value = "退款金额", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "元=")
    private Long refundAmount;

    /**
     * 退款时间
     */
    @ExcelProperty(value = "退款时间")
    private Date refundTime;

    /**
     * 退款原因
     */
    @ExcelProperty(value = "退款原因")
    private String refundReason;

    /**
     * 订单状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    @ExcelProperty(value = "订单状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_order_status")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
