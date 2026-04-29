package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpFeedbackVo;
import org.dromara.swimming.domain.bo.SpFeedbackBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 反馈Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpFeedbackService {

    /**
     * 查询反馈
     *
     * @param id 主键
     * @return 反馈
     */
    SpFeedbackVo queryById(Long id);

    /**
     * 分页查询反馈列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 反馈分页列表
     */
    TableDataInfo<SpFeedbackVo> queryPageList(SpFeedbackBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的反馈列表
     *
     * @param bo 查询条件
     * @return 反馈列表
     */
    List<SpFeedbackVo> queryList(SpFeedbackBo bo);

    /**
     * 新增反馈
     *
     * @param bo 反馈
     * @return 是否新增成功
     */
    Boolean insertByBo(SpFeedbackBo bo);

    /**
     * 修改反馈
     *
     * @param bo 反馈
     * @return 是否修改成功
     */
    Boolean updateByBo(SpFeedbackBo bo);

    /**
     * 校验并批量删除反馈信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
