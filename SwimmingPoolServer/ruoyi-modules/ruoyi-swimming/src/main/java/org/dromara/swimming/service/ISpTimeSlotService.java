package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpTimeSlotVo;
import org.dromara.swimming.domain.bo.SpTimeSlotBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 预约时段Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpTimeSlotService {

    /**
     * 查询预约时段
     *
     * @param id 主键
     * @return 预约时段
     */
    SpTimeSlotVo queryById(Long id);

    /**
     * 分页查询预约时段列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 预约时段分页列表
     */
    TableDataInfo<SpTimeSlotVo> queryPageList(SpTimeSlotBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的预约时段列表
     *
     * @param bo 查询条件
     * @return 预约时段列表
     */
    List<SpTimeSlotVo> queryList(SpTimeSlotBo bo);

    /**
     * 新增预约时段
     *
     * @param bo 预约时段
     * @return 是否新增成功
     */
    Boolean insertByBo(SpTimeSlotBo bo);

    /**
     * 修改预约时段
     *
     * @param bo 预约时段
     * @return 是否修改成功
     */
    Boolean updateByBo(SpTimeSlotBo bo);

    /**
     * 校验并批量删除预约时段信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
