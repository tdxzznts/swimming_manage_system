package org.dromara.swimming.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.swimming.domain.SpReservation;
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
 * 预约记录视图对象 sp_reservation
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpReservation.class)
public class SpReservationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
     */
    @ExcelProperty(value = "预约ID")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @ExcelProperty(value = "用户ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sys_user.user_id")
    private Long userId;

    /**
     * 时段ID（关联sp_time_slot.id）
     */
    @ExcelProperty(value = "时段ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sp_time_slot.id")
    private Long timeSlotId;

    /**
     * 泳道ID（关联sp_lane.id）
     */
    @ExcelProperty(value = "泳道ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sp_lane.id")
    private Long laneId;

    /**
     * 预约单号
     */
    @ExcelProperty(value = "预约单号")
    private String reservationNo;

    /**
     * 预约日期
     */
    @ExcelProperty(value = "预约日期")
    private Date reservationDate;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_reservation_status")
    private String status;

    /**
     * 金额（元）
     */
    @ExcelProperty(value = "金额", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "元=")
    private Long amount;

    /**
     * 订单ID（关联sp_order.id）
     */
    @ExcelProperty(value = "订单ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sp_order.id")
    private Long orderId;

    /**
     * 签到时间
     */
    @ExcelProperty(value = "签到时间")
    private Date checkInTime;

    /**
     * 签退时间
     */
    @ExcelProperty(value = "签退时间")
    private Date checkOutTime;

    /**
     * 取消时间
     */
    @ExcelProperty(value = "取消时间")
    private Date cancelTime;

    /**
     * 取消原因
     */
    @ExcelProperty(value = "取消原因")
    private String cancelReason;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
