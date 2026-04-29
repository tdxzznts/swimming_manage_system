export interface ReviewVO {
  /**
   * 评价ID
   */
  id: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId: string | number;

  /**
   * 预约ID（关联sp_reservation.id）
   */
  reservationId: string | number;

  /**
   * 评分（1-5星）
   */
  rating: number;

  /**
   * 评价内容
   */
  content: string;

  /**
   * 图片URL（JSON数组）
   */
  images: string;

  /**
   * 状态（0待审核 1已通过 2已拒绝）
   */
  status: string;

  /**
   * 管理员回复
   */
  reply: string;

  /**
   * 回复人ID
   */
  replyUserId: string | number;

  /**
   * 回复时间
   */
  replyTime: string;

  /**
   * 备注
   */
  remark: string;

}

export interface ReviewForm extends BaseEntity {
  /**
   * 评价ID
   */
  id?: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 预约ID（关联sp_reservation.id）
   */
  reservationId?: string | number;

  /**
   * 评分（1-5星）
   */
  rating?: number;

  /**
   * 评价内容
   */
  content?: string;

  /**
   * 图片URL（JSON数组）
   */
  images?: string;

  /**
   * 状态（0待审核 1已通过 2已拒绝）
   */
  status?: string;

  /**
   * 管理员回复
   */
  reply?: string;

  /**
   * 回复人ID
   */
  replyUserId?: string | number;

  /**
   * 回复时间
   */
  replyTime?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface ReviewQuery extends PageQuery {

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 预约ID（关联sp_reservation.id）
   */
  reservationId?: string | number;

  /**
   * 评分（1-5星）
   */
  rating?: number;

  /**
   * 评价内容
   */
  content?: string;

  /**
   * 图片URL（JSON数组）
   */
  images?: string;

  /**
   * 状态（0待审核 1已通过 2已拒绝）
   */
  status?: string;

  /**
   * 管理员回复
   */
  reply?: string;

  /**
   * 回复人ID
   */
  replyUserId?: string | number;

  /**
   * 回复时间
   */
  replyTime?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
