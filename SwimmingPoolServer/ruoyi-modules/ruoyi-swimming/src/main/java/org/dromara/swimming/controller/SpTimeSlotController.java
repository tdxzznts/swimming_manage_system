package org.dromara.swimming.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.swimming.domain.vo.SpTimeSlotVo;
import org.dromara.swimming.domain.bo.SpTimeSlotBo;
import org.dromara.swimming.service.ISpTimeSlotService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 预约时段
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/timeSlot")
public class SpTimeSlotController extends BaseController {

    private final ISpTimeSlotService spTimeSlotService;

    /**
     * 查询预约时段列表
     */
    @SaCheckPermission("swimming:timeSlot:list")
    @GetMapping("/list")
    public TableDataInfo<SpTimeSlotVo> list(SpTimeSlotBo bo, PageQuery pageQuery) {
        return spTimeSlotService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出预约时段列表
     */
    @SaCheckPermission("swimming:timeSlot:export")
    @Log(title = "预约时段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpTimeSlotBo bo, HttpServletResponse response) {
        List<SpTimeSlotVo> list = spTimeSlotService.queryList(bo);
        ExcelUtil.exportExcel(list, "预约时段", SpTimeSlotVo.class, response);
    }

    /**
     * 获取预约时段详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:timeSlot:query")
    @GetMapping("/{id}")
    public R<SpTimeSlotVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spTimeSlotService.queryById(id));
    }

    /**
     * 新增预约时段
     */
    @SaCheckPermission("swimming:timeSlot:add")
    @Log(title = "预约时段", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpTimeSlotBo bo) {
        return toAjax(spTimeSlotService.insertByBo(bo));
    }

    /**
     * 修改预约时段
     */
    @SaCheckPermission("swimming:timeSlot:edit")
    @Log(title = "预约时段", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpTimeSlotBo bo) {
        return toAjax(spTimeSlotService.updateByBo(bo));
    }

    /**
     * 删除预约时段
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:timeSlot:remove")
    @Log(title = "预约时段", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spTimeSlotService.deleteWithValidByIds(List.of(ids), true));
    }
}
