/**
 * 反馈信息
 */
export interface Feedback {
  id: number
  feedbackNo: string
  feedbackType: string
  feedbackTypeName: string
  title: string
  content: string
  contactInfo: string
  status: string
  statusName: string
  reply: string | null
  replyTime: string | null
  createTime: string
  updateTime: string
}

/**
 * 创建反馈请求
 */
export interface FeedbackForm {
  feedbackType: string
  title: string
  content: string
  contactInfo: string
  satisfaction?: number
}

/**
 * 我的反馈查询请求
 */
export interface MyFeedbackRequest {
  status?: string
  feedbackType?: string
}
