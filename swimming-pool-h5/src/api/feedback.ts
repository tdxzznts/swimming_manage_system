import { request } from '@/utils/request'
import type {
  Feedback,
  FeedbackForm,
  MyFeedbackRequest
} from '@/types/feedback'

/**
 * 创建反馈
 */
export const createFeedback = (data: FeedbackForm) => {
  return request.post<Feedback>('/h5/feedback', data)
}

/**
 * 查询我的反馈列表
 */
export const listMyFeedbacks = (params: MyFeedbackRequest) => {
  return request.get<Feedback[]>('/h5/feedback/my', params)
}

/**
 * 获取反馈详情
 */
export const getFeedbackDetail = (feedbackId: number) => {
  return request.get<Feedback>(`/h5/feedback/${feedbackId}`)
}
