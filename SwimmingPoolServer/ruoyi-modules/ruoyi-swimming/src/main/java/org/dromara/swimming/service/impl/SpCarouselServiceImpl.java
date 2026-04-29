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
import org.dromara.swimming.domain.bo.SpCarouselBo;
import org.dromara.swimming.domain.vo.SpCarouselVo;
import org.dromara.swimming.domain.SpCarousel;
import org.dromara.swimming.mapper.SpCarouselMapper;
import org.dromara.swimming.service.ISpCarouselService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 轮播图Service业务层处理
 *
 * @author W
 * @date 2026-02-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpCarouselServiceImpl implements ISpCarouselService {

    private final SpCarouselMapper baseMapper;

    /**
     * 查询轮播图
     *
     * @param id 主键
     * @return 轮播图
     */
    @Override
    public SpCarouselVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询轮播图列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 轮播图分页列表
     */
    @Override
    public TableDataInfo<SpCarouselVo> queryPageList(SpCarouselBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpCarousel> lqw = buildQueryWrapper(bo);
        Page<SpCarouselVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的轮播图列表
     *
     * @param bo 查询条件
     * @return 轮播图列表
     */
    @Override
    public List<SpCarouselVo> queryList(SpCarouselBo bo) {
        LambdaQueryWrapper<SpCarousel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpCarousel> buildQueryWrapper(SpCarouselBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpCarousel> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SpCarousel::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), SpCarousel::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getImageUrl()), SpCarousel::getImageUrl, bo.getImageUrl());
        lqw.eq(bo.getSortOrder() != null, SpCarousel::getSortOrder, bo.getSortOrder());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpCarousel::getStatus, bo.getStatus());
        lqw.eq(bo.getStartTime() != null, SpCarousel::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, SpCarousel::getEndTime, bo.getEndTime());
        return lqw;
    }

    /**
     * 新增轮播图
     *
     * @param bo 轮播图
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpCarouselBo bo) {
        SpCarousel add = MapstructUtils.convert(bo, SpCarousel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改轮播图
     *
     * @param bo 轮播图
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpCarouselBo bo) {
        SpCarousel update = MapstructUtils.convert(bo, SpCarousel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpCarousel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除轮播图信息
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
