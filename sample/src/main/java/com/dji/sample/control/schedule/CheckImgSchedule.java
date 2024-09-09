package com.dji.sample.control.schedule;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSSException;
import com.dji.sample.common.util.ImgUtil;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.impl.OssServiceContext;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.media.model.MediaFileEntity;
import com.dji.sample.media.service.IFileService;
import com.dji.sample.screen.model.ErrorMediaFileEntity;
import com.dji.sample.screen.service.IErrorFileService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author Zhouyuji
 * @version v1.0
 * @Description
 * @since 2024/6/15
 */
@Slf4j
@Component
public class CheckImgSchedule {

    @Autowired
    private IFileService fileService;

    @Autowired
    private IErrorFileService errorFileService;

    @Autowired
    private Executor threadPool;

    @Autowired
    private OssServiceContext ossService;


    @Scheduled(cron = "0 */1 * * * ?")
    public void CheckErrorImgSchedule() {
        log.info("----定时任务开始：当前事件是：{}----", LocalDateTime.now());
        Long lastTime = Long.valueOf(Optional.ofNullable(RedisOpsUtils.get("prod:media:file:error:last:")).orElse(0).toString());
        List<MediaFileEntity> mediaFileDTOList = fileService.getAllFilesByTime(lastTime);

        mediaFileDTOList.forEach(mediaFileDTO -> {
            threadPool.execute(() -> {
                try {
                    //检验是否重复判断
                    if (errorFileService.getErrorMediaById(mediaFileDTO.getFileName())) {
                        return;
                    }
                    URL imgUrl = ossService.getObjectUrl(OssConfiguration.bucket, mediaFileDTO.getObjectKey());
                    String json = JSONUtil.toJsonStr(new Body(imgUrl.toString(), "detect_normal"));

                    //请求接口
                    HttpResponse execute = HttpUtil.createPost("http://120.27.159.164:50021/v1/object-detection/yolov5")
                            .body(json)
                            .execute();

                    if (execute.getStatus() == 200) {
                        Boolean flag = JSONUtil.parseObj(execute.body()).getBool("flag");
                        String positionJson = JSONUtil.parseObj(execute.body()).getStr("data");

                        if (flag) {
                            //执行错误保存
                            errorFileService.saveErrorFile(convert(mediaFileDTO, imgUrl, positionJson));
                        }

                        //save redis
                        Long now = mediaFileDTO.getCreateTime();
                        Long before = Long.valueOf(Optional.ofNullable(RedisOpsUtils.get("prod:media:file:error:last:")).orElse(0).toString());
                        if (before < now) {
                            RedisOpsUtils.set("prod:media:file:error:last:", now);
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof OSSException) {
                        log.error("当前图片:{}发生异常:{}", mediaFileDTO.getFileName(),e.getMessage());
                    } else {
                        log.error("图片处理异常", e);
                    }
                }
            });
        });
    }

    public ErrorMediaFileEntity convert(MediaFileEntity mediaFileEntity, URL imgUrl, String positionJson) {

        ErrorMediaFileEntity errorMediaFileEntity = new ErrorMediaFileEntity();
        errorMediaFileEntity.setFileId(mediaFileEntity.getFileId());
        errorMediaFileEntity.setFileName(mediaFileEntity.getFileName());
        errorMediaFileEntity.setFilePath(mediaFileEntity.getFilePath());
        errorMediaFileEntity.setDrone(mediaFileEntity.getDrone());
        errorMediaFileEntity.setFingerprint(mediaFileEntity.getFingerprint());
        errorMediaFileEntity.setIsOriginal(mediaFileEntity.getIsOriginal());
        errorMediaFileEntity.setJobId(mediaFileEntity.getJobId());
        errorMediaFileEntity.setObjectKey(mediaFileEntity.getObjectKey());
        errorMediaFileEntity.setPayload(mediaFileEntity.getPayload());
        errorMediaFileEntity.setSubFileType(mediaFileEntity.getSubFileType());
        errorMediaFileEntity.setWorkspaceId(mediaFileEntity.getWorkspaceId());
        errorMediaFileEntity.setTinnyFingerprint(mediaFileEntity.getTinnyFingerprint());
        errorMediaFileEntity.setPositionJson(positionJson.replace("\\", ""));
        // 图片链接
        try {
            ImgUtil.ImgLoLa imageLoLa = ImgUtil.getImageLoLa(imgUrl.openStream());
            errorMediaFileEntity.setLongitude(imageLoLa.getLongitude());
            errorMediaFileEntity.setLatitude(imageLoLa.getLatitude());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return errorMediaFileEntity;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Body {

        private String image_url;

        private String option;


    }

    @Data
    static class Result {
        private String flag;

        private DataResult data;
    }

    @Data
    static class DataResult {

        private String cls;

        private List<Integer> box;
    }

}
