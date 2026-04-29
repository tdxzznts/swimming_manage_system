package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.SpAnnouncementVo;
import org.dromara.swimming.domain.bo.SpAnnouncementBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 系统公告Service接口
 *
 * @author W
 * @date 2026-02-05
 */
public interface ISpAnnouncementService {

    /**
     * 查询系统公告
     *
     * @param id 主键
     * @return 系统公告
     */
    SpAnnouncementVo queryById(Long id);

    /**
     * 分页查询系统公告列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 系统公告分页列表
     */
    TableDataInfo<SpAnnouncementVo> queryPageList(SpAnnouncementBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的系统公告列表
     *
     * @param bo 查询条件
     * @return 系统公告列表
     */
    List<SpAnnouncementVo> queryList(SpAnnouncementBo bo);

    /**
     * 新增系统公告
     *
     * @param bo 系统公告
     * @return 是否新增成功
     */
    Boolean insertByBo(SpAnnouncementBo bo);

    /**
     * 修改系统公告
     *
     * @param bo 系统公告
     * @return 是否修改成功
     */
    Boolean updateByBo(SpAnnouncementBo bo);

    /**
     * 校验并批量删除系统公告信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
