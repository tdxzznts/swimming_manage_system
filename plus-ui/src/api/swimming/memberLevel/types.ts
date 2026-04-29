export interface MemberLevelVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 等级编码(normal/silver/gold/diamond)
   */
  levelCode: string;

  /**
   * 等级名称
   */
  levelName: string;

  /**
   * 英文名称
   */
  levelEn: string;

  /**
   * 等级值(0-3)
   */
  levelValue: number;

  /**
   * 折扣比例(如95表示95折)
   */
  discount: number;

  /**
   * 升级阈值
   */
  price: number;

  /**
   * 卡片渐变起始色
   */
  cardColorStart: string;

  /**
   * 卡片渐变结束色
   */
  cardColorEnd: string;

  /**
   * 等级描述
   */
  description: string;

  /**
   * 状态(0正常 1停用)
   */
  status: string;

  /**
   * 排序
   */
  sortOrder: number;

  /**
   * 备注
   */
  remark: string;

}

export interface MemberLevelForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 等级编码(normal/silver/gold/diamond)
   */
  levelCode?: string;

  /**
   * 等级名称
   */
  levelName?: string;

  /**
   * 英文名称
   */
  levelEn?: string;

  /**
   * 等级值(0-3)
   */
  levelValue?: number;

  /**
   * 折扣比例(如95表示95折)
   */
  discount?: number;

  /**
   * 升级阈值
   */
  price?: number;

  /**
   * 卡片渐变起始色
   */
  cardColorStart?: string;

  /**
   * 卡片渐变结束色
   */
  cardColorEnd?: string;

  /**
   * 等级描述
   */
  description?: string;

  /**
   * 状态(0正常 1停用)
   */
  status?: string;

  /**
   * 排序
   */
  sortOrder?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface MemberLevelQuery extends PageQuery {

  /**
   * 等级编码(normal/silver/gold/diamond)
   */
  levelCode?: string;

  /**
   * 等级名称
   */
  levelName?: string;

  /**
   * 英文名称
   */
  levelEn?: string;

  /**
   * 等级值(0-3)
   */
  levelValue?: number;

  /**
   * 折扣比例(如95表示95折)
   */
  discount?: number;

  /**
   * 升级阈值
   */
  price?: number;

  /**
   * 卡片渐变起始色
   */
  cardColorStart?: string;

  /**
   * 卡片渐变结束色
   */
  cardColorEnd?: string;

  /**
   * 等级描述
   */
  description?: string;

  /**
   * 状态(0正常 1停用)
   */
  status?: string;

  /**
   * 排序
   */
  sortOrder?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}
