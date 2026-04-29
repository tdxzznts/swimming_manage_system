package org.dromara.swimming.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.*;
import org.dromara.swimming.domain.vo.SpIotDeviceDataVo;
import org.dromara.swimming.service.ISpIotDeviceDataService;

import java.util.List;

/**
 * IoT设备数据Controller
 *
 * @author swimming
 * @date 2026-02-08
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/iot/data")
public class SpIotDataController extends BaseController {

    private final ISpIotDeviceDataService deviceDataService;

    /**
     * 查询设备最新数据
     *
     * @param deviceKey 设备key
     */
    @GetMapping("/latest/{deviceKey}")
    public R<SpIotDeviceDataVo> getLatestData(@PathVariable String deviceKey) {
        return R.ok(deviceDataService.getLatestData(deviceKey));
    }

    /**
     * 查询设备历史数据(分页)
     *
     * @param deviceKey  设备key
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageQuery  分页参数
     */
    @GetMapping("/history")
    public TableDataInfo<SpIotDeviceDataVo> getHistoryData(
        @RequestParam String deviceKey,
        @RequestParam String startTime,
        @RequestParam String endTime,
        PageQuery pageQuery) {

        List<SpIotDeviceDataVo> records = deviceDataService.queryHistoryData(
            deviceKey, startTime, endTime, pageQuery);

        TableDataInfo<SpIotDeviceDataVo> tableDataInfo = new TableDataInfo<>();
        tableDataInfo.setRows(records);
        tableDataInfo.setTotal(records.size());
        return tableDataInfo;
    }

    /**
     * 获取设备数据统计
     *
     * @param deviceKey 设备key
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @GetMapping("/statistics")
    public R<Object> getDataStatistics(
        @RequestParam String deviceKey,
        @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime) {

        return R.ok(deviceDataService.getDataStatistics(deviceKey, startTime, endTime));
    }
}
