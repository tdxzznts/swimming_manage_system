import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TimeSlotVO, TimeSlotForm, TimeSlotQuery } from '@/api/swimming/timeSlot/types';

/**
 * 查询预约时段列表
 * @param query
 * @returns {*}
 */

export const listTimeSlot = (query?: TimeSlotQuery): AxiosPromise<TimeSlotVO[]> => {
  return request({
    url: '/swimming/timeSlot/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询预约时段详细
 * @param id
 */
export const getTimeSlot = (id: string | number): AxiosPromise<TimeSlotVO> => {
  return request({
    url: '/swimming/timeSlot/' + id,
    method: 'get'
  });
};

/**
 * 新增预约时段
 * @param data
 */
export const addTimeSlot = (data: TimeSlotForm) => {
  return request({
    url: '/swimming/timeSlot',
    method: 'post',
    data: data
  });
};

/**
 * 修改预约时段
 * @param data
 */
export const updateTimeSlot = (data: TimeSlotForm) => {
  return request({
    url: '/swimming/timeSlot',
    method: 'put',
    data: data
  });
};

/**
 * 删除预约时段
 * @param id
 */
export const delTimeSlot = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/timeSlot/' + id,
    method: 'delete'
  });
};
