package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpOrderVo;
import org.dromara.swimming.domain.bo.SpOrderBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 订单Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpOrderService {

    /**
     * 查询订单
     *
     * @param id 主键
     * @return 订单
     */
    SpOrderVo queryById(Long id);

    /**
     * 分页查询订单列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 订单分页列表
     */
    TableDataInfo<SpOrderVo> queryPageList(SpOrderBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的订单列表
     *
     * @param bo 查询条件
     * @return 订单列表
     */
    List<SpOrderVo> queryList(SpOrderBo bo);

    /**
     * 新增订单
     *
     * @param bo 订单
     * @return 是否新增成功
     */
    Boolean insertByBo(SpOrderBo bo);

    /**
     * 修改订单
     *
     * @param bo 订单
     * @return 是否修改成功
     */
    Boolean updateByBo(SpOrderBo bo);

    /**
     * 校验并批量删除订单信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
