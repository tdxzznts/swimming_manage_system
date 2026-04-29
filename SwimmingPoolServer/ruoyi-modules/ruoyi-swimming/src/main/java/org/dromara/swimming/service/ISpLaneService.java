package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpLaneVo;
import org.dromara.swimming.domain.bo.SpLaneBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 泳道Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpLaneService {

    /**
     * 查询泳道
     *
     * @param id 主键
     * @return 泳道
     */
    SpLaneVo queryById(Long id);

    /**
     * 分页查询泳道列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 泳道分页列表
     */
    TableDataInfo<SpLaneVo> queryPageList(SpLaneBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的泳道列表
     *
     * @param bo 查询条件
     * @return 泳道列表
     */
    List<SpLaneVo> queryList(SpLaneBo bo);

    /**
     * 新增泳道
     *
     * @param bo 泳道
     * @return 是否新增成功
     */
    Boolean insertByBo(SpLaneBo bo);

    /**
     * 修改泳道
     *
     * @param bo 泳道
     * @return 是否修改成功
     */
    Boolean updateByBo(SpLaneBo bo);

    /**
     * 校验并批量删除泳道信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
