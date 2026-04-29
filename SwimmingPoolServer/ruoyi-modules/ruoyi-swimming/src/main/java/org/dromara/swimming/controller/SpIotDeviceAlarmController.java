package org.dromara.swimming.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.dromara.swimming.domain.bo.SpIotDeviceAlarmBo;
import org.dromara.swimming.domain.vo.SpIotDeviceAlarmVo;
import org.dromara.swimming.service.ISpIotDeviceAlarmService;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * IoT设备告警Controller
 *
 * @author swimming
 * @date 2026-02-08
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/iot/alarm")
public class SpIotDeviceAlarmController extends BaseController {

    private final ISpIotDeviceAlarmService alarmService;

    /**
     * 查询IoT设备告警列表
     */
    @GetMapping("/list")
    public TableDataInfo<SpIotDeviceAlarmVo> list(SpIotDeviceAlarmBo bo, PageQuery pageQuery) {
        List<SpIotDeviceAlarmVo> records = alarmService.queryPageList(bo, pageQuery);
        TableDataInfo<SpIotDeviceAlarmVo> tableDataInfo = new TableDataInfo<>();
        tableDataInfo.setRows(records);
        tableDataInfo.setTotal(records.size());
        return tableDataInfo;
    }

    /**
     * 导出IoT设备告警列表
     */
    @Log(title = "IoT设备告警", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpIotDeviceAlarmBo bo, HttpServletResponse response) {
        List<SpIotDeviceAlarmVo> list = alarmService.queryList(bo);
        ExcelUtil.exportExcel(list, "IoT设备告警", SpIotDeviceAlarmVo.class, response);
    }

    /**
     * 获取IoT设备告警详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<SpIotDeviceAlarmVo> getInfo(@PathVariable Long id) {
        return R.ok(alarmService.queryById(id));
    }

    /**
     * 新增IoT设备告警
     */
    @Log(title = "IoT设备告警", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpIotDeviceAlarmBo bo) {
        return toAjax(alarmService.insertByBo(bo));
    }

    /**
     * 修改IoT设备告警
     */
    @Log(title = "IoT设备告警", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpIotDeviceAlarmBo bo) {
        return toAjax(alarmService.updateByBo(bo));
    }

    /**
     * 删除IoT设备告警
     *
     * @param ids 主键串
     */
    @Log(title = "IoT设备告警", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(alarmService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 获取告警统计信息
     */
    @GetMapping("/statistics")
    public R<Object> getStatistics() {
        return R.ok(alarmService.getAlarmStatistics());
    }

    /**
     * 处理告警
     *
     * @param alarmId      告警ID
     * @param handleResult 处理结果
     */
    @Log(title = "处理告警", businessType = BusinessType.UPDATE)
    @PutMapping("/handle/{alarmId}")
    public R<Void> handleAlarm(@PathVariable Long alarmId, @RequestBody String handleResult) {
        return toAjax(alarmService.handleAlarm(alarmId, handleResult));
    }

    /**
     * 批量处理告警
     *
     * @param alarmIds     告警ID列表
     * @param handleResult 处理结果
     */
    @Log(title = "批量处理告警", businessType = BusinessType.UPDATE)
    @PutMapping("/batchHandle")
    public R<Integer> batchHandle(@RequestBody List<Long> alarmIds, @RequestBody String handleResult) {
        return R.ok(alarmService.batchHandleAlarms(alarmIds, handleResult));
    }
}
