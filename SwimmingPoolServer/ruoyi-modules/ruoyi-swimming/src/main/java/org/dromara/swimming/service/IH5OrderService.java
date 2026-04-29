package org.dromara.swimming.service;

import org.dromara.swimming.domain.vo.H5OrderRecordVo;

import java.util.List;

/**
 * H5订单Service接口
 *
 * @author W
 * @date 2026-02-04
 */
public interface IH5OrderService {

    /**
     * 获取最近订单记录（前5条）
     *
     * @return 订单记录列表
     */
    List<H5OrderRecordVo> getRecentOrders();

    /**
     * 获取订单记录列表（支持分页和筛选）
     *
     * @param orderType 订单类型（可选）
     * @return 订单记录列表
     */
    List<H5OrderRecordVo> getOrderList(String orderType);
}
