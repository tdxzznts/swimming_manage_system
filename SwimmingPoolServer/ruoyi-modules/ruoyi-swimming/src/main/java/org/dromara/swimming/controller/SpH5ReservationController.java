package org.dromara.swimming.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.swimming.domain.bo.SpH5MyReservationBo;
import org.dromara.swimming.domain.bo.SpH5ReservationBo;
import org.dromara.swimming.domain.vo.SpH5LaneVo;
import org.dromara.swimming.domain.vo.SpH5ReservationVo;
import org.dromara.swimming.domain.vo.SpH5TimeSlotVo;
import org.dromara.swimming.service.ISpH5ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * H5预约控制器
 *
 * @author swimming
 * @date 2026-02-03
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/h5/reservation")
public class SpH5ReservationController extends BaseController {

    private final ISpH5ReservationService reservationService;

    /**
     * 查询可用的泳道列表
     *
     * @return 泳道列表
     */
    @SaCheckLogin
    @GetMapping("/lanes")
    public R<List<SpH5LaneVo>> listLanes() {
        return reservationService.listLanes();
    }

    /**
     * 根据日期查询可用时段
     *
     * @param date 预约日期 (yyyy-MM-dd)
     * @param laneId 泳道ID（可选）
     * @return 时段列表
     */
    @SaCheckLogin
    @GetMapping("/timeslots")
    public R<List<SpH5TimeSlotVo>> listTimeSlots(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
        @RequestParam(required = false) Long laneId) {
        return reservationService.listTimeSlots(date, laneId);
    }

    /**
     * 创建预约
     *
     * @param bo 预约信息
     * @return 预约结果
     */
    @SaCheckLogin
    @PostMapping("/create")
    public R<SpH5ReservationVo> createReservation(@RequestBody SpH5ReservationBo bo) {
        return reservationService.createReservation(bo);
    }

    /**
     * 查询我的预约列表
     *
     * @param bo 查询条件
     * @return 预约列表
     */
    @SaCheckLogin
    @GetMapping("/my")
    public R<List<SpH5ReservationVo>> listMyReservations(SpH5MyReservationBo bo) {
        return reservationService.listMyReservations(bo);
    }

    /**
     * 取消预约
     *
     * @param reservationId 预约ID
     * @return 取消结果
     */
    @SaCheckLogin
    @PostMapping("/cancel/{reservationId}")
    public R<Void> cancelReservation(@PathVariable Long reservationId) {
        return reservationService.cancelReservation(reservationId);
    }

    /**
     * 删除预约（仅已取消的订单可删除）
     *
     * @param reservationId 预约ID
     * @return 删除结果
     */
    @SaCheckLogin
    @DeleteMapping("/{reservationId}")
    public R<Void> deleteReservation(@PathVariable Long reservationId) {
        return reservationService.deleteReservation(reservationId);
    }

    /**
     * 获取首页统计信息
     *
     * @return 统计信息
     */
    @SaCheckLogin
    @GetMapping("/home/statistics")
    public R<org.dromara.swimming.domain.vo.SpH5HomeStatisticsVo> getHomeStatistics() {
        return reservationService.getHomeStatistics();
    }
}
