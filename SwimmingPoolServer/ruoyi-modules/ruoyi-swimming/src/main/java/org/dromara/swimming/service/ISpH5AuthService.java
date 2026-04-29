package org.dromara.swimming.service;

import org.dromara.common.core.domain.R;
import org.dromara.swimming.domain.bo.SpH5LoginBo;
import org.dromara.swimming.domain.bo.SpH5RegisterBo;
import org.dromara.swimming.domain.vo.SpH5LoginVo;

/**
 * H5认证服务接口
 *
 * @author swimming
 */
public interface ISpH5AuthService {

    /**
     * H5用户登录
     *
     * @param loginBo 登录信息
     * @return 登录结果
     */
    R<SpH5LoginVo> login(SpH5LoginBo loginBo);

    /**
     * H5用户注册
     *
     * @param registerBo 注册信息
     * @return 注册结果
     */
    R<Void> register(SpH5RegisterBo registerBo);

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @return 发送结果
     */
    R<Void> sendEmailCode(String email);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    R<SpH5LoginVo> getUserInfo();

}
