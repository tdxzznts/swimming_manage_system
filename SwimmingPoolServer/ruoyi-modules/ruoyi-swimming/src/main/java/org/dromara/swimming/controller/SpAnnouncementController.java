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
import org.dromara.swimming.domain.vo.SpAnnouncementVo;
import org.dromara.swimming.domain.bo.SpAnnouncementBo;
import org.dromara.swimming.service.ISpAnnouncementService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 系统公告
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/announcement")
public class SpAnnouncementController extends BaseController {

    private final ISpAnnouncementService spAnnouncementService;

    /**
     * 查询系统公告列表
     */
    @SaCheckPermission("swimming:announcement:list")
    @GetMapping("/list")
    public TableDataInfo<SpAnnouncementVo> list(SpAnnouncementBo bo, PageQuery pageQuery) {
        return spAnnouncementService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出系统公告列表
     */
    @SaCheckPermission("swimming:announcement:export")
    @Log(title = "系统公告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpAnnouncementBo bo, HttpServletResponse response) {
        List<SpAnnouncementVo> list = spAnnouncementService.queryList(bo);
        ExcelUtil.exportExcel(list, "系统公告", SpAnnouncementVo.class, response);
    }

    /**
     * 获取系统公告详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:announcement:query")
    @GetMapping("/{id}")
    public R<SpAnnouncementVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spAnnouncementService.queryById(id));
    }

    /**
     * 新增系统公告
     */
    @SaCheckPermission("swimming:announcement:add")
    @Log(title = "系统公告", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpAnnouncementBo bo) {
        return toAjax(spAnnouncementService.insertByBo(bo));
    }

    /**
     * 修改系统公告
     */
    @SaCheckPermission("swimming:announcement:edit")
    @Log(title = "系统公告", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpAnnouncementBo bo) {
        return toAjax(spAnnouncementService.updateByBo(bo));
    }

    /**
     * 删除系统公告
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:announcement:remove")
    @Log(title = "系统公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spAnnouncementService.deleteWithValidByIds(List.of(ids), true));
    }
}
