export interface TimeSlotVO {
  /**
   * 时段ID
   */
  id: string | number;

  /**
   * 泳道ID（关联sp_lane.id）
   */
  laneId: string | number;

  /**
   * 日期
   */
  slotDate: string;

  /**
   * 开始时间
   */
  startTime: string;

  /**
   * 结束时间
   */
  endTime: string;

  /**
   * 时段类型（1高峰期 2低峰期）
   */
  slotType: string;

  /**
   * 容量（人数）
   */
  capacity: number;

  /**
   * 已预约人数
   */
  bookedCount: number;

  /**
   * 单价（元）
   */
  price: number;

  /**
   * 是否可预约（0否 1是）
   */
  isAvailable: string;

  /**
   * 备注
   */
  remark: string;

}

export interface TimeSlotForm extends BaseEntity {
  /**
   * 时段ID
   */
  id?: string | number;

  /**
   * 泳道ID（关联sp_lane.id）
   */
  laneId?: string | number;

  /**
   * 日期
   */
  slotDate?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 时段类型（1高峰期 2低峰期）
   */
  slotType?: string;

  /**
   * 容量（人数）
   */
  capacity?: number;

  /**
   * 已预约人数
   */
  bookedCount?: number;

  /**
   * 单价（元）
   */
  price?: number;

  /**
   * 是否可预约（0否 1是）
   */
  isAvailable?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface TimeSlotQuery extends PageQuery {

  /**
   * 泳道ID（关联sp_lane.id）
   */
  laneId?: string | number;

  /**
   * 日期
   */
  slotDate?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 时段类型（1高峰期 2低峰期）
   */
  slotType?: string;

  /**
   * 容量（人数）
   */
  capacity?: number;

  /**
   * 已预约人数
   */
  bookedCount?: number;

  /**
   * 单价（元）
   */
  price?: number;

  /**
   * 是否可预约（0否 1是）
   */
  isAvailable?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
