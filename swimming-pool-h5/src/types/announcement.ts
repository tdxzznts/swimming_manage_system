/**
 * 公告信息
 */
export interface Announcement {
  id: string
  title: string
  announcementType: string
  announcementTypeName: string
  publishTime: string
  viewCount: number
}

/**
 * 公告详情
 */
export interface AnnouncementDetail {
  id: string
  title: string
  content: string
  announcementType: string
  announcementTypeName: string
  publishTime: string
  viewCount: number
}
