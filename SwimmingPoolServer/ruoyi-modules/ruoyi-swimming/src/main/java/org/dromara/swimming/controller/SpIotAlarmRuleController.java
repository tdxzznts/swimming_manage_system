package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
import org.dromara.swimming.domain.bo.SpIotAlarmRuleBo;
import org.dromara.swimming.domain.vo.SpIotAlarmRuleVo;
import org.dromara.swimming.service.ISpIotAlarmRuleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * IoT设备告警规则Controller
 *
 * @author swimming
 * @date 2026-02-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/iot/alarmRule")
public class SpIotAlarmRuleController extends BaseController {

    private final ISpIotAlarmRuleService alarmRuleService;

    /**
     * 查询IoT设备告警规则列表
     */
    @GetMapping("/list")
    public TableDataInfo<SpIotAlarmRuleVo> list(SpIotAlarmRuleBo bo, PageQuery pageQuery) {
        List<SpIotAlarmRuleVo> list = alarmRuleService.queryPageList(bo, pageQuery);
        return TableDataInfo.build(list);
    }

    /**
     * 导出IoT设备告警规则列表
     */
    @Log(title = "IoT设备告警规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpIotAlarmRuleBo bo, HttpServletResponse response) {
        List<SpIotAlarmRuleVo> list = alarmRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "IoT设备告警规则", SpIotAlarmRuleVo.class, response);
    }

    /**
     * 获取IoT设备告警规则详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<SpIotAlarmRuleVo> getInfo(@PathVariable Long id) {
        return R.ok(alarmRuleService.queryById(id));
    }

    /**
     * 新增IoT设备告警规则
     */
    @Log(title = "IoT设备告警规则", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpIotAlarmRuleBo bo) {
        return toAjax(alarmRuleService.insertByBo(bo));
    }

    /**
     * 修改IoT设备告警规则
     */
    @Log(title = "IoT设备告警规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpIotAlarmRuleBo bo) {
        return toAjax(alarmRuleService.updateByBo(bo));
    }

    /**
     * 删除IoT设备告警规则
     *
     * @param ids 主键串
     */
    @Log(title = "IoT设备告警规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(alarmRuleService.deleteWithValidByIds(List.of(ids), true));
    }
}
