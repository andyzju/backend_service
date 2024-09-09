package com.dji.sample.manage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Zhouyuji
 * @version v1.0
 * @Description
 * @since 2024/6/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScreenDeviceDTO {

    private String deviceName;

    private String workspaceName;

    private Boolean status;

    private String childDeviceSn;

    private Integer RTKSign;

    /*百分比*/
    private Integer capacityPercent;

    private Integer gpsSign;

    /*KB*/
    private Long storageLess;

    private String firmwareVersion;

    private LocalDateTime loginTime;

    private String type;

    public static ScreenDeviceDTO defaultDevice() {
        ScreenDeviceDTO screenDeviceDTO = new ScreenDeviceDTO();
        screenDeviceDTO.setDeviceName("-");
        screenDeviceDTO.setWorkspaceName("-");
        screenDeviceDTO.setStatus(false);
        screenDeviceDTO.setRTKSign(0);
        screenDeviceDTO.setCapacityPercent(0);
        screenDeviceDTO.setGpsSign(0);
        screenDeviceDTO.setStorageLess(0L);
        screenDeviceDTO.setFirmwareVersion("-");
        screenDeviceDTO.setType("-");
        screenDeviceDTO.setLoginTime(null);
        return screenDeviceDTO;
    }
}
