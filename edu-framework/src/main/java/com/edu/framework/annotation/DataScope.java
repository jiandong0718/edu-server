package com.edu.framework.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 标记需要进行数据权限过滤的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 表别名
     */
    String tableAlias() default "";

    /**
     * 校区ID字段名
     */
    String campusField() default "campus_id";

    /**
     * 是否忽略数据权限
     */
    boolean ignore() default false;
}
