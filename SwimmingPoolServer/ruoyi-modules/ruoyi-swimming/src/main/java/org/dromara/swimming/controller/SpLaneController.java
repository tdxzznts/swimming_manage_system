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
import org.dromara.swimming.domain.vo.SpLaneVo;
import org.dromara.swimming.domain.bo.SpLaneBo;
import org.dromara.swimming.service.ISpLaneService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 泳道
 *
 * @author W
 * @date 2026-02-05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/swimming/lane")
public class SpLaneController extends BaseController {

    private final ISpLaneService spLaneService;

    /**
     * 查询泳道列表
     */
    @SaCheckPermission("swimming:lane:list")
    @GetMapping("/list")
    public TableDataInfo<SpLaneVo> list(SpLaneBo bo, PageQuery pageQuery) {
        return spLaneService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出泳道列表
     */
    @SaCheckPermission("swimming:lane:export")
    @Log(title = "泳道", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SpLaneBo bo, HttpServletResponse response) {
        List<SpLaneVo> list = spLaneService.queryList(bo);
        ExcelUtil.exportExcel(list, "泳道", SpLaneVo.class, response);
    }

    /**
     * 获取泳道详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("swimming:lane:query")
    @GetMapping("/{id}")
    public R<SpLaneVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(spLaneService.queryById(id));
    }

    /**
     * 新增泳道
     */
    @SaCheckPermission("swimming:lane:add")
    @Log(title = "泳道", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SpLaneBo bo) {
        return toAjax(spLaneService.insertByBo(bo));
    }

    /**
     * 修改泳道
     */
    @SaCheckPermission("swimming:lane:edit")
    @Log(title = "泳道", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SpLaneBo bo) {
        return toAjax(spLaneService.updateByBo(bo));
    }

    /**
     * 删除泳道
     *
     * @param ids 主键串
     */
    @SaCheckPermission("swimming:lane:remove")
    @Log(title = "泳道", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(spLaneService.deleteWithValidByIds(List.of(ids), true));
    }
}
