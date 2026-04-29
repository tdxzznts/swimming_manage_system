package org.dromara.swimming.mapper;

import org.dromara.swimming.domain.SpFeedback;
import org.dromara.swimming.domain.bo.SpFeedbackBo;
import org.dromara.swimming.domain.vo.SpFeedbackVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 反馈Mapper接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface SpFeedbackMapper extends BaseMapperPlus<SpFeedback, SpFeedbackVo> {

    /**
     * 查询反馈列表（关联用户表）
     *
     * @param queryWrapper 查询条件
     * @return 反馈列表
     */
    List<SpFeedbackVo> selectVoList(@Param("ew") SpFeedbackBo queryWrapper);

    /**
     * 根据ID查询反馈（关联用户表）
     *
     * @param id 反馈ID
     * @return 反馈信息
     */
    SpFeedbackVo selectVoById(Long id);
}
