import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { OrderVO, OrderForm, OrderQuery } from '@/api/swimming/order/types';

/**
 * 查询订单列表
 * @param query
 * @returns {*}
 */

export const listOrder = (query?: OrderQuery): AxiosPromise<OrderVO[]> => {
  return request({
    url: '/swimming/order/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询订单详细
 * @param id
 */
export const getOrder = (id: string | number): AxiosPromise<OrderVO> => {
  return request({
    url: '/swimming/order/' + id,
    method: 'get'
  });
};

/**
 * 新增订单
 * @param data
 */
export const addOrder = (data: OrderForm) => {
  return request({
    url: '/swimming/order',
    method: 'post',
    data: data
  });
};

/**
 * 修改订单
 * @param data
 */
export const updateOrder = (data: OrderForm) => {
  return request({
    url: '/swimming/order',
    method: 'put',
    data: data
  });
};

/**
 * 删除订单
 * @param id
 */
export const delOrder = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/order/' + id,
    method: 'delete'
  });
};
