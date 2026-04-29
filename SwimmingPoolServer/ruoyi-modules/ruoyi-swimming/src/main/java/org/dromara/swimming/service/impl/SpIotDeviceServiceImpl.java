package org.dromara.swimming.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.swimming.domain.SpIotDevice;
import org.dromara.swimming.domain.bo.SpIotDeviceBo;
import org.dromara.swimming.domain.vo.SpIotDeviceVo;
import org.dromara.swimming.mapper.SpIotDeviceMapper;
import org.dromara.swimming.service.ISpIotDeviceService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * IoT设备Service业务层处理
 *
 * @author swimming
 * @date 2026-02-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpIotDeviceServiceImpl implements ISpIotDeviceService {

    private final SpIotDeviceMapper baseMapper;

    @Override
    public SpIotDeviceVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public SpIotDeviceVo getByDeviceKey(String deviceKey) {
        LambdaQueryWrapper<SpIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SpIotDevice::getDeviceKey, deviceKey);
        return baseMapper.selectVoOne(lqw);
    }

    @Override
    public SpIotDevice getDeviceEntityByDeviceKey(String deviceKey) {
        LambdaQueryWrapper<SpIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SpIotDevice::getDeviceKey, deviceKey);
        return baseMapper.selectOne(lqw);
    }

    @Override
    public SpIotDeviceVo getByProductKey(String productKey) {
        LambdaQueryWrapper<SpIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SpIotDevice::getProductKey, productKey);
        lqw.last("LIMIT 1");
        return baseMapper.selectVoOne(lqw);
    }

    @Override
    public List<SpIotDeviceVo> queryList(SpIotDeviceBo bo) {
        LambdaQueryWrapper<SpIotDevice> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(SpIotDevice::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public TableDataInfo<SpIotDeviceVo> queryPageList(SpIotDeviceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpIotDevice> lqw = buildQueryWrapper(bo);
        Page<SpIotDevice> page = pageQuery.build();
        Page<SpIotDeviceVo> result = baseMapper.selectVoPage(page, lqw);
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<SpIotDevice> buildQueryWrapper(SpIotDeviceBo bo) {
        LambdaQueryWrapper<SpIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getId() != null, SpIotDevice::getId, bo.getId());
        lqw.eq(StringUtils.isNotBlank(bo.getProductKey()), SpIotDevice::getProductKey, bo.getProductKey());
        lqw.like(StringUtils.isNotBlank(bo.getDeviceName()), SpIotDevice::getDeviceName, bo.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceKey()), SpIotDevice::getDeviceKey, bo.getDeviceKey());
        lqw.eq(StringUtils.isNotBlank(bo.getMac()), SpIotDevice::getMac, bo.getMac());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), SpIotDevice::getDeviceType, bo.getDeviceType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpIotDevice::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getParentDeviceKey()), SpIotDevice::getParentDeviceKey, bo.getParentDeviceKey());
        return lqw;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SpIotDeviceBo bo) {
        SpIotDevice add = MapstructUtils.convert(bo, SpIotDevice.class);

        // 自动生成device_key: productKey.deviceName
        if (StringUtils.isBlank(add.getDeviceKey())) {
            String deviceKey = add.getProductKey() + "." + add.getDeviceName();
            add.setDeviceKey(deviceKey);
        }

        // 设置统一的device_secret（提示使用配置文件中的统一认证）
        if (StringUtils.isBlank(add.getDeviceSecret())) {
            // device_secret不再生成随机值，而是指向配置文件的统一认证
            add.setDeviceSecret("使用MQTT配置文件中的统一认证");
        }

        // 设置激活时间为当前时间
        if (add.getActiveTime() == null) {
            add.setActiveTime(LocalDateTime.now());
        }

        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 生成设备密钥
     */
    private String generateDeviceSecret(String deviceKey) {
        // 使用UUID + deviceKey生成密钥，确保唯一性
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return deviceKey + "_" + uuid.substring(0, 16);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SpIotDeviceBo bo) {
        SpIotDevice update = MapstructUtils.convert(bo, SpIotDevice.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpIotDevice entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpIotDeviceBo registerOrUpdateDevice(SpIotDeviceBo bo) {
        // 检查设备是否已存在
        SpIotDeviceVo existingDevice = getByDeviceKey(bo.getDeviceKey());

        if (existingDevice != null) {
            // 更新设备信息
            bo.setId(existingDevice.getId());
            updateByBo(bo);
            log.info("设备信息已更新: deviceKey={}", bo.getDeviceKey());
        } else {
            // 新建设备
            insertByBo(bo);
            log.info("新设备已注册: deviceKey={}", bo.getDeviceKey());
        }

        return bo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOnlineStatus(String deviceKey, Boolean online) {
        LambdaQueryWrapper<SpIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SpIotDevice::getDeviceKey, deviceKey);

        SpIotDevice device = new SpIotDevice();
        device.setStatus(online ? "1" : "0"); // 1在线 0离线
        device.setLastOnlineTime(LocalDateTime.now());

        return baseMapper.update(device, lqw) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchUpdateOfflineStatus(Integer timeoutMinutes) {
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(timeoutMinutes);

        LambdaQueryWrapper<SpIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SpIotDevice::getStatus, "1"); // 在线设备
        lqw.lt(SpIotDevice::getLastOnlineTime, timeout); // 最后在线时间超时

        SpIotDevice device = new SpIotDevice();
        device.setStatus("0"); // 标记为离线

        return baseMapper.update(device, lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDeviceShadow(String deviceKey, String shadow) {
        LambdaQueryWrapper<SpIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SpIotDevice::getDeviceKey, deviceKey);

        SpIotDevice device = new SpIotDevice();
        device.setShadow(shadow);

        return baseMapper.update(device, lqw) > 0;
    }

    @Override
    public Map<String, Object> getDeviceStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总设备数
        Long totalCount = baseMapper.selectCount(null);

        // 在线设备数
        LambdaQueryWrapper<SpIotDevice> onlineLqw = Wrappers.lambdaQuery();
        onlineLqw.eq(SpIotDevice::getStatus, "1");
        Long onlineCount = baseMapper.selectCount(onlineLqw);

        // 离线设备数
        LambdaQueryWrapper<SpIotDevice> offlineLqw = Wrappers.lambdaQuery();
        offlineLqw.eq(SpIotDevice::getStatus, "0");
        Long offlineCount = baseMapper.selectCount(offlineLqw);

        // 今日新增设备数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<SpIotDevice> todayLqw = Wrappers.lambdaQuery();
        todayLqw.ge(SpIotDevice::getCreateTime, todayStart);
        Long todayCount = baseMapper.selectCount(todayLqw);

        statistics.put("totalCount", totalCount);
        statistics.put("onlineCount", onlineCount);
        statistics.put("offlineCount", offlineCount);
        statistics.put("todayCount", todayCount);

        // 在线率
        if (totalCount > 0) {
            Double onlineRate = (onlineCount * 100.0) / totalCount;
            statistics.put("onlineRate", String.format("%.2f", onlineRate) + "%");
        } else {
            statistics.put("onlineRate", "0.00%");
        }

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateById(SpIotDevice device) {
        return baseMapper.updateById(device) > 0;
    }
}
