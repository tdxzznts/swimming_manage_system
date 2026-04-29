package org.dromara.swimming.mapper;

import org.dromara.swimming.domain.SpOrder;
import org.dromara.swimming.domain.bo.SpOrderBo;
import org.dromara.swimming.domain.vo.SpOrderVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.List;

/**
 * 订单Mapper接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface SpOrderMapper extends BaseMapperPlus<SpOrder, SpOrderVo> {

    /**
     * 分页查询订单列表（关联用户表）
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 订单分页列表
     */
    Page<SpOrderVo> selectVoPage(Page<SpOrderVo> page, @Param("ew") SpOrderBo queryWrapper);

    /**
     * 查询订单列表（关联用户表）
     *
     * @param queryWrapper 查询条件
     * @return 订单列表
     */
    List<SpOrderVo> selectVoList(@Param("ew") SpOrderBo queryWrapper);

    /**
     * 根据ID查询订单（关联用户表）
     *
     * @param id 订单ID
     * @return 订单信息
     */
    SpOrderVo selectVoById(Long id);
}
