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
import org.dromara.swimming.domain.bo.SpTimeSlotBo;
import org.dromara.swimming.domain.vo.SpTimeSlotVo;
import org.dromara.swimming.domain.SpTimeSlot;
import org.dromara.swimming.mapper.SpTimeSlotMapper;
import org.dromara.swimming.service.ISpTimeSlotService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 预约时段Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpTimeSlotServiceImpl implements ISpTimeSlotService {

    private final SpTimeSlotMapper baseMapper;

    /**
     * 查询预约时段
     *
     * @param id 主键
     * @return 预约时段
     */
    @Override
    public SpTimeSlotVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询预约时段列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 预约时段分页列表
     */
    @Override
    public TableDataInfo<SpTimeSlotVo> queryPageList(SpTimeSlotBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpTimeSlot> lqw = buildQueryWrapper(bo);
        Page<SpTimeSlotVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的预约时段列表
     *
     * @param bo 查询条件
     * @return 预约时段列表
     */
    @Override
    public List<SpTimeSlotVo> queryList(SpTimeSlotBo bo) {
        LambdaQueryWrapper<SpTimeSlot> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpTimeSlot> buildQueryWrapper(SpTimeSlotBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpTimeSlot> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SpTimeSlot::getId);
        lqw.eq(bo.getLaneId() != null, SpTimeSlot::getLaneId, bo.getLaneId());
        lqw.eq(bo.getSlotDate() != null, SpTimeSlot::getSlotDate, bo.getSlotDate());
        lqw.eq(bo.getStartTime() != null, SpTimeSlot::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, SpTimeSlot::getEndTime, bo.getEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getSlotType()), SpTimeSlot::getSlotType, bo.getSlotType());
        lqw.eq(bo.getCapacity() != null, SpTimeSlot::getCapacity, bo.getCapacity());
        lqw.eq(bo.getBookedCount() != null, SpTimeSlot::getBookedCount, bo.getBookedCount());
        lqw.eq(bo.getPrice() != null, SpTimeSlot::getPrice, bo.getPrice());
        lqw.eq(StringUtils.isNotBlank(bo.getIsAvailable()), SpTimeSlot::getIsAvailable, bo.getIsAvailable());
        return lqw;
    }

    /**
     * 新增预约时段
     *
     * @param bo 预约时段
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpTimeSlotBo bo) {
        SpTimeSlot add = MapstructUtils.convert(bo, SpTimeSlot.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改预约时段
     *
     * @param bo 预约时段
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpTimeSlotBo bo) {
        SpTimeSlot update = MapstructUtils.convert(bo, SpTimeSlot.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpTimeSlot entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除预约时段信息
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
