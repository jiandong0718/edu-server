package com.edu.teaching.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 教师状态枚举
 */
@Getter
@AllArgsConstructor
public enum TeacherStatus {

    /**
     * 在职
     */
    ACTIVE("active", "在职"),

    /**
     * 休假
     */
    ON_LEAVE("on_leave", "休假"),

    /**
     * 离职
     */
    RESIGNED("resigned", "离职");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据代码获取枚举
     */
    public static TeacherStatus fromCode(String code) {
        for (TeacherStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的教师状态代码: " + code);
    }

    /**
     * 验证状态代码是否有效
     */
    public static boolean isValid(String code) {
        for (TeacherStatus status : values()) {
            if (status.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
