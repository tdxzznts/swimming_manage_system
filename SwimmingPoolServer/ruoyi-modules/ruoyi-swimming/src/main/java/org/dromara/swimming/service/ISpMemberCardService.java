package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpMemberCardVo;
import org.dromara.swimming.domain.bo.SpMemberCardBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员卡Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpMemberCardService {

    /**
     * 查询会员卡
     *
     * @param id 主键
     * @return 会员卡
     */
    SpMemberCardVo queryById(Long id);

    /**
     * 分页查询会员卡列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员卡分页列表
     */
    TableDataInfo<SpMemberCardVo> queryPageList(SpMemberCardBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员卡列表
     *
     * @param bo 查询条件
     * @return 会员卡列表
     */
    List<SpMemberCardVo> queryList(SpMemberCardBo bo);

    /**
     * 新增会员卡
     *
     * @param bo 会员卡
     * @return 是否新增成功
     */
    Boolean insertByBo(SpMemberCardBo bo);

    /**
     * 修改会员卡
     *
     * @param bo 会员卡
     * @return 是否修改成功
     */
    Boolean updateByBo(SpMemberCardBo bo);

    /**
     * 校验并批量删除会员卡信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
