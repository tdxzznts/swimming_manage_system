package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.vo.SpH5UserStatisticsVo;
import org.dromara.swimming.service.ISpH5UserService;
import org.springframework.web.bind.annotation.*;

/**
 * H5用户控制器
 *
 * @author swimming
 * @date 2026-02-04
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/user")
public class SpH5UserController extends BaseController {

    private final ISpH5UserService userService;

    /**
     * 获取用户统计数据
     *
     * @return 统计数据
     */
    @SaCheckLogin
    @GetMapping("/statistics")
    public R<SpH5UserStatisticsVo> getUserStatistics() {
        return userService.getUserStatistics();
    }
}
