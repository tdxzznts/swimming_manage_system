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
import org.dromara.swimming.domain.vo.SpMemberLevelVo;
import org.dromara.swimming.domain.bo.SpMemberLevelBo;
import org.dromara.swimming.service.ISpMemberLevelService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员等级配置
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/memberLevel")
public class SpMemberLevelController extends BaseController {

    private final ISpMemberLevelService spMemberLevelService;

    /**
     * 查询会员等级配置列表
     */
    @SaCheckPermission("swimming:memberLevel:list")
    @GetMapping("/list")
    public TableDataInfo<SpMemberLevelVo> list(SpMemberLevelBo bo, PageQuery pageQuery) {
        return spMemberLevelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员等级配置列表
     */
    @SaCheckPermission("swimming:memberLevel:export")
    @Log(title = "会员等级配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpMemberLevelBo bo, HttpServletResponse response) {
        List<SpMemberLevelVo> list = spMemberLevelService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员等级配置", SpMemberLevelVo.class, response);
    }

    /**
     * 获取会员等级配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:memberLevel:query")
    @GetMapping("/{id}")
    public R<SpMemberLevelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spMemberLevelService.queryById(id));
    }

    /**
     * 新增会员等级配置
     */
    @SaCheckPermission("swimming:memberLevel:add")
    @Log(title = "会员等级配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpMemberLevelBo bo) {
        return toAjax(spMemberLevelService.insertByBo(bo));
    }

    /**
     * 修改会员等级配置
     */
    @SaCheckPermission("swimming:memberLevel:edit")
    @Log(title = "会员等级配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpMemberLevelBo bo) {
        return toAjax(spMemberLevelService.updateByBo(bo));
    }

    /**
     * 删除会员等级配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:memberLevel:remove")
    @Log(title = "会员等级配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spMemberLevelService.deleteWithValidByIds(List.of(ids), true));
    }
}
