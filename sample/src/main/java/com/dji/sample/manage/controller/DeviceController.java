package com.dji.sample.manage.controller;

import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.dto.DeviceFirmwareUpgradeDTO;
import com.dji.sample.manage.model.dto.ScreenDeviceDTO;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sdk.cloudapi.device.OsdDockDrone;
import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.PaginationData;
import com.dji.sdk.exception.CloudSDKErrorEnum;
import com.dji.sdk.mqtt.property.PropertySetReplyResultEnum;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author sean.zhou
 * @version 0.1
 * @date 2021/11/15
 */
@RestController
@Slf4j
@RequestMapping("${url.manage.prefix}${url.manage.version}/devices")
public class DeviceController {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceRedisService deviceRedisService;

    @GetMapping("/screen/{device_sn}")
    public HttpResultResponse<ScreenDeviceDTO> getDevice(@PathVariable("device_sn") String deviceSn) {
        ScreenDeviceDTO screenDeviceDTO = ScreenDeviceDTO.defaultDevice();
        Optional<DeviceDTO> deviceOpt = deviceService.getDeviceBySn(deviceSn);
        Optional<OsdDockDrone> deviceOsd = deviceRedisService.getDeviceOsd(deviceSn, OsdDockDrone.class);

        if (deviceOpt.isPresent()) {
            DeviceDTO deviceDTO = deviceOpt.get();
            screenDeviceDTO.setDeviceName(deviceDTO.getDeviceName());
            screenDeviceDTO.setWorkspaceName(deviceDTO.getWorkspaceName());
            screenDeviceDTO.setStatus(deviceDTO.getStatus());
            screenDeviceDTO.setChildDeviceSn(deviceDTO.getChildDeviceSn());
            screenDeviceDTO.setLoginTime(deviceDTO.getLoginTime());
            screenDeviceDTO.setFirmwareVersion(deviceDTO.getFirmwareVersion());
            screenDeviceDTO.setType(deviceDTO.getType().name());

            if (deviceOsd.isPresent()) {
                OsdDockDrone osdDockDrone = deviceOsd.get();
                screenDeviceDTO.setStorageLess(subtract(osdDockDrone.getStorage().getTotal(), osdDockDrone.getStorage().getUsed()));
                screenDeviceDTO.setRTKSign(osdDockDrone.getPositionState().getRtkNumber());
                screenDeviceDTO.setGpsSign(osdDockDrone.getPositionState().getGpsNumber());
                screenDeviceDTO.setCapacityPercent(osdDockDrone.getBattery().getCapacityPercent());
            }
        }

        return HttpResultResponse.success(screenDeviceDTO);
    }

    private static Long subtract(Long one, Long two) {
        return BigDecimal.valueOf(one).subtract(BigDecimal.valueOf(two)).longValue();
    }

    @GetMapping("/screen/paload/{device_sn}")
    public HttpResultResponse<PayloadIndex> getDevicePayload(@PathVariable("device_sn") String deviceSn) {
        return HttpResultResponse.success(deviceService.getDevicePayload(deviceSn));
    }

    /**
     * Get the topology list of all online devices in one workspace.
     * @param workspaceId
     * @return
     */
    @GetMapping("/{workspace_id}/devices")
    public HttpResultResponse<List<DeviceDTO>> getDevices(@PathVariable("workspace_id") String workspaceId) {
        List<DeviceDTO> devicesList = deviceService.getDevicesTopoForWeb(workspaceId);

        return HttpResultResponse.success(devicesList);
    }

    /**
     * After binding the device to the workspace, the device data can only be seen on the web.
     * @param device
     * @param deviceSn
     * @return
     */
    @PostMapping("/{device_sn}/binding")
    public HttpResultResponse bindDevice(@RequestBody DeviceDTO device, @PathVariable("device_sn") String deviceSn) {
        device.setDeviceSn(deviceSn);
        boolean isUpd = deviceService.bindDevice(device);
        return isUpd ? HttpResultResponse.success() : HttpResultResponse.error();
    }

    /**
     * Obtain device information according to device sn.
     * @param workspaceId
     * @param deviceSn
     * @return
     */
    @GetMapping("/{workspace_id}/devices/{device_sn}")
    public HttpResultResponse getDevice(@PathVariable("workspace_id") String workspaceId,
                                        @PathVariable("device_sn") String deviceSn) {
        Optional<DeviceDTO> deviceOpt = deviceService.getDeviceBySn(deviceSn);
        return deviceOpt.isEmpty() ? HttpResultResponse.error("device not found.") : HttpResultResponse.success(deviceOpt.get());
    }

    /**
     * Get the binding devices list in one workspace.
     * @param workspaceId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/{workspace_id}/devices/bound")
    public HttpResultResponse<PaginationData<DeviceDTO>> getBoundDevicesWithDomain(
            @PathVariable("workspace_id") String workspaceId, Integer domain,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(value = "page_size", defaultValue = "50") Long pageSize) {
        PaginationData<DeviceDTO> devices = deviceService.getBoundDevicesWithDomain(workspaceId, page, pageSize, domain);

        return HttpResultResponse.success(devices);
    }

    /**
     * Removing the binding state of the device.
     * @param deviceSn
     * @return
     */
    @DeleteMapping("/{device_sn}/unbinding")
    public HttpResultResponse unbindingDevice(@PathVariable("device_sn") String deviceSn) {
        deviceService.unbindDevice(deviceSn);
        return HttpResultResponse.success();
    }

    /**
     * Update device information.
     * @param device
     * @param workspaceId
     * @param deviceSn
     * @return
     */
    @PutMapping("/{workspace_id}/devices/{device_sn}")
    public HttpResultResponse updateDevice(@RequestBody DeviceDTO device,
                                           @PathVariable("workspace_id") String workspaceId,
                                           @PathVariable("device_sn") String deviceSn) {
        device.setDeviceSn(deviceSn);
        boolean isUpd = deviceService.updateDevice(device);
        return isUpd ? HttpResultResponse.success() : HttpResultResponse.error();
    }

    /**
     * Delivers offline firmware upgrade tasks.
     * @param workspaceId
     * @param upgradeDTOS
     * @return
     */
    @PostMapping("/{workspace_id}/devices/ota")
    public HttpResultResponse createOtaJob(@PathVariable("workspace_id") String workspaceId,
                                           @RequestBody List<DeviceFirmwareUpgradeDTO> upgradeDTOS) {
        return deviceService.createDeviceOtaJob(workspaceId, upgradeDTOS);
    }

    /**
     * Set the property parameters of the drone.
     * @param workspaceId
     * @param dockSn
     * @param param
     * @return
     */
    @PutMapping("/{workspace_id}/devices/{device_sn}/property")
    public HttpResultResponse devicePropertySet(@PathVariable("workspace_id") String workspaceId,
                                                @PathVariable("device_sn") String dockSn,
                                                @RequestBody JsonNode param) {
        if (param.size() != 1) {
            return HttpResultResponse.error(CloudSDKErrorEnum.INVALID_PARAMETER);
        }

        int result = deviceService.devicePropertySet(workspaceId, dockSn, param);
        return PropertySetReplyResultEnum.SUCCESS.getResult() == result ?
                HttpResultResponse.success() : HttpResultResponse.error(result, String.valueOf(result));
    }
}