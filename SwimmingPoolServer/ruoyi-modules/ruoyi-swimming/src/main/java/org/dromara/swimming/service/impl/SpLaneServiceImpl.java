package org.dromara.swimming.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.swimming.domain.bo.SpLaneBo;
import org.dromara.swimming.domain.vo.SpLaneVo;
import org.dromara.swimming.domain.SpLane;
import org.dromara.swimming.mapper.SpLaneMapper;
import org.dromara.swimming.service.ISpLaneService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 泳道Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpLaneServiceImpl implements ISpLaneService {

    private final SpLaneMapper baseMapper;

    /**
     * 查询泳道
     *
     * @param id 主键
     * @return 泳道
     */
    @Override
    public SpLaneVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询泳道列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 泳道分页列表
     */
    @Override
    public TableDataInfo<SpLaneVo> queryPageList(SpLaneBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpLane> lqw = buildQueryWrapper(bo);
        Page<SpLaneVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的泳道列表
     *
     * @param bo 查询条件
     * @return 泳道列表
     */
    @Override
    public List<SpLaneVo> queryList(SpLaneBo bo) {
        LambdaQueryWrapper<SpLane> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpLane> buildQueryWrapper(SpLaneBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpLane> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SpLane::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getLaneNo()), SpLane::getLaneNo, bo.getLaneNo());
        lqw.eq(StringUtils.isNotBlank(bo.getLaneType()), SpLane::getLaneType, bo.getLaneType());
        lqw.eq(bo.getCapacity() != null, SpLane::getCapacity, bo.getCapacity());
        lqw.eq(bo.getLength() != null, SpLane::getLength, bo.getLength());
        lqw.eq(bo.getWidth() != null, SpLane::getWidth, bo.getWidth());
        lqw.eq(bo.getDepth() != null, SpLane::getDepth, bo.getDepth());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpLane::getStatus, bo.getStatus());
        lqw.eq(bo.getSort() != null, SpLane::getSort, bo.getSort());
        return lqw;
    }

    /**
     * 新增泳道
     *
     * @param bo 泳道
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpLaneBo bo) {
        SpLane add = MapstructUtils.convert(bo, SpLane.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改泳道
     *
     * @param bo 泳道
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpLaneBo bo) {
        SpLane update = MapstructUtils.convert(bo, SpLane.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpLane entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除泳道信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
