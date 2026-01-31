package com.edu.teaching.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.framework.file.FileService;
import com.edu.teaching.domain.dto.TeacherCertificateDTO;
import com.edu.teaching.domain.entity.Teacher;
import com.edu.teaching.domain.entity.TeacherCertificate;
import com.edu.teaching.domain.vo.TeacherCertificateVO;
import com.edu.teaching.mapper.TeacherCertificateMapper;
import com.edu.teaching.mapper.TeacherMapper;
import com.edu.teaching.service.TeacherCertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 教师资质证书服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherCertificateServiceImpl extends ServiceImpl<TeacherCertificateMapper, TeacherCertificate> implements TeacherCertificateService {

    private final FileService fileService;
    private final TeacherMapper teacherMapper;

    private static final Map<String, String> CERT_TYPE_MAP = new HashMap<>();

    static {
        CERT_TYPE_MAP.put("teacher_qualification", "教师资格证");
        CERT_TYPE_MAP.put("degree", "学历证书");
        CERT_TYPE_MAP.put("skill", "技能证书");
        CERT_TYPE_MAP.put("other", "其他");
    }

    @Override
    public List<TeacherCertificate> getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherCertificate::getTeacherId, teacherId)
                .orderByDesc(TeacherCertificate::getIssueDate);
        return list(wrapper);
    }

    @Override
    public Page<TeacherCertificateVO> pageQuery(Integer pageNum, Integer pageSize, Long teacherId, String certType, Long campusId) {
        Page<TeacherCertificate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TeacherCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(teacherId != null, TeacherCertificate::getTeacherId, teacherId)
                .eq(StrUtil.isNotBlank(certType), TeacherCertificate::getCertType, certType)
                .eq(campusId != null, TeacherCertificate::getCampusId, campusId)
                .orderByDesc(TeacherCertificate::getCreateTime);

        page(page, wrapper);

        // 转换为VO
        Page<TeacherCertificateVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<TeacherCertificateVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public TeacherCertificateVO getDetailById(Long id) {
        TeacherCertificate certificate = getById(id);
        if (certificate == null) {
            throw new BusinessException("证书不存在");
        }
        return convertToVO(certificate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCertificate(TeacherCertificateDTO dto) {
        // 验证教师是否存在
        Teacher teacher = teacherMapper.selectById(dto.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }

        // 验证证书类型
        if (!CERT_TYPE_MAP.containsKey(dto.getCertType())) {
            throw new BusinessException("无效的证书类型");
        }

        // 验证有效期
        if (dto.getExpireDate() != null && dto.getIssueDate() != null) {
            if (dto.getExpireDate().isBefore(dto.getIssueDate())) {
                throw new BusinessException("有效期不能早于颁发日期");
            }
        }

        TeacherCertificate certificate = new TeacherCertificate();
        BeanUtil.copyProperties(dto, certificate);

        // 如果没有指定校区，使用教师的校区
        if (certificate.getCampusId() == null) {
            certificate.setCampusId(teacher.getCampusId());
        }

        // 默认状态为启用
        if (certificate.getStatus() == null) {
            certificate.setStatus(1);
        }

        save(certificate);
        return certificate.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCertificate(TeacherCertificateDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("证书ID不能为空");
        }

        TeacherCertificate certificate = getById(dto.getId());
        if (certificate == null) {
            throw new BusinessException("证书不存在");
        }

        // 验证证书类型
        if (StrUtil.isNotBlank(dto.getCertType()) && !CERT_TYPE_MAP.containsKey(dto.getCertType())) {
            throw new BusinessException("无效的证书类型");
        }

        // 验证有效期
        if (dto.getExpireDate() != null && dto.getIssueDate() != null) {
            if (dto.getExpireDate().isBefore(dto.getIssueDate())) {
                throw new BusinessException("有效期不能早于颁发日期");
            }
        }

        // 如果更换了文件，删除旧文件
        if (StrUtil.isNotBlank(dto.getFileUrl()) && !dto.getFileUrl().equals(certificate.getFileUrl())) {
            try {
                fileService.delete(certificate.getFileUrl());
            } catch (Exception e) {
                log.warn("删除旧证书文件失败: {}", certificate.getFileUrl(), e);
            }
        }

        BeanUtil.copyProperties(dto, certificate, "id", "teacherId", "createTime", "createBy");
        return updateById(certificate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCertificate(Long id) {
        TeacherCertificate certificate = getById(id);
        if (certificate == null) {
            throw new BusinessException("证书不存在");
        }

        // 删除文件
        if (StrUtil.isNotBlank(certificate.getFileUrl())) {
            try {
                fileService.delete(certificate.getFileUrl());
            } catch (Exception e) {
                log.warn("删除证书文件失败: {}", certificate.getFileUrl(), e);
            }
        }

        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 查询所有证书
        List<TeacherCertificate> certificates = listByIds(ids);

        // 删除文件
        certificates.forEach(cert -> {
            if (StrUtil.isNotBlank(cert.getFileUrl())) {
                try {
                    fileService.delete(cert.getFileUrl());
                } catch (Exception e) {
                    log.warn("删除证书文件失败: {}", cert.getFileUrl(), e);
                }
            }
        });

        return removeByIds(ids);
    }

    @Override
    public String uploadCertificateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".pdf");
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException("只支持上传 JPG、PNG、PDF 格式的文件");
        }

        // 验证文件大小（10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 上传文件
        String path = "certificates/" + LocalDate.now().getYear() + "/" + LocalDate.now().getMonthValue();
        return fileService.upload(file, path);
    }

    @Override
    public boolean checkExpired(Long id) {
        TeacherCertificate certificate = getById(id);
        if (certificate == null) {
            throw new BusinessException("证书不存在");
        }

        if (certificate.getExpireDate() == null) {
            return false; // 永久有效
        }

        return certificate.getExpireDate().isBefore(LocalDate.now());
    }

    @Override
    public List<TeacherCertificateVO> getExpiringCertificates(Long campusId) {
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysLater = now.plusDays(30);

        LambdaQueryWrapper<TeacherCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(campusId != null, TeacherCertificate::getCampusId, campusId)
                .isNotNull(TeacherCertificate::getExpireDate)
                .between(TeacherCertificate::getExpireDate, now, thirtyDaysLater)
                .eq(TeacherCertificate::getStatus, 1)
                .orderByAsc(TeacherCertificate::getExpireDate);

        List<TeacherCertificate> certificates = list(wrapper);
        return certificates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private TeacherCertificateVO convertToVO(TeacherCertificate certificate) {
        TeacherCertificateVO vo = new TeacherCertificateVO();
        BeanUtil.copyProperties(certificate, vo);

        // 设置证书类型名称
        vo.setCertTypeName(CERT_TYPE_MAP.getOrDefault(certificate.getCertType(), "未知"));

        // 设置是否过期
        if (certificate.getExpireDate() != null) {
            vo.setExpired(certificate.getExpireDate().isBefore(LocalDate.now()));
        } else {
            vo.setExpired(false);
        }

        // 查询教师姓名
        if (certificate.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(certificate.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
            }
        }

        return vo;
    }
}
