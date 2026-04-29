import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ReservationVO, ReservationForm, ReservationQuery } from '@/api/swimming/reservation/types';

/**
 * 查询预约记录列表
 * @param query
 * @returns {*}
 */

export const listReservation = (query?: ReservationQuery): AxiosPromise<ReservationVO[]> => {
  return request({
    url: '/swimming/reservation/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询预约记录详细
 * @param id
 */
export const getReservation = (id: string | number): AxiosPromise<ReservationVO> => {
  return request({
    url: '/swimming/reservation/' + id,
    method: 'get'
  });
};

/**
 * 新增预约记录
 * @param data
 */
export const addReservation = (data: ReservationForm) => {
  return request({
    url: '/swimming/reservation',
    method: 'post',
    data: data
  });
};

/**
 * 修改预约记录
 * @param data
 */
export const updateReservation = (data: ReservationForm) => {
  return request({
    url: '/swimming/reservation',
    method: 'put',
    data: data
  });
};

/**
 * 删除预约记录
 * @param id
 */
export const delReservation = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/reservation/' + id,
    method: 'delete'
  });
};
