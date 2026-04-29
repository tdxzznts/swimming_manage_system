package org.dromara.swimming.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.swimming.domain.SpOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serial;
import java.io.Serializable;

/**
 * H5充值业务对象
 *
 * @author swimming
 * @date 2026-02-04
 */
@Data
public class SpH5RechargeBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 充值金额（单位：元）
     */
    @NotNull(message = "充值金额不能为空")
    @Positive(message = "充值金额必须大于0")
    private Long amount;

    /**
     * 赠送金额（单位：元）
     */
    @NotNull(message = "赠送金额不能为空")
    private Long bonusAmount;

    /**
     * 支付类型（alipay-支付宝）
     */
    @NotBlank(message = "支付类型不能为空")
    private String payType;
}
