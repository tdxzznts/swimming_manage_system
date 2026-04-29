package org.dromara.swimming.service;

import org.dromara.common.core.domain.R;
import org.dromara.swimming.domain.bo.SpH5MyReservationBo;
import org.dromara.swimming.domain.bo.SpH5ReservationBo;
import org.dromara.swimming.domain.vo.SpH5HomeStatisticsVo;
import org.dromara.swimming.domain.vo.SpH5LaneVo;
import org.dromara.swimming.domain.vo.SpH5ReservationVo;
import org.dromara.swimming.domain.vo.SpH5TimeSlotVo;

import java.util.Date;
import java.util.List;

/**
 * H5预约服务接口
 *
 * @author swimming
 * @date 2026-02-03
 */
public interface ISpH5ReservationService {

    /**
     * 查询可用的泳道列表
     *
     * @return 泳道列表
     */
    R<List<SpH5LaneVo>> listLanes();

    /**
     * 根据日期查询可用时段
     *
     * @param date 预约日期
     * @param laneId 泳道ID（可选）
     * @return 时段列表
     */
    R<List<SpH5TimeSlotVo>> listTimeSlots(Date date, Long laneId);

    /**
     * 创建预约
     *
     * @param bo 预约信息
     * @return 预约结果
     */
    R<SpH5ReservationVo> createReservation(SpH5ReservationBo bo);

    /**
     * 查询我的预约列表
     *
     * @param bo 查询条件
     * @return 预约列表
     */
    R<List<SpH5ReservationVo>> listMyReservations(SpH5MyReservationBo bo);

    /**
     * 取消预约
     *
     * @param reservationId 预约ID
     * @return 取消结果
     */
    R<Void> cancelReservation(Long reservationId);

    /**
     * 删除预约（仅已取消的订单可删除）
     *
     * @param reservationId 预约ID
     * @return 删除结果
     */
    R<Void> deleteReservation(Long reservationId);

    /**
     * 获取首页统计信息
     *
     * @return 统计信息
     */
    R<SpH5HomeStatisticsVo> getHomeStatistics();
}
