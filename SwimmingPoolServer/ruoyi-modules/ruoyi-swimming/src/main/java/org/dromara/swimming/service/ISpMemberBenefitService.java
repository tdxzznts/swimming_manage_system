package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpMemberBenefitVo;
import org.dromara.swimming.domain.bo.SpMemberBenefitBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员权益配置Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpMemberBenefitService {

    /**
     * 查询会员权益配置
     *
     * @param id 主键
     * @return 会员权益配置
     */
    SpMemberBenefitVo queryById(Long id);

    /**
     * 分页查询会员权益配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员权益配置分页列表
     */
    TableDataInfo<SpMemberBenefitVo> queryPageList(SpMemberBenefitBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员权益配置列表
     *
     * @param bo 查询条件
     * @return 会员权益配置列表
     */
    List<SpMemberBenefitVo> queryList(SpMemberBenefitBo bo);

    /**
     * 新增会员权益配置
     *
     * @param bo 会员权益配置
     * @return 是否新增成功
     */
    Boolean insertByBo(SpMemberBenefitBo bo);

    /**
     * 修改会员权益配置
     *
     * @param bo 会员权益配置
     * @return 是否修改成功
     */
    Boolean updateByBo(SpMemberBenefitBo bo);

    /**
     * 校验并批量删除会员权益配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
