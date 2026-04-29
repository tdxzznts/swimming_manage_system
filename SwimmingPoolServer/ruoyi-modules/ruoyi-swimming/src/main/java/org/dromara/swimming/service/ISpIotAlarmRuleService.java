package org.dromara.swimming.service;

import org.dromara.swimming.domain.bo.SpIotAlarmRuleBo;
import org.dromara.swimming.domain.SpIotAlarmRule;
import org.dromara.swimming.domain.vo.SpIotAlarmRuleVo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * IoT设备告警规则Service接口
 *
 * @author swimming
 * @date 2026-02-08
 */
public interface ISpIotAlarmRuleService {

    /**
     * 查询IoT设备告警规则
     *
     * @param id 主键
     * @return IoT设备告警规则
     */
    SpIotAlarmRuleVo queryById(Long id);

    /**
     * 查询IoT设备告警规则列表
     *
     * @param bo 查询条件
     * @return IoT设备告警规则列表
     */
    List<SpIotAlarmRuleVo> queryList(SpIotAlarmRuleBo bo);

    /**
     * 分页查询IoT设备告警规则列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return IoT设备告警规则分页列表
     */
    List<SpIotAlarmRuleVo> queryPageList(SpIotAlarmRuleBo bo, PageQuery pageQuery);

    /**
     * 新增IoT设备告警规则
     *
     * @param bo IoT设备告警规则
     * @return 是否新增成功
     */
    Boolean insertByBo(SpIotAlarmRuleBo bo);

    /**
     * 修改IoT设备告警规则
     *
     * @param bo IoT设备告警规则
     * @return 是否修改成功
     */
    Boolean updateByBo(SpIotAlarmRuleBo bo);

    /**
     * 校验并批量删除IoT设备告警规则信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据设备key查询启用的告警规则
     *
     * @param deviceKey 设备key
     * @return 告警规则列表
     */
    List<SpIotAlarmRule> getEnabledRulesByDeviceKey(String deviceKey);
}
