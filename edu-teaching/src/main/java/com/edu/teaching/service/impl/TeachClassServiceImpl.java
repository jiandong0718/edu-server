package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.dto.BatchClassGraduationDTO;
import com.edu.teaching.domain.dto.BatchClassPromotionDTO;
import com.edu.teaching.domain.dto.ClassGraduationDTO;
import com.edu.teaching.domain.dto.ClassPromotionDTO;
import com.edu.teaching.domain.entity.ClassStudent;
import com.edu.teaching.domain.entity.TeachClass;
import com.edu.teaching.domain.vo.BatchClassGraduationResultVO;
import com.edu.teaching.domain.vo.BatchClassPromotionResultVO;
import com.edu.teaching.domain.vo.ClassGraduationResultVO;
import com.edu.teaching.domain.vo.ClassPromotionResultVO;
import com.edu.teaching.event.ClassGraduationEvent;
import com.edu.teaching.event.ClassPromotionEvent;
import com.edu.teaching.mapper.ClassStudentMapper;
import com.edu.teaching.mapper.TeachClassMapper;
import com.edu.teaching.service.TeachClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 班级服务实现
 */
@Service
@RequiredArgsConstructor
public class TeachClassServiceImpl extends ServiceImpl<TeachClassMapper, TeachClass> implements TeachClassService {

