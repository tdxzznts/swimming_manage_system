import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ReviewVO, ReviewForm, ReviewQuery } from '@/api/swimming/review/types';

/**
 * 查询评价列表
 * @param query
 * @returns {*}
 */

export const listReview = (query?: ReviewQuery): AxiosPromise<ReviewVO[]> => {
  return request({
    url: '/swimming/review/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询评价详细
 * @param id
 */
export const getReview = (id: string | number): AxiosPromise<ReviewVO> => {
  return request({
    url: '/swimming/review/' + id,
    method: 'get'
  });
};

/**
 * 新增评价
 * @param data
 */
export const addReview = (data: ReviewForm) => {
  return request({
    url: '/swimming/review',
    method: 'post',
    data: data
  });
};

/**
 * 修改评价
 * @param data
 */
export const updateReview = (data: ReviewForm) => {
  return request({
    url: '/swimming/review',
    method: 'put',
    data: data
  });
};

/**
 * 删除评价
 * @param id
 */
export const delReview = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/review/' + id,
    method: 'delete'
  });
};
