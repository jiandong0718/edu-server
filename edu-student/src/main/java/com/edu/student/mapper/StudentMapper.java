package com.edu.student.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.student.domain.entity.Student;
import org.apache.ibatis.annotations.Param;

/**
 * 学员 Mapper
 */
@DS("student")
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学员列表（包含关联信息）
     */
    IPage<Student> selectStudentPage(IPage<Student> page, @Param("query") Student query);

    /**
     * 根据ID查询学员详情（包含联系人和标签）
     */
    Student selectStudentDetail(@Param("id") Long id);

    /**
     * 生成学员编号
     */
    String generateStudentNo(@Param("campusCode") String campusCode);
}