    private final ClassStudentMapper classStudentMapper;
    private final ApplicationEventPublisher eventPublisher;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassPromotionResultVO promoteClass(ClassPromotionDTO dto) {
        ClassPromotionResultVO result = new ClassPromotionResultVO();
        result.setPromotionTime(LocalDateTime.now());
        result.setSuccess(false);

        try {
            // 1. 验证原班级
            TeachClass originalClass = getById(dto.getClassId());
            if (originalClass == null) {
                result.setFailureReason("原班级不存在");
                return result;
            }

            if (!"ongoing".equals(originalClass.getStatus()) && !"finished".equals(originalClass.getStatus())) {
                result.setFailureReason("只有进行中或已结班的班级才能升班");
                return result;
            }

            result.setOriginalClassId(originalClass.getId());
            result.setOriginalClassName(originalClass.getName());

            // 2. 查询班级所有在读学员
            List<ClassStudent> classStudents = classStudentMapper.selectList(
                    new LambdaQueryWrapper<ClassStudent>()
                            .eq(ClassStudent::getClassId, dto.getClassId())
                            .eq(ClassStudent::getStatus, "active")
            );

            if (classStudents.isEmpty()) {
                result.setFailureReason("班级没有在读学员，无法升班");
                return result;
            }

            List<Long> studentIds = classStudents.stream()
                    .map(ClassStudent::getStudentId)
                    .collect(Collectors.toList());

            // 3. 处理目标班级
            TeachClass targetClass;
            boolean newClassCreated = false;

            if (dto.getTargetClassId() != null) {
                // 转入已有班级
                targetClass = getById(dto.getTargetClassId());
                if (targetClass == null) {
                    result.setFailureReason("目标班级不存在");
                    return result;
                }

                if (!"pending".equals(targetClass.getStatus()) && !"ongoing".equals(targetClass.getStatus())) {
                    result.setFailureReason("目标班级状态不正确，只能转入待开班或进行中的班级");
                    return result;
                }

                // 检查容量
                if (targetClass.getCurrentCount() + studentIds.size() > targetClass.getCapacity()) {
                    result.setFailureReason("目标班级容量不足");
                    return result;
                }
            } else {
                // 创建新班级
                if (!StringUtils.hasText(dto.getNewClassName()) || !StringUtils.hasText(dto.getNewClassCode())) {
                    result.setFailureReason("创建新班级时，班级名称和编码不能为空");
                    return result;
                }

                targetClass = new TeachClass();
                targetClass.setName(dto.getNewClassName());
                targetClass.setCode(dto.getNewClassCode());
                targetClass.setCourseId(dto.getTargetCourseId());
                targetClass.setTeacherId(dto.getTeacherId() != null ? dto.getTeacherId() : originalClass.getTeacherId());
                targetClass.setAssistantId(dto.getAssistantId() != null ? dto.getAssistantId() : originalClass.getAssistantId());
                targetClass.setClassroomId(dto.getClassroomId() != null ? dto.getClassroomId() : originalClass.getClassroomId());
                targetClass.setCampusId(originalClass.getCampusId());
                targetClass.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : originalClass.getCapacity());
                targetClass.setCurrentCount(0);
                targetClass.setStatus("pending");
                targetClass.setRemark("由班级【" + originalClass.getName() + "】升班创建");

                save(targetClass);
                newClassCreated = true;
            }

            result.setTargetClassId(targetClass.getId());
            result.setTargetClassName(targetClass.getName());
            result.setNewClassCreated(newClassCreated);

            // 4. 转移学员
            LocalDate now = LocalDate.now();
            for (ClassStudent classStudent : classStudents) {
                // 更新原班级学员状态为已退出
                classStudent.setStatus("left");
                classStudent.setLeaveDate(now);
                classStudentMapper.updateById(classStudent);

                // 添加到新班级
                ClassStudent newClassStudent = new ClassStudent();
                newClassStudent.setClassId(targetClass.getId());
                newClassStudent.setStudentId(classStudent.getStudentId());
                newClassStudent.setJoinDate(now);
                newClassStudent.setStatus("active");
                classStudentMapper.insert(newClassStudent);
            }

            // 5. 更新班级人数
            originalClass.setCurrentCount(0);
            if (dto.getKeepOriginalClass()) {
                // 保留原班级，标记为已结班
                originalClass.setStatus("finished");
                originalClass.setEndDate(now);
                originalClass.setRemark((originalClass.getRemark() != null ? originalClass.getRemark() + "；" : "")
                        + "已升班至【" + targetClass.getName() + "】");
                updateById(originalClass);
            } else {
                // 删除原班级
                removeById(originalClass.getId());
            }

            targetClass.setCurrentCount(targetClass.getCurrentCount() + studentIds.size());
            updateById(targetClass);

            result.setTransferredStudentCount(studentIds.size());
            result.setTransferredStudentIds(studentIds);
            result.setSuccess(true);

            // 6. 发布升班事件
            eventPublisher.publishEvent(new ClassPromotionEvent(
                    this,
                    originalClass.getId(),
                    originalClass.getName(),
                    targetClass.getId(),
                    targetClass.getName(),
                    studentIds,
                    newClassCreated,
                    dto.getRemark()
            ));

        } catch (Exception e) {
            result.setFailureReason("升班失败：" + e.getMessage());
            throw e;
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchClassPromotionResultVO batchPromoteClass(BatchClassPromotionDTO dto) {
        BatchClassPromotionResultVO batchResult = new BatchClassPromotionResultVO();
        List<ClassPromotionResultVO> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (Long classId : dto.getClassIds()) {
            ClassPromotionDTO promotionDTO = new ClassPromotionDTO();
            promotionDTO.setClassId(classId);
            promotionDTO.setTargetCourseId(dto.getTargetCourseId());
            promotionDTO.setKeepOriginalClass(dto.getKeepOriginalClass());
            promotionDTO.setRemark(dto.getRemark());

            // 自动生成新班级名称和编码
            TeachClass originalClass = getById(classId);
            if (originalClass != null) {
                promotionDTO.setNewClassName(originalClass.getName() + "-升级班");
                promotionDTO.setNewClassCode(originalClass.getCode() + "-UP");
            }

            try {
                ClassPromotionResultVO result = promoteClass(promotionDTO);
                results.add(result);
                if (result.getSuccess()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                ClassPromotionResultVO errorResult = new ClassPromotionResultVO();
                errorResult.setOriginalClassId(classId);
                errorResult.setSuccess(false);
                errorResult.setFailureReason(e.getMessage());
                results.add(errorResult);
                failureCount++;
            }
        }

        batchResult.setTotal(dto.getClassIds().size());
        batchResult.setSuccessCount(successCount);
        batchResult.setFailureCount(failureCount);
        batchResult.setResults(results);

        return batchResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassGraduationResultVO graduateClass(ClassGraduationDTO dto) {
        ClassGraduationResultVO result = new ClassGraduationResultVO();
        result.setGraduationTime(LocalDateTime.now());
        result.setSuccess(false);

        try {
            // 1. 验证班级
            TeachClass teachClass = getById(dto.getClassId());
            if (teachClass == null) {
                result.setFailureReason("班级不存在");
                return result;
            }

            if (!"ongoing".equals(teachClass.getStatus())) {
                result.setFailureReason("只有进行中的班级才能结业");
                return result;
            }

            result.setClassId(teachClass.getId());
            result.setClassName(teachClass.getName());

            // 2. 查询班级所有在读学员
            List<ClassStudent> classStudents = classStudentMapper.selectList(
                    new LambdaQueryWrapper<ClassStudent>()
                            .eq(ClassStudent::getClassId, dto.getClassId())
                            .eq(ClassStudent::getStatus, "active")
            );

            if (classStudents.isEmpty()) {
                result.setFailureReason("班级没有在读学员");
                return result;
            }

            List<Long> studentIds = classStudents.stream()
                    .map(ClassStudent::getStudentId)
                    .collect(Collectors.toList());

            // 3. 更新学员状态为已结业
            LocalDate graduationDate = dto.getGraduationDate() != null ? dto.getGraduationDate() : LocalDate.now();
            for (ClassStudent classStudent : classStudents) {
                classStudent.setStatus("graduated");
                classStudent.setLeaveDate(graduationDate);
                classStudentMapper.updateById(classStudent);
            }

            // 4. 更新班级状态为已结班
            teachClass.setStatus("finished");
            teachClass.setEndDate(graduationDate);
            teachClass.setRemark((teachClass.getRemark() != null ? teachClass.getRemark() + "；" : "")
                    + "结业日期：" + graduationDate);
            if (StringUtils.hasText(dto.getGraduationComment())) {
                teachClass.setRemark(teachClass.getRemark() + "；结业评语：" + dto.getGraduationComment());
            }
            updateById(teachClass);

            // 5. 生成结业统计数据
            result.setGraduationDate(graduationDate);
            result.setGraduatedStudentCount(studentIds.size());
            result.setGraduatedStudentIds(studentIds);

            // TODO: 查询课时统计数据
            result.setTotalLessons(0);
            result.setCompletedLessons(0);
            result.setAttendanceRate(0.0);

            // 6. 生成结业证书（可选）
            List<Long> certificateIds = new ArrayList<>();
            if (dto.getGenerateCertificate()) {
                // TODO: 实现结业证书生成逻辑
                // 这里可以调用证书生成服务
                result.setCertificateGenerated(true);
                result.setCertificateIds(certificateIds);
            } else {
                result.setCertificateGenerated(false);
            }

            result.setSuccess(true);

            // 7. 发布结业事件
            eventPublisher.publishEvent(new ClassGraduationEvent(
                    this,
                    teachClass.getId(),
                    teachClass.getName(),
                    graduationDate,
                    studentIds,
                    dto.getGenerateCertificate(),
                    certificateIds,
                    dto.getGraduationComment(),
                    dto.getRemark()
            ));

        } catch (Exception e) {
            result.setFailureReason("结业失败：" + e.getMessage());
            throw e;
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchClassGraduationResultVO batchGraduateClass(BatchClassGraduationDTO dto) {
        BatchClassGraduationResultVO batchResult = new BatchClassGraduationResultVO();
        List<ClassGraduationResultVO> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (Long classId : dto.getClassIds()) {
            ClassGraduationDTO graduationDTO = new ClassGraduationDTO();
            graduationDTO.setClassId(classId);
            graduationDTO.setGraduationDate(dto.getGraduationDate());
            graduationDTO.setGenerateCertificate(dto.getGenerateCertificate());
            graduationDTO.setGraduationComment(dto.getGraduationComment());
            graduationDTO.setRemark(dto.getRemark());

            try {
                ClassGraduationResultVO result = graduateClass(graduationDTO);
                results.add(result);
                if (result.getSuccess()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                ClassGraduationResultVO errorResult = new ClassGraduationResultVO();
                errorResult.setClassId(classId);
                errorResult.setSuccess(false);
                errorResult.setFailureReason(e.getMessage());
                results.add(errorResult);
                failureCount++;
            }
        }

        batchResult.setTotal(dto.getClassIds().size());
        batchResult.setSuccessCount(successCount);
        batchResult.setFailureCount(failureCount);
        batchResult.setResults(results);

        return batchResult;
    }
}
