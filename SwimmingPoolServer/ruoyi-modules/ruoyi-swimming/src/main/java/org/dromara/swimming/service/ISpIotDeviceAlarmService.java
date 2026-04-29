package org.dromara.swimming.service;

import org.dromara.swimming.domain.bo.SpIotDeviceAlarmBo;
import org.dromara.swimming.domain.vo.SpIotDeviceAlarmVo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IoT设备告警Service接口
 *
 * @author swimming
 * @date 2026-02-08
 */
public interface ISpIotDeviceAlarmService {

    /**
     * 查询IoT设备告警
     *
     * @param id 主键
     * @return IoT设备告警
     */
    SpIotDeviceAlarmVo queryById(Long id);

    /**
     * 查询IoT设备告警列表
     *
     * @param bo 查询条件
     * @return IoT设备告警列表
     */
    List<SpIotDeviceAlarmVo> queryList(SpIotDeviceAlarmBo bo);

    /**
     * 分页查询IoT设备告警列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return IoT设备告警分页列表
     */
    List<SpIotDeviceAlarmVo> queryPageList(SpIotDeviceAlarmBo bo, PageQuery pageQuery);

    /**
     * 新增IoT设备告警
     *
     * @param bo IoT设备告警
     * @return 是否新增成功
     */
    Boolean insertByBo(SpIotDeviceAlarmBo bo);

    /**
     * 修改IoT设备告警
     *
     * @param bo IoT设备告警
     * @return 是否修改成功
     */
    Boolean updateByBo(SpIotDeviceAlarmBo bo);

    /**
     * 校验并批量删除IoT设备告警信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 创建告警
     *
     * @param deviceKey 设备key
     * @param alarmType 告警类型
     * @param alarmLevel 告警级别
     * @param alarmTitle 告警标题
     * @param alarmMessage 告警内容
     * @param alarmData 告警数据
     * @return 是否创建成功
     */
    Boolean createAlarm(String deviceKey, String alarmType, String alarmLevel,
                       String alarmTitle, String alarmMessage, String alarmData);

    /**
     * 创建告警（实体对象）
     *
     * @param alarm 告警实体
     * @return 是否创建成功
     */
    Boolean createAlarm(org.dromara.swimming.domain.SpIotDeviceAlarm alarm);

    /**
     * 处理告警
     *
     * @param alarmId 告警ID
     * @param handleResult 处理结果
     * @return 是否处理成功
     */
    Boolean handleAlarm(Long alarmId, String handleResult);

    /**
     * 批量处理告警
     *
     * @param alarmIds 告警ID集合
     * @param handleResult 处理结果
     * @return 处理数量
     */
    Integer batchHandleAlarms(Collection<Long> alarmIds, String handleResult);

    /**
     * 获取告警统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getAlarmStatistics();

    /**
     * 检查数据阈值并创建告警
     *
     * @param deviceKey 设备key
     * @param params 数据参数
     */
    void checkThresholdAndCreateAlarm(String deviceKey, Map<String, Object> params);
}
