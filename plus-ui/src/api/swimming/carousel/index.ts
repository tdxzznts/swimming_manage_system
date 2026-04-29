import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CarouselVO, CarouselForm, CarouselQuery } from '@/api/swimming/carousel/types';

/**
 * 查询轮播图列表
 * @param query
 * @returns {*}
 */

export const listCarousel = (query?: CarouselQuery): AxiosPromise<CarouselVO[]> => {
  return request({
    url: '/swimming/carousel/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询轮播图详细
 * @param id
 */
export const getCarousel = (id: string | number): AxiosPromise<CarouselVO> => {
  return request({
    url: '/swimming/carousel/' + id,
    method: 'get'
  });
};

/**
 * 新增轮播图
 * @param data
 */
export const addCarousel = (data: CarouselForm) => {
  return request({
    url: '/swimming/carousel',
    method: 'post',
    data: data
  });
};

/**
 * 修改轮播图
 * @param data
 */
export const updateCarousel = (data: CarouselForm) => {
  return request({
    url: '/swimming/carousel',
    method: 'put',
    data: data
  });
};

/**
 * 删除轮播图
 * @param id
 */
export const delCarousel = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/carousel/' + id,
    method: 'delete'
  });
};
