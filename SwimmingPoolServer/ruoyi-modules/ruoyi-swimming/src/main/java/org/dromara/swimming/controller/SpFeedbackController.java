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
import org.dromara.swimming.domain.vo.SpFeedbackVo;
import org.dromara.swimming.domain.bo.SpFeedbackBo;
import org.dromara.swimming.service.ISpFeedbackService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 反馈
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/feedback")
public class SpFeedbackController extends BaseController {

    private final ISpFeedbackService spFeedbackService;

    /**
     * 查询反馈列表
     */
    @SaCheckPermission("swimming:feedback:list")
    @GetMapping("/list")
    public TableDataInfo<SpFeedbackVo> list(SpFeedbackBo bo, PageQuery pageQuery) {
        return spFeedbackService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出反馈列表
     */
    @SaCheckPermission("swimming:feedback:export")
    @Log(title = "反馈", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpFeedbackBo bo, HttpServletResponse response) {
        List<SpFeedbackVo> list = spFeedbackService.queryList(bo);
        ExcelUtil.exportExcel(list, "反馈", SpFeedbackVo.class, response);
    }

    /**
     * 获取反馈详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:feedback:query")
    @GetMapping("/{id}")
    public R<SpFeedbackVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spFeedbackService.queryById(id));
    }

    /**
     * 新增反馈
     */
    @SaCheckPermission("swimming:feedback:add")
    @Log(title = "反馈", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpFeedbackBo bo) {
        return toAjax(spFeedbackService.insertByBo(bo));
    }

    /**
     * 修改反馈
     */
    @SaCheckPermission("swimming:feedback:edit")
    @Log(title = "反馈", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpFeedbackBo bo) {
        return toAjax(spFeedbackService.updateByBo(bo));
    }

    /**
     * 删除反馈
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:feedback:remove")
    @Log(title = "反馈", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spFeedbackService.deleteWithValidByIds(List.of(ids), true));
    }
}
