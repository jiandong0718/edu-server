package com.edu.student.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.student.domain.entity.StudentTag;
import com.edu.student.domain.entity.StudentTagRelation;
import com.edu.student.mapper.StudentTagMapper;
import com.edu.student.mapper.StudentTagRelationMapper;
import com.edu.student.service.StudentTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 学员标签服务实现
 *
 * @author edu
 * @since 2024-01-30
 */
@Service
@RequiredArgsConstructor
public class StudentTagServiceImpl extends ServiceImpl<StudentTagMapper, StudentTag> implements StudentTagService {

    private final StudentTagRelationMapper studentTagRelationMapper;

    @Override
    public Page<StudentTag> pageWithUsage(Page<StudentTag> page, LambdaQueryWrapper<StudentTag> wrapper) {
        super.page(page, wrapper);
        fillUsageCount(page.getRecords());
        return page;
    }

    @Override
    public List<StudentTag> listWithUsage(LambdaQueryWrapper<StudentTag> wrapper) {
        List<StudentTag> tags = super.list(wrapper);
        fillUsageCount(tags);
        return tags;
    }

    @Override
    public StudentTag getByIdWithUsage(Long id) {
        StudentTag tag = super.getById(id);
        if (tag == null) {
            return null;
        }
        fillUsageCount(Collections.singletonList(tag));
        return tag;
    }

    private void fillUsageCount(List<StudentTag> tags) {
        if (CollUtil.isEmpty(tags)) {
            return;
        }

        List<Long> tagIds = tags.stream()
                .map(StudentTag::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(tagIds)) {
            tags.forEach(tag -> tag.setUsageCount(0));
            return;
        }

        QueryWrapper<StudentTagRelation> wrapper = new QueryWrapper<>();
        wrapper.select("tag_id", "COUNT(1) AS usage_count")
                .in("tag_id", tagIds)
                .groupBy("tag_id");

        Map<Long, Integer> usageCountMap = studentTagRelationMapper.selectMaps(wrapper).stream()
                .collect(Collectors.toMap(
                        row -> asLong(getMapValue(row, "tag_id", "tagId", "TAG_ID", "TAGID")),
                        row -> asInteger(getMapValue(row, "usage_count", "usageCount", "USAGE_COUNT", "USAGECOUNT")),
                        (left, right) -> left
                ));

        tags.forEach(tag -> tag.setUsageCount(usageCountMap.getOrDefault(tag.getId(), 0)));
    }

    private Long asLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return value == null ? null : Long.parseLong(String.valueOf(value));
    }

    private Integer asInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return value == null ? 0 : Integer.parseInt(String.valueOf(value));
    }

    private Object getMapValue(Map<String, Object> row, String... keys) {
        for (String key : keys) {
            if (row.containsKey(key)) {
                return row.get(key);
            }
        }
        return null;
    }
}
