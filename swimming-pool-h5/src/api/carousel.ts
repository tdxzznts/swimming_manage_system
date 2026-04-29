import request from '@/utils/request'
import type { Carousel } from '@/types/carousel'

/**
 * 获取启用的轮播图列表
 */
export const getActiveCarouselList = () => {
  return request.get<Carousel[]>('/h5/carousel/list')
}
