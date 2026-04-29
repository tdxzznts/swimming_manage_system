package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.swimming.domain.bo.SpH5LoginBo;
import org.dromara.swimming.domain.bo.SpH5RegisterBo;
import org.dromara.swimming.domain.vo.SpH5LoginVo;
import org.dromara.swimming.service.ISpH5AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * H5认证控制器
 *
 * @author swimming
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/auth")
public class SpH5AuthController extends BaseController {

    private final ISpH5AuthService h5AuthService;

    /**
     * H5用户登录
     *
     * @param loginBo 登录信息
     * @return 登录结果
     */
    @Log(title = "H5用户登录", businessType = BusinessType.OTHER)
    @PostMapping("/login")
    public R<SpH5LoginVo> login(@Validated @RequestBody SpH5LoginBo loginBo) {
        return h5AuthService.login(loginBo);
    }

    /**
     * H5用户注册
     *
     * @param registerBo 注册信息
     * @return 注册结果
     */
    @Log(title = "H5用户注册", businessType = BusinessType.INSERT)
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody SpH5RegisterBo registerBo) {
        return h5AuthService.register(registerBo);
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @return 发送结果
     */
    @Log(title = "发送邮箱验证码", businessType = BusinessType.OTHER)
    @RateLimiter(count = 5, time = 60, limitType = LimitType.IP)
    @PostMapping("/sendEmailCode")
    public R<Void> sendEmailCode(@RequestParam String email) {
        return h5AuthService.sendEmailCode(email);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    public R<SpH5LoginVo> getUserInfo() {
        return h5AuthService.getUserInfo();
    }

    /**
     * 退出登录
     *
     * @return 退出结果
     */
    @Log(title = "H5用户退出", businessType = BusinessType.OTHER)
    @PostMapping("/logout")
    public R<Void> logout() {
        cn.dev33.satoken.stp.StpUtil.logout();
        return R.ok("退出成功");
    }
}
