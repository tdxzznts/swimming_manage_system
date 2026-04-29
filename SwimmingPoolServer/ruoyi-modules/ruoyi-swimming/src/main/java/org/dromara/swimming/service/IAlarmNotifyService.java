package org.dromara.swimming.service;

import org.dromara.swimming.domain.SpIotDeviceAlarm;

/**
 * 告警通知服务
 *
 * @author swimming
 * @date 2026-02-09
 */
public interface IAlarmNotifyService {

    /**
     * 发送告警通知（SSE推送 + 邮件）
     *
     * @param alarm 告警信息
     */
    void sendAlarmNotification(SpIotDeviceAlarm alarm);
}
