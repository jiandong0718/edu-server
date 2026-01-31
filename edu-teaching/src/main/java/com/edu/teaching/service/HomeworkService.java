package com.edu.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.Homework;
import com.edu.teaching.domain.vo.HomeworkStatsVO;

/**
 * 作业服务接口
 */
public interface HomeworkService extends IService<Homework> {

    /**
     * 分页查询作业列表
     */
    IPage<Homework> getHomeworkPage(IPage<Homework> page, Homework query);

    /**
     * 创建作业
     */
    boolean createHomework(Homework homework);

    /**
     * 发布作业
     */
    boolean publish(Long id);

    /**
     * 关闭作业
     */
    boolean close(Long id);

    /**
     * 删除作业
     */
    boolean deleteHomework(Long id);

    /**
     * 获取作业统计信息
     */
    HomeworkStatsVO getHomeworkStats(Long homeworkId);
}
