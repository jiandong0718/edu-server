package com.edu.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.mapper.SysCampusMapper;
import com.edu.system.service.SysCampusService;
import org.springframework.stereotype.Service;

/**
 * 校区服务实现
 */
@Service
public class SysCampusServiceImpl extends ServiceImpl<SysCampusMapper, SysCampus> implements SysCampusService {

    @Override
    public boolean checkCodeUnique(String code, Long campusId) {
        LambdaQueryWrapper<SysCampus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCampus::getCode, code);
        if (campusId != null) {
            wrapper.ne(SysCampus::getId, campusId);
        }
        return count(wrapper) == 0;
    }
}
