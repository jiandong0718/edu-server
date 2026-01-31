package com.edu.student.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.student.domain.entity.StudentTag;
import com.edu.student.mapper.StudentTagMapper;
import com.edu.student.service.StudentTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 学员标签服务实现
 *
 * @author edu
 * @since 2024-01-30
 */
@Service
@RequiredArgsConstructor
public class StudentTagServiceImpl extends ServiceImpl<StudentTagMapper, StudentTag> implements StudentTagService {
}
