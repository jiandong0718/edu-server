package com.edu.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.dto.SysHolidayDTO;
import com.edu.system.domain.dto.SysHolidayQueryDTO;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.domain.entity.SysHoliday;
import com.edu.system.domain.vo.SysHolidayVO;
import com.edu.system.mapper.SysCampusMapper;
import com.edu.system.mapper.SysHolidayMapper;
import com.edu.system.service.SysHolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 节假日服务实现
 */
@Service
@RequiredArgsConstructor
public class SysHolidayServiceImpl extends ServiceImpl<SysHolidayMapper, SysHoliday> implements SysHolidayService {

    private final SysCampusMapper campusMapper;

    // 节假日类型映射
    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(1, "法定节假日");
        TYPE_MAP.put(2, "调休");
        TYPE_MAP.put(3, "公司假期");
    }

    @Override
    public Page<SysHolidayVO> pageHolidays(SysHolidayQueryDTO queryDTO) {
        Page<SysHoliday> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysHoliday> wrapper = buildQueryWrapper(queryDTO);
        wrapper.orderByDesc(SysHoliday::getStartDate);

        Page<SysHoliday> holidayPage = page(page, wrapper);

        // 转换为 VO
        Page<SysHolidayVO> voPage = new Page<>(holidayPage.getCurrent(), holidayPage.getSize(), holidayPage.getTotal());
        List<SysHolidayVO> voList = holidayPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public SysHolidayVO getHolidayById(Long id) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new BusinessException("节假日不存在");
        }
        return convertToVO(holiday);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addHoliday(SysHolidayDTO holidayDTO) {
        // 验证日期范围
        if (holidayDTO.getStartDate().isAfter(holidayDTO.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }

        // 检查日期冲突
        if (!checkDateConflict(holidayDTO.getStartDate(), holidayDTO.getEndDate(), null, holidayDTO.getCampusId())) {
            throw new BusinessException("该日期范围与已有节假日冲突");
        }

        // 如果指定了校区，验证校区是否存在
        if (holidayDTO.getCampusId() != null) {
            SysCampus campus = campusMapper.selectById(holidayDTO.getCampusId());
            if (campus == null) {
                throw new BusinessException("校区不存在");
            }
        }

        SysHoliday holiday = BeanUtil.copyProperties(holidayDTO, SysHoliday.class);
        // 设置默认值
        if (holiday.getIsWorkday() == null) {
            holiday.setIsWorkday(0);
        }
        if (holiday.getStatus() == null) {
            holiday.setStatus(1);
        }

        return save(holiday);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHoliday(SysHolidayDTO holidayDTO) {
        if (holidayDTO.getId() == null) {
            throw new BusinessException("节假日ID不能为空");
        }

        // 验证节假日是否存在
        SysHoliday existHoliday = getById(holidayDTO.getId());
        if (existHoliday == null) {
            throw new BusinessException("节假日不存在");
        }

        // 验证日期范围
        if (holidayDTO.getStartDate().isAfter(holidayDTO.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }

        // 检查日期冲突
        if (!checkDateConflict(holidayDTO.getStartDate(), holidayDTO.getEndDate(), holidayDTO.getId(), holidayDTO.getCampusId())) {
            throw new BusinessException("该日期范围与已有节假日冲突");
        }

        // 如果指定了校区，验证校区是否存在
        if (holidayDTO.getCampusId() != null) {
            SysCampus campus = campusMapper.selectById(holidayDTO.getCampusId());
            if (campus == null) {
                throw new BusinessException("校区不存在");
            }
        }

        SysHoliday holiday = BeanUtil.copyProperties(holidayDTO, SysHoliday.class);
        return updateById(holiday);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHoliday(Long id) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new BusinessException("节假日不存在");
        }
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteHolidays(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的节假日");
        }
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new BusinessException("节假日不存在");
        }

        holiday.setStatus(status);
        return updateById(holiday);
    }

    @Override
    public boolean checkDateConflict(LocalDate startDate, LocalDate endDate, Long id, Long campusId) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .le(SysHoliday::getStartDate, endDate)
                .ge(SysHoliday::getEndDate, startDate)
        );

        // 排除当前记录
        if (id != null) {
            wrapper.ne(SysHoliday::getId, id);
        }

        // 校区过滤：检查全局节假日和指定校区的节假日
        if (campusId != null) {
            wrapper.and(w -> w.isNull(SysHoliday::getCampusId).or().eq(SysHoliday::getCampusId, campusId));
        } else {
            // 如果是全局节假日，需要检查所有节假日
            wrapper.isNull(SysHoliday::getCampusId);
        }

        return count(wrapper) == 0;
    }

    @Override
    public boolean isHoliday(LocalDate date, Long campusId) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHoliday::getStatus, 1)
                .le(SysHoliday::getStartDate, date)
                .ge(SysHoliday::getEndDate, date)
                .eq(SysHoliday::getIsWorkday, 0);

        // 校区过滤：查询全局节假日和指定校区的节假日
        if (campusId != null) {
            wrapper.and(w -> w.isNull(SysHoliday::getCampusId).or().eq(SysHoliday::getCampusId, campusId));
        } else {
            wrapper.isNull(SysHoliday::getCampusId);
        }

        return count(wrapper) > 0;
    }

    @Override
    public boolean isWorkday(LocalDate date, Long campusId) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHoliday::getStatus, 1)
                .le(SysHoliday::getStartDate, date)
                .ge(SysHoliday::getEndDate, date)
                .eq(SysHoliday::getIsWorkday, 1);

        // 校区过滤：查询全局节假日和指定校区的节假日
        if (campusId != null) {
            wrapper.and(w -> w.isNull(SysHoliday::getCampusId).or().eq(SysHoliday::getCampusId, campusId));
        } else {
            wrapper.isNull(SysHoliday::getCampusId);
        }

        return count(wrapper) > 0;
    }

    @Override
    public List<SysHoliday> getHolidaysByDateRange(LocalDate startDate, LocalDate endDate, Long campusId) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHoliday::getStatus, 1)
                .and(w -> w
                        .le(SysHoliday::getStartDate, endDate)
                        .ge(SysHoliday::getEndDate, startDate)
                );

        // 校区过滤：查询全局节假日和指定校区的节假日
        if (campusId != null) {
            wrapper.and(w -> w.isNull(SysHoliday::getCampusId).or().eq(SysHoliday::getCampusId, campusId));
        } else {
            wrapper.isNull(SysHoliday::getCampusId);
        }

        wrapper.orderByAsc(SysHoliday::getStartDate);
        return list(wrapper);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<SysHoliday> buildQueryWrapper(SysHolidayQueryDTO queryDTO) {
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getName() != null, SysHoliday::getName, queryDTO.getName())
                .eq(queryDTO.getType() != null, SysHoliday::getType, queryDTO.getType())
                .eq(queryDTO.getStatus() != null, SysHoliday::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getStartDate() != null, SysHoliday::getStartDate, queryDTO.getStartDate())
                .le(queryDTO.getEndDate() != null, SysHoliday::getEndDate, queryDTO.getEndDate());

        // 校区过滤
        if (queryDTO.getCampusId() != null) {
            wrapper.and(w -> w.isNull(SysHoliday::getCampusId).or().eq(SysHoliday::getCampusId, queryDTO.getCampusId()));
        }

        return wrapper;
    }

    /**
     * 转换为 VO
     */
    private SysHolidayVO convertToVO(SysHoliday holiday) {
        SysHolidayVO vo = BeanUtil.copyProperties(holiday, SysHolidayVO.class);

        // 设置类型名称
        vo.setTypeName(TYPE_MAP.get(holiday.getType()));

        // 设置校区名称
        if (holiday.getCampusId() != null) {
            SysCampus campus = campusMapper.selectById(holiday.getCampusId());
            if (campus != null) {
                vo.setCampusName(campus.getName());
            }
        } else {
            vo.setCampusName("全局");
        }

        return vo;
    }
}
