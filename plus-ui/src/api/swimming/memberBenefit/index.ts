import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberBenefitVO, MemberBenefitForm, MemberBenefitQuery } from '@/api/swimming/memberBenefit/types';

/**
 * 查询会员权益配置列表
 * @param query
 * @returns {*}
 */

export const listMemberBenefit = (query?: MemberBenefitQuery): AxiosPromise<MemberBenefitVO[]> => {
  return request({
    url: '/swimming/memberBenefit/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询会员权益配置详细
 * @param id
 */
export const getMemberBenefit = (id: string | number): AxiosPromise<MemberBenefitVO> => {
  return request({
    url: '/swimming/memberBenefit/' + id,
    method: 'get'
  });
};

/**
 * 新增会员权益配置
 * @param data
 */
export const addMemberBenefit = (data: MemberBenefitForm) => {
  return request({
    url: '/swimming/memberBenefit',
    method: 'post',
    data: data
  });
};

/**
 * 修改会员权益配置
 * @param data
 */
export const updateMemberBenefit = (data: MemberBenefitForm) => {
  return request({
    url: '/swimming/memberBenefit',
    method: 'put',
    data: data
  });
};

/**
 * 删除会员权益配置
 * @param id
 */
export const delMemberBenefit = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/memberBenefit/' + id,
    method: 'delete'
  });
};
