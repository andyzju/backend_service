package com.dji.sample.common.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Zhouyuji
 * @version v1.0
 * @Description
 * @since 2024/6/21
 */
public class ImgUtil {

    public static ImgLoLa getImageLoLa(InputStream inputStream) {
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(inputStream);
        } catch (ImageProcessingException | IOException e) {
            throw new RuntimeException(e);
        }
        ImgLoLa imgLoLa = new ImgLoLa();
        metadata.getDirectories().forEach(directory ->
                directory.getTags().forEach(tag -> {
                    if (tag.getTagName().equals("GPS Latitude")) {
                        imgLoLa.setLatitude(String.valueOf(dmsStringToDecimal(tag.getDescription(),true)));
                    }
                    if (tag.getTagName().equals("GPS Longitude")) {
                        imgLoLa.setLongitude(String.valueOf(dmsStringToDecimal(tag.getDescription(),false)));
                    }
                }));
        return imgLoLa;
    }

    @Data
    public static class ImgLoLa {

        private String longitude;

        private String latitude;
    }

    // 将度分秒格式转换为十进制度格式
    private static double dmsToDecimal(int degrees, int minutes, double seconds, boolean isNegative) {
        double decimal = degrees + (minutes / 60.0) + (seconds / 3600.0);
        return isNegative ? -decimal : decimal;
    }

    // 从字符串解析度分秒格式并转换为十进制度格式
    private static double dmsStringToDecimal(String dms, boolean isLatitude) {
        String[] dmsArray = dms.split("[°'\"]");

        // 解析度、分、秒
        int degrees = Integer.parseInt(dmsArray[0].trim());
        int minutes = Integer.parseInt(dmsArray[1].trim());
        double seconds = Double.parseDouble(dmsArray[2].trim());

        // 检查是否为负数（南纬或西经）
        boolean isNegative = (isLatitude && dms.toUpperCase().contains("S")) ||
                (!isLatitude && dms.toUpperCase().contains("W"));

        return dmsToDecimal(degrees, minutes, seconds, isNegative);
    }
}
