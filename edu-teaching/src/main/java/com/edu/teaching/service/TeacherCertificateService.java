package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.TeacherCertificateDTO;
import com.edu.teaching.domain.entity.TeacherCertificate;
import com.edu.teaching.domain.vo.TeacherCertificateVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教师资质证书服务接口
 */
public interface TeacherCertificateService extends IService<TeacherCertificate> {

    /**
     * 获取教师的证书列表
     */
    List<TeacherCertificate> getByTeacherId(Long teacherId);

    /**
     * 分页查询证书列表
     */
    Page<TeacherCertificateVO> pageQuery(Integer pageNum, Integer pageSize, Long teacherId, String certType, Long campusId);

    /**
     * 获取证书详情
     */
    TeacherCertificateVO getDetailById(Long id);

    /**
     * 新增证书
     */
    Long addCertificate(TeacherCertificateDTO dto);

    /**
     * 修改证书
     */
    boolean updateCertificate(TeacherCertificateDTO dto);

    /**
     * 删除证书（同时删除文件）
     */
    boolean deleteCertificate(Long id);

    /**
     * 批量删除证书
     */
    boolean deleteBatch(List<Long> ids);

    /**
     * 上传证书文件
     */
    String uploadCertificateFile(MultipartFile file);

    /**
     * 检查证书是否过期
     */
    boolean checkExpired(Long id);

    /**
     * 获取即将过期的证书列表（30天内）
     */
    List<TeacherCertificateVO> getExpiringCertificates(Long campusId);
}
