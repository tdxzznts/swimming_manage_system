package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.swimming.domain.bo.SpH5RechargeBo;
import org.dromara.swimming.domain.vo.SpH5OrderStatusVo;
import org.dromara.swimming.domain.vo.SpH5RechargeVo;
import org.dromara.swimming.service.ISpH5RechargeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * H5充值控制器
 *
 * @author swimming
 * @date 2026-02-04
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/recharge")
public class SpH5RechargeController extends BaseController {

    private final ISpH5RechargeService rechargeService;

    /**
     * 创建充值订单
     *
     * @param rechargeBo 充值信息
     * @return 充值订单信息
     */
    @Log(title = "创建充值订单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/create")
    public R<SpH5RechargeVo> createRechargeOrder(@Validated @RequestBody SpH5RechargeBo rechargeBo) {
        SpH5RechargeVo result = rechargeService.createRechargeOrder(rechargeBo);
        return R.ok(result);
    }

    /**
     * 查询订单状态
     *
     * @param orderNo 订单号
     * @return 订单状态信息
     */
    @GetMapping("/orderStatus")
    public R<SpH5OrderStatusVo> getOrderStatus(@RequestParam String orderNo) {
        SpH5OrderStatusVo result = rechargeService.queryOrderStatus(orderNo);
        return R.ok(result);
    }
}
