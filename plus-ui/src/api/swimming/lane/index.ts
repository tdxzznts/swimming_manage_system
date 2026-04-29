import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LaneVO, LaneForm, LaneQuery } from '@/api/swimming/lane/types';

/**
 * 查询泳道列表
 * @param query
 * @returns {*}
 */

export const listLane = (query?: LaneQuery): AxiosPromise<LaneVO[]> => {
  return request({
    url: '/swimming/lane/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询泳道详细
 * @param id
 */
export const getLane = (id: string | number): AxiosPromise<LaneVO> => {
  return request({
    url: '/swimming/lane/' + id,
    method: 'get'
  });
};

/**
 * 新增泳道
 * @param data
 */
export const addLane = (data: LaneForm) => {
  return request({
    url: '/swimming/lane',
    method: 'post',
    data: data
  });
};

/**
 * 修改泳道
 * @param data
 */
export const updateLane = (data: LaneForm) => {
  return request({
    url: '/swimming/lane',
    method: 'put',
    data: data
  });
};

/**
 * 删除泳道
 * @param id
 */
export const delLane = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/lane/' + id,
    method: 'delete'
  });
};
