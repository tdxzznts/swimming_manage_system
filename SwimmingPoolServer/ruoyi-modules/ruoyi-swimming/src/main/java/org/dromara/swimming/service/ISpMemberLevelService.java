package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpMemberLevelVo;
import org.dromara.swimming.domain.bo.SpMemberLevelBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员等级配置Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpMemberLevelService {

    /**
     * 查询会员等级配置
     *
     * @param id 主键
     * @return 会员等级配置
     */
    SpMemberLevelVo queryById(Long id);

    /**
     * 分页查询会员等级配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员等级配置分页列表
     */
    TableDataInfo<SpMemberLevelVo> queryPageList(SpMemberLevelBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员等级配置列表
     *
     * @param bo 查询条件
     * @return 会员等级配置列表
     */
    List<SpMemberLevelVo> queryList(SpMemberLevelBo bo);

    /**
     * 新增会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否新增成功
     */
    Boolean insertByBo(SpMemberLevelBo bo);

    /**
     * 修改会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否修改成功
     */
    Boolean updateByBo(SpMemberLevelBo bo);

    /**
     * 校验并批量删除会员等级配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
