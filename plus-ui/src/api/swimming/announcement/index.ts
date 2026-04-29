import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { AnnouncementVO, AnnouncementForm, AnnouncementQuery } from '@/api/swimming/announcement/types';

/**
 * 查询系统公告列表
 * @param query
 * @returns {*}
 */

export const listAnnouncement = (query?: AnnouncementQuery): AxiosPromise<AnnouncementVO[]> => {
  return request({
    url: '/swimming/announcement/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询系统公告详细
 * @param id
 */
export const getAnnouncement = (id: string | number): AxiosPromise<AnnouncementVO> => {
  return request({
    url: '/swimming/announcement/' + id,
    method: 'get'
  });
};

/**
 * 新增系统公告
 * @param data
 */
export const addAnnouncement = (data: AnnouncementForm) => {
  return request({
    url: '/swimming/announcement',
    method: 'post',
    data: data
  });
};

/**
 * 修改系统公告
 * @param data
 */
export const updateAnnouncement = (data: AnnouncementForm) => {
  return request({
    url: '/swimming/announcement',
    method: 'put',
    data: data
  });
};

/**
 * 删除系统公告
 * @param id
 */
export const delAnnouncement = (id: string | number | Array<string | number>) => {
  return request({
    url: '/swimming/announcement/' + id,
    method: 'delete'
  });
};
