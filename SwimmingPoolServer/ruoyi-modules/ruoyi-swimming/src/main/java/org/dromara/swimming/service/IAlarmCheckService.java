package org.dromara.swimming.service;

import cn.hutool.json.JSONObject;
import java.util.Map;

/**
 * 告警检查服务
 *
 * @author swimming
 * @date 2026-02-08
 */
public interface IAlarmCheckService {

    /**
     * 检查设备数据并触发告警
     *
     * @param deviceKey 设备key
     * @param data 设备上报的数据
     */
    void checkAndTriggerAlarm(String deviceKey, Map<String, Object> data);
}
