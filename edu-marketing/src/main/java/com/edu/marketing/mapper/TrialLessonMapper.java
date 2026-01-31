package com.edu.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.marketing.domain.dto.TrialLessonQueryDTO;
import com.edu.marketing.domain.entity.TrialLesson;
import com.edu.marketing.domain.vo.TrialLessonVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 试听记录Mapper接口
 *
 * @author edu
 * @since 2024-01-30
 */
@Mapper
public interface TrialLessonMapper extends BaseMapper<TrialLesson> {

    /**
     * 分页查询试听记录列表（包含关联信息）
     */
    IPage<TrialLesson> selectTrialLessonPage(IPage<TrialLesson> page, @Param("query") TrialLesson query);

    /**
     * 分页查询试听记录VO列表（包含关联信息）
     */
    IPage<TrialLessonVO> selectTrialLessonVOPage(IPage<TrialLessonVO> page, @Param("query") TrialLessonQueryDTO query);
}
