package org.dromara.swimming.domain.bo;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * H5创建预约请求对象
 *
 * @author swimming
 * @date 2026-02-03
 */
@Data
public class SpH5ReservationBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 时段ID
     */
    @NotNull(message = "时段不能为空")
    private Long timeSlotId;

    /**
     * 预约日期
     */
    @NotNull(message = "预约日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reservationDate;
}
