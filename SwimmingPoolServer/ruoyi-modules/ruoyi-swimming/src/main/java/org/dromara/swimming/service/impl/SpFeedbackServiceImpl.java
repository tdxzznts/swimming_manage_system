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
import org.dromara.swimming.domain.bo.SpFeedbackBo;
import org.dromara.swimming.domain.vo.SpFeedbackVo;
import org.dromara.swimming.domain.SpFeedback;
import org.dromara.swimming.mapper.SpFeedbackMapper;
import org.dromara.swimming.service.ISpFeedbackService;

import java.util.Collection;

/**
 * 反馈Service业务层处理
 *
 * @author W
 * @date 2026-02-05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpFeedbackServiceImpl implements ISpFeedbackService {

    private final SpFeedbackMapper baseMapper;

    /**
     * 查询反馈
     *
     * @param id 主键
     * @return 反馈
     */
    @Override
    public SpFeedbackVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询反馈列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 反馈分页列表
     */
    @Override
    public TableDataInfo<SpFeedbackVo> queryPageList(SpFeedbackBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SpFeedback> lqw = buildQueryWrapper(bo);
        Page<SpFeedback> page = pageQuery.build();

        // 调试日志
        System.out.println("=== Service Debug Info ===");
        System.out.println("PageQuery - pageNum: " + pageQuery.getPageNum() + ", pageSize: " + pageQuery.getPageSize());
        System.out.println("Page object - current: " + page.getCurrent() + ", size: " + page.getSize());
        System.out.println("UserId from Bo: " + bo.getUserId());

        Page<SpFeedbackVo> result = baseMapper.selectVoPage(page, lqw);

        System.out.println("Result - total: " + result.getTotal() + ", records size: " + result.getRecords().size());
        System.out.println("==========================");

        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的反馈列表
     *
     * @param bo 查询条件
     * @return 反馈列表
     */
    @Override
    public java.util.List<SpFeedbackVo> queryList(SpFeedbackBo bo) {
        return baseMapper.selectVoList(bo);
    }

    /**
     * 构建查询条件
     *
     * @param bo 查询条件
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<SpFeedback> buildQueryWrapper(SpFeedbackBo bo) {
        LambdaQueryWrapper<SpFeedback> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, SpFeedback::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getFeedbackType()), SpFeedback::getFeedbackType, bo.getFeedbackType());
        lqw.like(StringUtils.isNotBlank(bo.getTitle()), SpFeedback::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SpFeedback::getStatus, bo.getStatus());
        lqw.eq(bo.getHandleUserId() != null, SpFeedback::getHandleUserId, bo.getHandleUserId());
        return lqw;
    }

    /**
     * 新增反馈
     *
     * @param bo 反馈
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SpFeedbackBo bo) {
        SpFeedback add = MapstructUtils.convert(bo, SpFeedback.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改反馈
     *
     * @param bo 反馈
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SpFeedbackBo bo) {
        SpFeedback update = MapstructUtils.convert(bo, SpFeedback.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SpFeedback entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除反馈信息
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
