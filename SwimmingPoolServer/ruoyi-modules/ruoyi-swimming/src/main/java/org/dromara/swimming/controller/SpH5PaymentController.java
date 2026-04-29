package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.vo.SpH5PaymentVo;
import org.dromara.swimming.service.ISpH5PaymentService;
import org.springframework.web.bind.annotation.*;

/**
 * H5支付控制器
 *
 * @author swimming
 * @date 2026-02-04
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/payment")
public class SpH5PaymentController extends BaseController {

    private final ISpH5PaymentService paymentService;

    /**
     * 获取订单支付信息
     *
     * @param orderId 订单ID
     * @return 支付信息
     */
    @SaCheckLogin
    @GetMapping("/info/{orderId}")
    public R<SpH5PaymentVo> getPaymentInfo(@PathVariable Long orderId) {
        return paymentService.getPaymentInfo(orderId);
    }

    /**
     * 余额支付
     *
     * @param orderId 订单ID
     * @param useFreeTimes 是否使用免费次数支付（true=使用免费次数，false=使用余额）
     * @return 支付结果
     */
    @SaCheckLogin
    @PostMapping("/balance-pay/{orderId}")
    public R<Void> balancePay(@PathVariable Long orderId, @RequestParam(defaultValue = "false") Boolean useFreeTimes) {
        return paymentService.balancePay(orderId, useFreeTimes);
    }
}
