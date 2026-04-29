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
import org.dromara.swimming.domain.vo.SpMemberCardVo;
import org.dromara.swimming.domain.bo.SpMemberCardBo;
import org.dromara.swimming.service.ISpMemberCardService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员卡
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/memberCard")
public class SpMemberCardController extends BaseController {

    private final ISpMemberCardService spMemberCardService;

    /**
     * 查询会员卡列表
     */
    @SaCheckPermission("swimming:memberCard:list")
    @GetMapping("/list")
    public TableDataInfo<SpMemberCardVo> list(SpMemberCardBo bo, PageQuery pageQuery) {
        return spMemberCardService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员卡列表
     */
    @SaCheckPermission("swimming:memberCard:export")
    @Log(title = "会员卡", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpMemberCardBo bo, HttpServletResponse response) {
        List<SpMemberCardVo> list = spMemberCardService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员卡", SpMemberCardVo.class, response);
    }

    /**
     * 获取会员卡详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:memberCard:query")
    @GetMapping("/{id}")
    public R<SpMemberCardVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spMemberCardService.queryById(id));
    }

    /**
     * 新增会员卡
     */
    @SaCheckPermission("swimming:memberCard:add")
    @Log(title = "会员卡", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpMemberCardBo bo) {
        return toAjax(spMemberCardService.insertByBo(bo));
    }

    /**
     * 修改会员卡
     */
    @SaCheckPermission("swimming:memberCard:edit")
    @Log(title = "会员卡", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpMemberCardBo bo) {
        return toAjax(spMemberCardService.updateByBo(bo));
    }

    /**
     * 删除会员卡
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:memberCard:remove")
    @Log(title = "会员卡", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spMemberCardService.deleteWithValidByIds(List.of(ids), true));
    }
}
