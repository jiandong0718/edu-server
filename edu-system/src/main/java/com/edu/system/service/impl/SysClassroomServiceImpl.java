package com.edu.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.dto.ClassroomDTO;
import com.edu.system.domain.dto.ClassroomQueryDTO;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.domain.entity.SysClassroom;
import com.edu.system.domain.vo.ClassroomVO;
import com.edu.system.mapper.SysCampusMapper;
import com.edu.system.mapper.SysClassroomMapper;
import com.edu.system.service.SysClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教室服务实现
 */
@Service
@RequiredArgsConstructor
public class SysClassroomServiceImpl extends ServiceImpl<SysClassroomMapper, SysClassroom> implements SysClassroomService {

    private final SysCampusMapper campusMapper;

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

    @Override
    public boolean checkCodeUnique(String code, Long id) {
        LambdaQueryWrapper<SysClassroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysClassroom::getCode, code);
        if (id != null) {
            wrapper.ne(SysClassroom::getId, id);
        }
        return count(wrapper) == 0;
    }

    @Override
    public Page<ClassroomVO> getClassroomPage(ClassroomQueryDTO queryDTO) {
        Page<SysClassroom> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysClassroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StrUtil.isNotBlank(queryDTO.getKeyword()), w ->
                w.like(SysClassroom::getName, queryDTO.getKeyword())
                 .or()
                 .like(SysClassroom::getCode, queryDTO.getKeyword()))
                .eq(queryDTO.getCampusId() != null, SysClassroom::getCampusId, queryDTO.getCampusId())
                .eq(StrUtil.isNotBlank(queryDTO.getBuilding()), SysClassroom::getBuilding, queryDTO.getBuilding())
                .eq(queryDTO.getFloor() != null, SysClassroom::getFloor, queryDTO.getFloor())
                .eq(queryDTO.getStatus() != null, SysClassroom::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getMinCapacity() != null, SysClassroom::getCapacity, queryDTO.getMinCapacity())
                .le(queryDTO.getMaxCapacity() != null, SysClassroom::getCapacity, queryDTO.getMaxCapacity())
                .orderByAsc(SysClassroom::getSortOrder)
                .orderByDesc(SysClassroom::getCreateTime);

        Page<SysClassroom> classroomPage = page(page, wrapper);

        // 获取校区信息
        List<Long> campusIds = classroomPage.getRecords().stream()
                .map(SysClassroom::getCampusId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> campusMap = campusMapper.selectBatchIds(campusIds).stream()
                .collect(Collectors.toMap(SysCampus::getId, SysCampus::getName));

        // 转换为VO
        Page<ClassroomVO> voPage = new Page<>(classroomPage.getCurrent(), classroomPage.getSize(), classroomPage.getTotal());
        List<ClassroomVO> voList = classroomPage.getRecords().stream()
                .map(classroom -> convertToVO(classroom, campusMap.get(classroom.getCampusId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public ClassroomVO getClassroomDetail(Long id) {
        SysClassroom classroom = getById(id);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }

        SysCampus campus = campusMapper.selectById(classroom.getCampusId());
        String campusName = campus != null ? campus.getName() : null;

        return convertToVO(classroom, campusName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createClassroom(ClassroomDTO dto) {
        // 校验名称唯一性
        if (!checkNameUnique(dto.getName(), dto.getCampusId(), null)) {
            throw new BusinessException("该校区下教室名称已存在");
        }

        // 校验编码唯一性
        if (!checkCodeUnique(dto.getCode(), null)) {
            throw new BusinessException("教室编码已存在");
        }

        SysClassroom classroom = new SysClassroom();
        BeanUtil.copyProperties(dto, classroom);

        // 转换设施列表为JSON字符串
        if (dto.getFacilities() != null && !dto.getFacilities().isEmpty()) {
            classroom.setFacilities(JSONUtil.toJsonStr(dto.getFacilities()));
        }

        save(classroom);
        return classroom.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClassroom(Long id, ClassroomDTO dto) {
        SysClassroom classroom = getById(id);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }

        // 校验名称唯一性
        if (!checkNameUnique(dto.getName(), dto.getCampusId(), id)) {
            throw new BusinessException("该校区下教室名称已存在");
        }

        // 校验编码唯一性
        if (!checkCodeUnique(dto.getCode(), id)) {
            throw new BusinessException("教室编码已存在");
        }

        BeanUtil.copyProperties(dto, classroom, "id");

        // 转换设施列表为JSON字符串
        if (dto.getFacilities() != null) {
            classroom.setFacilities(JSONUtil.toJsonStr(dto.getFacilities()));
        }

        updateById(classroom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClassroom(Long id) {
        SysClassroom classroom = getById(id);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }

        // TODO: 检查是否有关联的排课记录

        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteClassroom(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的教室");
        }

        // TODO: 检查是否有关联的排课记录

        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        SysClassroom classroom = getById(id);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }

        classroom.setStatus(status);
        updateById(classroom);
    }

    @Override
    public List<ClassroomVO> getAvailableClassrooms(Long campusId) {
        LambdaQueryWrapper<SysClassroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(campusId != null, SysClassroom::getCampusId, campusId)
                .eq(SysClassroom::getStatus, 1)
                .orderByAsc(SysClassroom::getSortOrder);

        List<SysClassroom> classrooms = list(wrapper);

        // 获取校区信息
        Map<Long, String> campusMap = campusMapper.selectBatchIds(
                classrooms.stream().map(SysClassroom::getCampusId).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(SysCampus::getId, SysCampus::getName));

        return classrooms.stream()
                .map(classroom -> convertToVO(classroom, campusMap.get(classroom.getCampusId())))
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private ClassroomVO convertToVO(SysClassroom classroom, String campusName) {
        ClassroomVO vo = new ClassroomVO();
        BeanUtil.copyProperties(classroom, vo);
        vo.setCampusName(campusName);

        // 解析设施JSON字符串为列表
        if (StrUtil.isNotBlank(classroom.getFacilities())) {
            try {
                vo.setFacilities(JSONUtil.toList(classroom.getFacilities(), String.class));
            } catch (Exception e) {
                vo.setFacilities(List.of());
            }
        }

        // 设置状态文本
        vo.setStatusText(classroom.getStatus() == 1 ? "启用" : "禁用");

        return vo;
    }
}
