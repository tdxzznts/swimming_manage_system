package org.dromara.swimming.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * H5登录响应VO
 *
 * @author swimming
 */
@Data
public class SpH5LoginVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phonenumber;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 会员卡号
     */
    private String cardNo;

    /**
     * 会员等级
     */
    private String memberLevel;

}
