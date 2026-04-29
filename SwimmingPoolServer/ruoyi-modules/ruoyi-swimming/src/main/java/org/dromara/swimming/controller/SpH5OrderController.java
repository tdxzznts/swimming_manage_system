package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.vo.H5OrderRecordVo;
import org.dromara.swimming.service.IH5OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5订单控制器
 *
 * @author W
 * @date 2026-02-04
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/order")
public class SpH5OrderController extends BaseController {

    private final IH5OrderService h5OrderService;

    /**
     * 获取最近订单记录
     *
     * @return 订单记录列表
     */
    @GetMapping("/recent")
    public R<List<H5OrderRecordVo>> getRecentOrders() {
        List<H5OrderRecordVo> result = h5OrderService.getRecentOrders();
        return R.ok(result);
    }

    /**
     * 获取订单记录列表
     *
     * @param orderType 订单类型（可选）
     * @return 订单记录列表
     */
    @GetMapping("/list")
    public R<List<H5OrderRecordVo>> getOrderList(@RequestParam(required = false) String orderType) {
        List<H5OrderRecordVo> result = h5OrderService.getOrderList(orderType);
        return R.ok(result);
    }
}
