package com.edu.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.dto.ClassroomDTO;
import com.edu.system.domain.dto.ClassroomQueryDTO;
import com.edu.system.domain.entity.SysClassroom;
import com.edu.system.domain.vo.ClassroomVO;

import java.util.List;

/**
 * 教室服务接口
 */
public interface SysClassroomService extends IService<SysClassroom> {

    /**
     * 检查教室名称是否唯一
     */
    boolean checkNameUnique(String name, Long campusId, Long id);

    /**
     * 检查教室编码是否唯一
     */
    boolean checkCodeUnique(String code, Long id);

    /**
     * 分页查询教室列表
     */
    Page<ClassroomVO> getClassroomPage(ClassroomQueryDTO queryDTO);

    /**
     * 获取教室详情
     */
    ClassroomVO getClassroomDetail(Long id);

    /**
     * 创建教室
     */
    Long createClassroom(ClassroomDTO dto);

    /**
     * 更新教室
     */
    void updateClassroom(Long id, ClassroomDTO dto);

    /**
     * 删除教室
     */
    void deleteClassroom(Long id);

    /**
     * 批量删除教室
     */
    void batchDeleteClassroom(List<Long> ids);

    /**
     * 更新教室状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取可用教室列表
     */
    List<ClassroomVO> getAvailableClassrooms(Long campusId);
}
