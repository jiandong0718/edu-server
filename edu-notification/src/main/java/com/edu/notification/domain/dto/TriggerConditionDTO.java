package com.edu.notification.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 触发条件DTO
 */
@Data
public class TriggerConditionDTO {

    /**
     * 条件类型：AND-与, OR-或, SIMPLE-简单条件
     */
    private String type;

    /**
     * 子条件列表（用于AND/OR）
     */
    private List<ConditionItem> conditions;

    /**
     * 条件项
     */
    @Data
    public static class ConditionItem {
        /**
         * 字段名
         */
        private String field;

        /**
         * 操作符：==, !=, >, <, >=, <=, in, not_in, contains
         */
        private String operator;

        /**
         * 比较值
         */
        private Object value;
    }
}
