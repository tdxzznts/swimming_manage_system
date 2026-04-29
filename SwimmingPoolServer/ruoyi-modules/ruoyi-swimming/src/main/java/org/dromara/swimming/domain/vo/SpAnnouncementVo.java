package org.dromara.swimming.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.swimming.domain.SpAnnouncement;
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
 * 系统公告视图对象 sp_announcement
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpAnnouncement.class)
public class SpAnnouncementVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    @ExcelProperty(value = "公告ID")
    private Long id;

    /**
     * 公告标题
     */
    @ExcelProperty(value = "公告标题")
    private String title;

    /**
     * 公告内容
     */
    @ExcelProperty(value = "公告内容")
    private String content;

    /**
     * 公告类型（1系统公告 2活动通知 3紧急通知）
     */
    @ExcelProperty(value = "公告类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_announcement_type")
    private String announcementType;

    /**
     * 发布时间
     */
    @ExcelProperty(value = "发布时间")
    private Date publishTime;

    /**
     * 状态（0草稿 1已发布）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=草稿,1=已发布")
    private String status;

    /**
     * 浏览次数
     */
    @ExcelProperty(value = "浏览次数")
    private Long viewCount;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
