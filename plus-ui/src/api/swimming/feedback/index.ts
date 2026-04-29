import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { FeedbackVO, FeedbackForm, FeedbackQuery } from '@/api/swimming/feedback/types';

/**
 * 查询反馈列表
 * @param query
 * @returns {*}
 */

export const listFeedback = (query?: FeedbackQuery): AxiosPromise<FeedbackVO[]> => {
  return request({
    url: '/swimming/feedback/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询反馈详细
 * @param id
 */
export const getFeedback = (id: string | number): AxiosPromise<FeedbackVO> => {
  return request({
    url: '/swimming/feedback/' + id,
    method: 'get'
  });
};

/**
 * 新增反馈
 * @param data
 */
export const addFeedback = (data: FeedbackForm) => {
  return request({
    url: '/swimming/feedback',
    method: 'post',
    data: data
  });
};

/**
 * 修改反馈
 * @param data
 */
export const updateFeedback = (data: FeedbackForm) => {
  return request({
    url: '/swimming/feedback',
    method: 'put',
    data: data
  });
};

/**
 * 删除反馈
 * @param id
 */
export const delFeedback = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/feedback/' + id,
    method: 'delete'
  });
};
