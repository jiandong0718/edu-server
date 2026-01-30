package com.edu.framework.datasource;

import java.lang.annotation.*;

/**
 * 教育系统主库数据源注解
 * 用于注入 Mapper 时指定使用 edu 主库
 *
 * 使用示例：
 * <pre>
 * @DB_Edu
 * private UserMapper userMapper;
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DB_Edu {
}
