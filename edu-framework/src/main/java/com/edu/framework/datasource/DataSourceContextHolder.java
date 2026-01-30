package com.edu.framework.datasource;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源上下文持有者
 * 用于在当前线程中存储和获取数据源标识
 */
@Slf4j
public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源
     */
    public static void setDataSource(String dataSource) {
        log.debug("切换数据源: {}", dataSource);
        CONTEXT_HOLDER.set(dataSource);
    }

    /**
     * 获取当前数据源
     */
    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除数据源
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
