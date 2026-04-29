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
import org.dromara.swimming.domain.bo.SpReviewBo;
import org.dromara.swimming.domain.vo.SpReviewVo;
import org.dromara.swimming.domain.SpReview;
import org.dromara.swimming.mapper.SpReviewMapper;
import org.dromara.swimming.service.ISpReviewService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 评价Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpReviewServiceImpl implements ISpReviewService {

    private final SpReviewMapper baseMapper;

    /**
     * 查询评价
     *
     * @param id 主键
     * @return 评价
     */
    @Override
    public SpReviewVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询评价列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 评价分页列表
     */
    @Override
    public TableDataInfo<SpReviewVo> queryPageList(SpReviewBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpReview> lqw = buildQueryWrapper(bo);
        Page<SpReviewVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的评价列表
     *
     * @param bo 查询条件
     * @return 评价列表
     */
    @Override
    public List<SpReviewVo> queryList(SpReviewBo bo) {
        LambdaQueryWrapper<SpReview> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpReview> buildQueryWrapper(SpReviewBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpReview> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SpReview::getId);
        lqw.eq(bo.getUserId() != null, SpReview::getUserId, bo.getUserId());
        lqw.eq(bo.getReservationId() != null, SpReview::getReservationId, bo.getReservationId());
        lqw.eq(bo.getRating() != null, SpReview::getRating, bo.getRating());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), SpReview::getContent, bo.getContent());
        lqw.eq(StringUtils.isNotBlank(bo.getImages()), SpReview::getImages, bo.getImages());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpReview::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getReply()), SpReview::getReply, bo.getReply());
        lqw.eq(bo.getReplyUserId() != null, SpReview::getReplyUserId, bo.getReplyUserId());
        lqw.eq(bo.getReplyTime() != null, SpReview::getReplyTime, bo.getReplyTime());
        return lqw;
    }

    /**
     * 新增评价
     *
     * @param bo 评价
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpReviewBo bo) {
        SpReview add = MapstructUtils.convert(bo, SpReview.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改评价
     *
     * @param bo 评价
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpReviewBo bo) {
        SpReview update = MapstructUtils.convert(bo, SpReview.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpReview entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除评价信息
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
