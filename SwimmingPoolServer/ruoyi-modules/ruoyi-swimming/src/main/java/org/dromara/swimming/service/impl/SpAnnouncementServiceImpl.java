package org.dromara.swimming.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.swimming.domain.bo.SpAnnouncementBo;
import org.dromara.swimming.domain.vo.SpAnnouncementVo;
import org.dromara.swimming.domain.SpAnnouncement;
import org.dromara.swimming.mapper.SpAnnouncementMapper;
import org.dromara.swimming.service.ISpAnnouncementService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 系统公告Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpAnnouncementServiceImpl implements ISpAnnouncementService {

    private final SpAnnouncementMapper baseMapper;

    /**
     * 查询系统公告
     *
     * @param id 主键
     * @return 系统公告
     */
    @Override
    public SpAnnouncementVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询系统公告列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 系统公告分页列表
     */
    @Override
    public TableDataInfo<SpAnnouncementVo> queryPageList(SpAnnouncementBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpAnnouncement> lqw = buildQueryWrapper(bo);
        Page<SpAnnouncementVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的系统公告列表
     *
     * @param bo 查询条件
     * @return 系统公告列表
     */
    @Override
    public List<SpAnnouncementVo> queryList(SpAnnouncementBo bo) {
        LambdaQueryWrapper<SpAnnouncement> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpAnnouncement> buildQueryWrapper(SpAnnouncementBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpAnnouncement> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(SpAnnouncement::getPublishTime);
        lqw.like(StringUtils.isNotBlank(bo.getTitle()), SpAnnouncement::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getAnnouncementType()), SpAnnouncement::getAnnouncementType, bo.getAnnouncementType());
        lqw.eq(bo.getPublishTime() != null, SpAnnouncement::getPublishTime, bo.getPublishTime());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpAnnouncement::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增系统公告
     *
     * @param bo 系统公告
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpAnnouncementBo bo) {
        SpAnnouncement add = MapstructUtils.convert(bo, SpAnnouncement.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改系统公告
     *
     * @param bo 系统公告
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpAnnouncementBo bo) {
        SpAnnouncement update = MapstructUtils.convert(bo, SpAnnouncement.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpAnnouncement entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除系统公告信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
