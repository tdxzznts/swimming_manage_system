package org.dromara.swimming.domain.vo;

import org.dromara.common.translation.annotation.Translation;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.common.translation.constant.TransConstant;
import org.dromara.swimming.domain.SpCarousel;
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
 * 轮播图视图对象 sp_carousel
 *
 * @author W
 * @date 2026-02-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpCarousel.class)
public class SpCarouselVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 图片URL
     */
    @ExcelProperty(value = "图片URL")
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "imageUrl")
    private String imageUrl;

    /**
     * 排序号（数值越小越靠前）
     */
    @ExcelProperty(value = "排序号", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "数=值越小越靠前")
    private Long sortOrder;

    /**
     * 状态（0停用 1启用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=停用,1=启用")
    private String status;

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
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
