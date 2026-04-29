package org.dromara.swimming.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.swimming.domain.SpIotDeviceData;
import org.dromara.swimming.domain.vo.SpIotDeviceDataVo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * IoT设备数据Mapper接口
 *
 * @author swimming
 * @date 2026-02-08
 */
public interface SpIotDeviceDataMapper extends BaseMapperPlus<SpIotDeviceData, SpIotDeviceDataVo> {

    /**
     * 查询设备最新数据
     *
     * @param deviceKey 设备key
     * @return 最新数据
     */
    SpIotDeviceDataVo getLatestData(@Param("deviceKey") String deviceKey);

    /**
     * 查询设备历史数据(分页)
     *
     * @param page 分页参数
     * @param deviceKey 设备key
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 历史数据列表
     */
    IPage<SpIotDeviceDataVo> queryHistoryData(
        Page<SpIotDeviceData> page,
        @Param("deviceKey") String deviceKey,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 批量插入设备数据
     *
     * @param dataList 数据列表
     * @return 插入条数
     */
    int batchInsert(@Param("dataList") List<SpIotDeviceData> dataList);
}
