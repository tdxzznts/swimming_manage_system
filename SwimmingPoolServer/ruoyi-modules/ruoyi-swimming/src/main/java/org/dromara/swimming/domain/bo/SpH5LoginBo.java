package org.dromara.swimming.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * H5登录BO
 *
 * @author swimming
 */
@Data
public class SpH5LoginBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号（邮箱或手机号）
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}
