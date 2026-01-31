package com.edu.student.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 学员导入结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentImportResultDTO {

    /**
     * 总数
     */
    private Integer total;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer failureCount;

    /**
     * 错误详情列表
     */
    @Builder.Default
    private List<ImportError> errors = new ArrayList<>();

    /**
     * 导入错误详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        /**
         * 行号
         */
        private Integer rowIndex;

        /**
         * 学员姓名
         */
        private String studentName;

        /**
         * 错误信息
         */
        private String errorMessage;
    }
}
