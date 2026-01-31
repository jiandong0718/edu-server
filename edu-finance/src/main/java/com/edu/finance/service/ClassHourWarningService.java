package com.edu.finance.service;

import com.edu.finance.domain.dto.ClassHourWarningQueryDTO;
import com.edu.finance.domain.vo.ClassHourWarningVO;

import java.util.List;

/**
 * 课时预警服务接口
 */
public interface ClassHourWarningService {

    /**
     * 获取预警列表
     *
     * @param query 查询条件
     * @return 预警列表
     */
    List<ClassHourWarningVO> getWarningList(ClassHourWarningQueryDTO query);

    /**
     * 检查并发送预警通知
     * 由定时任务调用
     *
     * @return 预警数量
     */
    int checkAndSendWarnings();
}
