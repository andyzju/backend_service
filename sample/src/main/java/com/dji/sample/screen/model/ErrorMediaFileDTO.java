package com.dji.sample.screen.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author sean
 * @version 0.2
 * @date 2021/12/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMediaFileDTO {

    private String fileId;

    private String fileName;

    private String filePath;

    private String objectKey;

    private String subFileType;

    private Boolean isOriginal;

    private String drone;

    private String payload;

    private String tinnyFingerprint;

    private String fingerprint;

    private LocalDateTime createTime;

    private String jobId;

    private String allUrlFile;

    private String longitude;

    private String latitude;

    private String positionJson;
}
