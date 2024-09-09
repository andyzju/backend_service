package com.dji.sample.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author pxx
 * @date 2019/4/18 10:24
 */
public class EmptyUtils {
    /**
     * 非空判断
     * @param objs
     * @return
     */
    public final static boolean isNull(Object[] objs) {
        if (objs == null || objs.length == 0) {
            return true;
        }
        return false;
    }
    public final static boolean isNull(Object obj) {
        if (obj == null || isNull(obj.toString())){
            return true;
        }
        return false;
    }

    public final static boolean isNull(Integer integer) {
        if (integer == null || integer == 0) {
            return true;
        }
        return false;
    }

    public final static boolean isNull(Collection collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        }
        return false;
    }

    public final static boolean isNull(Map map) {
        if (map == null || map.size() == 0) {
            return true;
        }
        return false;
    }

    public final static boolean isNull(String str) {
        return str == null || "".equals(str.trim())
                || "null".equals(str.toLowerCase());
    }

    public final static boolean isNull(Long longs) {
        if (longs == null || longs == 0) {
            return true;
        }
        return false;
    }

}
