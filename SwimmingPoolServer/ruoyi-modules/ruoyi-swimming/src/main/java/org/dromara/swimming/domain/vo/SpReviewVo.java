package org.dromara.swimming.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.swimming.domain.SpReview;
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
 * 评价视图对象 sp_review
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpReview.class)
public class SpReviewVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评价ID
     */
    @ExcelProperty(value = "评价ID")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @ExcelProperty(value = "用户ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sys_user.user_id")
    private Long userId;

    /**
     * 预约ID（关联sp_reservation.id）
     */
    @ExcelProperty(value = "预约ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sp_reservation.id")
    private Long reservationId;

    /**
     * 评分（1-5星）
     */
    @ExcelProperty(value = "评分", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-5星")
    private Long rating;

    /**
     * 评价内容
     */
    @ExcelProperty(value = "评价内容")
    private String content;

    /**
     * 图片URL（JSON数组）
     */
    @ExcelProperty(value = "图片URL", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "J=SON数组")
    private String images;

    /**
     * 状态（0待审核 1已通过 2已拒绝）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=待审核,1=已通过,2=已拒绝")
    private String status;

    /**
     * 管理员回复
     */
    @ExcelProperty(value = "管理员回复")
    private String reply;

    /**
     * 回复人ID
     */
    @ExcelProperty(value = "回复人ID")
    private Long replyUserId;

    /**
     * 回复时间
     */
    @ExcelProperty(value = "回复时间")
    private Date replyTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
