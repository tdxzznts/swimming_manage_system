package org.dromara.swimming.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.swimming.domain.SpIotAlarmRule;
import org.dromara.swimming.domain.bo.SpIotAlarmRuleBo;
import org.dromara.swimming.domain.vo.SpIotAlarmRuleVo;
import org.dromara.swimming.mapper.SpIotAlarmRuleMapper;
import org.dromara.swimming.service.ISpIotAlarmRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * IoT设备告警规则Service业务层处理
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpIotAlarmRuleServiceImpl implements ISpIotAlarmRuleService {

    private final SpIotAlarmRuleMapper baseMapper;

    @Override
    public SpIotAlarmRuleVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public List<SpIotAlarmRuleVo> queryList(SpIotAlarmRuleBo bo) {
        LambdaQueryWrapper<SpIotAlarmRule> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(SpIotAlarmRule::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<SpIotAlarmRuleVo> queryPageList(SpIotAlarmRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpIotAlarmRule> lqw = buildQueryWrapper(bo);
        Page<SpIotAlarmRule> page = pageQuery.build();
        Page<SpIotAlarmRuleVo> result = baseMapper.selectVoPage(page, lqw);
        return result.getRecords();
    }

    private LambdaQueryWrapper<SpIotAlarmRule> buildQueryWrapper(SpIotAlarmRuleBo bo) {
        LambdaQueryWrapper<SpIotAlarmRule> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getId() != null, SpIotAlarmRule::getId, bo.getId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceKey()), SpIotAlarmRule::getDeviceKey, bo.getDeviceKey());
        lqw.like(StringUtils.isNotBlank(bo.getRuleName()), SpIotAlarmRule::getRuleName, bo.getRuleName());
        lqw.eq(StringUtils.isNotBlank(bo.getMetricName()), SpIotAlarmRule::getMetricName, bo.getMetricName());
        lqw.eq(StringUtils.isNotBlank(bo.getAlarmLevel()), SpIotAlarmRule::getAlarmLevel, bo.getAlarmLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpIotAlarmRule::getStatus, bo.getStatus());
        return lqw;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SpIotAlarmRuleBo bo) {
        SpIotAlarmRule add = MapstructUtils.convert(bo, SpIotAlarmRule.class);
        validEntityBeforeSave(add);
        return baseMapper.insert(add) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SpIotAlarmRuleBo bo) {
        SpIotAlarmRule update = MapstructUtils.convert(bo, SpIotAlarmRule.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    private void validEntityBeforeSave(SpIotAlarmRule entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public List<SpIotAlarmRule> getEnabledRulesByDeviceKey(String deviceKey) {
        return baseMapper.selectEnabledRulesByDeviceKey(deviceKey);
    }
}
