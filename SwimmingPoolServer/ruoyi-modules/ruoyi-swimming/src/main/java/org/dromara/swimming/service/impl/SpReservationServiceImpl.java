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
import org.dromara.swimming.domain.bo.SpReservationBo;
import org.dromara.swimming.domain.vo.SpReservationVo;
import org.dromara.swimming.domain.SpReservation;
import org.dromara.swimming.mapper.SpReservationMapper;
import org.dromara.swimming.service.ISpReservationService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 预约记录Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpReservationServiceImpl implements ISpReservationService {

    private final SpReservationMapper baseMapper;

    /**
     * 查询预约记录
     *
     * @param id 主键
     * @return 预约记录
     */
    @Override
    public SpReservationVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询预约记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 预约记录分页列表
     */
    @Override
    public TableDataInfo<SpReservationVo> queryPageList(SpReservationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpReservation> lqw = buildQueryWrapper(bo);
        Page<SpReservationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的预约记录列表
     *
     * @param bo 查询条件
     * @return 预约记录列表
     */
    @Override
    public List<SpReservationVo> queryList(SpReservationBo bo) {
        LambdaQueryWrapper<SpReservation> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpReservation> buildQueryWrapper(SpReservationBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpReservation> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SpReservation::getId);
        lqw.eq(bo.getUserId() != null, SpReservation::getUserId, bo.getUserId());
        lqw.eq(bo.getTimeSlotId() != null, SpReservation::getTimeSlotId, bo.getTimeSlotId());
        lqw.eq(bo.getLaneId() != null, SpReservation::getLaneId, bo.getLaneId());
        lqw.eq(StringUtils.isNotBlank(bo.getReservationNo()), SpReservation::getReservationNo, bo.getReservationNo());
        lqw.eq(bo.getReservationDate() != null, SpReservation::getReservationDate, bo.getReservationDate());
        lqw.eq(bo.getStartTime() != null, SpReservation::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, SpReservation::getEndTime, bo.getEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpReservation::getStatus, bo.getStatus());
        lqw.eq(bo.getAmount() != null, SpReservation::getAmount, bo.getAmount());
        lqw.eq(bo.getOrderId() != null, SpReservation::getOrderId, bo.getOrderId());
        lqw.eq(bo.getCheckInTime() != null, SpReservation::getCheckInTime, bo.getCheckInTime());
        lqw.eq(bo.getCheckOutTime() != null, SpReservation::getCheckOutTime, bo.getCheckOutTime());
        lqw.eq(bo.getCancelTime() != null, SpReservation::getCancelTime, bo.getCancelTime());
        lqw.eq(StringUtils.isNotBlank(bo.getCancelReason()), SpReservation::getCancelReason, bo.getCancelReason());
        return lqw;
    }

    /**
     * 新增预约记录
     *
     * @param bo 预约记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpReservationBo bo) {
        SpReservation add = MapstructUtils.convert(bo, SpReservation.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改预约记录
     *
     * @param bo 预约记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpReservationBo bo) {
        SpReservation update = MapstructUtils.convert(bo, SpReservation.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpReservation entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除预约记录信息
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
