package com.dji.sample.wayline.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class ScreenDeviceJobInfoDTO {

    private Integer successTask;

    private Integer totalTask;

    private Integer errorTask;

    private Integer pareTask;

}
