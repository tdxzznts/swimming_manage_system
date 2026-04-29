package org.dromara.swimming.domain.bo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5我的预约查询对象
 *
 * @author swimming
 * @date 2026-02-03
 */
@Data
public class SpH5MyReservationBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态（0待支付 1已支付 2已完成 3已取消 4已退款）
     */
    private String status;

    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
