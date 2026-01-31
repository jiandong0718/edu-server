package com.edu.marketing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.marketing.domain.dto.LeadImportResultDTO;
import com.edu.marketing.domain.entity.FollowUp;
import com.edu.marketing.domain.entity.Lead;

import java.util.List;

/**
 * 线索服务接口
 */
public interface LeadService extends IService<Lead> {

    /**
     * 分页查询线索列表
     */
    IPage<Lead> pageList(IPage<Lead> page, Lead query);

    /**
     * 创建线索
     */
    boolean createLead(Lead lead);

    /**
     * 分配线索
     */
    boolean assignLead(Long leadId, Long advisorId);

    /**
     * 批量分配线索
     */
    boolean batchAssignLead(List<Long> leadIds, Long advisorId);

    /**
     * 添加跟进记录
     */
    boolean addFollowUp(FollowUp followUp);

    /**
     * 获取跟进记录列表
     */
    List<FollowUp> getFollowUpList(Long leadId);

    /**
     * 更新线索状态
     */
    boolean updateStatus(Long id, String status, String lostReason);

    /**
     * 转化为学员
     */
    Long convertToStudent(Long leadId);

    /**
     * 检查手机号是否已存在
     */
    boolean checkPhoneExists(String phone, Long excludeId);

    /**
     * 批量导入线索
     */
    boolean importFromExcel(byte[] fileData);

    /**
     * 批量导入线索（增强版）
     *
     * @param fileData Excel文件数据
     * @return 导入结果
     */
    LeadImportResultDTO batchImportLeads(byte[] fileData);

    /**
     * 下载导入模板
     *
     * @return Excel模板数据
     */
    byte[] downloadImportTemplate();

    /**
     * 自动分配线索
     */
    boolean autoAssignLeads(List<Long> leadIds, Long campusId);
}
