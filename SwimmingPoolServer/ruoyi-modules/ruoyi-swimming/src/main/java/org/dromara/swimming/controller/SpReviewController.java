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
import org.dromara.swimming.domain.vo.SpReviewVo;
import org.dromara.swimming.domain.bo.SpReviewBo;
import org.dromara.swimming.service.ISpReviewService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 评价
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/review")
public class SpReviewController extends BaseController {

    private final ISpReviewService spReviewService;

    /**
     * 查询评价列表
     */
    @SaCheckPermission("swimming:review:list")
    @GetMapping("/list")
    public TableDataInfo<SpReviewVo> list(SpReviewBo bo, PageQuery pageQuery) {
        return spReviewService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出评价列表
     */
    @SaCheckPermission("swimming:review:export")
    @Log(title = "评价", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpReviewBo bo, HttpServletResponse response) {
        List<SpReviewVo> list = spReviewService.queryList(bo);
        ExcelUtil.exportExcel(list, "评价", SpReviewVo.class, response);
    }

    /**
     * 获取评价详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:review:query")
    @GetMapping("/{id}")
    public R<SpReviewVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spReviewService.queryById(id));
    }

    /**
     * 新增评价
     */
    @SaCheckPermission("swimming:review:add")
    @Log(title = "评价", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpReviewBo bo) {
        return toAjax(spReviewService.insertByBo(bo));
    }

    /**
     * 修改评价
     */
    @SaCheckPermission("swimming:review:edit")
    @Log(title = "评价", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpReviewBo bo) {
        return toAjax(spReviewService.updateByBo(bo));
    }

    /**
     * 删除评价
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:review:remove")
    @Log(title = "评价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spReviewService.deleteWithValidByIds(List.of(ids), true));
    }
}
