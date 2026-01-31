package com.edu.student.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 学员查询DTO（供其他模块调用）
 */
@Data
public class StudentQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学员姓名（模糊查询）
     */
    private String name;

    /**
     * 学员编号
     */
    private String studentNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：potential-潜在，trial-试听，enrolled-在读，suspended-休学，graduated-结业，refunded-退费
     */
    private String status;

    /**
     * 所属校区ID
     */
    private Long campusId;

    /**
     * 跟进顾问ID
     */
    private Long advisorId;

    /**
     * 班级ID（用于查询班级学员）
     */
    private Long classId;

    /**
     * 标签ID（用于按标签查询）
     */
    private Long tagId;
}
