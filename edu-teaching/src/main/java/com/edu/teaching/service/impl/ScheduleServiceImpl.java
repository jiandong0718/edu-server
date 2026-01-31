package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.service.SysHolidayService;
import com.edu.teaching.domain.dto.BatchRescheduleDTO;
import com.edu.teaching.domain.dto.BatchScheduleDTO;
import com.edu.teaching.domain.dto.CancelScheduleDTO;
import com.edu.teaching.domain.dto.RescheduleDTO;
import com.edu.teaching.domain.dto.ScheduleConflictCheckDTO;
import com.edu.teaching.domain.dto.SubstituteTeacherDTO;
import com.edu.teaching.domain.entity.Schedule;
import com.edu.teaching.domain.entity.ScheduleHistory;
import com.edu.teaching.domain.entity.TeachClass;
import com.edu.teaching.domain.entity.Teacher;
import com.edu.teaching.domain.vo.BatchScheduleResultVO;
import com.edu.teaching.domain.vo.ScheduleConflictVO;
import com.edu.teaching.event.CancelScheduleEvent;
import com.edu.teaching.event.ScheduleRescheduleEvent;
import com.edu.teaching.event.SubstituteTeacherEvent;
import com.edu.teaching.mapper.ScheduleHistoryMapper;
import com.edu.teaching.mapper.ScheduleMapper;
import com.edu.teaching.service.ScheduleService;
import com.edu.teaching.service.TeachClassService;
import com.edu.teaching.service.TeacherAvailableTimeService;
import com.edu.teaching.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 排课服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    private final TeachClassService teachClassService;
    private final SysHolidayService sysHolidayService;
    private final TeacherService teacherService;
    private final TeacherAvailableTimeService teacherAvailableTimeService;
    private final ScheduleHistoryMapper scheduleHistoryMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<Schedule> getScheduleList(Long campusId, LocalDate startDate, LocalDate endDate,
                                          Long teacherId, Long classId, Long classroomId) {
        return baseMapper.selectScheduleList(campusId, startDate, endDate, teacherId, classId, classroomId);
    }

    @Override
    public boolean createSchedule(Schedule schedule) {
        // 检查冲突
        if (checkConflict(schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime(),
                schedule.getTeacherId(), schedule.getClassroomId(), null)) {
            throw new BusinessException("排课时间冲突，请检查教师或教室是否已被占用");
        }
        schedule.setStatus("scheduled");
        return save(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateSchedule(Long classId, LocalDate startDate, LocalDate endDate,
                                       List<Integer> weekdays, LocalTime startTime, LocalTime endTime) {
        // 遍历日期范围，按星期几生成排课
        LocalDate current = startDate;
        int lessonNo = 1;

        while (!current.isAfter(endDate)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (weekdays.contains(dayOfWeek.getValue())) {
                Schedule schedule = new Schedule();
                schedule.setClassId(classId);
                schedule.setScheduleDate(current);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setLessonNo(lessonNo++);
                schedule.setStatus("scheduled");

                // TODO: 从班级获取课程、教师、教室等信息
                save(schedule);
            }
            current = current.plusDays(1);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchScheduleResultVO batchCreateScheduleEnhanced(BatchScheduleDTO dto) {
        log.info("开始批量排课，班级ID: {}, 开始日期: {}, 结束日期: {}, 总课次: {}",
                dto.getClassId(), dto.getStartDate(), dto.getEndDate(), dto.getTotalLessons());

        // 1. 验证参数
        validateBatchScheduleParams(dto);

        // 2. 获取班级信息
        TeachClass teachClass = teachClassService.getById(dto.getClassId());
        if (teachClass == null) {
            throw new BusinessException("班级不存在");
        }

        // 3. 设置默认值
        Long courseId = dto.getCourseId() != null ? dto.getCourseId() : teachClass.getCourseId();
        Long teacherId = dto.getTeacherId() != null ? dto.getTeacherId() : teachClass.getTeacherId();
        Long classroomId = dto.getClassroomId() != null ? dto.getClassroomId() : teachClass.getClassroomId();
        Long campusId = teachClass.getCampusId();
        Integer classHours = dto.getClassHours() != null ? dto.getClassHours() : 2;
        boolean skipHolidays = dto.getSkipHolidays() != null ? dto.getSkipHolidays() : true;
        boolean skipWeekends = dto.getSkipWeekends() != null ? dto.getSkipWeekends() : false;

        // 4. 生成排课日期列表
        List<LocalDate> scheduleDates = generateScheduleDates(dto, skipHolidays, skipWeekends, campusId);

        // 5. 批量创建排课
        BatchScheduleResultVO.BatchScheduleResultVOBuilder resultBuilder = BatchScheduleResultVO.builder();
        List<Long> scheduleIds = new ArrayList<>();
        List<BatchScheduleResultVO.SkippedDateInfo> skippedDates = new ArrayList<>();
        List<BatchScheduleResultVO.FailedDateInfo> failedDates = new ArrayList<>();

        int lessonNo = 1;
        List<Schedule> schedulesToSave = new ArrayList<>();

        for (LocalDate date : scheduleDates) {
            try {
                // 检查是否为节假日
                if (skipHolidays && sysHolidayService.isHoliday(date, campusId)) {
                    skippedDates.add(BatchScheduleResultVO.SkippedDateInfo.builder()
                            .date(date)
                            .reason("节假日")
                            .build());
                    continue;
                }

                // 检查是否为调休工作日
                if (skipHolidays && sysHolidayService.isWorkday(date, campusId)) {
                    // 调休工作日，正常排课
                }

                // 检查是否跳过周末
                if (skipWeekends && isWeekend(date)) {
                    skippedDates.add(BatchScheduleResultVO.SkippedDateInfo.builder()
                            .date(date)
                            .reason("周末")
                            .build());
                    continue;
                }

                // 检查排课冲突
                if (checkConflict(date, dto.getStartTime(), dto.getEndTime(), teacherId, classroomId, null)) {
                    skippedDates.add(BatchScheduleResultVO.SkippedDateInfo.builder()
                            .date(date)
                            .reason("时间冲突（教师或教室已被占用）")
                            .build());
                    continue;
                }

                // 创建排课记录
                Schedule schedule = new Schedule();
                schedule.setClassId(dto.getClassId());
                schedule.setCourseId(courseId);
                schedule.setTeacherId(teacherId);
                schedule.setClassroomId(classroomId);
                schedule.setCampusId(campusId);
                schedule.setScheduleDate(date);
                schedule.setStartTime(dto.getStartTime());
                schedule.setEndTime(dto.getEndTime());
                schedule.setClassHours(classHours);
                schedule.setLessonNo(lessonNo++);
                schedule.setStatus("scheduled");

                // 设置课节主题
                if (dto.getTopicPrefix() != null && !dto.getTopicPrefix().isEmpty()) {
                    schedule.setTopic(dto.getTopicPrefix().replace("{n}", String.valueOf(schedule.getLessonNo())));
                }

                schedule.setRemark(dto.getRemark());

                schedulesToSave.add(schedule);

            } catch (Exception e) {
                log.error("创建排课失败，日期: {}, 错误: {}", date, e.getMessage(), e);
                failedDates.add(BatchScheduleResultVO.FailedDateInfo.builder()
                        .date(date)
                        .reason(e.getMessage())
                        .build());
            }
        }

        // 批量保存排课记录
        if (!schedulesToSave.isEmpty()) {
            saveBatch(schedulesToSave);
            schedulesToSave.forEach(schedule -> scheduleIds.add(schedule.getId()));
        }

        // 6. 构建返回结果
        BatchScheduleResultVO result = resultBuilder
                .successCount(scheduleIds.size())
                .skippedCount(skippedDates.size())
                .failedCount(failedDates.size())
                .scheduleIds(scheduleIds)
                .skippedDates(skippedDates)
                .failedDates(failedDates)
                .build();

        log.info("批量排课完成，成功: {}, 跳过: {}, 失败: {}",
                result.getSuccessCount(), result.getSkippedCount(), result.getFailedCount());

        return result;
    }

    /**
     * 验证批量排课参数
     */
    private void validateBatchScheduleParams(BatchScheduleDTO dto) {
        if (dto.getEndDate() == null && dto.getTotalLessons() == null) {
            throw new BusinessException("结束日期和总课次必须至少指定一个");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        if (dto.getTotalLessons() != null && dto.getTotalLessons() <= 0) {
            throw new BusinessException("总课次必须大于0");
        }

        if (dto.getWeekdays() == null || dto.getWeekdays().isEmpty()) {
            throw new BusinessException("上课星期不能为空");
        }

        for (Integer weekday : dto.getWeekdays()) {
            if (weekday < 1 || weekday > 7) {
                throw new BusinessException("星期几必须在1-7之间");
            }
        }

        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new BusinessException("开始时间必须早于结束时间");
        }
    }

    /**
     * 生成排课日期列表
     */
    private List<LocalDate> generateScheduleDates(BatchScheduleDTO dto, boolean skipHolidays,
                                                   boolean skipWeekends, Long campusId) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();

        // 如果指定了总课次，则按课次生成
        if (dto.getTotalLessons() != null) {
            int count = 0;
            int maxIterations = dto.getTotalLessons() * 10; // 防止无限循环
            int iterations = 0;

            while (count < dto.getTotalLessons() && iterations < maxIterations) {
                iterations++;
                DayOfWeek dayOfWeek = current.getDayOfWeek();

                // 检查是否为指定的上课星期
                if (dto.getWeekdays().contains(dayOfWeek.getValue())) {
                    dates.add(current);
                    count++;
                }

                current = current.plusDays(1);

                // 如果有结束日期限制，不能超过
                if (endDate != null && current.isAfter(endDate)) {
                    break;
                }
            }
        } else {
            // 按日期范围生成
            while (!current.isAfter(endDate)) {
                DayOfWeek dayOfWeek = current.getDayOfWeek();

                // 检查是否为指定的上课星期
                if (dto.getWeekdays().contains(dayOfWeek.getValue())) {
                    dates.add(current);
                }

                current = current.plusDays(1);
            }
        }

        return dates;
    }

    /**
     * 判断是否为周末
     */
    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    @Override
    public boolean reschedule(Long id, LocalDate newDate, LocalTime newStartTime,
                              LocalTime newEndTime, Long newClassroomId) {
        Schedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 检查冲突
        Long classroomId = newClassroomId != null ? newClassroomId : schedule.getClassroomId();
        if (checkConflict(newDate, newStartTime, newEndTime, schedule.getTeacherId(), classroomId, id)) {
            throw new BusinessException("调课时间冲突");
        }

        schedule.setScheduleDate(newDate);
        schedule.setStartTime(newStartTime);
        schedule.setEndTime(newEndTime);
        if (newClassroomId != null) {
            schedule.setClassroomId(newClassroomId);
        }
        return updateById(schedule);
    }

    @Override
    public boolean substitute(Long id, Long newTeacherId) {
        Schedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 检查新教师是否有冲突
        if (checkConflict(schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime(),
                newTeacherId, schedule.getClassroomId(), id)) {
            throw new BusinessException("代课教师在该时间段已有其他课程");
        }

        schedule.setTeacherId(newTeacherId);
        return updateById(schedule);
    }

    @Override
    public boolean cancelSchedule(Long id) {
        Schedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }
        schedule.setStatus("cancelled");
        return updateById(schedule);
    }

    @Override
    public boolean checkConflict(LocalDate scheduleDate, LocalTime startTime, LocalTime endTime,
                                 Long teacherId, Long classroomId, Long excludeId) {
        return baseMapper.checkConflict(scheduleDate, startTime, endTime, teacherId, classroomId, excludeId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean substituteTeacher(SubstituteTeacherDTO dto) {
        log.info("开始代课操作，排课ID: {}, 代课教师ID: {}", dto.getScheduleId(), dto.getSubstituteTeacherId());

        // 1. 获取排课信息
        Schedule schedule = getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        if ("cancelled".equals(schedule.getStatus())) {
            throw new BusinessException("已取消的课程不能代课");
        }

        if ("finished".equals(schedule.getStatus())) {
            throw new BusinessException("已完成的课程不能代课");
        }

        // 2. 检查代课教师是否存在且可用
        Teacher substituteTeacher = teacherService.getById(dto.getSubstituteTeacherId());
        if (substituteTeacher == null) {
            throw new BusinessException("代课教师不存在");
        }

        if (!"active".equals(substituteTeacher.getStatus())) {
            throw new BusinessException("代课教师状态不可用，当前状态：" + substituteTeacher.getStatus());
        }

        // 3. 检查代课教师是否有时间冲突
        if (!isTeacherAvailable(dto.getSubstituteTeacherId(), schedule.getScheduleDate(),
                schedule.getStartTime(), schedule.getEndTime(), dto.getScheduleId())) {
            throw new BusinessException("代课教师在该时间段已有其他课程安排");
        }

        // 4. 获取原教师信息
        Teacher originalTeacher = teacherService.getById(schedule.getTeacherId());
        String originalTeacherName = originalTeacher != null ? originalTeacher.getName() : "未知";

        // 5. 保存原教师ID并更新为代课教师
        Long originalTeacherId = schedule.getTeacherId();
        schedule.setOriginalTeacherId(originalTeacherId);
        schedule.setTeacherId(dto.getSubstituteTeacherId());
        schedule.setSubstituteReason(dto.getReason());
        if (dto.getRemark() != null) {
            schedule.setRemark(dto.getRemark());
        }

        boolean updated = updateById(schedule);

        // 6. 发布代课事件，用于通知相关人员
        if (updated) {
            SubstituteTeacherEvent event = new SubstituteTeacherEvent(
                    this,
                    schedule.getId(),
                    schedule.getClassId(),
                    schedule.getCourseId(),
                    originalTeacherId,
                    originalTeacherName,
                    dto.getSubstituteTeacherId(),
                    substituteTeacher.getName(),
                    schedule.getScheduleDate(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    dto.getReason(),
                    schedule.getCampusId()
            );
            eventPublisher.publishEvent(event);
            log.info("代课操作完成，已发布代课事件");
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelScheduleWithDetails(CancelScheduleDTO dto) {
        log.info("开始停课操作，排课ID: {}, 停课原因: ", dto.getScheduleId(), dto.getCancelReason());

        // 1. 获取排课信息
        Schedule schedule = getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        if ("cancelled".equals(schedule.getStatus())) {
            throw new BusinessException("课程已经取消，无需重复操作");
        }

        if ("finished".equals(schedule.getStatus())) {
            throw new BusinessException("已完成的课程不能取消");
        }

        // 2. 解析补课时间（如果需要补课）
        LocalDate makeupDate = null;
        LocalTime makeupStartTime = null;
        LocalTime makeupEndTime = null;

        if (Boolean.TRUE.equals(dto.getNeedMakeup())) {
            if (dto.getMakeupDate() == null || dto.getMakeupStartTime() == null || dto.getMakeupEndTime() == null) {
                throw new BusinessException("需要补课时，必须提供补课日期和时间");
            }

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                makeupDate = LocalDate.parse(dto.getMakeupDate(), dateFormatter);
                makeupStartTime = LocalTime.parse(dto.getMakeupStartTime(), timeFormatter);
                makeupEndTime = LocalTime.parse(dto.getMakeupEndTime(), timeFormatter);

                // 验证补课时间的合理性
                if (makeupDate.isBefore(LocalDate.now())) {
                    throw new BusinessException("补课日期不能早于今天");
                }

                if (makeupStartTime.isAfter(makeupEndTime) || makeupStartTime.equals(makeupEndTime)) {
                    throw new BusinessException("补课开始时间必须早于结束时间");
                }

                // 检查补课时间是否有冲突
                if (checkConflict(makeupDate, makeupStartTime, makeupEndTime,
                        schedule.getTeacherId(), schedule.getClassroomId(), null)) {
                    throw new BusinessException("补课时间与其他课程冲突，请重新选择时间");
                }
            } catch (Exception e) {
                if (e instanceof BusinessException) {
                    throw e;
                }
                throw new BusinessException("日期或时间格式错误：" + e.getMessage());
            }
        }

        // 3. 获取教师信息
        Teacher teacher = teacherService.getById(schedule.getTeacherId());
        String teacherName = teacher != null ? teacher.getName() : "未知";

        // 4. 更新排课状态
        schedule.setStatus("cancelled");
        schedule.setCancelReason(dto.getCancelReason());
        schedule.setNeedMakeup(dto.getNeedMakeup());
        schedule.setMakeupDate(makeupDate);
        schedule.setMakeupStartTime(makeupStartTime);
        schedule.setMakeupEndTime(makeupEndTime);
        if (dto.getRemark() != null) {
            schedule.setRemark(dto.getRemark());
        }

        boolean updated = updateById(schedule);

        // 5. 发布停课事件，用于通知相关人员
        if (updated) {
            CancelScheduleEvent event = new CancelScheduleEvent(
                    this,
                    schedule.getId(),
                    schedule.getClassId(),
                    schedule.getCourseId(),
                    schedule.getTeacherId(),
                    teacherName,
                    schedule.getScheduleDate(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    dto.getCancelReason(),
                    dto.getNeedMakeup(),
                    makeupDate,
                    makeupStartTime,
                    makeupEndTime,
                    schedule.getCampusId()
            );
            eventPublisher.publishEvent(event);
            log.info("停课操作完成，已发布停课事件");
        }

        return updated;
    }

    @Override
    public boolean isTeacherAvailable(Long teacherId, LocalDate scheduleDate,
                                      LocalTime startTime, LocalTime endTime,
                                      Long excludeScheduleId) {
        // 1. 检查教师是否存在
        Teacher teacher = teacherService.getById(teacherId);
        if (teacher == null) {
            log.warn("教师不存在，教师ID: {}", teacherId);
            return false;
        }

        // 2. 检查教师状态
        if (!"active".equals(teacher.getStatus())) {
            log.warn("教师状态不可用，教师ID: {}, 状态: {}", teacherId, teacher.getStatus());
            return false;
        }

        // 3. 检查时间冲突
        boolean hasConflict = checkConflict(scheduleDate, startTime, endTime, teacherId, null, excludeScheduleId);
        if (hasConflict) {
            log.warn("教师时间冲突，教师ID: {}, 日期: {}, 时间: {}-{}", teacherId, scheduleDate, startTime, endTime);
        }

        return !hasConflict;
    }

    @Override
    public ScheduleConflictVO checkConflictDetail(ScheduleConflictCheckDTO checkDTO) {
        log.info("开始详细冲突检测，日期: {}, 时间: {}-{}, 教师ID: {}, 教室ID: {}, 班级ID: {}",
                checkDTO.getScheduleDate(), checkDTO.getStartTime(), checkDTO.getEndTime(),
                checkDTO.getTeacherId(), checkDTO.getClassroomId(), checkDTO.getClassId());

        ScheduleConflictVO result = ScheduleConflictVO.builder()
                .hasConflict(false)
                .teacherConflicts(new ArrayList<>())
                .classroomConflicts(new ArrayList<>())
                .studentConflicts(new ArrayList<>())
                .build();

        // 1. 检查教师时间冲突
        List<Schedule> teacherConflictSchedules = baseMapper.selectTeacherConflicts(
                checkDTO.getScheduleDate(),
                checkDTO.getStartTime(),
                checkDTO.getEndTime(),
                checkDTO.getTeacherId(),
                checkDTO.getScheduleId()
        );

        if (!teacherConflictSchedules.isEmpty()) {
            result.setHasConflict(true);
            for (Schedule schedule : teacherConflictSchedules) {
                result.getTeacherConflicts().add(buildConflictDetail(schedule));
            }
            log.warn("检测到教师时间冲突，教师ID: {}, 冲突数量: {}", checkDTO.getTeacherId(), teacherConflictSchedules.size());
        }

        // 2. 检查教室时间冲突
        List<Schedule> classroomConflictSchedules = baseMapper.selectClassroomConflicts(
                checkDTO.getScheduleDate(),
                checkDTO.getStartTime(),
                checkDTO.getEndTime(),
                checkDTO.getClassroomId(),
                checkDTO.getScheduleId()
        );

        if (!classroomConflictSchedules.isEmpty()) {
            result.setHasConflict(true);
            for (Schedule schedule : classroomConflictSchedules) {
                result.getClassroomConflicts().add(buildConflictDetail(schedule));
            }
            log.warn("检测到教室时间冲突，教室ID: {}, 冲突数量: {}", checkDTO.getClassroomId(), classroomConflictSchedules.size());
        }

        // 3. 检查学员时间冲突
        List<Schedule> studentConflictSchedules = baseMapper.selectStudentConflictSchedules(
                checkDTO.getScheduleDate(),
                checkDTO.getStartTime(),
                checkDTO.getEndTime(),
                checkDTO.getClassId(),
                checkDTO.getScheduleId()
        );

        if (!studentConflictSchedules.isEmpty()) {
            result.setHasConflict(true);
            // 这里简化处理，实际应该查询具体哪些学员冲突
            for (Schedule schedule : studentConflictSchedules) {
                ScheduleConflictVO.StudentConflictDetail studentConflict = ScheduleConflictVO.StudentConflictDetail.builder()
                        .studentId(null) // 需要进一步查询具体学员
                        .studentName("班级学员")
                        .studentNo("")
                        .conflictSchedule(buildConflictDetail(schedule))
                        .build();
                result.getStudentConflicts().add(studentConflict);
            }
            log.warn("检测到学员时间冲突，班级ID: {}, 冲突数量: {}", checkDTO.getClassId(), studentConflictSchedules.size());
        }

        // 4. 检查教师可用时间配置
        ScheduleConflictVO.TeacherAvailabilityConflict teacherAvailabilityConflict =
                checkTeacherAvailability(checkDTO.getTeacherId(), checkDTO.getScheduleDate(),
                        checkDTO.getStartTime(), checkDTO.getEndTime());

        if (teacherAvailabilityConflict != null && teacherAvailabilityConflict.getHasConflict()) {
            result.setHasConflict(true);
            result.setTeacherAvailabilityConflict(teacherAvailabilityConflict);
            log.warn("检测到教师可用时间冲突，教师ID: {}, 原因: {}",
                    checkDTO.getTeacherId(), teacherAvailabilityConflict.getReason());
        }

        // 5. 检查教室状态
        ScheduleConflictVO.ClassroomStatusConflict classroomStatusConflict =
                checkClassroomStatus(checkDTO.getClassroomId());

        if (classroomStatusConflict != null && classroomStatusConflict.getHasConflict()) {
            result.setHasConflict(true);
            result.setClassroomStatusConflict(classroomStatusConflict);
            log.warn("检测到教室状态冲突，教室ID: {}, 原因: {}",
                    checkDTO.getClassroomId(), classroomStatusConflict.getReason());
        }

        log.info("冲突检测完成，是否有冲突: {}", result.getHasConflict());
        return result;
    }

    /**
     * 构建冲突详情
     */
    private ScheduleConflictVO.ConflictDetail buildConflictDetail(Schedule schedule) {
        return ScheduleConflictVO.ConflictDetail.builder()
                .scheduleId(schedule.getId())
                .classId(schedule.getClassId())
                .className(schedule.getClassName())
                .courseName(schedule.getCourseName())
                .scheduleDate(schedule.getScheduleDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .teacherId(schedule.getTeacherId())
                .teacherName(schedule.getTeacherName())
                .classroomId(schedule.getClassroomId())
                .classroomName(schedule.getClassroomName())
                .build();
    }

    /**
     * 检查教师可用时间配置
     */
    private ScheduleConflictVO.TeacherAvailabilityConflict checkTeacherAvailability(
            Long teacherId, LocalDate scheduleDate, LocalTime startTime, LocalTime endTime) {

        // 获取教师信息
        Teacher teacher = teacherService.getById(teacherId);
        if (teacher == null) {
            return ScheduleConflictVO.TeacherAvailabilityConflict.builder()
                    .hasConflict(true)
                    .teacherId(teacherId)
                    .teacherName("未知")
                    .reason("教师不存在")
                    .build();
        }

        // 检查教师状态
        if (!"active".equals(teacher.getStatus())) {
            return ScheduleConflictVO.TeacherAvailabilityConflict.builder()
                    .hasConflict(true)
                    .teacherId(teacherId)
                    .teacherName(teacher.getName())
                    .reason("教师状态不可用：" + teacher.getStatus())
                    .build();
        }

        // 检查教师可用时间配置
        int dayOfWeek = scheduleDate.getDayOfWeek().getValue();
        List<com.edu.teaching.domain.entity.TeacherAvailableTime> availableTimes =
                teacherAvailableTimeService.lambdaQuery()
                        .eq(com.edu.teaching.domain.entity.TeacherAvailableTime::getTeacherId, teacherId)
                        .eq(com.edu.teaching.domain.entity.TeacherAvailableTime::getDayOfWeek, dayOfWeek)
                        .eq(com.edu.teaching.domain.entity.TeacherAvailableTime::getStatus, 1)
                        .list();

        if (availableTimes.isEmpty()) {
            return ScheduleConflictVO.TeacherAvailabilityConflict.builder()
                    .hasConflict(true)
                    .teacherId(teacherId)
                    .teacherName(teacher.getName())
                    .reason("教师在该时间段（星期" + dayOfWeek + "）未配置可用时间")
                    .build();
        }

        // 检查时间段是否在可用时间范围内
        boolean isInAvailableTime = false;
        for (com.edu.teaching.domain.entity.TeacherAvailableTime availableTime : availableTimes) {
            LocalTime availStart = LocalTime.parse(availableTime.getStartTime());
            LocalTime availEnd = LocalTime.parse(availableTime.getEndTime());

            // 检查排课时间是否完全在可用时间范围内
            if (!startTime.isBefore(availStart) && !endTime.isAfter(availEnd)) {
                isInAvailableTime = true;
                break;
            }
        }

        if (!isInAvailableTime) {
            return ScheduleConflictVO.TeacherAvailabilityConflict.builder()
                    .hasConflict(true)
                    .teacherId(teacherId)
                    .teacherName(teacher.getName())
                    .reason("排课时间不在教师可用时间范围内")
                    .build();
        }

        // 没有冲突
        return ScheduleConflictVO.TeacherAvailabilityConflict.builder()
                .hasConflict(false)
                .teacherId(teacherId)
                .teacherName(teacher.getName())
                .build();
    }

    /**
     * 检查教室状态
     */
    private ScheduleConflictVO.ClassroomStatusConflict checkClassroomStatus(Long classroomId) {
        // 这里需要调用系统模块的教室服务
        // 由于跨模块调用，这里简化处理，实际应该通过API或事件方式调用

        // 简化实现：假设教室状态正常
        return ScheduleConflictVO.ClassroomStatusConflict.builder()
                .hasConflict(false)
                .classroomId(classroomId)
                .classroomName("")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rescheduleEnhanced(RescheduleDTO dto) {
        log.info("开始调课，排课ID: {}, 原因: {}", dto.getScheduleId(), dto.getReason());

        // 1. 获取原排课记录
        Schedule schedule = getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }

        // 2. 保存原始数据用于历史记录
        LocalDate oldScheduleDate = schedule.getScheduleDate();
        LocalTime oldStartTime = schedule.getStartTime();
        LocalTime oldEndTime = schedule.getEndTime();
        Long oldTeacherId = schedule.getTeacherId();
        Long oldClassroomId = schedule.getClassroomId();

        // 3. 确定新的值（如果没有提供则使用原值）
        LocalDate newScheduleDate = dto.getNewScheduleDate() != null ? dto.getNewScheduleDate() : oldScheduleDate;
        LocalTime newStartTime = dto.getNewStartTime() != null ? dto.getNewStartTime() : oldStartTime;
        LocalTime newEndTime = dto.getNewEndTime() != null ? dto.getNewEndTime() : oldEndTime;
        Long newTeacherId = dto.getNewTeacherId() != null ? dto.getNewTeacherId() : oldTeacherId;
        Long newClassroomId = dto.getNewClassroomId() != null ? dto.getNewClassroomId() : oldClassroomId;

        // 4. 检查是否有实际变更
        boolean hasChange = !newScheduleDate.equals(oldScheduleDate) ||
                !newStartTime.equals(oldStartTime) ||
                !newEndTime.equals(oldEndTime) ||
                !newTeacherId.equals(oldTeacherId) ||
                !newClassroomId.equals(oldClassroomId);

        if (!hasChange) {
            throw new BusinessException("调课信息未发生变化");
        }

        // 5. 检查冲突
        if (checkConflict(newScheduleDate, newStartTime, newEndTime, newTeacherId, newClassroomId, dto.getScheduleId())) {
            throw new BusinessException("调课时间冲突，请检查教师或教室是否已被占用");
        }

        // 6. 更新排课记录
        schedule.setScheduleDate(newScheduleDate);
        schedule.setStartTime(newStartTime);
        schedule.setEndTime(newEndTime);
        schedule.setTeacherId(newTeacherId);
        schedule.setClassroomId(newClassroomId);
        boolean updated = updateById(schedule);

        if (!updated) {
            throw new BusinessException("调课失败");
        }

        // 7. 记录调课历史
        ScheduleHistory history = new ScheduleHistory();
        history.setScheduleId(dto.getScheduleId());
        history.setOperationType("reschedule");
        history.setOldScheduleDate(oldScheduleDate);
        history.setNewScheduleDate(newScheduleDate);
        history.setOldStartTime(oldStartTime);
        history.setNewStartTime(newStartTime);
        history.setOldEndTime(oldEndTime);
        history.setNewEndTime(newEndTime);
        history.setOldTeacherId(oldTeacherId);
        history.setNewTeacherId(newTeacherId);
        history.setOldClassroomId(oldClassroomId);
        history.setNewClassroomId(newClassroomId);
        history.setReason(dto.getReason());
        history.setRemark(dto.getRemark());
        scheduleHistoryMapper.insert(history);

        // 8. 发布调课事件（用于通知）
        if (dto.getSendNotification() != null && dto.getSendNotification()) {
            ScheduleRescheduleEvent event = new ScheduleRescheduleEvent(
                    this,
                    dto.getScheduleId(),
                    schedule.getClassId(),
                    oldTeacherId,
                    newTeacherId,
                    oldScheduleDate,
                    newScheduleDate,
                    oldStartTime,
                    newStartTime,
                    oldEndTime,
                    newEndTime,
                    oldClassroomId,
                    newClassroomId,
                    dto.getReason(),
                    dto.getSendNotification()
            );
            eventPublisher.publishEvent(event);
        }

        log.info("调课成功，排课ID: {}", dto.getScheduleId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchReschedule(BatchRescheduleDTO dto) {
        log.info("开始批量调课，排课数量: {}, 原因: {}", dto.getScheduleIds().size(), dto.getReason());

        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (Long scheduleId : dto.getScheduleIds()) {
            try {
                RescheduleDTO rescheduleDTO = new RescheduleDTO();
                rescheduleDTO.setScheduleId(scheduleId);
                rescheduleDTO.setNewScheduleDate(dto.getNewScheduleDate());
                rescheduleDTO.setNewStartTime(dto.getNewStartTime());
                rescheduleDTO.setNewEndTime(dto.getNewEndTime());
                rescheduleDTO.setNewTeacherId(dto.getNewTeacherId());
                rescheduleDTO.setNewClassroomId(dto.getNewClassroomId());
                rescheduleDTO.setReason(dto.getReason());
                rescheduleDTO.setRemark(dto.getRemark());
                rescheduleDTO.setSendNotification(dto.getSendNotification());

                rescheduleEnhanced(rescheduleDTO);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add("排课ID " + scheduleId + ": " + e.getMessage());
                log.error("批量调课失败，排课ID: {}, 错误: {}", scheduleId, e.getMessage(), e);
            }
        }

        log.info("批量调课完成，成功: {}, 失败: {}", successCount, failCount);

        if (failCount > 0) {
            throw new BusinessException("批量调课部分失败，成功: " + successCount + ", 失败: " + failCount +
                    ". 错误详情: " + String.join("; ", errors));
        }

        return true;
    }

    @Override
    public List<ScheduleHistory> getScheduleHistory(Long scheduleId) {
        return scheduleHistoryMapper.selectHistoryByScheduleId(scheduleId);
    }
}

