/**
 * 泳道信息
 */
export interface Lane {
  id: number
  laneNo: string
  laneType: string
  laneTypeName: string
  capacity: number
  length: number
  width: number
  depth: number
  status: string
  statusName: string
  sort: number
}

/**
 * 时段信息
 */
export interface TimeSlot {
  id: number
  laneId: number
  laneNo: string
  laneType: string
  slotDate: string
  startTime: string
  endTime: string
  slotType: string
  slotTypeName: string
  capacity: number
  bookedCount: number
  remainingCount: number
  price: number
  isAvailable: string
}

/**
 * 预约记录
 */
export interface Reservation {
  id: number
  orderId?: number
  reservationNo: string
  reservationDate: string
  startTime: string
  endTime: string
  laneNo: string
  laneTypeName: string
  status: string
  statusName: string
  amount: number
  discountAmount?: number
  actualAmount?: number
  payType?: string
  payTypeName?: string
  checkInTime: string | null
  checkOutTime: string | null
  cancelTime: string | null
  canCancel: boolean
}

/**
 * 创建预约请求
 */
export interface CreateReservationRequest {
  timeSlotId: number
  reservationDate: string
}

/**
 * 我的预约查询请求
 */
export interface MyReservationsRequest {
  status?: string
  startDate?: string
  endDate?: string
}

/**
 * 首页统计信息
 */
export interface HomeStatistics {
  remainingSlots: number
  currentPeopleCount: number
}
