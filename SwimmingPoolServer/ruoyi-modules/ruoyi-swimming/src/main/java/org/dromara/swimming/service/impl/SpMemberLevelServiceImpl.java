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
import org.springframework.transaction.annotation.Transactional;
import org.dromara.swimming.domain.bo.SpMemberLevelBo;
import org.dromara.swimming.domain.vo.SpMemberLevelVo;
import org.dromara.swimming.domain.SpMemberLevel;
import org.dromara.swimming.mapper.SpMemberLevelMapper;
import org.dromara.swimming.service.ISpMemberLevelService;
import org.dromara.system.domain.SysDictData;
import org.dromara.system.domain.bo.SysDictDataBo;
import org.dromara.system.mapper.SysDictDataMapper;
import org.dromara.system.service.ISysDictDataService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 会员等级配置Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpMemberLevelServiceImpl implements ISpMemberLevelService {

    private final SpMemberLevelMapper baseMapper;
    private final SysDictDataMapper sysDictDataMapper;
    private final ISysDictDataService sysDictDataService;

    /**
     * 会员等级字典类型
     */
    private static final String DICT_TYPE = "sp_member_level";

    /**
     * 查询会员等级配置
     *
     * @param id 主键
     * @return 会员等级配置
     */
    @Override
    public SpMemberLevelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员等级配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员等级配置分页列表
     */
    @Override
    public TableDataInfo<SpMemberLevelVo> queryPageList(SpMemberLevelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpMemberLevel> lqw = buildQueryWrapper(bo);
        Page<SpMemberLevelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员等级配置列表
     *
     * @param bo 查询条件
     * @return 会员等级配置列表
     */
    @Override
    public List<SpMemberLevelVo> queryList(SpMemberLevelBo bo) {
        LambdaQueryWrapper<SpMemberLevel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpMemberLevel> buildQueryWrapper(SpMemberLevelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpMemberLevel> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SpMemberLevel::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getLevelCode()), SpMemberLevel::getLevelCode, bo.getLevelCode());
        lqw.like(StringUtils.isNotBlank(bo.getLevelName()), SpMemberLevel::getLevelName, bo.getLevelName());
        lqw.eq(StringUtils.isNotBlank(bo.getLevelEn()), SpMemberLevel::getLevelEn, bo.getLevelEn());
        lqw.eq(bo.getLevelValue() != null, SpMemberLevel::getLevelValue, bo.getLevelValue());
        lqw.eq(bo.getDiscount() != null, SpMemberLevel::getDiscount, bo.getDiscount());
        lqw.eq(bo.getPrice() != null, SpMemberLevel::getPrice, bo.getPrice());
        lqw.eq(StringUtils.isNotBlank(bo.getCardColorStart()), SpMemberLevel::getCardColorStart, bo.getCardColorStart());
        lqw.eq(StringUtils.isNotBlank(bo.getCardColorEnd()), SpMemberLevel::getCardColorEnd, bo.getCardColorEnd());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), SpMemberLevel::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpMemberLevel::getStatus, bo.getStatus());
        lqw.eq(bo.getSortOrder() != null, SpMemberLevel::getSortOrder, bo.getSortOrder());
        return lqw;
    }

    /**
     * 新增会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SpMemberLevelBo bo) {
        SpMemberLevel add = MapstructUtils.convert(bo, SpMemberLevel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            // 同步到字典表
            syncToDictData(add);
        }
        return flag;
    }

    /**
     * 修改会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(SpMemberLevelBo bo) {
        SpMemberLevel update = MapstructUtils.convert(bo, SpMemberLevel.class);
        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
            // 同步到字典表
            syncToDictData(update);
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpMemberLevel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 同步会员等级数据到字典表
     *
     * @param level 会员等级数据
     */
    private void syncToDictData(SpMemberLevel level) {
        try {
            // 查询是否已存在对应的字典数据
            LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDictData::getDictType, DICT_TYPE)
                   .eq(SysDictData::getDictValue, level.getLevelValue().toString());

            SysDictData existingDict = sysDictDataMapper.selectOne(wrapper);

            if (existingDict != null) {
                // 更新已存在的字典数据
                SysDictDataBo bo = new SysDictDataBo();
                bo.setDictCode(existingDict.getDictCode());
                bo.setDictSort(level.getSortOrder() != null ? level.getSortOrder().intValue() : 0);
                bo.setDictLabel(level.getLevelName());
                bo.setDictValue(level.getLevelValue().toString());
                bo.setDictType(DICT_TYPE);
                bo.setListClass("success");
                bo.setIsDefault("N");
                bo.setRemark(level.getDescription());

                sysDictDataService.updateDictData(bo);
                log.info("更新会员等级字典数据: levelValue={}, dictLabel={}", level.getLevelValue(), level.getLevelName());
            } else {
                // 新增字典数据
                SysDictDataBo bo = new SysDictDataBo();
                bo.setDictSort(level.getSortOrder() != null ? level.getSortOrder().intValue() : null);
                bo.setDictLabel(level.getLevelName());
                bo.setDictValue(level.getLevelValue().toString());
                bo.setDictType(DICT_TYPE);
                bo.setListClass("success");
                bo.setIsDefault("N");
                bo.setRemark(level.getDescription());

                sysDictDataService.insertDictData(bo);
                log.info("新增会员等级字典数据: levelValue={}, dictLabel={}", level.getLevelValue(), level.getLevelName());
            }
        } catch (Exception e) {
            log.error("同步会员等级到字典表失败: levelValue={}", level.getLevelValue(), e);
            throw new RuntimeException("同步字典数据失败: " + e.getMessage());
        }
    }

    /**
     * 删除会员等级对应的字典数据
     *
     * @param level 会员等级数据
     */
    private void deleteDictData(SpMemberLevel level) {
        try {
            LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDictData::getDictType, DICT_TYPE)
                   .eq(SysDictData::getDictValue, level.getLevelValue().toString());

            SysDictData dictData = sysDictDataMapper.selectOne(wrapper);
            if (dictData != null) {
                sysDictDataMapper.deleteById(dictData.getDictCode());
                log.info("删除会员等级字典数据: levelValue={}", level.getLevelValue());
            }
        } catch (Exception e) {
            log.error("删除会员等级字典数据失败: levelValue={}", level.getLevelValue(), e);
        }
    }

    /**
     * 校验并批量删除会员等级配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }

        // 删除前先查询会员等级数据，获取levelValue用于删除字典
        List<SpMemberLevel> levels = baseMapper.selectBatchIds(ids);

        // 删除会员等级
        boolean flag = baseMapper.deleteByIds(ids) > 0;

        if (flag && levels != null && !levels.isEmpty()) {
            // 同步删除字典表中的数据
            for (SpMemberLevel level : levels) {
                deleteDictData(level);
            }
        }

        return flag;
    }
}
