import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberCardVO, MemberCardForm, MemberCardQuery } from '@/api/swimming/memberCard/types';

/**
 * 查询会员卡列表
 * @param query
 * @returns {*}
 */

export const listMemberCard = (query?: MemberCardQuery): AxiosPromise<MemberCardVO[]> => {
  return request({
    url: '/swimming/memberCard/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询会员卡详细
 * @param id
 */
export const getMemberCard = (id: string | number): AxiosPromise<MemberCardVO> => {
  return request({
    url: '/swimming/memberCard/' + id,
    method: 'get'
  });
};

/**
 * 新增会员卡
 * @param data
 */
export const addMemberCard = (data: MemberCardForm) => {
  return request({
    url: '/swimming/memberCard',
    method: 'post',
    data: data
  });
};

/**
 * 修改会员卡
 * @param data
 */
export const updateMemberCard = (data: MemberCardForm) => {
  return request({
    url: '/swimming/memberCard',
    method: 'put',
    data: data
  });
};

/**
 * 删除会员卡
 * @param id
 */
export const delMemberCard = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/memberCard/' + id,
    method: 'delete'
  });
};
