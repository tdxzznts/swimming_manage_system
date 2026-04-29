export interface LaneVO {
  /**
   * 泳道ID
   */
  id: string | number;

  /**
   * 泳道编号
   */
  laneNo: string;

  /**
   * 泳道类型（1快速 2中速 3慢速）
   */
  laneType: string;

  /**
   * 容量（人数）
   */
  capacity: number;

  /**
   * 泳道长度（米）
   */
  length: number;

  /**
   * 泳道宽度（米）
   */
  width: string | number;

  /**
   * 泳道深度（米）
   */
  depth: number;

  /**
   * 状态（0开放 1关闭 2维修）
   */
  status: string;

  /**
   * 排序
   */
  sort: number;

  /**
   * 备注
   */
  remark: string;

}

export interface LaneForm extends BaseEntity {
  /**
   * 泳道ID
   */
  id?: string | number;

  /**
   * 泳道编号
   */
  laneNo?: string;

  /**
   * 泳道类型（1快速 2中速 3慢速）
   */
  laneType?: string;

  /**
   * 容量（人数）
   */
  capacity?: number;

  /**
   * 泳道长度（米）
   */
  length?: number;

  /**
   * 泳道宽度（米）
   */
  width?: string | number;

  /**
   * 泳道深度（米）
   */
  depth?: number;

  /**
   * 状态（0开放 1关闭 2维修）
   */
  status?: string;

  /**
   * 排序
   */
  sort?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface LaneQuery extends PageQuery {

  /**
   * 泳道编号
   */
  laneNo?: string;

  /**
   * 泳道类型（1快速 2中速 3慢速）
   */
  laneType?: string;

  /**
   * 容量（人数）
   */
  capacity?: number;

  /**
   * 泳道长度（米）
   */
  length?: number;

  /**
   * 泳道宽度（米）
   */
  width?: string | number;

  /**
   * 泳道深度（米）
   */
  depth?: number;

  /**
   * 状态（0开放 1关闭 2维修）
   */
  status?: string;

  /**
   * 排序
   */
  sort?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}
