package com.edu.framework.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Mapper 数据源注入处理器
 * 根据字段上的 @DB_Edu 或 @DB_Edu_Slave 注解，注入对应数据源的 Mapper
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.datasource.dynamic", name = "enabled", havingValue = "true")
public class MapperDataSourceBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        ReflectionUtils.doWithFields(clazz, field -> {
            if (field.isAnnotationPresent(DB_Edu.class)) {
                injectMapper(bean, field, DataSourceNames.EDU_MASTER);
            } else if (field.isAnnotationPresent(DB_Edu_Slave.class)) {
                injectMapper(bean, field, DataSourceNames.EDU_SLAVE);
            }
        });

        return bean;
    }

    private void injectMapper(Object bean, Field field, String dataSourceName) {
        try {
            Class<?> mapperInterface = field.getType();
            if (!mapperInterface.isInterface()) {
                log.warn("@DB_Edu/@DB_Edu_Slave 只能用于 Mapper 接口类型的字段: {}", field);
                return;
            }

            SqlSessionTemplate sqlSessionTemplate = getSqlSessionTemplate(dataSourceName);
            Object mapper = sqlSessionTemplate.getMapper(mapperInterface);

            field.setAccessible(true);
            field.set(bean, mapper);

            log.debug("注入 Mapper [{}] 使用数据源 [{}]", mapperInterface.getSimpleName(), dataSourceName);
        } catch (Exception e) {
            log.error("注入 Mapper 失败: {}", field, e);
        }
    }

    private SqlSessionTemplate getSqlSessionTemplate(String dataSourceName) {
        if (DataSourceNames.EDU_MASTER.equals(dataSourceName)) {
            return beanFactory.getBean("eduMasterSqlSessionTemplate", SqlSessionTemplate.class);
        } else if (DataSourceNames.EDU_SLAVE.equals(dataSourceName)) {
            return beanFactory.getBean("eduSlaveSqlSessionTemplate", SqlSessionTemplate.class);
        }
        throw new IllegalArgumentException("未知的数据源: " + dataSourceName);
    }
}
