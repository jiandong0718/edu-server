package com.edu.framework.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.edu.framework.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

/**
 * 校区数据权限拦截器
 * 自动为查询添加 campus_id 条件，实现数据隔离
 */
@Slf4j
public class CampusDataInterceptor implements InnerInterceptor {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CampusDataInterceptor.class);

    /**
     * 需要进行数据隔离的表前缀
     */
    private static final String[] DATA_SCOPE_TABLES = {
            "stu_student", "stu_contact", "stu_tag",
            "tch_teacher", "tch_course", "tch_class", "tch_schedule", "tch_attendance",
            "fin_contract", "fin_payment", "fin_refund",
            "mkt_lead", "mkt_follow_up", "mkt_trial_lesson",
            "msg_notification"
    };

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 获取当前用户的校区ID
        Long campusId = getCurrentCampusId();
        if (campusId == null) {
            // 超级管理员或未登录用户，不进行数据过滤
            return;
        }

        String originalSql = boundSql.getSql();

        // 检查是否需要添加数据权限
        if (needDataScope(originalSql)) {
            String newSql = addCampusCondition(originalSql, campusId);
            // 通过反射修改 SQL
            try {
                java.lang.reflect.Field field = boundSql.getClass().getDeclaredField("sql");
                field.setAccessible(true);
                field.set(boundSql, newSql);
            } catch (Exception e) {
                log.error("Failed to modify SQL for data scope", e);
            }
        }
    }

    /**
     * 获取当前用户的校区ID
     */
    private Long getCurrentCampusId() {
        try {
            // 从 SecurityContext 获取当前用户信息
            // 如果是超级管理员，返回 null 表示可以访问所有数据
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return null;
            }

            // 检查是否是超级管理员
            if (SecurityUtils.isAdmin()) {
                return null;
            }

            return SecurityUtils.getCampusId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查 SQL 是否需要添加数据权限
     */
    private boolean needDataScope(String sql) {
        String lowerSql = sql.toLowerCase();
        for (String table : DATA_SCOPE_TABLES) {
            if (lowerSql.contains(table.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 为 SQL 添加校区条件
     */
    private String addCampusCondition(String sql, Long campusId) {
        // 简单实现：在 WHERE 子句后添加 campus_id 条件
        // 实际项目中可能需要更复杂的 SQL 解析
        String lowerSql = sql.toLowerCase();

        if (lowerSql.contains(" where ")) {
            // 已有 WHERE 子句，添加 AND 条件
            int whereIndex = lowerSql.indexOf(" where ");
            int insertIndex = whereIndex + 7;

            // 找到合适的插入位置（在第一个条件之前）
            return sql.substring(0, insertIndex) +
                    " campus_id = " + campusId + " AND " +
                    sql.substring(insertIndex);
        } else if (lowerSql.contains(" order by ")) {
            // 没有 WHERE 但有 ORDER BY，在 ORDER BY 前插入 WHERE
            int orderIndex = lowerSql.indexOf(" order by ");
            return sql.substring(0, orderIndex) +
                    " WHERE campus_id = " + campusId +
                    sql.substring(orderIndex);
        } else if (lowerSql.contains(" group by ")) {
            // 没有 WHERE 但有 GROUP BY，在 GROUP BY 前插入 WHERE
            int groupIndex = lowerSql.indexOf(" group by ");
            return sql.substring(0, groupIndex) +
                    " WHERE campus_id = " + campusId +
                    sql.substring(groupIndex);
        } else {
            // 没有 WHERE、ORDER BY、GROUP BY，直接在末尾添加
            return sql + " WHERE campus_id = " + campusId;
        }
    }
}
