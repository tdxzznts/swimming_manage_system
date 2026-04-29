package org.dromara.swimming.service;

import org.dromara.common.core.domain.R;
import org.dromara.swimming.domain.bo.SpH5RechargeBo;
import org.dromara.swimming.domain.vo.SpH5OrderStatusVo;
import org.dromara.swimming.domain.vo.SpH5RechargeVo;

/**
 * H5充值Service接口
 *
 * @author swimming
 * @date 2026-02-04
 */
public interface ISpH5RechargeService {

    /**
     * 创建充值订单
     *
     * @param rechargeBo 充值信息
     * @return 充值订单信息
     */
    SpH5RechargeVo createRechargeOrder(SpH5RechargeBo rechargeBo);

    /**
     * 查询订单状态
     *
     * @param orderNo 订单号
     * @return 订单状态信息
     */
    SpH5OrderStatusVo queryOrderStatus(String orderNo);

    /**
     * 处理支付成功回调
     *
     * @param orderNo 订单号
     * @return 处理结果
     */
    Boolean handlePaymentSuccess(String orderNo);
}
