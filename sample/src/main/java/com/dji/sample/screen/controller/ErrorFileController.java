package com.dji.sample.screen.controller;

import com.dji.sample.screen.model.ErrorMediaFileDTO;
import com.dji.sample.screen.service.IErrorFileService;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.PaginationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sean
 * @version 0.2
 * @date 2021/12/9
 */
@RestController
@RequestMapping("${url.media.prefix}${url.media.version}/error/files")
public class ErrorFileController {

    @Autowired
    private IErrorFileService fileService;

    @GetMapping("/{workspace_id}/files/{device_sn}/{job_id}")
    public HttpResultResponse<List<ErrorMediaFileDTO>> getFilesList(
            @PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "device_sn") String deviceSn,
            @PathVariable(name = "job_id") String jobId) {
        return HttpResultResponse.success(fileService.getErrorListByWorkspace(workspaceId, deviceSn, jobId));
    }

    @GetMapping("/{workspace_id}/latest/{device_sn}/{job_id}")
    public HttpResultResponse<ErrorMediaFileDTO> getLatestErrorFile(
            @PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "device_sn") String deviceSn,
            @PathVariable(name = "job_id") String jobId) {
        return HttpResultResponse.success(fileService.getLatestErrorFile(workspaceId, deviceSn, jobId));
    }

    @GetMapping("/{workspace_id}/files/{device_sn}/{job_id}/page")
    public HttpResultResponse<PaginationData<ErrorMediaFileDTO>> getFilesListPage(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(name = "page_size", defaultValue = "10") Long pageSize,
            @PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "device_sn") String deviceSn,
            @PathVariable(name = "job_id") String jobId) {
        return HttpResultResponse.success(fileService.getErrorListByWorkspacePage(workspaceId, deviceSn, jobId, page, pageSize));
    }
}
