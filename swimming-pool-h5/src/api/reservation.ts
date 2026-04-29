import { request } from '@/utils/request'
import type {
  Lane,
  TimeSlot,
  Reservation,
  CreateReservationRequest,
  MyReservationsRequest,
  HomeStatistics
} from '@/types/reservation'

/**
 * 查询可用的泳道列表
 */
export const listLanes = () => {
  return request.get<Lane[]>('/h5/reservation/lanes')
}

/**
 * 根据日期和泳道ID查询可用时段
 */
export const listTimeSlots = (date: string, laneId?: number) => {
  const params: Record<string, any> = { date }
  if (laneId !== undefined) {
    params.laneId = laneId
  }
  return request.get<TimeSlot[]>('/h5/reservation/timeslots', params)
}

/**
 * 创建预约
 */
export const createReservation = (data: CreateReservationRequest) => {
  return request.post<Reservation>('/h5/reservation/create', data)
}

/**
 * 查询我的预约列表
 */
export const listMyReservations = (params: MyReservationsRequest) => {
  return request.get<Reservation[]>('/h5/reservation/my', params)
}

/**
 * 取消预约
 */
export const cancelReservation = (reservationId: number) => {
  return request.post<void>(`/h5/reservation/cancel/${reservationId}`)
}

/**
 * 删除预约
 */
export const deleteReservation = (reservationId: number) => {
  return request.delete<void>(`/h5/reservation/${reservationId}`)
}

/**
 * 获取首页统计信息
 */
export const getHomeStatistics = () => {
  return request.get<HomeStatistics>('/h5/reservation/home/statistics')
}
