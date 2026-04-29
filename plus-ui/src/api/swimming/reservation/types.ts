export interface ReservationVO {
  /**
   * 预约ID
   */
  id: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId: string | number;

  /**
   * 时段ID（关联sp_time_slot.id）
   */
  timeSlotId: string | number;

  /**
   * 泳道ID（关联sp_lane.id）
   */
  laneId: string | number;

  /**
   * 预约单号
   */
  reservationNo: string;

  /**
   * 预约日期
   */
  reservationDate: string;

  /**
   * 开始时间
   */
  startTime: string;

  /**
   * 结束时间
   */
  endTime: string;

  /**
   * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
   */
  status: string;

  /**
   * 金额（元）
   */
  amount: number;

  /**
   * 订单ID（关联sp_order.id）
   */
  orderId: string | number;

  /**
   * 签到时间
   */
  checkInTime: string;

  /**
   * 签退时间
   */
  checkOutTime: string;

  /**
   * 取消时间
   */
  cancelTime: string;

  /**
   * 取消原因
   */
  cancelReason: string;

  /**
   * 备注
   */
  remark: string;

}

export interface ReservationForm extends BaseEntity {
  /**
   * 预约ID
   */
  id?: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 时段ID（关联sp_time_slot.id）
   */
  timeSlotId?: string | number;

  /**
   * 泳道ID（关联sp_lane.id）
   */
  laneId?: string | number;

  /**
   * 预约单号
   */
  reservationNo?: string;

  /**
   * 预约日期
   */
  reservationDate?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
   */
  status?: string;

  /**
   * 金额（元）
   */
  amount?: number;

  /**
   * 订单ID（关联sp_order.id）
   */
  orderId?: string | number;

  /**
   * 签到时间
   */
  checkInTime?: string;

  /**
   * 签退时间
   */
  checkOutTime?: string;

  /**
   * 取消时间
   */
  cancelTime?: string;

  /**
   * 取消原因
   */
  cancelReason?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface ReservationQuery extends PageQuery {

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 时段ID（关联sp_time_slot.id）
   */
  timeSlotId?: string | number;

  /**
   * 泳道ID（关联sp_lane.id）
   */
  laneId?: string | number;

  /**
   * 预约单号
   */
  reservationNo?: string;

  /**
   * 预约日期
   */
  reservationDate?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
   */
  status?: string;

  /**
   * 金额（元）
   */
  amount?: number;

  /**
   * 订单ID（关联sp_order.id）
   */
  orderId?: string | number;

  /**
   * 签到时间
   */
  checkInTime?: string;

  /**
   * 签退时间
   */
  checkOutTime?: string;

  /**
   * 取消时间
   */
  cancelTime?: string;

  /**
   * 取消原因
   */
  cancelReason?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
