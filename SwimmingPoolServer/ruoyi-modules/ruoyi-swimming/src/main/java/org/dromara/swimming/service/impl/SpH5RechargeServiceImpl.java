package org.dromara.swimming.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.domain.SpOrder;
import org.dromara.swimming.domain.bo.SpH5RechargeBo;
import org.dromara.swimming.domain.vo.SpH5OrderStatusVo;
import org.dromara.swimming.domain.vo.SpH5RechargeVo;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.mapper.SpOrderMapper;
import org.dromara.swimming.service.ISpH5RechargeService;
import org.dromara.swimming.service.IMemberUpgradeService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * H5充值Service业务层处理
 *
 * @author swimming
 * @date 2026-02-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpH5RechargeServiceImpl implements ISpH5RechargeService {

    private final SpOrderMapper orderMapper;
    private final SpMemberCardMapper memberCardMapper;
    private final IMemberUpgradeService memberUpgradeService;

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.private-key}")
    private String privateKey;

    @Value("${alipay.public-key}")
    private String alipayPublicKey;

    @Value("${alipay.server-url}")
    private String serverUrl;

    @Value("${alipay.connect-timeout-ms:5000}")
    private String connectTimeout;

    @Value("${alipay.read-timeout-ms:20000}")
    private String readTimeout;

    /**
     * 创建充值订单
     *
     * @param rechargeBo 充值信息
     * @return 充值订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpH5RechargeVo createRechargeOrder(SpH5RechargeBo rechargeBo) {
        // 获取当前登录用户ID
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw new ServiceException("用户未登录");
        }
        Long userId = loginUser.getUserId();

        // 校验支付类型
        if (!"alipay".equals(rechargeBo.getPayType())) {
            throw new ServiceException("暂不支持该支付方式");
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        SpOrder order = new SpOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setOrderType("2"); // 2-充值
        order.setAmount(rechargeBo.getAmount());
        order.setDiscountAmount(rechargeBo.getBonusAmount()); // 赠送金额保存到优惠金额字段
        order.setActualAmount(0l);
        order.setPayType("1"); // 1-支付宝
        order.setPayStatus("0"); // 0-待支付
        order.setStatus("0"); // 0-待支付
        order.setCreateTime(new Date());
        orderMapper.insert(order);

        // 调用支付宝API生成支付二维码
        try {
            String qrCodeUrl = createAlipayQrCode(orderNo, rechargeBo.getAmount());

            // 计算过期时间（15分钟后）
            Date expireTime = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

            // 返回结果
            SpH5RechargeVo result = new SpH5RechargeVo();
            result.setOrderNo(orderNo);
            result.setQrCodeUrl(qrCodeUrl);
            result.setExpireTime(expireTime);
            result.setAmount(rechargeBo.getAmount());
            result.setBonusAmount(rechargeBo.getBonusAmount());
            result.setActualAmount(rechargeBo.getAmount());

            return result;
        } catch (Exception e) {
            log.error("创建支付宝订单失败", e);
            throw new ServiceException("创建支付订单失败：" + e.getMessage());
        }
    }

    /**
     * 查询订单状态
     *
     * @param orderNo 订单号
     * @return 订单状态信息
     */
    @Override
    public SpH5OrderStatusVo queryOrderStatus(String orderNo) {
        // 查询本地订单
        LambdaQueryWrapper<SpOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SpOrder::getOrderNo, orderNo);
        SpOrder order = orderMapper.selectOne(wrapper);

        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        // 查询支付宝订单状态
        if ("0".equals(order.getPayStatus())) {
            try {
                AlipayTradeQueryResponse response = queryAlipayOrder(orderNo);
                if (response != null && "10000".equals(response.getCode())) {
                    String tradeStatus = response.getTradeStatus();
                    // TRADE_SUCCESS 或 TRADE_FINISHED 表示支付成功
                    if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                        // 支付成功，更新订单状态和会员卡余额
                        handlePaymentSuccess(orderNo);
                        // 重新查询订单
                        order = orderMapper.selectOne(wrapper);
                    }
                }
            } catch (Exception e) {
                log.error("查询支付宝订单状态失败", e);
            }
        }

        // 构建返回结果
        SpH5OrderStatusVo result = new SpH5OrderStatusVo();
        result.setOrderNo(orderNo);
        result.setPayStatus(order.getPayStatus());
        result.setPayStatusDesc(getPayStatusDesc(order.getPayStatus()));
        result.setPayTime(order.getPayTime());
        result.setStatus(order.getStatus());
        result.setStatusDesc(getOrderStatusDesc(order.getStatus()));

        return result;
    }

    /**
     * 处理支付成功回调
     *
     * @param orderNo 订单号
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handlePaymentSuccess(String orderNo) {
        // 查询订单
        LambdaQueryWrapper<SpOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SpOrder::getOrderNo, orderNo);
        SpOrder order = orderMapper.selectOne(wrapper);

        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        // 如果已经支付，直接返回
        if ("1".equals(order.getPayStatus())) {
            return true;
        }

        // 更新订单状态
        order.setPayStatus("1"); // 1-已支付
        order.setStatus("1"); // 1-已支付
        order.setPayTime(new Date());
        orderMapper.updateById(order);

        // 更新会员卡余额和累计充值
        LambdaQueryWrapper<SpMemberCard> cardWrapper = Wrappers.lambdaQuery();
        cardWrapper.eq(SpMemberCard::getUserId, order.getUserId());
        SpMemberCard memberCard = memberCardMapper.selectOne(cardWrapper);

        if (memberCard == null) {
            throw new ServiceException("会员卡不存在");
        }

        // 增加余额（充值金额 + 赠送金额）
        Long totalAmount = order.getAmount() + order.getDiscountAmount();
        memberCard.setBalance(memberCard.getBalance() + totalAmount);

        // 更新累计充值金额
        Long currentTotalRecharge = memberCard.getTotalRecharge() != null ? memberCard.getTotalRecharge() : 0L;
        memberCard.setTotalRecharge(currentTotalRecharge + order.getAmount());

        memberCardMapper.updateById(memberCard);

        log.info("充值成功，订单号：{}，充值金额：{}元，赠送金额：{}元，累计充值：{}元",
            orderNo, order.getAmount(), order.getDiscountAmount(), memberCard.getTotalRecharge());

        // 检查并升级会员等级
        try {
            Boolean upgraded = memberUpgradeService.checkAndUpgradeMemberLevel(order.getUserId());
            if (upgraded) {
                log.info("充值后会员等级已自动升级，userId: {}, orderNo: {}", order.getUserId(), orderNo);
            }
        } catch (Exception e) {
            log.error("会员等级升级失败，但不影响充值成功，userId: {}, orderNo: {}",
                order.getUserId(), orderNo, e);
            // 升级失败不影响充值成功
        }

        return true;
    }

    /**
     * 创建支付宝支付二维码
     */
    private String createAlipayQrCode(String orderNo, Long amount) throws AlipayApiException {
        // 初始化AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
            serverUrl,
            appId,
            privateKey,
            "json",
            "UTF-8",
            alipayPublicKey,
            "RSA2"
        );

        // 创建API请求
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

        // 设置订单信息
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(String.valueOf(amount)); // 单位：元
        model.setSubject("游泳馆会员充值");

        // 设置过期时间（15分钟）
        String timeExpire = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new Date(System.currentTimeMillis() + 15 * 60 * 1000));
        model.setTimeExpire(timeExpire);

        request.setBizModel(model);

        // 发送请求
        AlipayTradePrecreateResponse response = alipayClient.execute(request);

        if (response.isSuccess()) {
            String qrCode = response.getQrCode();
            log.info("支付宝二维码内容生成成功，订单号：{}，二维码内容：{}", orderNo, qrCode);

            // 使用第三方API将二维码内容转换为图片URL
            // 使用 qrserver.com 的公共API生成二维码图片
            try {
                String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" +
                    URLEncoder.encode(qrCode, "UTF-8");
                log.info("支付宝二维码图片URL生成成功，订单号：{}", orderNo);
                return qrCodeUrl;
            } catch (UnsupportedEncodingException e) {
                log.error("二维码URL编码失败", e);
                // 如果编码失败，直接返回支付宝的二维码内容（前端需要处理）
                return qrCode;
            }
        } else {
            log.error("支付宝二维码生成失败，订单号：{}，错误：{}", orderNo, response.getSubMsg());
            throw new ServiceException("创建支付二维码失败：" + response.getSubMsg());
        }
    }

    /**
     * 查询支付宝订单状态
     */
    private AlipayTradeQueryResponse queryAlipayOrder(String orderNo) throws AlipayApiException {
        // 初始化AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
            serverUrl,
            appId,
            privateKey,
            "json",
            "UTF-8",
            alipayPublicKey,
            "RSA2"
        );

        // 创建API请求
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        model.setOutTradeNo(orderNo);
        request.setBizModel(model);

        // 发送请求
        return alipayClient.execute(request);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "RCH" + System.currentTimeMillis() +
            (UUID.randomUUID().toString().replace("-", "").substring(0, 6)).toUpperCase();
    }

    /**
     * 获取支付状态描述
     */
    private String getPayStatusDesc(String payStatus) {
        return switch (payStatus) {
            case "0" -> "待支付";
            case "1" -> "已支付";
            case "2" -> "支付失败";
            case "3" -> "已退款";
            default -> "未知";
        };
    }

    /**
     * 获取订单状态描述
     */
    private String getOrderStatusDesc(String status) {
        return switch (status) {
            case "0" -> "待支付";
            case "1" -> "已支付";
            case "2" -> "已完成";
            case "3" -> "已取消";
            case "4" -> "已退款";
            default -> "未知";
        };
    }
}
