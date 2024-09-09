package com.dji.sample.screen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dji.sample.common.util.EmptyUtils;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.impl.OssServiceContext;
import com.dji.sample.screen.dao.IErrorFileMapper;
import com.dji.sample.screen.model.ErrorMediaFileDTO;
import com.dji.sample.screen.model.ErrorMediaFileEntity;
import com.dji.sample.screen.service.IErrorFileService;
import com.dji.sdk.common.Pagination;
import com.dji.sdk.common.PaginationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional
public class ErrorFileServiceImpl implements IErrorFileService {

    @Autowired
    private IErrorFileMapper mapper;

    @Autowired
    private OssServiceContext ossService;


    /**
     * 保存错误文件
     *
     * @param errorMediaFileEntity
     */
    @Override
    public void saveErrorFile(ErrorMediaFileEntity errorMediaFileEntity) {
        ErrorMediaFileEntity mediaFileEntity = mapper.selectOne(Wrappers.<ErrorMediaFileEntity>lambdaQuery()
                .eq(ErrorMediaFileEntity::getFileName, errorMediaFileEntity.getFileName()));

        if (Objects.nonNull(mediaFileEntity)){
            // 一般是报错
            return;
        }
        mapper.insert(errorMediaFileEntity);
    }

    /**
     * 通过workspaceId获取错误列表
     *
     * @param workspaceId
     * @param deviceSn
     * @param jobId
     * @return
     */
    @Override
    public List<ErrorMediaFileDTO> getErrorListByWorkspace(String workspaceId,
                                                           String deviceSn,
                                                           String jobId) {
        List<ErrorMediaFileEntity> fileEntityList =
                mapper.selectList(Wrappers.<ErrorMediaFileEntity>lambdaQuery()
                        .eq(ErrorMediaFileEntity::getJobId, jobId)
                        .eq(ErrorMediaFileEntity::getWorkspaceId, workspaceId)
                        .eq(ErrorMediaFileEntity::getDrone, deviceSn));
        return fileEntityList.stream()
                .map(this::entityConvertToDto)
                .collect(Collectors.toList());
    }

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
    @Override
    public PaginationData<ErrorMediaFileDTO> getErrorListByWorkspacePage(String workspaceId,
                                                                         String deviceSn,
                                                                         String jobId,
                                                                         long page,
                                                                         long pageSize) {
        Page<ErrorMediaFileEntity> pageData = mapper.selectPage(
                new Page<ErrorMediaFileEntity>(page, pageSize),
                new LambdaQueryWrapper<ErrorMediaFileEntity>()
                        .eq(ErrorMediaFileEntity::getWorkspaceId, workspaceId)
                        .eq(ErrorMediaFileEntity::getDrone, deviceSn)
                        .eq(ErrorMediaFileEntity::getJobId, jobId)
                        .orderByDesc(ErrorMediaFileEntity::getId));

        List<ErrorMediaFileDTO> records = pageData.getRecords()
                .stream()
                .map(this::entityConvertToDto)
                .collect(Collectors.toList());

        return new PaginationData<ErrorMediaFileDTO>(records,
                new Pagination(pageData.getCurrent(), pageData.getSize(), pageData.getTotal()));
    }

    /**
     * 通过文件id获取错误文件
     *
     * @param fileId 文件id
     * @return 有/没有
     */
    @Override
    public Boolean getErrorMediaById(String fileId) {
        List<ErrorMediaFileEntity> fileEntityList = mapper.selectList(Wrappers.<ErrorMediaFileEntity>lambdaQuery()
                .eq(ErrorMediaFileEntity::getFileId, fileId));
        return !EmptyUtils.isNull(fileEntityList);
    }

    /**
     * 获取最新的错误文件
     *
     * @param workspaceId
     * @param deviceSn
     * @param jobId
     * @return
     */
    @Override
    public ErrorMediaFileDTO getLatestErrorFile(String workspaceId, String deviceSn, String jobId) {
        ErrorMediaFileEntity errorMediaFileEntity = mapper.selectOne(Wrappers.<ErrorMediaFileEntity>lambdaQuery()
                .eq(ErrorMediaFileEntity::getWorkspaceId, workspaceId)
                .eq(ErrorMediaFileEntity::getDrone, deviceSn)
                .eq(ErrorMediaFileEntity::getJobId, jobId)
                .orderByDesc(ErrorMediaFileEntity::getCreateTime)
                .last("limit 1"));
        return entityConvertToDto(errorMediaFileEntity);
    }

    /**
     * Convert database entity objects into file data transfer object.
     * @param entity
     * @return
     */
    private ErrorMediaFileDTO entityConvertToDto(ErrorMediaFileEntity entity) {
        ErrorMediaFileDTO.ErrorMediaFileDTOBuilder builder = ErrorMediaFileDTO.builder();

        if (entity != null) {
            builder.fileName(entity.getFileName())
                    .fileId(entity.getFileId())
                    .filePath(entity.getFilePath())
                    .isOriginal(entity.getIsOriginal())
                    .fingerprint(entity.getFingerprint())
                    .objectKey(entity.getObjectKey())
                    .tinnyFingerprint(entity.getTinnyFingerprint())
                    .payload(entity.getPayload())
                    .createTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(entity.getCreateTime()), ZoneId.systemDefault()))
                    .drone(entity.getDrone())
                    .allUrlFile(ossService.getObjectUrl(OssConfiguration.bucket, entity.getObjectKey()).toString())
                    .longitude(entity.getLongitude())
                    .latitude(entity.getLatitude())
                    .positionJson(entity.getPositionJson())
                    .jobId(entity.getJobId());
        }

        return builder.build();
    }
}
