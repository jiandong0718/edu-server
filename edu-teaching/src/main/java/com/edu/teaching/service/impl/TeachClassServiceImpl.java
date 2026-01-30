package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.TeachClass;
import com.edu.teaching.mapper.TeachClassMapper;
import com.edu.teaching.service.TeachClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 班级服务实现
 */
@Service
@RequiredArgsConstructor
public class TeachClassServiceImpl extends ServiceImpl<TeachClassMapper, TeachClass> implements TeachClassService {

    @Override
    public IPage<TeachClass> pageList(IPage<TeachClass> page, TeachClass query) {
        return baseMapper.selectClassPage(page, query);
    }

    @Override
    public boolean start(Long id) {
        TeachClass teachClass = getById(id);
        if (teachClass == null) {
            throw new BusinessException("班级不存在");
        }
        if (!"pending".equals(teachClass.getStatus())) {
            throw new BusinessException("只有待开班状态的班级才能开班");
        }
        teachClass.setStatus("ongoing");
        return updateById(teachClass);
    }

    @Override
    public boolean finish(Long id) {
        TeachClass teachClass = getById(id);
        if (teachClass == null) {
            throw new BusinessException("班级不存在");
        }
        if (!"ongoing".equals(teachClass.getStatus())) {
            throw new BusinessException("只有进行中的班级才能结班");
        }
        teachClass.setStatus("finished");
        return updateById(teachClass);
    }

    @Override
    public boolean cancel(Long id) {
        TeachClass teachClass = getById(id);
        if (teachClass == null) {
            throw new BusinessException("班级不存在");
        }
        if ("finished".equals(teachClass.getStatus())) {
            throw new BusinessException("已结班的班级不能取消");
        }
        teachClass.setStatus("cancelled");
        return updateById(teachClass);
    }

    @Override
    public boolean addStudents(Long classId, List<Long> studentIds) {
        // TODO: 实现学员分班逻辑
        TeachClass teachClass = getById(classId);
        if (teachClass == null) {
            throw new BusinessException("班级不存在");
        }
        if (teachClass.getCurrentCount() + studentIds.size() > teachClass.getCapacity()) {
            throw new BusinessException("超出班级容量");
        }
        // 更新当前人数
        teachClass.setCurrentCount(teachClass.getCurrentCount() + studentIds.size());
        return updateById(teachClass);
    }

    @Override
    public boolean removeStudent(Long classId, Long studentId) {
        // TODO: 实现学员退班逻辑
        TeachClass teachClass = getById(classId);
        if (teachClass == null) {
            throw new BusinessException("班级不存在");
        }
        teachClass.setCurrentCount(Math.max(0, teachClass.getCurrentCount() - 1));
        return updateById(teachClass);
    }
}
