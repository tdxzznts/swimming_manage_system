package org.dromara.swimming.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.swimming.domain.SpOrder;
import org.dromara.swimming.domain.vo.H5OrderRecordVo;
import org.dromara.swimming.mapper.SpOrderMapper;
import org.dromara.swimming.service.IH5OrderService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * H5订单Service实现
 *
 * @author W
 * @date 2026-02-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class H5OrderServiceImpl implements IH5OrderService {

    private final SpOrderMapper orderMapper;

    @Override
    public List<H5OrderRecordVo> getRecentOrders() {
        Long userId = LoginHelper.getUserId();

        LambdaQueryWrapper<SpOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpOrder::getUserId, userId)
               .orderByDesc(SpOrder::getCreateTime)
               .last("LIMIT 5");

        List<SpOrder> orders = orderMapper.selectList(wrapper);
        return convertToVo(orders);
    }

    @Override
    public List<H5OrderRecordVo> getOrderList(String orderType) {
        Long userId = LoginHelper.getUserId();

        LambdaQueryWrapper<SpOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpOrder::getUserId, userId)
               .orderByDesc(SpOrder::getCreateTime);

        if (orderType != null && !orderType.isEmpty()) {
            wrapper.eq(SpOrder::getOrderType, orderType);
        }

        List<SpOrder> orders = orderMapper.selectList(wrapper);
        return convertToVo(orders);
    }

    /**
     * 转换为VO
     */
    private List<H5OrderRecordVo> convertToVo(List<SpOrder> orders) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

        return orders.stream().map(order -> {
            H5OrderRecordVo vo = new H5OrderRecordVo();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderType(order.getOrderType());
            vo.setOrderTypeName(getOrderTypeName(order.getOrderType()));
            vo.setAmount(order.getAmount());
            vo.setDiscountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : 0L);
            vo.setActualAmount(order.getActualAmount() != null ? order.getActualAmount() : 0L);
            vo.setPayType(order.getPayType());
            vo.setPayTypeName(getPayTypeName(order.getPayType()));
            vo.setPayStatus(order.getPayStatus());
            vo.setPayStatusName(getPayStatusName(order.getPayStatus()));
            vo.setPayTime(order.getPayTime());
            vo.setCreateTime(order.getCreateTime());

            // 设置图标
            vo.setIconName(getIconName(order.getOrderType()));

            // 设置金额文本和样式
            if ("2".equals(order.getOrderType())) {
                // 充值订单，显示为正
                vo.setAmountText("+" + order.getAmount());
                vo.setAmountClass("amount-increase");
            } else if ("3".equals(order.getOrderType())) {
                // 积分订单
                vo.setAmountText("+" + order.getAmount() + "积分");
                vo.setAmountClass("amount-increase");
            } else {
                // 预约订单，显示为负
                vo.setAmountText("-" + order.getActualAmount());
                vo.setAmountClass("amount-decrease");
            }

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取订单类型名称
     */
    private String getOrderTypeName(String orderType) {
        return switch (orderType) {
            case "1" -> "预约";
            case "2" -> "充值";
            case "3" -> "积分";
            default -> "未知";
        };
    }

    /**
     * 获取支付方式名称
     */
    private String getPayTypeName(String payType) {
        return switch (payType) {
            case "1" -> "支付宝";
            case "2" -> "余额";
            default -> "未知";
        };
    }

    /**
     * 获取支付状态名称
     */
    private String getPayStatusName(String payStatus) {
        return switch (payStatus) {
            case "0" -> "待支付";
            case "1" -> "已支付";
            case "2" -> "支付失败";
            case "3" -> "已退款";
            default -> "未知";
        };
    }

    /**
     * 获取图标名称
     */
    private String getIconName(String orderType) {
        return switch (orderType) {
            case "1" -> "Tickets";
            case "2" -> "Wallet";
            case "3" -> "Star";
            default -> "Document";
        };
    }
}
