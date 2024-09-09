package com.dji.sample.wayline.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dji.sample.wayline.model.enums.WaylineJobStatusEnum;
import com.dji.sdk.cloudapi.wayline.OutOfControlActionEnum;
import com.dji.sdk.cloudapi.wayline.TaskTypeEnum;
import com.dji.sdk.cloudapi.wayline.WaylineTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Zhouyuji
 * @version v1.0
 * @Description
 * @since 2024/6/16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScreenJobDTO {

    private String name;

    private TaskTypeEnum taskType;

    private WaylineTypeEnum waylineType;

    private String username;

    private LocalDateTime executeTime;

    private LocalDateTime endTime;

    private WaylineJobStatusEnum status;

    private Integer rthAltitude;

    private OutOfControlActionEnum outOfControlAction;

    private LocalDateTime beginTime;

    private LocalDateTime completedTime;

}
