export interface AnnouncementVO {
  /**
   * 公告ID
   */
  id: string | number;

  /**
   * 公告标题
   */
  title: string;

  /**
   * 公告内容
   */
  content: string;

  /**
   * 公告类型（1系统公告 2活动通知 3紧急通知）
   */
  announcementType: string;

  /**
   * 发布时间
   */
  publishTime: string;

  /**
   * 状态（0草稿 1已发布）
   */
  status: string;

  /**
   * 浏览次数
   */
  viewCount: number;

  /**
   * 备注
   */
  remark: string;

}

export interface AnnouncementForm extends BaseEntity {
  /**
   * 公告ID
   */
  id?: string | number;

  /**
   * 公告标题
   */
  title?: string;

  /**
   * 公告内容
   */
  content?: string;

  /**
   * 公告类型（1系统公告 2活动通知 3紧急通知）
   */
  announcementType?: string;

  /**
   * 发布时间
   */
  publishTime?: string;

  /**
   * 状态（0草稿 1已发布）
   */
  status?: string;

  /**
   * 浏览次数
   */
  viewCount?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface AnnouncementQuery extends PageQuery {

  /**
   * 公告标题
   */
  title?: string;

  /**
   * 公告内容
   */
  content?: string;

  /**
   * 公告类型（1系统公告 2活动通知 3紧急通知）
   */
  announcementType?: string;

  /**
   * 发布时间
   */
  publishTime?: string;

  /**
   * 状态（0草稿 1已发布）
   */
  status?: string;

  /**
   * 浏览次数
   */
  viewCount?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}
