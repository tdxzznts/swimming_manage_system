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
import org.dromara.swimming.domain.bo.SpMemberCardBo;
import org.dromara.swimming.domain.vo.SpMemberCardVo;
import org.dromara.swimming.domain.SpMemberCard;
import org.dromara.swimming.mapper.SpMemberCardMapper;
import org.dromara.swimming.service.ISpMemberCardService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 会员卡Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpMemberCardServiceImpl implements ISpMemberCardService {

    private final SpMemberCardMapper baseMapper;

    /**
     * 查询会员卡
     *
     * @param id 主键
     * @return 会员卡
     */
    @Override
    public SpMemberCardVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员卡列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员卡分页列表
     */
    @Override
    public TableDataInfo<SpMemberCardVo> queryPageList(SpMemberCardBo bo, PageQuery pageQuery) {
        Page<SpMemberCardVo> result = baseMapper.selectVoPage(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员卡列表
     *
     * @param bo 查询条件
     * @return 会员卡列表
     */
    @Override
    public List<SpMemberCardVo> queryList(SpMemberCardBo bo) {
        return baseMapper.selectVoList(bo);
    }

    /**
     * 新增会员卡
     *
     * @param bo 会员卡
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpMemberCardBo bo) {
        SpMemberCard add = MapstructUtils.convert(bo, SpMemberCard.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员卡
     *
     * @param bo 会员卡
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpMemberCardBo bo) {
        SpMemberCard update = MapstructUtils.convert(bo, SpMemberCard.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpMemberCard entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员卡信息
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
