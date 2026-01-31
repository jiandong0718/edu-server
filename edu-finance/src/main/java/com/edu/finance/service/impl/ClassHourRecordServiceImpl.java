package com.edu.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.finance.domain.entity.ClassHourRecord;
import com.edu.finance.mapper.ClassHourRecordMapper;
import com.edu.finance.service.ClassHourRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课时消耗记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassHourRecordServiceImpl extends ServiceImpl<ClassHourRecordMapper, ClassHourRecord> implements ClassHourRecordService {

    @Override
    public List<ClassHourRecord> getByStudentId(Long studentId, Long accountId) {
        return baseMapper.selectByStudentId(studentId, accountId);
    }
}
