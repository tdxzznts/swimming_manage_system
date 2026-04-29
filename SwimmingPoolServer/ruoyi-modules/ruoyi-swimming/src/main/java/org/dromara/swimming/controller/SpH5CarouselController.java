package org.dromara.swimming.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.bo.SpCarouselBo;
import org.dromara.swimming.domain.vo.SpCarouselVo;
import org.dromara.swimming.service.ISpCarouselService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * H5轮播图控制器
 *
 * @author swimming
 * @date 2026-02-07
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/carousel")
public class SpH5CarouselController extends BaseController {

    private final ISpCarouselService carouselService;

    /**
     * 获取启用的轮播图列表
     * 只返回当前时间在startTime和endTime之间的轮播图
     *
     * @return 轮播图列表
     */
    @GetMapping("/list")
    public R<List<SpCarouselVo>> getActiveCarouselList() {
        SpCarouselBo bo = new SpCarouselBo();
        bo.setStatus("1");
        List<SpCarouselVo> list = carouselService.queryList(bo);

        // 获取当前时间
        Date now = new Date();

        // 过滤出当前时间在startTime和endTime之间的轮播图
        List<SpCarouselVo> filteredList = list.stream()
            .filter(item -> {
                // 如果startTime和endTime都为空，则显示
                if (item.getStartTime() == null && item.getEndTime() == null) {
                    return true;
                }
                // 如果startTime为空，只判断endTime（当前时间 <= endTime）
                if (item.getStartTime() == null) {
                    return !now.after(item.getEndTime());
                }
                // 如果endTime为空，只判断startTime（当前时间 >= startTime）
                if (item.getEndTime() == null) {
                    return !now.before(item.getStartTime());
                }
                // 都不为空，判断是否在时间范围内（startTime <= 当前时间 <= endTime）
                return !now.before(item.getStartTime()) && !now.after(item.getEndTime());
            })
            .collect(Collectors.toList());

        return R.ok(filteredList);
    }
}
