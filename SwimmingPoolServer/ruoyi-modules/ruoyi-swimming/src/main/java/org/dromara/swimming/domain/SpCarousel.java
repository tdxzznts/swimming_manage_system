package org.dromara.swimming.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.translation.annotation.Translation;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;

/**
 * 轮播图对象 sp_carousel
 *
 * @author W
 * @date 2026-02-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sp_carousel")
public class SpCarousel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 轮播图ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
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

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;


}
