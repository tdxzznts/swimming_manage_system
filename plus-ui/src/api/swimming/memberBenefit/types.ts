export interface MemberBenefitVO {
  /**
   * 主键ID
   */
  id: string | number;

  /**
   * 权益编码
   */
  benefitCode: string;

  /**
   * 权益名称
   */
  benefitName: string;

  /**
   * 权益描述
   */
  benefitDesc: string;

  /**
   * 图标名称
   */
  iconName: string;

  /**
   * 权益值(如"95折","3天")
   */
  benefitValue: string;

  /**
   * 标签类型(success/warning/danger/info)
   */
  tagType: string;

  /**
   * 适用等级(1、2、3)
   */
  levelValue: string;

  /**
   * 排序
   */
  sortOrder: number;

  /**
   * 状态(0正常 1停用)
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

}

export interface MemberBenefitForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 权益编码
   */
  benefitCode?: string;

  /**
   * 权益名称
   */
  benefitName?: string;

  /**
   * 权益描述
   */
  benefitDesc?: string;

  /**
   * 图标名称
   */
  iconName?: string;

  /**
   * 权益值(如"95折","3天")
   */
  benefitValue?: string;

  /**
   * 标签类型(success/warning/danger/info)
   */
  tagType?: string;

  /**
   * 适用等级(1、2、3)
   */
  levelValue?: string;

  /**
   * 排序
   */
  sortOrder?: number;

  /**
   * 状态(0正常 1停用)
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface MemberBenefitQuery extends PageQuery {

  /**
   * 权益编码
   */
  benefitCode?: string;

  /**
   * 权益名称
   */
  benefitName?: string;

  /**
   * 权益描述
   */
  benefitDesc?: string;

  /**
   * 图标名称
   */
  iconName?: string;

  /**
   * 权益值(如"95折","3天")
   */
  benefitValue?: string;

  /**
   * 标签类型(success/warning/danger/info)
   */
  tagType?: string;

  /**
   * 适用等级(1、2、3)
   */
  levelValue?: string;

  /**
   * 排序
   */
  sortOrder?: number;

  /**
   * 状态(0正常 1停用)
   */
  status?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
