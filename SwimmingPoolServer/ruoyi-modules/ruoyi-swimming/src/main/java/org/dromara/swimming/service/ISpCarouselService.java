package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpCarouselVo;
import org.dromara.swimming.domain.bo.SpCarouselBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 轮播图Service接口
 *
 * @author W
 * @date 2026-02-07
 */
public interface ISpCarouselService {

    /**
     * 查询轮播图
     *
     * @param id 主键
     * @return 轮播图
     */
    SpCarouselVo queryById(Long id);

    /**
     * 分页查询轮播图列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 轮播图分页列表
     */
    TableDataInfo<SpCarouselVo> queryPageList(SpCarouselBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的轮播图列表
     *
     * @param bo 查询条件
     * @return 轮播图列表
     */
    List<SpCarouselVo> queryList(SpCarouselBo bo);

    /**
     * 新增轮播图
     *
     * @param bo 轮播图
     * @return 是否新增成功
     */
    Boolean insertByBo(SpCarouselBo bo);

    /**
     * 修改轮播图
     *
     * @param bo 轮播图
     * @return 是否修改成功
     */
    Boolean updateByBo(SpCarouselBo bo);

    /**
     * 校验并批量删除轮播图信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
