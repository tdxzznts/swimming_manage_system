export interface OrderVO {
  /**
   * 订单ID
   */
  id: string | number;

  /**
   * 订单号
   */
  orderNo: string;

  /**
   * 用户ID（关联sys_user.user_id）
   */
  userId: string | number;

  /**
   * 用户名
   */
  userName?: string;

  /**
   * 预约ID（关联sp_reservation.id）
   */
  reservationId: string | number;

  /**
   * 订单类型（1预约 2充值 3积分）
   */
  orderType: string;

  /**
   * 订单金额（元）
   */
  amount: number;

  /**
   * 优惠金额（元）
   */
  discountAmount: number;

  /**
   * 实际支付金额（元）
   */
  actualAmount: number;

  /**
   * 获得积分（元）
   */
  getPoint: number;

  /**
   * 支付方式（1支付宝 2余额）
   */
  payType: string;

  /**
   * 支付状态（0待支付 1已支付 2支付失败 3已退款）
   */
  payStatus: string;

  /**
   * 支付时间
   */
  payTime: string;

  /**
   * 第三方交易号
   */
  tradeNo?: string;

  /**
   * 第三方订单号
   */
  thirdPartyNo?: string;

  /**
   * 退款金额（元）
   */
  refundAmount: number;

  /**
   * 退款时间
   */
  refundTime: string;

  /**
   * 退款原因
   */
  refundReason: string;

  /**
   * 订单状态（0待支付 1已支付 2已完成 3已取消 4已退款）
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

}

export interface OrderForm extends BaseEntity {
  /**
   * 订单ID
   */
  id?: string | number;

  /**
   * 订单号
   */
  orderNo?: string;

  /**
   * 订单类型（1预约 2充值 3积分）
   */
  orderType?: string;

  /**
   * 订单金额（元）
   */
  amount?: number;

  /**
   * 优惠金额（元）
   */
  discountAmount?: number;

  /**
   * 实际支付金额（元）
   */
  actualAmount?: number;

  /**
   * 获得积分（元）
   */
  getPoint?: number;

  /**
   * 支付方式（1支付宝 2余额）
   */
  payType?: string;

  /**
   * 支付状态（0待支付 1已支付 2支付失败 3已退款）
   */
  payStatus?: string;

  /**
   * 支付时间
   */
  payTime?: string;

  /**
   * 退款金额（元）
   */
  refundAmount?: number;

  /**
   * 退款时间
   */
  refundTime?: string;

  /**
   * 退款原因
   */
  refundReason?: string;

  /**
   * 订单状态（0待支付 1已支付 2已完成 3已取消 4已退款）
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface OrderQuery extends PageQuery {

  /**
   * 订单号
   */
  orderNo?: string;

  /**
   * 用户名
   */
  userName?: string;

  /**
   * 订单类型（1预约 2充值 3积分）
   */
  orderType?: string;

  /**
   * 支付方式（1支付宝 2余额）
   */
  payType?: string;

  /**
   * 支付状态（0待支付 1已支付 2支付失败 3已退款）
   */
  payStatus?: string;

  /**
   * 订单状态（0待支付 1已支付 2已完成 3已取消 4已退款）
   */
  status?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
