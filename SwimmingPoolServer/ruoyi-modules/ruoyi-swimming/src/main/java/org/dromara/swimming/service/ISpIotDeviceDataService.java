package org.dromara.swimming.service;

import org.dromara.swimming.domain.SpIotDeviceData;
import org.dromara.swimming.domain.vo.SpIotDeviceDataVo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.List;
import java.util.Map;

/**
 * IoT设备数据Service接口
 *
 * @author swimming
 * @date 2026-02-08
 */
public interface ISpIotDeviceDataService {

    /**
     * 保存设备数据
     *
     * @param deviceData 设备数据
     * @return 是否保存成功
     */
    Boolean save(SpIotDeviceData deviceData);

    /**
     * 批量保存设备数据
     *
     * @param deviceKey 设备key
     * @param records    数据记录列表
     * @return 保存条数
     */
    Integer batchSave(String deviceKey, List<Map<String, Object>> records);

    /**
     * 获取设备最新数据
     *
     * @param deviceKey 设备key
     * @return 最新数据
     */
    SpIotDeviceDataVo getLatestData(String deviceKey);

    /**
     * 查询设备历史数据(分页)
     *
     * @param deviceKey  设备key
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageQuery  分页参数
     * @return 历史数据列表
     */
    List<SpIotDeviceDataVo> queryHistoryData(String deviceKey, String startTime, String endTime, PageQuery pageQuery);

    /**
     * 获取设备数据统计
     *
     * @param deviceKey 设备key
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    Map<String, Object> getDataStatistics(String deviceKey, String startTime, String endTime);
}
