import { request } from '@/utils/request'
import type { R } from '@/types/user'

/**
 * 会员等级VO
 */
export interface MemberLevel {
  id: number
  type: string
  name: string
  desc: string
  price: number
  levelEn: string
}

/**
 * 会员权益VO
 */
export interface MemberBenefit {
  id: number
  benefitCode: string
  levelValue: number
  icon: string
  name: string
  desc: string
  value: string
  tagType: string
}

/**
 * 会员信息VO
 */
export interface MemberInfo {
  level: number
  levelName: string
  levelEn: string
  cardNo: string
  userName: string
  balance: number
  points: number
  validThru: string
  priorityBookingDays: number
}

/**
 * 会员规则VO
 */
export interface MemberRules {
  levels: MemberLevel[]
  benefits: MemberBenefit[]
  ruleDescription: string
}

/**
 * 获取所有会员等级
 */
export function getMemberLevels(): Promise<R<MemberLevel[]>> {
  return request.get<R<MemberLevel[]>>('/h5/member/config/levels')
}

/**
 * 获取指定等级的会员权益
 */
export function getMemberBenefits(levelCode: string): Promise<R<MemberBenefit[]>> {
  return request.get<R<MemberBenefit[]>>(`/h5/member/config/benefits/${levelCode}`)
}

/**
 * 获取当前用户会员信息
 */
export function getMemberInfo(): Promise<R<MemberInfo>> {
  return request.get<R<MemberInfo>>('/h5/member/config/info')
}

/**
 * 获取会员规则
 */
export function getMemberRules(): Promise<R<MemberRules>> {
  return request.get<R<MemberRules>>('/h5/member/config/rules')
}
