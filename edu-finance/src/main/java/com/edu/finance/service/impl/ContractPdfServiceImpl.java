package com.edu.finance.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ContractPdfDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractItem;
import com.edu.finance.mapper.ContractItemMapper;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.ContractPdfService;
import com.edu.framework.file.FileService;
import com.edu.student.api.StudentApi;
import com.edu.student.api.dto.StudentDTO;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同PDF服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPdfServiceImpl implements ContractPdfService {

    private final ContractMapper contractMapper;
    private final ContractItemMapper contractItemMapper;
    private final StudentApi studentApi;
    private final FileService fileService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    @Override
    public String generateContractPdf(Long contractId) {
        // 获取合同数据
        ContractPdfDTO pdfData = buildContractPdfData(contractId);

        // 生成PDF
        ByteArrayOutputStream pdfStream = generatePdfStream(pdfData);

        // 上传文件
        String fileName = "contract_" + pdfData.getContractNo() + ".pdf";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfStream.toByteArray());
        String fileUrl = fileService.upload(inputStream, "contracts", fileName);

        log.info("合同PDF生成成功: contractId={}, fileUrl={}", contractId, fileUrl);
        return fileUrl;
    }

    @Override
    public ByteArrayOutputStream generatePdfStream(ContractPdfDTO pdfData) {
        try {
            // 生成HTML内容
            String htmlContent = generateHtmlContent(pdfData);

            // 转换为PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ConverterProperties converterProperties = new ConverterProperties();

            // 设置字体提供者（支持中文）
            DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);
            converterProperties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(htmlContent, outputStream, converterProperties);

            return outputStream;
        } catch (Exception e) {
            log.error("生成PDF失败", e);
            throw new BusinessException("生成PDF失败: " + e.getMessage());
        }
    }

    @Override
    public String previewContractHtml(Long contractId) {
        ContractPdfDTO pdfData = buildContractPdfData(contractId);
        return generateHtmlContent(pdfData);
    }

    /**
     * 构建合同PDF数据
     */
    private ContractPdfDTO buildContractPdfData(Long contractId) {
        // 查询合同
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 查询合同明细
        List<ContractItem> items = contractItemMapper.selectByContractId(contractId);

        // 查询学员信息
        StudentDTO student = studentApi.getStudentById(contract.getStudentId());
        if (student == null) {
            throw new BusinessException("学员信息不存在");
        }

        // 构建PDF数据
        ContractPdfDTO pdfData = new ContractPdfDTO();
        pdfData.setContractNo(contract.getContractNo());
        pdfData.setSignDate(contract.getSignDate());
        pdfData.setEffectiveDate(contract.getEffectiveDate());
        pdfData.setExpireDate(contract.getExpireDate());
        pdfData.setTotalAmount(contract.getAmount());
        pdfData.setDiscountAmount(contract.getDiscountAmount());
        pdfData.setPaidAmount(contract.getPaidAmount());
        pdfData.setPaymentMethod("分期付款"); // 可以从合同中获取
        pdfData.setRemark(contract.getRemark());

        // 甲方信息（机构）
        ContractPdfDTO.PartyInfo partyA = new ContractPdfDTO.PartyInfo();
        partyA.setName("XX教育培训机构"); // 可以从配置中获取
        partyA.setContact("张老师");
        partyA.setPhone("400-123-4567");
        partyA.setAddress("XX市XX区XX路XX号");
        pdfData.setPartyA(partyA);

        // 乙方信息（学员/家长）
        ContractPdfDTO.PartyInfo partyB = new ContractPdfDTO.PartyInfo();
        partyB.setName(student.getName());
        partyB.setPhone(student.getPhone());
        partyB.setAddress(student.getAddress());
        partyB.setIdCard(student.getIdCard());

        // 如果有联系人，使用主要联系人信息
        if (student.getContacts() != null && !student.getContacts().isEmpty()) {
            student.getContacts().stream()
                    .filter(c -> c.getIsPrimary() != null && c.getIsPrimary())
                    .findFirst()
                    .ifPresent(contact -> {
                        partyB.setContact(contact.getName());
                        if (StrUtil.isNotBlank(contact.getPhone())) {
                            partyB.setPhone(contact.getPhone());
                        }
                    });
        }
        pdfData.setPartyB(partyB);

        // 课程明细
        List<ContractPdfDTO.CourseItem> courseItems = items.stream()
                .map(item -> {
                    ContractPdfDTO.CourseItem courseItem = new ContractPdfDTO.CourseItem();
                    courseItem.setCourseName(item.getCourseName());
                    courseItem.setHours(item.getHours());
                    courseItem.setUnitPrice(item.getUnitPrice());
                    courseItem.setAmount(item.getAmount());
                    return courseItem;
                })
                .collect(Collectors.toList());
        pdfData.setCourseItems(courseItems);

        // 合同条款（默认条款）
        pdfData.setTerms(getDefaultTerms());

        return pdfData;
    }

    /**
     * 生成HTML内容
     */
    private String generateHtmlContent(ContractPdfDTO pdfData) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("body { font-family: SimSun, serif; font-size: 12pt; line-height: 1.6; margin: 40px; }");
        html.append("h1 { text-align: center; font-size: 20pt; margin-bottom: 30px; }");
        html.append("h2 { font-size: 14pt; margin-top: 20px; margin-bottom: 10px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("table, th, td { border: 1px solid #000; }");
        html.append("th, td { padding: 8px; text-align: left; }");
        html.append("th { background-color: #f0f0f0; font-weight: bold; }");
        html.append(".info-row { margin: 10px 0; }");
        html.append(".label { display: inline-block; width: 120px; font-weight: bold; }");
        html.append(".signature { margin-top: 50px; }");
        html.append(".signature-line { display: inline-block; width: 200px; border-bottom: 1px solid #000; margin-left: 20px; }");
        html.append(".terms { margin: 20px 0; line-height: 1.8; text-indent: 2em; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        // 标题
        html.append("<h1>教育培训服务合同</h1>");

        // 合同编号和日期
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">合同编号：</span>");
        html.append("<span>").append(pdfData.getContractNo()).append("</span>");
        html.append("</div>");

        if (pdfData.getSignDate() != null) {
            html.append("<div class=\"info-row\">");
            html.append("<span class=\"label\">签订日期：</span>");
            html.append("<span>").append(pdfData.getSignDate().format(DATE_FORMATTER)).append("</span>");
            html.append("</div>");
        }

        // 甲方信息
        html.append("<h2>甲方（培训机构）</h2>");
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">名称：</span>");
        html.append("<span>").append(pdfData.getPartyA().getName()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">联系人：</span>");
        html.append("<span>").append(pdfData.getPartyA().getContact()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">电话：</span>");
        html.append("<span>").append(pdfData.getPartyA().getPhone()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">地址：</span>");
        html.append("<span>").append(pdfData.getPartyA().getAddress()).append("</span>");
        html.append("</div>");

        // 乙方信息
        html.append("<h2>乙方（学员/家长）</h2>");
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">姓名：</span>");
        html.append("<span>").append(pdfData.getPartyB().getName()).append("</span>");
        html.append("</div>");
        if (StrUtil.isNotBlank(pdfData.getPartyB().getContact())) {
            html.append("<div class=\"info-row\">");
            html.append("<span class=\"label\">监护人：</span>");
            html.append("<span>").append(pdfData.getPartyB().getContact()).append("</span>");
            html.append("</div>");
        }
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">电话：</span>");
        html.append("<span>").append(pdfData.getPartyB().getPhone()).append("</span>");
        html.append("</div>");
        if (StrUtil.isNotBlank(pdfData.getPartyB().getIdCard())) {
            html.append("<div class=\"info-row\">");
            html.append("<span class=\"label\">身份证号：</span>");
            html.append("<span>").append(pdfData.getPartyB().getIdCard()).append("</span>");
            html.append("</div>");
        }
        if (StrUtil.isNotBlank(pdfData.getPartyB().getAddress())) {
            html.append("<div class=\"info-row\">");
            html.append("<span class=\"label\">地址：</span>");
            html.append("<span>").append(pdfData.getPartyB().getAddress()).append("</span>");
            html.append("</div>");
        }

        // 课程明细
        html.append("<h2>课程明细</h2>");
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>课程名称</th>");
        html.append("<th>课时数</th>");
        html.append("<th>单价（元）</th>");
        html.append("<th>金额（元）</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");

        for (ContractPdfDTO.CourseItem item : pdfData.getCourseItems()) {
            html.append("<tr>");
            html.append("<td>").append(item.getCourseName()).append("</td>");
            html.append("<td>").append(item.getHours()).append("</td>");
            html.append("<td>").append(item.getUnitPrice()).append("</td>");
            html.append("<td>").append(item.getAmount()).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody>");
        html.append("</table>");

        // 费用汇总
        html.append("<h2>费用汇总</h2>");
        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">合同总金额：</span>");
        html.append("<span>").append(pdfData.getTotalAmount()).append(" 元</span>");
        html.append("</div>");

        if (pdfData.getDiscountAmount() != null && pdfData.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            html.append("<div class=\"info-row\">");
            html.append("<span class=\"label\">优惠金额：</span>");
            html.append("<span>").append(pdfData.getDiscountAmount()).append(" 元</span>");
            html.append("</div>");
        }

        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">实付金额：</span>");
        html.append("<span style=\"font-weight: bold; color: #d00;\">").append(pdfData.getPaidAmount()).append(" 元</span>");
        html.append("</div>");

        html.append("<div class=\"info-row\">");
        html.append("<span class=\"label\">付款方式：</span>");
        html.append("<span>").append(pdfData.getPaymentMethod()).append("</span>");
        html.append("</div>");

        // 合同期限
        if (pdfData.getEffectiveDate() != null && pdfData.getExpireDate() != null) {
            html.append("<h2>合同期限</h2>");
            html.append("<div class=\"info-row\">");
            html.append("<span class=\"label\">生效日期：</span>");
            html.append("<span>").append(pdfData.getEffectiveDate().format(DATE_FORMATTER)).append("</span>");
            html.append("</div>");
            html.append("<div class=\"info-row\">");
            html.append("<span class=\"label\">到期日期：</span>");
            html.append("<span>").append(pdfData.getExpireDate().format(DATE_FORMATTER)).append("</span>");
            html.append("</div>");
        }

        // 合同条款
        html.append("<h2>合同条款</h2>");
        html.append("<div class=\"terms\">");
        html.append(pdfData.getTerms().replace("\n", "<br/>"));
        html.append("</div>");

        // 备注
        if (StrUtil.isNotBlank(pdfData.getRemark())) {
            html.append("<h2>备注</h2>");
            html.append("<div class=\"info-row\">");
            html.append("<span>").append(pdfData.getRemark()).append("</span>");
            html.append("</div>");
        }

        // 签字
        html.append("<div class=\"signature\">");
        html.append("<div style=\"margin: 20px 0;\">");
        html.append("<span class=\"label\">甲方（盖章）：</span>");
        html.append("<span class=\"signature-line\"></span>");
        html.append("</div>");
        html.append("<div style=\"margin: 20px 0;\">");
        html.append("<span class=\"label\">乙方（签字）：</span>");
        html.append("<span class=\"signature-line\"></span>");
        html.append("</div>");
        html.append("</div>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 获取默认合同条款
     */
    private String getDefaultTerms() {
        return "一、甲方责任\n" +
                "1. 甲方应按照约定的课程内容、时间和地点提供教学服务。\n" +
                "2. 甲方应配备具有相应资质的教师进行授课。\n" +
                "3. 甲方应保证教学设施和环境符合安全标准。\n\n" +
                "二、乙方责任\n" +
                "1. 乙方应按时缴纳培训费用。\n" +
                "2. 乙方应按时参加课程，如需请假应提前通知甲方。\n" +
                "3. 乙方应遵守甲方的各项规章制度。\n\n" +
                "三、课时管理\n" +
                "1. 课时有效期为合同约定的期限内。\n" +
                "2. 因乙方原因缺课的，课时照常扣除。\n" +
                "3. 因甲方原因停课的，应为乙方补课或延长有效期。\n\n" +
                "四、退费规定\n" +
                "1. 合同签订后7日内，乙方可无条件申请退费，甲方应在15个工作日内退还全部费用。\n" +
                "2. 合同签订7日后，如乙方申请退费，甲方应扣除已消耗课时费用及违约金（不超过合同总额的20%）后退还剩余费用。\n" +
                "3. 因甲方原因导致无法继续履行合同的，应全额退还剩余费用。\n\n" +
                "五、违约责任\n" +
                "1. 任何一方违反合同约定，应承担相应的违约责任。\n" +
                "2. 因不可抗力导致合同无法履行的，双方均不承担违约责任。\n\n" +
                "六、争议解决\n" +
                "本合同在履行过程中发生争议，双方应友好协商解决；协商不成的，可向甲方所在地人民法院提起诉讼。\n\n" +
                "七、其他约定\n" +
                "1. 本合同一式两份，甲乙双方各执一份，具有同等法律效力。\n" +
                "2. 本合同自双方签字（盖章）之日起生效。";
    }
}
