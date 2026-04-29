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
import org.dromara.swimming.domain.vo.SpReservationVo;
import org.dromara.swimming.domain.bo.SpReservationBo;
import org.dromara.swimming.service.ISpReservationService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 预约记录
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/reservation")
public class SpReservationController extends BaseController {

    private final ISpReservationService spReservationService;

    /**
     * 查询预约记录列表
     */
    @SaCheckPermission("swimming:reservation:list")
    @GetMapping("/list")
    public TableDataInfo<SpReservationVo> list(SpReservationBo bo, PageQuery pageQuery) {
        return spReservationService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出预约记录列表
     */
    @SaCheckPermission("swimming:reservation:export")
    @Log(title = "预约记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpReservationBo bo, HttpServletResponse response) {
        List<SpReservationVo> list = spReservationService.queryList(bo);
        ExcelUtil.exportExcel(list, "预约记录", SpReservationVo.class, response);
    }

    /**
     * 获取预约记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:reservation:query")
    @GetMapping("/{id}")
    public R<SpReservationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spReservationService.queryById(id));
    }

    /**
     * 新增预约记录
     */
    @SaCheckPermission("swimming:reservation:add")
    @Log(title = "预约记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpReservationBo bo) {
        return toAjax(spReservationService.insertByBo(bo));
    }

    /**
     * 修改预约记录
     */
    @SaCheckPermission("swimming:reservation:edit")
    @Log(title = "预约记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpReservationBo bo) {
        return toAjax(spReservationService.updateByBo(bo));
    }

    /**
     * 删除预约记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:reservation:remove")
    @Log(title = "预约记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spReservationService.deleteWithValidByIds(List.of(ids), true));
    }
}
