import { request } from '@/utils/request'
import type {
  Announcement,
  AnnouncementDetail
} from '@/types/announcement'

/**
 * 查询最新公告列表
 */
export const listLatestAnnouncements = (limit = 10) => {
  return request.get<Announcement[]>('/h5/announcement/list', { limit })
}

/**
 * 查询全部公告列表
 */
export const listAllAnnouncements = () => {
  return request.get<Announcement[]>('/h5/announcement/list')
}

/**
 * 查询公告详情
 */
export const getAnnouncementDetail = (announcementId: string) => {
  return request.get<AnnouncementDetail>(`/h5/announcement/${announcementId}`)
}

/**
 * 增加公告浏览次数
 */
export const incrementViewCount = (announcementId: string) => {
  return request.post(`/h5/announcement/${announcementId}/view`)
}
