export interface MemberCardVO {
  /**
   * 会员卡ID
   */
  id: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId: string | number;

  /**
   * 用户名
   */
  userName?: string;

  /**
   * 会员卡号
   */
  cardNo: string;

  /**
   * 会员等级（0普通 1银卡 2金卡 3钻石）
   */
  cardLevel: string;

  /**
   * 余额
   */
  balance: number;

  /**
   * 累计充值金额
   */
  totalRecharge: number;

  /**
   * 累计消费金额
   */
  totalConsume: number;

  /**
   * 积分余额
   */
  totalPoint: number;

  /**
   * 免费次数
   */
  freeTimes: number;

  /**
   * 发卡日期
   */
  issueDate: string;

  /**
   * 到期日期
   */
  expiryDate: string;

  /**
   * 状态（0正常 1挂失 2冻结）
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

}

export interface MemberCardForm extends BaseEntity {
  /**
   * 会员卡ID
   */
  id?: string | number;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 会员卡号
   */
  cardNo?: string;

  /**
   * 会员等级（0普通 1银卡 2金卡 3钻石）
   */
  cardLevel?: string;

  /**
   * 余额
   */
  balance?: number;

  /**
   * 累计充值金额
   */
  totalRecharge?: number;

  /**
   * 累计消费金额
   */
  totalConsume?: number;

  /**
   * 积分余额
   */
  totalPoint?: number;

  /**
   * 免费次数
   */
  freeTimes?: number;

  /**
   * 发卡日期
   */
  issueDate?: string;

  /**
   * 到期日期
   */
  expiryDate?: string;

  /**
   * 状态（0正常 1挂失 2冻结）
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface MemberCardQuery extends PageQuery {

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId?: string | number;

  /**
   * 用户名
   */
  userName?: string;

  /**
   * 会员卡号
   */
  cardNo?: string;

  /**
   * 会员等级（0普通 1银卡 2金卡 3钻石）
   */
  cardLevel?: string;

  /**
   * 余额
   */
  balance?: number;

  /**
   * 累计充值金额
   */
  totalRecharge?: number;

  /**
   * 累计消费金额
   */
  totalConsume?: number;

  /**
   * 积分余额
   */
  totalPoint?: number;

  /**
   * 免费次数
   */
  freeTimes?: number;

  /**
   * 发卡日期
   */
  issueDate?: string;

  /**
   * 到期日期
   */
  expiryDate?: string;

  /**
   * 状态（0正常 1挂失 2冻结）
   */
  status?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
