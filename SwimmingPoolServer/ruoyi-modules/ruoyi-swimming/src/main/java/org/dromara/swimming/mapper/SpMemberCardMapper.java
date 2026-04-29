package org.dromara.swimming.mapper;

import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.domain.bo.SpMemberCardBo;
import org.dromara.swimming.domain.vo.SpMemberCardVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员卡Mapper接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface SpMemberCardMapper extends BaseMapperPlus<SpMemberCard, SpMemberCardVo> {

    /**
     * 分页查询会员卡列表（关联用户表）
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 会员卡分页列表
     */
    Page<SpMemberCardVo> selectVoPage(Page<SpMemberCardVo> page, @Param("ew") SpMemberCardBo queryWrapper);

    /**
     * 查询会员卡列表（关联用户表）
     *
     * @param queryWrapper 查询条件
     * @return 会员卡列表
     */
    List<SpMemberCardVo> selectVoList(@Param("ew") SpMemberCardBo queryWrapper);

    /**
     * 根据ID查询会员卡（关联用户表）
     *
     * @param id 会员卡ID
     * @return 会员卡信息
     */
    SpMemberCardVo selectVoById(Long id);
}
