export interface FeedbackVO {
  /**
   * 反馈ID
   */
  id: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId: string | number;

  /**
   * 反馈类型（1建议 2投诉）
   */
  feedbackType: string;

  /**
   * 反馈标题
   */
  title: string;

  /**
   * 反馈内容
   */
  content: string;

  /**
   * 图片URL（JSON数组）
   */
  images: string;

  /**
   * 联系方式
   */
  contactInfo: string;

  /**
   * 状态（0待处理 1处理中 2已完成）
   */
  status: string;

  /**
   * 处理人ID
   */
  handleUserId: string | number;

  /**
   * 处理结果
   */
  handleResult: string;

  /**
   * 处理时间
   */
  handleTime: string;

  /**
   * 满意度评分（1-5）
   */
  satisfaction: number;

  /**
   * 备注
   */
  remark: string;

}

export interface FeedbackForm extends BaseEntity {
  /**
   * 反馈ID
   */
  id?: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 反馈类型（1建议 2投诉）
   */
  feedbackType?: string;

  /**
   * 反馈标题
   */
  title?: string;

  /**
   * 反馈内容
   */
  content?: string;

  /**
   * 图片URL（JSON数组）
   */
  images?: string;

  /**
   * 联系方式
   */
  contactInfo?: string;

  /**
   * 状态（0待处理 1处理中 2已完成）
   */
  status?: string;

  /**
   * 处理人ID
   */
  handleUserId?: string | number;

  /**
   * 处理结果
   */
  handleResult?: string;

  /**
   * 处理时间
   */
  handleTime?: string;

  /**
   * 满意度评分（1-5）
   */
  satisfaction?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface FeedbackQuery extends PageQuery {

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 反馈类型（1建议 2投诉）
   */
  feedbackType?: string;

  /**
   * 反馈标题
   */
  title?: string;

  /**
   * 反馈内容
   */
  content?: string;

  /**
   * 图片URL（JSON数组）
   */
  images?: string;

  /**
   * 联系方式
   */
  contactInfo?: string;

  /**
   * 状态（0待处理 1处理中 2已完成）
   */
  status?: string;

  /**
   * 处理人ID
   */
  handleUserId?: string | number;

  /**
   * 处理结果
   */
  handleResult?: string;

  /**
   * 处理时间
   */
  handleTime?: string;

  /**
   * 满意度评分（1-5）
   */
  satisfaction?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}
