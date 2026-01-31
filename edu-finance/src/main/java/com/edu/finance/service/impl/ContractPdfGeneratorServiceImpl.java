package com.edu.finance.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractItem;
import com.edu.finance.mapper.ContractItemMapper;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.ContractPdfGeneratorService;
import com.edu.framework.file.FileService;
import com.edu.student.api.StudentApi;
import com.edu.student.api.dto.StudentDTO;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 合同PDF生成服务实现
 * 使用iText 7直接生成PDF
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPdfGeneratorServiceImpl implements ContractPdfGeneratorService {

    private final ContractMapper contractMapper;
    private final ContractItemMapper contractItemMapper;
    private final StudentApi studentApi;
    private final FileService fileService;

    @Value("${file.upload.path:/data/files}")
    private String uploadPath;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    private static final String FONT_PATH = "STSong-Light"; // 使用iText内置的中文字体
    private static final String FONT_ENCODING = "UniGB-UCS2-H";

    @Override
    public String generateAndSavePdf(Long contractId) {
        // 检查合同状态
        if (!canGeneratePdf(contractId)) {
            throw new BusinessException("只有已签署的合同才能生成PDF");
        }

        try {
            // 生成PDF到字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            generatePdfToStream(contractId, baos);

            // 获取合同信息
            Contract contract = contractMapper.selectById(contractId);
            String fileName = "contract_" + contract.getContractNo() + "_" + System.currentTimeMillis() + ".pdf";

            // 保存到本地文件系统
            String localPath = uploadPath + "/contract/pdf/";
            FileUtil.mkdir(localPath);
            String localFilePath = localPath + fileName;

            try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
                fos.write(baos.toByteArray());
            }

            // 同时上传到文件服务（可能是OSS）
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            String fileUrl = fileService.upload(bais, "contract/pdf", fileName);

            log.info("合同PDF生成成功: contractId={}, localPath={}, fileUrl={}",
                    contractId, localFilePath, fileUrl);

            // 更新合同的PDF生成时间（如果Contract实体有该字段）
            // contract.setPdfGeneratedTime(LocalDateTime.now());
            // contractMapper.updateById(contract);

            return fileUrl;
        } catch (Exception e) {
            log.error("生成合同PDF失败: contractId={}", contractId, e);
            throw new BusinessException("生成合同PDF失败: " + e.getMessage());
        }
    }

    @Override
    public void generatePdfToStream(Long contractId, OutputStream outputStream) {
        try {
            // 查询合同信息
            Contract contract = contractMapper.selectById(contractId);
            if (contract == null) {
                throw new BusinessException("合同不存在");
            }

            // 查询合同明细
            List<ContractItem> items = contractItemMapper.selectByContractId(contractId);
            if (items == null || items.isEmpty()) {
                throw new BusinessException("合同明细不存在");
            }

            // 查询学员信息
            StudentDTO student = studentApi.getStudentById(contract.getStudentId());
            if (student == null) {
                throw new BusinessException("学员信息不存在");
            }

            // 创建PDF文档
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(50, 50, 50, 50);

            // 加载中文字体
            PdfFont font = PdfFontFactory.createFont(FONT_PATH, FONT_ENCODING, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            PdfFont boldFont = PdfFontFactory.createFont(FONT_PATH, FONT_ENCODING, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

            // 设置文档默认字体
            document.setFont(font);

            // 添加标题
            addTitle(document, boldFont);

            // 添加合同基本信息
            addContractInfo(document, contract, font);

            // 添加甲方信息
            addPartyAInfo(document, font, boldFont);

            // 添加乙方信息
            addPartyBInfo(document, student, font, boldFont);

            // 添加课程明细表格
            addCourseTable(document, items, font, boldFont);

            // 添加费用汇总
            addAmountSummary(document, contract, font, boldFont);

            // 添加合同期限
            addContractPeriod(document, contract, font, boldFont);

            // 添加合同条款
            addContractTerms(document, font, boldFont);

            // 添加备注
            if (StrUtil.isNotBlank(contract.getRemark())) {
                addRemark(document, contract.getRemark(), font, boldFont);
            }

            // 添加签字栏
            addSignatureSection(document, contract, font, boldFont);

            // 关闭文档
            document.close();

            log.info("合同PDF生成完成: contractId={}", contractId);

        } catch (Exception e) {
            log.error("生成PDF到流失败: contractId={}", contractId, e);
            throw new BusinessException("生成PDF失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] downloadPdf(Long contractId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            generatePdfToStream(contractId, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("下载合同PDF失败: contractId={}", contractId, e);
            throw new BusinessException("下载合同PDF失败: " + e.getMessage());
        }
    }

    @Override
    public boolean canGeneratePdf(Long contractId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            return false;
        }
        // 只有已签署的合同才能生成PDF
        return "signed".equals(contract.getStatus()) ||
               "completed".equals(contract.getStatus());
    }

    /**
     * 添加标题
     */
    private void addTitle(Document document, PdfFont boldFont) {
        Paragraph title = new Paragraph("教育培训服务合同")
                .setFont(boldFont)
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);
    }

    /**
     * 添加合同基本信息
     */
    private void addContractInfo(Document document, Contract contract, PdfFont font) {
        Paragraph contractNo = new Paragraph()
                .add("合同编号：")
                .add(contract.getContractNo())
                .setFont(font)
                .setFontSize(12)
                .setMarginBottom(10);
        document.add(contractNo);

        if (contract.getSignDate() != null) {
            Paragraph signDate = new Paragraph()
                    .add("签订日期：")
                    .add(contract.getSignDate().format(DATE_FORMATTER))
                    .setFont(font)
                    .setFontSize(12)
                    .setMarginBottom(15);
            document.add(signDate);
        }
    }

    /**
     * 添加甲方信息
     */
    private void addPartyAInfo(Document document, PdfFont font, PdfFont boldFont) {
        Paragraph partyATitle = new Paragraph("甲方（培训机构）")
                .setFont(boldFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(partyATitle);

        // 这里可以从配置或数据库读取机构信息
        addInfoLine(document, "名称：", "XX教育培训机构", font);
        addInfoLine(document, "联系人：", "张老师", font);
        addInfoLine(document, "电话：", "400-123-4567", font);
        addInfoLine(document, "地址：", "XX市XX区XX路XX号", font);
    }

    /**
     * 添加乙方信息
     */
    private void addPartyBInfo(Document document, StudentDTO student, PdfFont font, PdfFont boldFont) {
        Paragraph partyBTitle = new Paragraph("乙方（学员/家长）")
                .setFont(boldFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(10);
        document.add(partyBTitle);

        addInfoLine(document, "姓名：", student.getName(), font);

        // 如果有联系人，显示监护人信息
        if (student.getContacts() != null && !student.getContacts().isEmpty()) {
            student.getContacts().stream()
                    .filter(c -> c.getIsPrimary() != null && c.getIsPrimary())
                    .findFirst()
                    .ifPresent(contact -> {
                        addInfoLine(document, "监护人：", contact.getName(), font);
                    });
        }

        if (StrUtil.isNotBlank(student.getPhone())) {
            addInfoLine(document, "电话：", student.getPhone(), font);
        }

        if (StrUtil.isNotBlank(student.getIdCard())) {
            addInfoLine(document, "身份证号：", student.getIdCard(), font);
        }

        if (StrUtil.isNotBlank(student.getAddress())) {
            addInfoLine(document, "地址：", student.getAddress(), font);
        }
    }

    /**
     * 添加课程明细表格
     */
    private void addCourseTable(Document document, List<ContractItem> items, PdfFont font, PdfFont boldFont) {
        Paragraph tableTitle = new Paragraph("课程明细")
                .setFont(boldFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(10);
        document.add(tableTitle);

        // 创建表格（4列）
        float[] columnWidths = {3, 1.5f, 1.5f, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // 表头背景色
        DeviceRgb headerBgColor = new DeviceRgb(240, 240, 240);

        // 添加表头
        table.addHeaderCell(createHeaderCell("课程名称", font, boldFont, headerBgColor));
        table.addHeaderCell(createHeaderCell("课时数", font, boldFont, headerBgColor));
        table.addHeaderCell(createHeaderCell("单价（元）", font, boldFont, headerBgColor));
        table.addHeaderCell(createHeaderCell("金额（元）", font, boldFont, headerBgColor));

        // 添加数据行
        for (ContractItem item : items) {
            table.addCell(createDataCell(item.getCourseName(), font));
            table.addCell(createDataCell(String.valueOf(item.getHours()), font));
            table.addCell(createDataCell(item.getUnitPrice().toString(), font));
            table.addCell(createDataCell(item.getAmount().toString(), font));
        }

        document.add(table);
    }

    /**
     * 添加费用汇总
     */
    private void addAmountSummary(Document document, Contract contract, PdfFont font, PdfFont boldFont) {
        Paragraph summaryTitle = new Paragraph("费用汇总")
                .setFont(boldFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(10);
        document.add(summaryTitle);

        addInfoLine(document, "合同总金额：", contract.getAmount() + " 元", font);

        if (contract.getDiscountAmount() != null &&
            contract.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            addInfoLine(document, "优惠金额：", contract.getDiscountAmount() + " 元", font);
        }

        // 实付金额用红色加粗显示
        Paragraph paidAmount = new Paragraph()
                .add(new Paragraph("实付金额：").setFont(font).setFontSize(12))
                .add(new Paragraph(contract.getPaidAmount() + " 元")
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setBold()
                        .setFontColor(new DeviceRgb(208, 0, 0)))
                .setMarginBottom(5);
        document.add(paidAmount);

        if (contract.getReceivedAmount() != null) {
            addInfoLine(document, "已付金额：", contract.getReceivedAmount() + " 元", font);

            BigDecimal unpaid = contract.getPaidAmount().subtract(contract.getReceivedAmount());
            if (unpaid.compareTo(BigDecimal.ZERO) > 0) {
                addInfoLine(document, "待付金额：", unpaid + " 元", font);
            }
        }

        addInfoLine(document, "付款方式：", "分期付款", font);
    }

    /**
     * 添加合同期限
     */
    private void addContractPeriod(Document document, Contract contract, PdfFont font, PdfFont boldFont) {
        if (contract.getEffectiveDate() != null && contract.getExpireDate() != null) {
            Paragraph periodTitle = new Paragraph("合同期限")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(15)
                    .setMarginBottom(10);
            document.add(periodTitle);

            addInfoLine(document, "生效日期：", contract.getEffectiveDate().format(DATE_FORMATTER), font);
            addInfoLine(document, "到期日期：", contract.getExpireDate().format(DATE_FORMATTER), font);
        }
    }

    /**
     * 添加合同条款
     */
    private void addContractTerms(Document document, PdfFont font, PdfFont boldFont) {
        Paragraph termsTitle = new Paragraph("合同条款")
                .setFont(boldFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(10);
        document.add(termsTitle);

        String terms = getDefaultTerms();
        Paragraph termsPara = new Paragraph(terms)
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMarginBottom(10);
        document.add(termsPara);
    }

    /**
     * 添加备注
     */
    private void addRemark(Document document, String remark, PdfFont font, PdfFont boldFont) {
        Paragraph remarkTitle = new Paragraph("备注")
                .setFont(boldFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(10);
        document.add(remarkTitle);

        Paragraph remarkPara = new Paragraph(remark)
                .setFont(font)
                .setFontSize(10)
                .setMarginBottom(10);
        document.add(remarkPara);
    }

    /**
     * 添加签字栏
     */
    private void addSignatureSection(Document document, Contract contract, PdfFont font, PdfFont boldFont) {
        // 添加一些空白
        document.add(new Paragraph("\n").setMarginTop(20));

        // 创建签字表格
        Table signTable = new Table(2);
        signTable.setWidth(UnitValue.createPercentValue(100));
        signTable.setBorder(Border.NO_BORDER);

        // 甲方签字
        Cell partyACell = new Cell()
                .add(new Paragraph("甲方（盖章）：").setFont(font).setFontSize(12))
                .add(new Paragraph("\n\n"))
                .add(new Paragraph("_____________________").setFont(font))
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10);
        signTable.addCell(partyACell);

        // 乙方签字
        Cell partyBCell = new Cell()
                .add(new Paragraph("乙方（签字）：").setFont(font).setFontSize(12))
                .add(new Paragraph("\n\n"))
                .add(new Paragraph("_____________________").setFont(font))
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10);
        signTable.addCell(partyBCell);

        document.add(signTable);

        // 添加日期
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        Paragraph datePara = new Paragraph("日期：" + dateStr)
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(10);
        document.add(datePara);
    }

    /**
     * 添加信息行
     */
    private void addInfoLine(Document document, String label, String value, PdfFont font) {
        if (value == null) {
            value = "";
        }
        Paragraph para = new Paragraph()
                .add(label)
                .add(value)
                .setFont(font)
                .setFontSize(12)
                .setMarginBottom(5);
        document.add(para);
    }

    /**
     * 创建表头单元格
     */
    private Cell createHeaderCell(String text, PdfFont font, PdfFont boldFont, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(text).setFont(boldFont).setFontSize(11).setBold())
                .setBackgroundColor(bgColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));
    }

    /**
     * 创建数据单元格
     */
    private Cell createDataCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(6)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));
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
