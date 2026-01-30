package com.edu.framework.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveParams() default true;

    /**
     * 是否保存响应结果
     */
    boolean saveResult() default false;

    /**
     * 操作类型枚举
     */
    enum OperationType {
        /**
         * 新增
         */
        CREATE,
        /**
         * 修改
         */
        UPDATE,
        /**
         * 删除
         */
        DELETE,
        /**
         * 查询
         */
        QUERY,
        /**
         * 导入
         */
        IMPORT,
        /**
         * 导出
         */
        EXPORT,
        /**
         * 登录
         */
        LOGIN,
        /**
         * 登出
         */
        LOGOUT,
        /**
         * 其他
         */
        OTHER
    }
}
