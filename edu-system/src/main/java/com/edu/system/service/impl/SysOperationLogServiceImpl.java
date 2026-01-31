package com.edu.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.system.domain.dto.OperationLogQueryDTO;
import com.edu.system.domain.entity.SysCampus;
import com.edu.system.domain.entity.SysOperationLog;
import com.edu.system.domain.vo.OperationLogVO;
import com.edu.system.mapper.SysOperationLogMapper;
import com.edu.system.service.SysCampusService;
import com.edu.system.service.SysOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 */
@Service
@RequiredArgsConstructor
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    private final SysCampusService campusService;

    private static final Map<Integer, String> BUSINESS_TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        BUSINESS_TYPE_MAP.put(0, "其他");
        BUSINESS_TYPE_MAP.put(1, "新增");
        BUSINESS_TYPE_MAP.put(2, "修改");
        BUSINESS_TYPE_MAP.put(3, "删除");
        BUSINESS_TYPE_MAP.put(4, "导出");
        BUSINESS_TYPE_MAP.put(5, "导入");
        BUSINESS_TYPE_MAP.put(6, "查询");
        BUSINESS_TYPE_MAP.put(7, "登录");
        BUSINESS_TYPE_MAP.put(8, "登出");

        STATUS_MAP.put(0, "失败");
        STATUS_MAP.put(1, "成功");
    }

    @Override
    public Page<OperationLogVO> pageQuery(OperationLogQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getTitle()), SysOperationLog::getTitle, queryDTO.getTitle())
                .like(StrUtil.isNotBlank(queryDTO.getOperatorName()), SysOperationLog::getOperatorName, queryDTO.getOperatorName())
                .like(StrUtil.isNotBlank(queryDTO.getIp()), SysOperationLog::getIp, queryDTO.getIp())
                .eq(queryDTO.getBusinessType() != null, SysOperationLog::getBusinessType, queryDTO.getBusinessType())
                .eq(queryDTO.getStatus() != null, SysOperationLog::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getCampusId() != null, SysOperationLog::getCampusId, queryDTO.getCampusId())
                .ge(queryDTO.getStartTime() != null, SysOperationLog::getCreateTime, queryDTO.getStartTime())
                .le(queryDTO.getEndTime() != null, SysOperationLog::getCreateTime, queryDTO.getEndTime())
                .orderByDesc(SysOperationLog::getCreateTime);

        // 分页查询
        Page<SysOperationLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        page(page, wrapper);

        // 转换为 VO
        Page<OperationLogVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<OperationLogVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public OperationLogVO getDetail(Long id) {
        SysOperationLog log = getById(id);
        if (log == null) {
            return null;
        }
        return convertToVO(log);
    }

    @Override
    public Boolean cleanLogs(Integer days) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
            wrapper.lt(SysOperationLog::getCreateTime, beforeTime);
        }
        return remove(wrapper);
    }

    /**
     * 转换为 VO
     */
    private OperationLogVO convertToVO(SysOperationLog log) {
        OperationLogVO vo = BeanUtil.copyProperties(log, OperationLogVO.class);

        // 设置业务类型名称
        vo.setBusinessTypeName(BUSINESS_TYPE_MAP.getOrDefault(log.getBusinessType(), "未知"));

        // 设置状态名称
        vo.setStatusName(STATUS_MAP.getOrDefault(log.getStatus(), "未知"));

        // 设置校区名称
        if (log.getCampusId() != null) {
            SysCampus campus = campusService.getById(log.getCampusId());
            if (campus != null) {
                vo.setCampusName(campus.getName());
            }
        }

        return vo;
    }
}
