package org.dromara.swimming.service;

import org.dromara.common.core.domain.R;
import org.dromara.swimming.domain.vo.SpH5PaymentVo;

/**
 * H5支付服务接口
 *
 * @author swimming
 * @date 2026-02-04
 */
public interface ISpH5PaymentService {

    /**
     * 获取订单支付信息
     *
     * @param orderId 订单ID
     * @return 支付信息
     */
    R<SpH5PaymentVo> getPaymentInfo(Long orderId);

    /**
     * 余额支付
     *
     * @param orderId 订单ID
     * @param useFreeTimes 是否使用免费次数支付（true=使用免费次数，false=使用余额）
     * @return 支付结果
     */
    R<Void> balancePay(Long orderId, Boolean useFreeTimes);
}
