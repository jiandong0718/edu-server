package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.framework.security.SecurityContextHolder;
import com.edu.teaching.domain.dto.TeacherStatusChangeDTO;
import com.edu.teaching.domain.entity.Teacher;
import com.edu.teaching.domain.entity.TeacherStatusLog;
import com.edu.teaching.domain.enums.TeacherStatus;
import com.edu.teaching.domain.vo.TeacherStatusLogVO;
import com.edu.teaching.event.TeacherStatusChangeEvent;
import com.edu.teaching.mapper.TeacherStatusLogMapper;
import com.edu.teaching.service.TeacherService;
import com.edu.teaching.service.TeacherStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师状态管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherStatusServiceImpl extends ServiceImpl<TeacherStatusLogMapper, TeacherStatusLog>
        implements TeacherStatusService {

    private final TeacherService teacherService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(TeacherStatusChangeDTO dto) {
        // 1. 验证教师是否存在
        Teacher teacher = teacherService.getById(dto.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }

        // 2. 验证新状态是否有效
        if (!TeacherStatus.isValid(dto.getToStatus())) {
            throw new BusinessException("无效的教师状态");
        }

        // 3. 获取当前状态
        String fromStatus = teacher.getStatus();
        String toStatus = dto.getToStatus();

        // 4. 检查状态是否相同
        if (fromStatus != null && fromStatus.equals(toStatus)) {
            throw new BusinessException("教师当前已是该状态，无需变更");
        }

        // 5. 验证休假状态必须填写预计返回日期
        if ("on_leave".equals(toStatus) && dto.getExpectedReturnDate() == null) {
            throw new BusinessException("休假状态必须填写预计返回日期");
        }

        // 6. 验证生效日期
        LocalDate effectiveDate = dto.getEffectiveDate();
        if (effectiveDate == null) {
            effectiveDate = LocalDate.now();
        }

        // 7. 验证预计返回日期必须大于生效日期
        if (dto.getExpectedReturnDate() != null &&
            dto.getExpectedReturnDate().isBefore(effectiveDate)) {
            throw new BusinessException("预计返回日期必须大于或等于生效日期");
        }

        // 8. 更新教师状态
        Teacher updateTeacher = new Teacher();
        updateTeacher.setId(teacher.getId());
        updateTeacher.setStatus(toStatus);
        boolean updated = teacherService.updateById(updateTeacher);

        if (!updated) {
            throw new BusinessException("更新教师状态失败");
        }

        // 9. 获取当前操作人信息
        Long operatorId = SecurityContextHolder.getUserId();
        String operatorName = SecurityContextHolder.getLoginUser() != null ?
                SecurityContextHolder.getLoginUser().getRealName() : null;

        // 10. 记录状态变更日志
        TeacherStatusLog log = new TeacherStatusLog();
        log.setTeacherId(teacher.getId());
        log.setTeacherName(teacher.getName());
        log.setTeacherNo(teacher.getTeacherNo());
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setReason(dto.getReason());
        log.setEffectiveDate(effectiveDate);
        log.setExpectedReturnDate(dto.getExpectedReturnDate());
        log.setCampusId(teacher.getCampusId());
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setRemark(dto.getRemark());
        // 其他字段由MyBatis-Plus自动填充
        save(log);

        // 11. 发布状态变更事件
        TeacherStatusChangeEvent event = new TeacherStatusChangeEvent(
                this,
                teacher.getId(),
                teacher.getName(),
                teacher.getTeacherNo(),
                fromStatus,
                toStatus,
                dto.getReason(),
                effectiveDate,
                dto.getExpectedReturnDate(),
                teacher.getCampusId(),
                operatorId,
                operatorName
        );
        eventPublisher.publishEvent(event);

        log.info("教师状态变更成功: teacherId={}, teacherName={}, fromStatus={}, toStatus={}, reason={}",
                teacher.getId(), teacher.getName(), fromStatus, toStatus, dto.getReason());

        return true;
    }

    @Override
    public String getCurrentStatus(Long teacherId) {
        Teacher teacher = teacherService.getById(teacherId);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        return teacher.getStatus();
    }

    @Override
    public Page<TeacherStatusLogVO> pageStatusLog(Integer pageNum, Integer pageSize,
                                                   Long teacherId, String teacherName,
                                                   String status, Long campusId) {
        Page<TeacherStatusLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TeacherStatusLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(teacherId != null, TeacherStatusLog::getTeacherId, teacherId)
                .like(teacherName != null, TeacherStatusLog::getTeacherName, teacherName)
                .eq(status != null, TeacherStatusLog::getToStatus, status)
                .eq(campusId != null, TeacherStatusLog::getCampusId, campusId)
                .orderByDesc(TeacherStatusLog::getCreateTime);

        page(page, wrapper);

        // 转换为VO
        Page<TeacherStatusLogVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<TeacherStatusLogVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<TeacherStatusLogVO> getStatusLogList(Long teacherId) {
        LambdaQueryWrapper<TeacherStatusLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStatusLog::getTeacherId, teacherId)
                .orderByDesc(TeacherStatusLog::getCreateTime);

        List<TeacherStatusLog> logs = list(wrapper);
        return logs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherStatusLogVO getLatestStatusLog(Long teacherId) {
        LambdaQueryWrapper<TeacherStatusLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStatusLog::getTeacherId, teacherId)
                .orderByDesc(TeacherStatusLog::getCreateTime)
                .last("LIMIT 1");

        TeacherStatusLog log = getOne(wrapper);
        return log != null ? convertToVO(log) : null;
    }

    @Override
    public Map<Long, String> batchGetStatus(List<Long> teacherIds) {
        if (teacherIds == null || teacherIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Teacher> teachers = teacherService.listByIds(teacherIds);
        return teachers.stream()
                .collect(Collectors.toMap(Teacher::getId, Teacher::getStatus));
    }

    /**
     * 转换为VO
     */
    private TeacherStatusLogVO convertToVO(TeacherStatusLog log) {
        TeacherStatusLogVO vo = new TeacherStatusLogVO();
        vo.setId(log.getId());
        vo.setTeacherId(log.getTeacherId());
        vo.setTeacherName(log.getTeacherName());
        vo.setTeacherNo(log.getTeacherNo());
        vo.setFromStatus(log.getFromStatus());
        vo.setFromStatusName(getStatusName(log.getFromStatus()));
        vo.setToStatus(log.getToStatus());
        vo.setToStatusName(getStatusName(log.getToStatus()));
        vo.setReason(log.getReason());
        vo.setEffectiveDate(log.getEffectiveDate());
        vo.setExpectedReturnDate(log.getExpectedReturnDate());
        vo.setOperatorId(log.getOperatorId());
        vo.setOperatorName(log.getOperatorName());
        vo.setCampusId(log.getCampusId());
        vo.setRemark(log.getRemark());
        vo.setCreateTime(log.getCreateTime());
        vo.setUpdateTime(log.getUpdateTime());
        return vo;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(String statusCode) {
        if (statusCode == null) {
            return null;
        }
        try {
            return TeacherStatus.fromCode(statusCode).getName();
        } catch (IllegalArgumentException e) {
            return statusCode;
        }
    }
}
