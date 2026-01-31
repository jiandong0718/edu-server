package com.edu.framework.mybatis;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 多校区数据隔离拦截器
 * 自动为 SQL 添加 campus_id 条件
 */
@Slf4j
public class CampusDataInterceptor implements InnerInterceptor {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CampusDataInterceptor.class);

    private static final String CAMPUS_ID_COLUMN = "campus_id";

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 获取当前用户的校区ID
        Long campusId = CampusContextHolder.getCampusId();
        if (campusId == null) {
            return;
        }

        String originalSql = boundSql.getSql();
        try {
            String newSql = addCampusCondition(originalSql, campusId, SqlCommandType.SELECT);
            PluginUtils.mpBoundSql(boundSql).sql(newSql);
        } catch (JSQLParserException e) {
            log.warn("SQL解析失败，跳过校区过滤: {}", originalSql, e);
        }
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        Long campusId = CampusContextHolder.getCampusId();
        if (campusId == null) {
            return;
        }

        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType commandType = ms.getSqlCommandType();

        if (commandType == SqlCommandType.UPDATE || commandType == SqlCommandType.DELETE) {
            BoundSql boundSql = mpSh.boundSql();
            String originalSql = boundSql.getSql();
            try {
                String newSql = addCampusCondition(originalSql, campusId, commandType);
                PluginUtils.mpBoundSql(boundSql).sql(newSql);
            } catch (JSQLParserException e) {
                log.warn("SQL解析失败，跳过校区过滤: {}", originalSql, e);
            }
        }
    }

    private String addCampusCondition(String sql, Long campusId, SqlCommandType commandType) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);

        if (commandType == SqlCommandType.SELECT && statement instanceof Select select) {
            if (select.getSelectBody() instanceof PlainSelect plainSelect) {
                Expression where = plainSelect.getWhere();
                Expression campusCondition = buildCampusCondition(campusId);
                if (where == null) {
                    plainSelect.setWhere(campusCondition);
                } else {
                    plainSelect.setWhere(new AndExpression(where, campusCondition));
                }
            }
        } else if (commandType == SqlCommandType.UPDATE && statement instanceof Update update) {
            Expression where = update.getWhere();
            Expression campusCondition = buildCampusCondition(campusId);
            if (where == null) {
                update.setWhere(campusCondition);
            } else {
                update.setWhere(new AndExpression(where, campusCondition));
            }
        } else if (commandType == SqlCommandType.DELETE && statement instanceof Delete delete) {
            Expression where = delete.getWhere();
            Expression campusCondition = buildCampusCondition(campusId);
            if (where == null) {
                delete.setWhere(campusCondition);
            } else {
                delete.setWhere(new AndExpression(where, campusCondition));
            }
        }

        return statement.toString();
    }

    private Expression buildCampusCondition(Long campusId) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(CAMPUS_ID_COLUMN));
        equalsTo.setRightExpression(new LongValue(campusId));
        return equalsTo;
    }
}
