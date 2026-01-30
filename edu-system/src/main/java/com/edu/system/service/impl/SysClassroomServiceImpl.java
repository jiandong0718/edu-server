package com.edu.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.system.domain.entity.SysClassroom;
import com.edu.system.mapper.SysClassroomMapper;
import com.edu.system.service.SysClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 教室服务实现
 */
@Service
@RequiredArgsConstructor
public class SysClassroomServiceImpl extends ServiceImpl<SysClassroomMapper, SysClassroom> implements SysClassroomService {

    @Override
    public boolean checkNameUnique(String name, Long campusId, Long id) {
        LambdaQueryWrapper<SysClassroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysClassroom::getName, name)
                .eq(SysClassroom::getCampusId, campusId);
        if (id != null) {
            wrapper.ne(SysClassroom::getId, id);
        }
        return count(wrapper) == 0;
    }
}
