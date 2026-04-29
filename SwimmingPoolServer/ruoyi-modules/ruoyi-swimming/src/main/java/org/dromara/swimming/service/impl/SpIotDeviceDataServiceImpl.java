package org.dromara.swimming.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.swimming.domain.SpIotDeviceData;
import org.dromara.swimming.domain.vo.SpIotDeviceDataVo;
import org.dromara.swimming.mapper.SpIotDeviceDataMapper;
import org.dromara.swimming.service.ISpIotDeviceDataService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * IoT设备数据Service业务层处理
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpIotDeviceDataServiceImpl implements ISpIotDeviceDataService {

    private final SpIotDeviceDataMapper baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(SpIotDeviceData deviceData) {
        return baseMapper.insert(deviceData) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchSave(String deviceKey, List<Map<String, Object>> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }

        List<SpIotDeviceData> dataList = new ArrayList<>();
        for (Map<String, Object> record : records) {
            SpIotDeviceData deviceData = new SpIotDeviceData();
            deviceData.setDeviceKey(deviceKey);
            deviceData.setMethod((String) record.get("method"));
            deviceData.setParams(JSONUtil.toJsonStr(record.get("params")));

            Object timestamp = record.get("timestamp");
            if (timestamp != null) {
                deviceData.setTimestamp(((Number) timestamp).longValue());
            }

            // 采集时间使用当前时间或时间戳转换
            deviceData.setCollectTime(LocalDateTime.now());

            dataList.add(deviceData);
        }

        return baseMapper.batchInsert(dataList);
    }

    @Override
    public SpIotDeviceDataVo getLatestData(String deviceKey) {
        return baseMapper.getLatestData(deviceKey);
    }

    @Override
    public List<SpIotDeviceDataVo> queryHistoryData(String deviceKey, String startTime, String endTime, PageQuery pageQuery) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        Page<SpIotDeviceData> page = pageQuery.build();
        IPage<SpIotDeviceDataVo> result = baseMapper.queryHistoryData(page, deviceKey, start, end);
        return result.getRecords();
    }

    @Override
    public Map<String, Object> getDataStatistics(String deviceKey, String startTime, String endTime) {
        Map<String, Object> statistics = new HashMap<>();

        // TODO: 实现数据统计逻辑
        // 1. 查询指定时间范围的数据
        // 2. 计算最大值、最小值、平均值
        // 3. 计算数据点数量

        statistics.put("dataCount", 0);
        statistics.put("avgValue", 0.0);
        statistics.put("maxValue", 0.0);
        statistics.put("minValue", 0.0);

        return statistics;
    }
}
