package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpReservationVo;
import org.dromara.swimming.domain.bo.SpReservationBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 预约记录Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpReservationService {

    /**
     * 查询预约记录
     *
     * @param id 主键
     * @return 预约记录
     */
    SpReservationVo queryById(Long id);

    /**
     * 分页查询预约记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 预约记录分页列表
     */
    TableDataInfo<SpReservationVo> queryPageList(SpReservationBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的预约记录列表
     *
     * @param bo 查询条件
     * @return 预约记录列表
     */
    List<SpReservationVo> queryList(SpReservationBo bo);

    /**
     * 新增预约记录
     *
     * @param bo 预约记录
     * @return 是否新增成功
     */
    Boolean insertByBo(SpReservationBo bo);

    /**
     * 修改预约记录
     *
     * @param bo 预约记录
     * @return 是否修改成功
     */
    Boolean updateByBo(SpReservationBo bo);

    /**
     * 校验并批量删除预约记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
