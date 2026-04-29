import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberLevelVO, MemberLevelForm, MemberLevelQuery } from '@/api/swimming/memberLevel/types';

/**
 * 查询会员等级配置列表
 * @param query
 * @returns {*}
 */

export const listMemberLevel = (query?: MemberLevelQuery): AxiosPromise<MemberLevelVO[]> => {
  return request({
    url: '/swimming/memberLevel/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询会员等级配置详细
 * @param id
 */
export const getMemberLevel = (id: string | number): AxiosPromise<MemberLevelVO> => {
  return request({
    url: '/swimming/memberLevel/' + id,
    method: 'get'
  });
};

/**
 * 新增会员等级配置
 * @param data
 */
export const addMemberLevel = (data: MemberLevelForm) => {
  return request({
    url: '/swimming/memberLevel',
    method: 'post',
    data: data
  });
};

/**
 * 修改会员等级配置
 * @param data
 */
export const updateMemberLevel = (data: MemberLevelForm) => {
  return request({
    url: '/swimming/memberLevel',
    method: 'put',
    data: data
  });
};

/**
 * 删除会员等级配置
 * @param id
 */
export const delMemberLevel = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/memberLevel/' + id,
    method: 'delete'
  });
};
