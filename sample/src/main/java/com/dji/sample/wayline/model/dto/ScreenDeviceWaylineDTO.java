package com.dji.sample.wayline.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Zhouyuji
 * @version v1.0
 * @Description
 * @since 2024/6/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScreenDeviceWaylineDTO {

    private String waylineId;

    private String jobId;

    /*dockSn*/
    private String deviceSn;

    private String deviceName;

    private String waylineName;

    private String jobName;

    private Boolean deviceStatus;

    private Integer jobStatus;

    private LocalDateTime completedTime;

    private String childSn;

}
