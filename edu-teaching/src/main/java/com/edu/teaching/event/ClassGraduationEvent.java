package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.util.List;

/**
 * 班级结业事件
 */
@Getter
public class ClassGraduationEvent extends ApplicationEvent {

    /**
     * 班级ID
     */
    private final Long classId;

    /**
     * 班级名称
     */
    private final String className;

    /**
     * 结业日期
     */
    private final LocalDate graduationDate;

    /**
     * 结业学员ID列表
     */
    private final List<Long> graduatedStudentIds;

    /**
     * 是否生成了结业证书
     */
    private final Boolean certificateGenerated;

    /**
     * 结业证书ID列表
     */
    private final List<Long> certificateIds;

    /**
     * 结业评语
     */
    private final String graduationComment;

    /**
     * 备注
     */
    private final String remark;

    public ClassGraduationEvent(Object source, Long classId, String className,
                                LocalDate graduationDate, List<Long> graduatedStudentIds,
                                Boolean certificateGenerated, List<Long> certificateIds,
                                String graduationComment, String remark) {
        super(source);
        this.classId = classId;
        this.className = className;
        this.graduationDate = graduationDate;
        this.graduatedStudentIds = graduatedStudentIds;
        this.certificateGenerated = certificateGenerated;
        this.certificateIds = certificateIds;
        this.graduationComment = graduationComment;
        this.remark = remark;
    }
}
