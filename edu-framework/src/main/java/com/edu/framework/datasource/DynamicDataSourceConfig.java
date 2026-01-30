package com.edu.framework.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置
 * 通过配置 spring.datasource.dynamic.enabled=true 启用
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.dynamic", name = "enabled", havingValue = "true")
public class DynamicDataSourceConfig {

    @Bean(name = "eduMasterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.edu-master")
    public DataSource eduMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "eduSlaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.edu-slave")
    public DataSource eduSlaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource(
            @Qualifier("eduMasterDataSource") DataSource eduMasterDataSource,
            @Qualifier("eduSlaveDataSource") DataSource eduSlaveDataSource) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceNames.EDU_MASTER, eduMasterDataSource);
        targetDataSources.put(DataSourceNames.EDU_SLAVE, eduSlaveDataSource);

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(eduMasterDataSource);
        return dynamicDataSource;
    }

    @Bean(name = "eduMasterSqlSessionFactory")
    public SqlSessionFactory eduMasterSqlSessionFactory(
            @Qualifier("eduMasterDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/**/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "eduSlaveSqlSessionFactory")
    public SqlSessionFactory eduSlaveSqlSessionFactory(
            @Qualifier("eduSlaveDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/**/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "eduMasterSqlSessionTemplate")
    public SqlSessionTemplate eduMasterSqlSessionTemplate(
            @Qualifier("eduMasterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "eduSlaveSqlSessionTemplate")
    public SqlSessionTemplate eduSlaveSqlSessionTemplate(
            @Qualifier("eduSlaveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
