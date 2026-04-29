package org.dromara.swimming.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mail.utils.MailUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.ISysUserService;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.domain.bo.SpH5LoginBo;
import org.dromara.swimming.domain.bo.SpH5RegisterBo;
import org.dromara.swimming.domain.vo.SpH5LoginVo;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.service.ISpH5AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

/**
 * H5认证服务实现
 *
 * @author swimming
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpH5AuthServiceImpl implements ISpH5AuthService {

    private final ISysUserService userService;
    private final SysUserMapper userMapper;
    private final SpMemberCardMapper memberCardMapper;

    @Value("${user.email.expire:5}")
    private Integer emailExpire;

    private static final String EMAIL_CODE_KEY = "email:code:";
    private static final String H5_USER_TYPE = "app_user"; // H5用户类型使用app_user

    // 邮箱正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    // 手机号正则表达式
    private static final String MOBILE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public R<SpH5LoginVo> login(SpH5LoginBo loginBo) {
        String account = loginBo.getAccount();
        String password = loginBo.getPassword();

        // 查找用户（通过邮箱或手机号）
        SysUserVo user = findUserByAccount(account);
        if (user == null) {
            return R.fail("账号或密码错误");
        }

        // 检查用户状态 - 允许app_user类型和sys_user类型
        if (!ObjectUtil.equal(user.getStatus(), "0")) {
            return R.fail("账号已被停用");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return R.fail("账号或密码错误");
        }

        // 构建登录用户
        LoginUser loginUser = buildLoginUser(user);

        // 创建登录参数 - 使用app客户端配置（token有效期30分钟，活跃超时7天）
        SaLoginParameter model = new SaLoginParameter();
        model.setTimeout(60 * 60 * 24 * 7); // 7天过期
        model.setActiveTimeout(60 * 60 * 24); // 1天活跃超时
        model.setDeviceType("android");
        // 不设置CLIENT_KEY，使用默认值
        LoginHelper.login(loginUser, model);

        // 获取token
        String token = StpUtil.getTokenValue();

        // 构建返回对象
        SpH5LoginVo loginVo = new SpH5LoginVo();
        loginVo.setToken(token);
        loginVo.setUserId(user.getUserId());
        loginVo.setUsername(user.getUserName());
        loginVo.setNickname(user.getNickName());
        loginVo.setEmail(user.getEmail());
        loginVo.setPhonenumber(user.getPhonenumber());

        return R.ok(loginVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> register(SpH5RegisterBo registerBo) {
        String username = registerBo.getUsername();
        String password = registerBo.getPassword();
        String confirmPassword = registerBo.getConfirmPassword();
        String email = registerBo.getEmail();
        String verifyCode = registerBo.getVerifyCode();

        // 校验两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return R.fail("两次密码输入不一致");
        }

        // 校验验证码
        String codeKey = EMAIL_CODE_KEY + email;
        String cachedCode = RedisUtils.getCacheObject(codeKey);
        if (StringUtils.isEmpty(cachedCode)) {
            return R.fail("验证码已过期");
        }
        if (!cachedCode.equals(verifyCode)) {
            return R.fail("验证码错误");
        }
        // 删除验证码
        RedisUtils.deleteObject(codeKey);

        // 检查用户名是否已存在
        boolean exist = userMapper.exists(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username)
        );
        if (exist) {
            return R.fail("用户名已存在");
        }

        // 检查邮箱是否已存在
        exist = userMapper.exists(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email)
        );
        if (exist) {
            return R.fail("邮箱已被注册");
        }

        // 创建用户
        SysUserBo sysUser = new SysUserBo();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(BCrypt.hashpw(password));
        sysUser.setEmail(email);
        sysUser.setUserType(H5_USER_TYPE);
        sysUser.setStatus("0"); // 正常状态

        boolean regFlag = userService.registerUser(sysUser);
        if (!regFlag) {
            return R.fail("注册失败");
        }

        // 注册成功后，创建会员卡
        try {
            // 查询刚创建的用户ID
            SysUserVo newUser = userMapper.selectVoOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username)
            );

            if (newUser != null) {
                createMemberCard(newUser.getUserId(), username);
                log.info("用户注册成功，已创建会员卡，userId: {}, username: {}", newUser.getUserId(), username);
            }
        } catch (Exception e) {
            log.error("创建会员卡失败", e);
            // 这里不抛出异常，因为用户已经注册成功，会员卡可以后续补充
        }

        return R.ok("注册成功");
    }

    /**
     * 创建会员卡
     */
    private void createMemberCard(Long userId, String username) {
        SpMemberCard memberCard = new SpMemberCard();
        memberCard.setUserId(userId);

        // 生成会员卡号：日期(6位) + 随机数(6位)，例如：240601123456
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        String randomStr = String.format("%06d", new Random().nextInt(1000000));
        String cardNo = dateStr + randomStr;
        memberCard.setCardNo(cardNo);

        // 设置为普通会员（等级0）
        memberCard.setCardLevel("0");

        // 初始余额为0
        memberCard.setBalance(0L);
        memberCard.setTotalRecharge(0L);
        memberCard.setTotalConsume(0L);

        // 发卡日期为当前时间
        memberCard.setIssueDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        // 有效期默认1年
        memberCard.setExpiryDate(Date.from(LocalDateTime.now().plusYears(1).atZone(ZoneId.systemDefault()).toInstant()));

        // 状态正常
        memberCard.setStatus("0");

        memberCardMapper.insert(memberCard);
        log.info("创建会员卡成功，cardNo: {}, userId: {}", cardNo, userId);
    }

    @Override
    public R<Void> sendEmailCode(String email) {
        // 校验邮箱格式
        if (!email.matches(EMAIL_REGEX)) {
            return R.fail("邮箱格式不正确");
        }

        // 检查邮箱是否已注册
        boolean exist = userMapper.exists(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email)
        );
        if (exist) {
            return R.fail("邮箱已被注册");
        }

        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        log.info("验证码：{}", code);
        // 存储到Redis，5分钟过期
        String codeKey = EMAIL_CODE_KEY + email;
        RedisUtils.setCacheObject(codeKey, code, Duration.ofMinutes(emailExpire));

        // 发送邮件
        try {
            MailUtils.sendText(email, "智能游泳馆 - 注册验证码", "您的验证码是：" + code + "，5分钟内有效。请勿将验证码告知他人。");
            return R.ok("验证码已发送");
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return R.fail("验证码发送失败");
        }
    }

    @Override
    public R<SpH5LoginVo> getUserInfo() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            SysUserVo user = userService.selectUserById(loginUser.getUserId());

            SpH5LoginVo loginVo = new SpH5LoginVo();
            loginVo.setUserId(user.getUserId());
            loginVo.setUsername(user.getUserName());
            loginVo.setNickname(user.getNickName());
            loginVo.setEmail(user.getEmail());
            loginVo.setPhonenumber(user.getPhonenumber());

            return R.ok(loginVo);
        } catch (Exception e) {
            return R.fail("获取用户信息失败");
        }
    }

    /**
     * 根据账号查找用户（邮箱或手机号）
     */
    private SysUserVo findUserByAccount(String account) {
        if (StringUtils.isEmpty(account)) {
            return null;
        }

        // 先尝试用邮箱查找
        if (account.matches(EMAIL_REGEX)) {
            SysUserVo user = userMapper.selectVoOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, account)
            );
            if (user != null) {
                return user;
            }
        }

        // 再尝试用手机号查找
        if (account.matches(MOBILE_REGEX)) {
            SysUserVo user = userService.selectUserByPhonenumber(account);
            if (user != null) {
                return user;
            }
        }

        // 最后尝试用用户名查找
        return userService.selectUserByUserName(account);
    }

    /**
     * 构建登录用户
     */
    private LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        return loginUser;
    }
}
