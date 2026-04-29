package org.dromara.swimming.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.satoken.utils.LoginHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.swimming.domain.SpIotDeviceAlarm;
import org.dromara.swimming.domain.bo.SpIotDeviceAlarmBo;
import org.dromara.swimming.domain.vo.SpIotDeviceAlarmVo;
import org.dromara.swimming.mapper.SpIotDeviceAlarmMapper;
import org.dromara.swimming.service.ISpIotDeviceAlarmService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * IoT设备告警Service业务层处理
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpIotDeviceAlarmServiceImpl implements ISpIotDeviceAlarmService {

    private final SpIotDeviceAlarmMapper baseMapper;

    @Override
    public SpIotDeviceAlarmVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public List<SpIotDeviceAlarmVo> queryList(SpIotDeviceAlarmBo bo) {
        LambdaQueryWrapper<SpIotDeviceAlarm> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(SpIotDeviceAlarm::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<SpIotDeviceAlarmVo> queryPageList(SpIotDeviceAlarmBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpIotDeviceAlarm> lqw = buildQueryWrapper(bo);
        Page<SpIotDeviceAlarm> page = pageQuery.build();
        Page<SpIotDeviceAlarmVo> result = baseMapper.selectVoPage(page, lqw);
        return result.getRecords();
    }

    private LambdaQueryWrapper<SpIotDeviceAlarm> buildQueryWrapper(SpIotDeviceAlarmBo bo) {
        LambdaQueryWrapper<SpIotDeviceAlarm> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getId() != null, SpIotDeviceAlarm::getId, bo.getId());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceKey()), SpIotDeviceAlarm::getDeviceKey, bo.getDeviceKey());
        lqw.eq(StringUtils.isNotBlank(bo.getAlarmType()), SpIotDeviceAlarm::getAlarmType, bo.getAlarmType());
        lqw.eq(StringUtils.isNotBlank(bo.getAlarmLevel()), SpIotDeviceAlarm::getAlarmLevel, bo.getAlarmLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getIsHandled()), SpIotDeviceAlarm::getIsHandled, bo.getIsHandled());
        return lqw;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SpIotDeviceAlarmBo bo) {
        SpIotDeviceAlarm add = MapstructUtils.convert(bo, SpIotDeviceAlarm.class);
        validEntityBeforeSave(add);
        return baseMapper.insert(add) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SpIotDeviceAlarmBo bo) {
        SpIotDeviceAlarm update = MapstructUtils.convert(bo, SpIotDeviceAlarm.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    private void validEntityBeforeSave(SpIotDeviceAlarm entity) {
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
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAlarm(String deviceKey, String alarmType, String alarmLevel,
                                String alarmTitle, String alarmMessage, String alarmData) {
        SpIotDeviceAlarm alarm = new SpIotDeviceAlarm();
        alarm.setDeviceKey(deviceKey);
        alarm.setAlarmType(alarmType);
        alarm.setAlarmLevel(alarmLevel);
        alarm.setAlarmTitle(alarmTitle);
        alarm.setAlarmMessage(alarmMessage);
        alarm.setAlarmData(alarmData);
        alarm.setIsHandled("0"); // 未处理
        alarm.setCreateTime(LocalDateTime.now());

        return baseMapper.insert(alarm) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAlarm(SpIotDeviceAlarm alarm) {
        if (alarm.getIsHandled() == null) {
            alarm.setIsHandled("0"); // 默认未处理
        }
        if (alarm.getCreateTime() == null) {
            alarm.setCreateTime(LocalDateTime.now());
        }
        return baseMapper.insert(alarm) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleAlarm(Long alarmId, String handleResult) {
        SpIotDeviceAlarm alarm = new SpIotDeviceAlarm();
        alarm.setId(alarmId);
        alarm.setIsHandled("1"); // 已处理
        alarm.setHandleUserId(LoginHelper.getUserId());
        alarm.setHandleTime(LocalDateTime.now());
        alarm.setHandleResult(handleResult);

        return baseMapper.updateById(alarm) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchHandleAlarms(Collection<Long> alarmIds, String handleResult) {
        if (alarmIds == null || alarmIds.isEmpty()) {
            return 0;
        }

        LambdaQueryWrapper<SpIotDeviceAlarm> lqw = Wrappers.lambdaQuery();
        lqw.in(SpIotDeviceAlarm::getId, alarmIds);

        SpIotDeviceAlarm alarm = new SpIotDeviceAlarm();
        alarm.setIsHandled("1");
        alarm.setHandleUserId(LoginHelper.getUserId());
        alarm.setHandleTime(LocalDateTime.now());
        alarm.setHandleResult(handleResult);

        return baseMapper.update(alarm, lqw);
    }

    @Override
    public Map<String, Object> getAlarmStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总告警数
        Long totalCount = baseMapper.selectCount(null);

        // 未处理告警数
        LambdaQueryWrapper<SpIotDeviceAlarm> unhandledLqw = Wrappers.lambdaQuery();
        unhandledLqw.eq(SpIotDeviceAlarm::getIsHandled, "0");
        Long unhandledCount = baseMapper.selectCount(unhandledLqw);

        // 今日告警数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<SpIotDeviceAlarm> todayLqw = Wrappers.lambdaQuery();
        todayLqw.ge(SpIotDeviceAlarm::getCreateTime, todayStart);
        Long todayCount = baseMapper.selectCount(todayLqw);

        // 严重告警数
        LambdaQueryWrapper<SpIotDeviceAlarm> criticalLqw = Wrappers.lambdaQuery();
        criticalLqw.eq(SpIotDeviceAlarm::getAlarmLevel, "critical");
        Long criticalCount = baseMapper.selectCount(criticalLqw);

        statistics.put("totalCount", totalCount);
        statistics.put("unhandledCount", unhandledCount);
        statistics.put("todayCount", todayCount);
        statistics.put("criticalCount", criticalCount);

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkThresholdAndCreateAlarm(String deviceKey, Map<String, Object> params) {
        // 定义阈值规则
        Map<String, ThresholdRule> rules = new HashMap<>();
        rules.put("ph_value", new ThresholdRule(6.5, 8.5, "warning"));
        rules.put("chlorine", new ThresholdRule(0.3, 1.5, "critical"));
        rules.put("temperature", new ThresholdRule(26, 30, "warning"));

        // 检查每个指标
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Number) {
                double numValue = ((Number) value).doubleValue();
                ThresholdRule rule = rules.get(key);

                if (rule != null) {
                    if (numValue < rule.min || numValue > rule.max) {
                        // 超出阈值，创建告警
                        String alarmLevel = rule.level;
                        String alarmType = "threshold_exceeded";
                        String alarmTitle = getAlarmTitle(key, alarmLevel);
                        String alarmMessage = String.format(
                            "%s异常: 当前值%.2f，标准范围%.2f-%.2f",
                            getMetricName(key),
                            numValue,
                            rule.min,
                            rule.max
                        );

                        Map<String, Object> alarmData = new HashMap<>();
                        alarmData.put("metric", key);
                        alarmData.put("currentValue", numValue);
                        alarmData.put("min", rule.min);
                        alarmData.put("max", rule.max);

                        createAlarm(deviceKey, alarmType, alarmLevel, alarmTitle, alarmMessage,
                                     JSONUtil.toJsonStr(alarmData));

                        log.warn("设备 {} 触发告警: {}", deviceKey, alarmMessage);
                    }
                }
            }
        }
    }

    private String getAlarmTitle(String metric, String level) {
        String metricName = getMetricName(metric);
        return metricName + (level.equals("critical") ? "严重告警" : "告警");
    }

    private String getMetricName(String metric) {
        Map<String, String> names = new HashMap<>();
        names.put("ph_value", "pH值");
        names.put("chlorine", "余氯");
        names.put("temperature", "水温");
        names.put("turbidity", "浊度");
        return names.getOrDefault(metric, metric);
    }

    /**
     * 阈值规则
     */
    private static class ThresholdRule {
        double min;
        double max;
        String level;

        ThresholdRule(double min, double max, String level) {
            this.min = min;
            this.max = max;
            this.level = level;
        }
    }
}
