package org.dromara.swimming.service;

import org.dromara.swimming.domain.bo.SpIotDeviceBo;
import org.dromara.swimming.domain.vo.SpIotDeviceVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IoT设备Service接口
 *
 * @author swimming
 * @date 2026-02-08
 */
public interface ISpIotDeviceService {

    /**
     * 查询IoT设备
     *
     * @param id 主键
     * @return IoT设备
     */
    SpIotDeviceVo queryById(Long id);

    /**
     * 根据deviceKey查询设备
     *
     * @param deviceKey 设备key
     * @return IoT设备
     */
    SpIotDeviceVo getByDeviceKey(String deviceKey);

    /**
     * 根据deviceKey查询设备实体
     *
     * @param deviceKey 设备key
     * @return IoT设备实体
     */
    org.dromara.swimming.domain.SpIotDevice getDeviceEntityByDeviceKey(String deviceKey);

    /**
     * 根据productKey查询产品
     *
     * @param productKey 产品key
     * @return IoT产品
     */
    SpIotDeviceVo getByProductKey(String productKey);

    /**
     * 查询IoT设备列表
     *
     * @param bo 查询条件
     * @return IoT设备列表
     */
    List<SpIotDeviceVo> queryList(SpIotDeviceBo bo);

    /**
     * 分页查询IoT设备列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return IoT设备分页列表
     */
    TableDataInfo<SpIotDeviceVo> queryPageList(SpIotDeviceBo bo, PageQuery pageQuery);

    /**
     * 新增IoT设备
     *
     * @param bo IoT设备
     * @return 是否新增成功
     */
    Boolean insertByBo(SpIotDeviceBo bo);

    /**
     * 修改IoT设备
     *
     * @param bo IoT设备
     * @return 是否修改成功
     */
    Boolean updateByBo(SpIotDeviceBo bo);

    /**
     * 校验并批量删除IoT设备信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 设备注册或更新
     *
     * @param bo 设备信息
     * @return 设备信息
     */
    SpIotDeviceBo registerOrUpdateDevice(SpIotDeviceBo bo);

    /**
     * 更新设备在线状态
     *
     * @param deviceKey 设备key
     * @param online    是否在线
     * @return 是否更新成功
     */
    Boolean updateOnlineStatus(String deviceKey, Boolean online);

    /**
     * 批量更新设备为离线状态
     * 用于定时任务检测设备心跳超时
     *
     * @param timeoutMinutes 超时分钟数
     * @return 更新数量
     */
    Integer batchUpdateOfflineStatus(Integer timeoutMinutes);

    /**
     * 更新设备影子
     *
     * @param deviceKey 设备key
     * @param shadow    设备影子
     * @return 是否更新成功
     */
    Boolean updateDeviceShadow(String deviceKey, String shadow);

    /**
     * 获取设备统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getDeviceStatistics();

    /**
     * 更新设备信息
     *
     * @param device 设备实体
     * @return 是否更新成功
     */
    Boolean updateById(org.dromara.swimming.domain.SpIotDevice device);
}
