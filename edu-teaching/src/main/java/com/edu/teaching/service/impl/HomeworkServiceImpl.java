package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Homework;
import com.edu.teaching.domain.vo.HomeworkStatsVO;
import com.edu.teaching.mapper.HomeworkMapper;
import com.edu.teaching.service.HomeworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 作业服务实现
 */
@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

    @Override
    public IPage<Homework> getHomeworkPage(IPage<Homework> page, Homework query) {
        return baseMapper.selectHomeworkPage(page, query);
    }

    @Override
    public boolean createHomework(Homework homework) {
        homework.setStatus("draft");
        return save(homework);
    }

    @Override
    public boolean publish(Long id) {
        Homework homework = getById(id);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        if (!"draft".equals(homework.getStatus())) {
            throw new BusinessException("只能发布草稿状态的作业");
        }

        homework.setStatus("published");
        return updateById(homework);
    }

    @Override
    public boolean close(Long id) {
        Homework homework = getById(id);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        if (!"published".equals(homework.getStatus())) {
            throw new BusinessException("只能关闭已发布的作业");
        }

        homework.setStatus("closed");
        return updateById(homework);
    }

    @Override
    public boolean deleteHomework(Long id) {
        Homework homework = getById(id);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        if ("published".equals(homework.getStatus())) {
            throw new BusinessException("已发布的作业不能删除");
        }

        return removeById(id);
    }

    @Override
    public HomeworkStatsVO getHomeworkStats(Long homeworkId) {
        return baseMapper.selectHomeworkStats(homeworkId);
    }
}
