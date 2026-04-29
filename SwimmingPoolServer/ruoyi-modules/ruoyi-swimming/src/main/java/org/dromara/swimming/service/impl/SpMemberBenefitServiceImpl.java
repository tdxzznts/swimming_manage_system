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
import org.dromara.swimming.domain.bo.SpMemberBenefitBo;
import org.dromara.swimming.domain.vo.SpMemberBenefitVo;
import org.dromara.swimming.domain.SpMemberBenefit;
import org.dromara.swimming.mapper.SpMemberBenefitMapper;
import org.dromara.swimming.service.ISpMemberBenefitService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 会员权益配置Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpMemberBenefitServiceImpl implements ISpMemberBenefitService {

    private final SpMemberBenefitMapper baseMapper;

    /**
     * 查询会员权益配置
     *
     * @param id 主键
     * @return 会员权益配置
     */
    @Override
    public SpMemberBenefitVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员权益配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员权益配置分页列表
     */
    @Override
    public TableDataInfo<SpMemberBenefitVo> queryPageList(SpMemberBenefitBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpMemberBenefit> lqw = buildQueryWrapper(bo);
        Page<SpMemberBenefitVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员权益配置列表
     *
     * @param bo 查询条件
     * @return 会员权益配置列表
     */
    @Override
    public List<SpMemberBenefitVo> queryList(SpMemberBenefitBo bo) {
        LambdaQueryWrapper<SpMemberBenefit> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SpMemberBenefit> buildQueryWrapper(SpMemberBenefitBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SpMemberBenefit> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SpMemberBenefit::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getBenefitCode()), SpMemberBenefit::getBenefitCode, bo.getBenefitCode());
        lqw.like(StringUtils.isNotBlank(bo.getBenefitName()), SpMemberBenefit::getBenefitName, bo.getBenefitName());
        lqw.eq(StringUtils.isNotBlank(bo.getBenefitDesc()), SpMemberBenefit::getBenefitDesc, bo.getBenefitDesc());
        lqw.like(StringUtils.isNotBlank(bo.getIconName()), SpMemberBenefit::getIconName, bo.getIconName());
        lqw.eq(StringUtils.isNotBlank(bo.getBenefitValue()), SpMemberBenefit::getBenefitValue, bo.getBenefitValue());
        lqw.eq(StringUtils.isNotBlank(bo.getTagType()), SpMemberBenefit::getTagType, bo.getTagType());
        lqw.eq(StringUtils.isNotBlank(bo.getLevelValue()), SpMemberBenefit::getLevelValue, bo.getLevelValue());
        lqw.eq(bo.getSortOrder() != null, SpMemberBenefit::getSortOrder, bo.getSortOrder());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpMemberBenefit::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增会员权益配置
     *
     * @param bo 会员权益配置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpMemberBenefitBo bo) {
        SpMemberBenefit add = MapstructUtils.convert(bo, SpMemberBenefit.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员权益配置
     *
     * @param bo 会员权益配置
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpMemberBenefitBo bo) {
        SpMemberBenefit update = MapstructUtils.convert(bo, SpMemberBenefit.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpMemberBenefit entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员权益配置信息
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
