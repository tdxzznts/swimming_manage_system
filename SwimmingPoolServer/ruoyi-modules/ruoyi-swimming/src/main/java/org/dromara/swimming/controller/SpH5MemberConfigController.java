package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.vo.MemberBenefitVo;
import org.dromara.swimming.domain.vo.MemberLevelVo;
import org.dromara.swimming.domain.vo.MemberInfoVo;
import org.dromara.swimming.domain.vo.MemberRulesVo;
import org.dromara.swimming.service.IMemberConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5会员配置控制器
 *
 * @author W
 * @date 2026-02-04
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/member/config")
public class SpH5MemberConfigController extends BaseController {

    private final IMemberConfigService memberConfigService;

    /**
     * 获取所有会员等级
     *
     * @return 会员等级列表
     */
    @GetMapping("/levels")
    public R<List<MemberLevelVo>> getMemberLevels() {
        List<MemberLevelVo> result = memberConfigService.getMemberLevels();
        return R.ok(result);
    }

    /**
     * 获取会员等级字典数据（用于前端下拉框和标签显示）
     *
     * @return 字典数据列表
     */
    @GetMapping("/levelDict")
    public R<List<java.util.Map<String, String>>> getMemberLevelDict() {
        List<java.util.Map<String, String>> result = memberConfigService.getMemberLevelDict();
        return R.ok(result);
    }

    /**
     * 获取指定等级的会员权益
     *
     * @param levelCode 等级编码
     * @return 会员权益列表
     */
    @GetMapping("/benefits/{levelCode}")
    public R<List<MemberBenefitVo>> getMemberBenefits(@PathVariable String levelCode) {
        List<MemberBenefitVo> result = memberConfigService.getMemberBenefits(levelCode);
        return R.ok(result);
    }

    /**
     * 获取当前用户会员信息
     *
     * @return 会员信息
     */
    @GetMapping("/info")
    public R<MemberInfoVo> getMemberInfo() {
        MemberInfoVo result = memberConfigService.getMemberInfo();
        return R.ok(result);
    }

    /**
     * 获取会员规则（包含所有等级和权益）
     *
     * @return 会员规则信息
     */
    @GetMapping("/rules")
    public R<MemberRulesVo> getMemberRules() {
        MemberRulesVo result = memberConfigService.getMemberRules();
        return R.ok(result);
    }
}
