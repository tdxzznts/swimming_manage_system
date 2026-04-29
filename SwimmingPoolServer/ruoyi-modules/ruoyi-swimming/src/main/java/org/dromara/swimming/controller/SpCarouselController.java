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
import org.dromara.swimming.domain.vo.SpCarouselVo;
import org.dromara.swimming.domain.bo.SpCarouselBo;
import org.dromara.swimming.service.ISpCarouselService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 轮播图
 *
 * @author W
 * @date 2026-02-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/carousel")
public class SpCarouselController extends BaseController {

    private final ISpCarouselService spCarouselService;

    /**
     * 查询轮播图列表
     */
    @SaCheckPermission("swimming:carousel:list")
    @GetMapping("/list")
    public TableDataInfo<SpCarouselVo> list(SpCarouselBo bo, PageQuery pageQuery) {
        return spCarouselService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出轮播图列表
     */
    @SaCheckPermission("swimming:carousel:export")
    @Log(title = "轮播图", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpCarouselBo bo, HttpServletResponse response) {
        List<SpCarouselVo> list = spCarouselService.queryList(bo);
        ExcelUtil.exportExcel(list, "轮播图", SpCarouselVo.class, response);
    }

    /**
     * 获取轮播图详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:carousel:query")
    @GetMapping("/{id}")
    public R<SpCarouselVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spCarouselService.queryById(id));
    }

    /**
     * 新增轮播图
     */
    @SaCheckPermission("swimming:carousel:add")
    @Log(title = "轮播图", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpCarouselBo bo) {
        return toAjax(spCarouselService.insertByBo(bo));
    }

    /**
     * 修改轮播图
     */
    @SaCheckPermission("swimming:carousel:edit")
    @Log(title = "轮播图", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpCarouselBo bo) {
        return toAjax(spCarouselService.updateByBo(bo));
    }

    /**
     * 删除轮播图
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:carousel:remove")
    @Log(title = "轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spCarouselService.deleteWithValidByIds(List.of(ids), true));
    }
}
