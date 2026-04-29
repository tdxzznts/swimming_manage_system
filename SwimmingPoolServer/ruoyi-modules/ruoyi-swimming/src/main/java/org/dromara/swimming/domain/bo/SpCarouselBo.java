package org.dromara.swimming.domain.bo;

import org.dromara.swimming.domain.SpCarousel;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.common.translation.annotation.Translation;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.common.translation.constant.TransConstant;

/**
 * 轮播图业务对象 sp_carousel
 *
 * @author W
 * @date 2026-02-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SpCarousel.class, reverseConvertGenerate = false)
public class SpCarouselBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;
    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    @NotBlank(message = "图片URL不能为空", groups = { AddGroup.class, EditGroup.class })
    private String imageUrl;

    /**
     * 排序号（数值越小越靠前）
     */
    private Long sortOrder;

    /**
     * 状态（0停用 1启用）
     */
    private String status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 备注
     */
    private String remark;


}
