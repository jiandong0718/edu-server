package com.edu.framework.mybatis;

/**
 * 校区上下文持有者
 * 用于在当前线程中存储和获取校区ID
 */
public class CampusContextHolder {

    private static final ThreadLocal<Long> CAMPUS_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前校区ID
     */
    public static void setCampusId(Long campusId) {
        CAMPUS_ID_HOLDER.set(campusId);
    }

    /**
     * 获取当前校区ID
     */
    public static Long getCampusId() {
        return CAMPUS_ID_HOLDER.get();
    }

    /**
     * 清除当前校区ID
     */
    public static void clear() {
        CAMPUS_ID_HOLDER.remove();
    }
}
