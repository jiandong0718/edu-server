package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.MakeupLesson;

/**
 * 补课记录服务接口
 */
public interface MakeupLessonService extends IService<MakeupLesson> {

    /**
     * 分页查询补课记录列表
     */
    IPage<MakeupLesson> getMakeupLessonPage(IPage<MakeupLesson> page, MakeupLesson query);

    /**
     * 安排补课
     */
    boolean arrangeMakeup(MakeupLesson makeupLesson);

    /**
     * 完成补课
     */
    boolean completeMakeup(Long id);

    /**
     * 取消补课
     */
    boolean cancelMakeup(Long id);

    /**
     * 检查补课时间冲突
     */
    boolean checkMakeupConflict(Long studentId, Long makeupScheduleId);
}
