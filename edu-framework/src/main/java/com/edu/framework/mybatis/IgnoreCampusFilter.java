package com.edu.framework.mybatis;

import java.lang.annotation.*;

/**
 * 忽略校区过滤注解
 * 标注此注解的方法将不会自动添加 campus_id 条件
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreCampusFilter {
}
