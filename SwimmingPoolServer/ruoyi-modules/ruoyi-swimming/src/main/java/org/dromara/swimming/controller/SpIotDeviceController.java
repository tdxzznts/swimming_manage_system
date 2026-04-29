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
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.dromara.swimming.domain.bo.SpIotDeviceBo;
import org.dromara.swimming.domain.vo.SpIotDeviceVo;
import org.dromara.swimming.service.ISpIotDeviceService;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * IoT设备管理Controller
 *
 * @author swimming
 * @date 2026-02-08
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/iot/device")
public class SpIotDeviceController extends BaseController {

    private final ISpIotDeviceService deviceService;

    /**
     * 查询IoT设备列表
     */
    @GetMapping("/list")
    public TableDataInfo<SpIotDeviceVo> list(SpIotDeviceBo bo, PageQuery pageQuery) {
        return deviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出IoT设备列表
     */
    @Log(title = "IoT设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpIotDeviceBo bo, HttpServletResponse response) {
        List<SpIotDeviceVo> list = deviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "IoT设备", SpIotDeviceVo.class, response);
    }

    /**
     * 获取IoT设备详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<SpIotDeviceVo> getInfo(@PathVariable Long id) {
        return R.ok(deviceService.queryById(id));
    }

    /**
     * 新增IoT设备
     */
    @Log(title = "IoT设备", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpIotDeviceBo bo) {
        return toAjax(deviceService.insertByBo(bo));
    }

    /**
     * 修改IoT设备
     */
    @Log(title = "IoT设备", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpIotDeviceBo bo) {
        return toAjax(deviceService.updateByBo(bo));
    }

    /**
     * 删除IoT设备
     *
     * @param ids 主键串
     */
    @Log(title = "IoT设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(deviceService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 获取设备统计信息
     */
    @GetMapping("/statistics")
    public R<Object> getStatistics() {
        return R.ok(deviceService.getDeviceStatistics());
    }

    /**
     * 更新设备配置（设备影子）
     *
     * @param deviceKey 设备key
     * @param shadow    设备影子
     */
    @Log(title = "更新设备配置", businessType = BusinessType.UPDATE)
    @PutMapping("/config/{deviceKey}")
    public R<Void> updateConfig(@PathVariable String deviceKey, @RequestBody String shadow) {
        return toAjax(deviceService.updateDeviceShadow(deviceKey, shadow));
    }
}
