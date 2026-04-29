package org.dromara.swimming.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.swimming.domain.SpIotAlarmRule;
import org.dromara.swimming.domain.vo.SpIotAlarmRuleVo;

import java.util.List;

/**
 * IoT设备告警规则Mapper接口
 *
 * @author swimming
 * @date 2026-02-08
 */
public interface SpIotAlarmRuleMapper extends BaseMapperPlus<SpIotAlarmRule, SpIotAlarmRuleVo> {

    /**
     * 根据设备key查询启用的告警规则
     *
     * @param deviceKey 设备key
     * @return 告警规则列表
     */
    List<SpIotAlarmRule> selectEnabledRulesByDeviceKey(String deviceKey);
}
