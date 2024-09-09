package com.dji.sample.screen.service;

import com.dji.sample.screen.model.ErrorMediaFileDTO;
import com.dji.sample.screen.model.ErrorMediaFileEntity;
import com.dji.sdk.common.PaginationData;

import java.util.List;


public interface IErrorFileService {


    /**
     * 保存错误文件
     * @param errorMediaFileEntity
     */
    void saveErrorFile(ErrorMediaFileEntity errorMediaFileEntity);

    /**
     * 通过workspaceId获取错误列表
     *
     * @param workspaceId
     * @param deviceSn
     * @param jobId
     * @return
     */
    List<ErrorMediaFileDTO> getErrorListByWorkspace(String workspaceId, String deviceSn, String jobId);

    /**
     * 通过workspaceId获取错误列表
     *
     * @param workspaceId
     * @param deviceSn
     * @param jobId
     * @param page
     * @param pageSize
     * @return
     */
    PaginationData<ErrorMediaFileDTO> getErrorListByWorkspacePage(String workspaceId,
                                                                  String deviceSn,
                                                                  String jobId,
                                                                  long page,
                                                                  long pageSize);

    /**
     * 通过错误文件获取名称
     * @param fileId 文件id
     * @return 有/没有
     */
    Boolean getErrorMediaById(String fileId);

    /**
     * 获取最新的错误文件
     *
     * @param workspaceId
     * @param deviceSn
     * @param jobId
     * @return
     */
    ErrorMediaFileDTO getLatestErrorFile(String workspaceId, String deviceSn, String jobId);
}
