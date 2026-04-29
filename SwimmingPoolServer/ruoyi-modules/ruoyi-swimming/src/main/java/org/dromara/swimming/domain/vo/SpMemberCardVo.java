package org.dromara.swimming.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.swimming.domain.SpMemberCard;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 会员卡视图对象 sp_member_card
 *
 * @author W
 * @date 2026-02-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SpMemberCard.class)
public class SpMemberCardVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡ID
     */
    @ExcelProperty(value = "会员卡ID")
    private Long id;

    /**
     * 用户ID（关联sys_user.user_id）
     */
    @ExcelProperty(value = "用户ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "关=联sys_user.user_id")
    private Long userId;

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名")
    private String userName;

    /**
     * 会员卡号
     */
    @ExcelProperty(value = "会员卡号")
    private String cardNo;

    /**
     * 会员等级（0普通 1银卡 2金卡 3钻石）
     */
    @ExcelProperty(value = "会员等级", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_member_level")
    private String cardLevel;

    /**
     * 余额
     */
    @ExcelProperty(value = "余额")
    private Long balance;

    /**
     * 累计充值金额
     */
    @ExcelProperty(value = "累计充值金额")
    private Long totalRecharge;

    /**
     * 累计消费金额
     */
    @ExcelProperty(value = "累计消费金额")
    private Long totalConsume;

    /**
     * 积分余额
     */
    @ExcelProperty(value = "积分余额")
    private Long totalPoint;

    /**
     * 免费次数
     */
    @ExcelProperty(value = "免费次数")
    private Long freeTimes;

    /**
     * 发卡日期
     */
    @ExcelProperty(value = "发卡日期")
    private Date issueDate;

    /**
     * 到期日期
     */
    @ExcelProperty(value = "到期日期")
    private Date expiryDate;

    /**
     * 状态（0正常 1挂失 2冻结）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sp_member_card_status")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
