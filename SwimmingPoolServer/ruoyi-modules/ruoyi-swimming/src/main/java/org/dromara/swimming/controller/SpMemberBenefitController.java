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
import org.dromara.swimming.domain.vo.SpMemberBenefitVo;
import org.dromara.swimming.domain.bo.SpMemberBenefitBo;
import org.dromara.swimming.service.ISpMemberBenefitService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员权益配置
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/memberBenefit")
public class SpMemberBenefitController extends BaseController {

    private final ISpMemberBenefitService spMemberBenefitService;

    /**
     * 查询会员权益配置列表
     */
    @SaCheckPermission("swimming:memberBenefit:list")
    @GetMapping("/list")
    public TableDataInfo<SpMemberBenefitVo> list(SpMemberBenefitBo bo, PageQuery pageQuery) {
        return spMemberBenefitService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员权益配置列表
     */
    @SaCheckPermission("swimming:memberBenefit:export")
    @Log(title = "会员权益配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpMemberBenefitBo bo, HttpServletResponse response) {
        List<SpMemberBenefitVo> list = spMemberBenefitService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员权益配置", SpMemberBenefitVo.class, response);
    }

    /**
     * 获取会员权益配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:memberBenefit:query")
    @GetMapping("/{id}")
    public R<SpMemberBenefitVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spMemberBenefitService.queryById(id));
    }

    /**
     * 新增会员权益配置
     */
    @SaCheckPermission("swimming:memberBenefit:add")
    @Log(title = "会员权益配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpMemberBenefitBo bo) {
        return toAjax(spMemberBenefitService.insertByBo(bo));
    }

    /**
     * 修改会员权益配置
     */
    @SaCheckPermission("swimming:memberBenefit:edit")
    @Log(title = "会员权益配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpMemberBenefitBo bo) {
        return toAjax(spMemberBenefitService.updateByBo(bo));
    }

    /**
     * 删除会员权益配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:memberBenefit:remove")
    @Log(title = "会员权益配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spMemberBenefitService.deleteWithValidByIds(List.of(ids), true));
    }
}
