export interface CarouselVO {
  /**
   * 标题
   */
  title: string;

  /**
   * 图片URL
   */
  imageUrl: string;

  /**
   * 排序号（数值越小越靠前）
   */
  sortOrder: number;

  /**
   * 状态（0停用 1启用）
   */
  status: string;

  /**
   * 开始时间
   */
  startTime: string;

  /**
   * 结束时间
   */
  endTime: string;

  /**
   * 备注
   */
  remark: string;

}

export interface CarouselForm extends BaseEntity {
  /**
   * 标题
   */
  title?: string;

  /**
   * 图片URL
   */
  imageUrl?: string;

  /**
   * 排序号（数值越小越靠前）
   */
  sortOrder?: number;

  /**
   * 状态（0停用 1启用）
   */
  status?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface CarouselQuery extends PageQuery {

  /**
   * 标题
   */
  title?: string;

  /**
   * 图片URL
   */
  imageUrl?: string;

  /**
   * 排序号（数值越小越靠前）
   */
  sortOrder?: number;

  /**
   * 状态（0停用 1启用）
   */
  status?: string;

  /**
   * 开始时间
   */
  startTime?: string;

  /**
   * 结束时间
   */
  endTime?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
