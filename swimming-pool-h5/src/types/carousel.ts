/**
 * 轮播图
 */
export interface Carousel {
  id: number
  title: string
  imageUrl: string
  linkType: string // 0无跳转 1H5页面 2外部链接
  linkUrl: string
  sortOrder: number
  status: string // 0停用 1启用
  startTime: string
  endTime: string
}
