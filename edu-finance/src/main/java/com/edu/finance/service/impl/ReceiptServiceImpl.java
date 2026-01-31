package com.edu.finance.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ReceiptDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractItem;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.mapper.ContractItemMapper;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.mapper.PaymentMapper;
import com.edu.finance.service.ReceiptService;
import com.edu.framework.file.FileService;
import com.edu.student.api.StudentApi;
import com.edu.student.api.dto.StudentDTO;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收据服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final PaymentMapper paymentMapper;
    private final ContractMapper contractMapper;
    private final ContractItemMapper contractItemMapper;
    private final StudentApi studentApi;
    private final FileService fileService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    @Override
    public ReceiptDTO getReceiptDetail(Long paymentId) {
        return buildReceiptData(paymentId);
    }

    @Override
    public String generateReceiptPdf(Long paymentId) {
        // 获取收据数据
        ReceiptDTO receiptData = buildReceiptData(paymentId);

        // 生成PDF
        ByteArrayOutputStream pdfStream = generatePdfStream(receiptData);

        // 上传文件
        String fileName = "receipt_" + receiptData.getReceiptNo() + ".pdf";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfStream.toByteArray());
        String fileUrl = fileService.upload(inputStream, "receipts", fileName);

        log.info("收据PDF生成成功: paymentId={}, fileUrl={}", paymentId, fileUrl);
        return fileUrl;
    }

    @Override
    public ByteArrayOutputStream generatePdfStream(ReceiptDTO receiptData) {
        try {
            // 生成HTML内容
            String htmlContent = generateHtmlContent(receiptData);

            // 转换为PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ConverterProperties converterProperties = new ConverterProperties();

            // 设置字体提供者（支持中文）
            DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);
            converterProperties.setFontProvider(fontProvider);

            HtmlConverter.convertToPdf(htmlContent, outputStream, converterProperties);

            return outputStream;
        } catch (Exception e) {
            log.error("生成收据PDF失败", e);
            throw new BusinessException("生成收据PDF失败: " + e.getMessage());
        }
    }

    @Override
    public String previewReceiptHtml(Long paymentId) {
        ReceiptDTO receiptData = buildReceiptData(paymentId);
        return generateHtmlContent(receiptData);
    }

    @Override
    public String generateBatchReceiptPdf(List<Long> paymentIds) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);

            ConverterProperties converterProperties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);
            converterProperties.setFontProvider(fontProvider);

            // 为每个收款记录生成收据页面
            for (int i = 0; i < paymentIds.size(); i++) {
                Long paymentId = paymentIds.get(i);
                ReceiptDTO receiptData = buildReceiptData(paymentId);
                String htmlContent = generateHtmlContent(receiptData);

                // 如果不是第一页，添加分页符
                if (i > 0) {
                    htmlContent = htmlContent.replace("<body>", "<body><div style=\"page-break-before: always;\"></div>");
                }

                HtmlConverter.convertToPdf(htmlContent, pdfDocument, converterProperties);
            }

            pdfDocument.close();

            // 上传文件
            String fileName = "receipts_batch_" + System.currentTimeMillis() + ".pdf";
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            String fileUrl = fileService.upload(inputStream, "receipts", fileName);

            log.info("批量收据PDF生成成功: paymentIds={}, fileUrl={}", paymentIds, fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("生成批量收据PDF失败", e);
            throw new BusinessException("生成批量收据PDF失败: " + e.getMessage());
        }
    }

    @Override
    public String generateReceiptNo() {
        // 生成收据编号：SJ + yyyyMMdd + 6位序号
        String dateStr = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        String prefix = "SJ" + dateStr;

        // 查询当天最大序号
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Payment::getPaymentNo, prefix)
                .orderByDesc(Payment::getPaymentNo)
                .last("LIMIT 1");
        Payment lastPayment = paymentMapper.selectOne(wrapper);

        int sequence = 1;
        if (lastPayment != null && StrUtil.isNotBlank(lastPayment.getPaymentNo())) {
            String lastNo = lastPayment.getPaymentNo();
            if (lastNo.length() >= prefix.length() + 6) {
                String lastSequence = lastNo.substring(prefix.length());
                try {
                    sequence = Integer.parseInt(lastSequence) + 1;
                } catch (NumberFormatException e) {
                    log.warn("解析收据编号序号失败: {}", lastNo);
                }
            }
        }

        return prefix + String.format("%06d", sequence);
    }

    /**
     * 构建收据数据
     */
    private ReceiptDTO buildReceiptData(Long paymentId) {
        // 查询收款记录
        Payment payment = paymentMapper.selectById(paymentId);
        if (payment == null) {
            throw new BusinessException("收款记录不存在");
        }

        // 查询合同信息
        Contract contract = null;
        if (payment.getContractId() != null) {
            contract = contractMapper.selectById(payment.getContractId());
        }

        // 查询学员信息
        StudentDTO student = studentApi.getStudentById(payment.getStudentId());
        if (student == null) {
            throw new BusinessException("学员信息不存在");
        }

        // 构建收据数据
        ReceiptDTO receiptData = new ReceiptDTO();
        receiptData.setReceiptNo(StrUtil.isNotBlank(payment.getPaymentNo()) ? payment.getPaymentNo() : generateReceiptNo());
        receiptData.setPaymentDate(payment.getPayTime() != null ? payment.getPayTime() : payment.getCreateTime());
        receiptData.setAmount(payment.getAmount());
        receiptData.setAmountInWords(convertToChineseNumber(payment.getAmount()));
        receiptData.setPaymentMethod(getPaymentMethodName(payment.getPaymentMethod()));
        receiptData.setReceiver("系统"); // 默认收款人
        receiptData.setRemark(payment.getRemark());

        // 学员信息
        ReceiptDTO.StudentInfo studentInfo = new ReceiptDTO.StudentInfo();
        studentInfo.setName(student.getName());
        studentInfo.setPhone(student.getPhone());
        studentInfo.setStudentNo(student.getStudentNo());
        receiptData.setStudentInfo(studentInfo);

        // 收款项目
        ReceiptDTO.PaymentItem paymentItem = new ReceiptDTO.PaymentItem();
        if (contract != null) {
            paymentItem.setContractNo(contract.getContractNo());

            // 查询合同明细获取课程信息
            List<ContractItem> items = contractItemMapper.selectByContractId(contract.getId());
            if (items != null && !items.isEmpty()) {
                ContractItem firstItem = items.get(0);
                paymentItem.setName(firstItem.getCourseName());
                paymentItem.setQuantity(firstItem.getHours());
                paymentItem.setUnitPrice(firstItem.getUnitPrice());
            } else {
                paymentItem.setName("培训费用");
                paymentItem.setQuantity(1);
                paymentItem.setUnitPrice(payment.getAmount());
            }
        } else {
            paymentItem.setName("培训费用");
            paymentItem.setContractNo("--");
            paymentItem.setQuantity(1);
            paymentItem.setUnitPrice(payment.getAmount());
        }
        receiptData.setPaymentItem(paymentItem);

        // 机构信息
        ReceiptDTO.InstitutionInfo institutionInfo = new ReceiptDTO.InstitutionInfo();
        institutionInfo.setName("XX教育培训机构"); // 可以从配置中获取
        institutionInfo.setAddress("XX市XX区XX路XX号"); // 可以从配置中获取
        institutionInfo.setPhone("400-123-4567"); // 可以从配置中获取
        institutionInfo.setCampusName("总部"); // 默认校区
        receiptData.setInstitutionInfo(institutionInfo);

        return receiptData;
    }

    /**
     * 生成HTML内容
     */
    private String generateHtmlContent(ReceiptDTO receiptData) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("body { font-family: SimSun, serif; font-size: 12pt; line-height: 1.6; margin: 0; padding: 40px; }");
        html.append(".receipt { width: 700px; margin: 0 auto; border: 2px solid #000; padding: 30px; }");
        html.append(".header { text-align: center; margin-bottom: 30px; }");
        html.append(".title { font-size: 24pt; font-weight: bold; margin-bottom: 10px; }");
        html.append(".receipt-no { font-size: 11pt; color: #666; }");
        html.append(".section { margin: 20px 0; }");
        html.append(".row { margin: 10px 0; display: flex; align-items: center; }");
        html.append(".label { font-weight: bold; width: 120px; display: inline-block; }");
        html.append(".value { flex: 1; border-bottom: 1px solid #000; padding: 0 10px; min-height: 24px; }");
        html.append(".amount-section { margin: 30px 0; padding: 20px; background-color: #f9f9f9; border: 1px solid #ddd; }");
        html.append(".amount-large { font-size: 18pt; font-weight: bold; color: #d00; text-align: center; margin: 10px 0; }");
        html.append(".footer { margin-top: 50px; }");
        html.append(".signature { display: flex; justify-content: space-between; margin-top: 30px; }");
        html.append(".signature-item { text-align: center; }");
        html.append(".signature-line { display: inline-block; width: 150px; border-bottom: 1px solid #000; margin-top: 40px; }");
        html.append(".institution-info { margin-top: 40px; padding-top: 20px; border-top: 1px dashed #999; font-size: 10pt; color: #666; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 15px 0; }");
        html.append("table, th, td { border: 1px solid #000; }");
        html.append("th, td { padding: 8px; text-align: left; }");
        html.append("th { background-color: #f0f0f0; font-weight: bold; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        html.append("<div class=\"receipt\">");

        // 标题
        html.append("<div class=\"header\">");
        html.append("<div class=\"title\">收据</div>");
        html.append("<div class=\"receipt-no\">收据编号：").append(receiptData.getReceiptNo()).append("</div>");
        html.append("</div>");

        // 收款日期
        html.append("<div class=\"section\">");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">收款日期：</span>");
        html.append("<span class=\"value\">").append(receiptData.getPaymentDate().format(DATE_TIME_FORMATTER)).append("</span>");
        html.append("</div>");
        html.append("</div>");

        // 学员信息
        html.append("<div class=\"section\">");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">学员姓名：</span>");
        html.append("<span class=\"value\">").append(receiptData.getStudentInfo().getName()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">学员编号：</span>");
        html.append("<span class=\"value\">").append(receiptData.getStudentInfo().getStudentNo()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">联系电话：</span>");
        html.append("<span class=\"value\">").append(receiptData.getStudentInfo().getPhone()).append("</span>");
        html.append("</div>");
        html.append("</div>");

        // 收款项目
        html.append("<div class=\"section\">");
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>收款项目</th>");
        html.append("<th>数量</th>");
        html.append("<th>单价（元）</th>");
        html.append("<th>金额（元）</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        html.append("<tr>");
        html.append("<td>").append(receiptData.getPaymentItem().getName()).append("</td>");
        html.append("<td>").append(receiptData.getPaymentItem().getQuantity()).append("</td>");
        html.append("<td>").append(receiptData.getPaymentItem().getUnitPrice()).append("</td>");
        html.append("<td>").append(receiptData.getAmount()).append("</td>");
        html.append("</tr>");
        html.append("</tbody>");
        html.append("</table>");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">合同编号：</span>");
        html.append("<span class=\"value\">").append(receiptData.getPaymentItem().getContractNo()).append("</span>");
        html.append("</div>");
        html.append("</div>");

        // 收款金额
        html.append("<div class=\"amount-section\">");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">收款金额：</span>");
        html.append("<span class=\"value\">").append(receiptData.getAmount()).append(" 元</span>");
        html.append("</div>");
        html.append("<div class=\"amount-large\">");
        html.append("人民币（大写）：").append(receiptData.getAmountInWords());
        html.append("</div>");
        html.append("</div>");

        // 收款方式和收款人
        html.append("<div class=\"section\">");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">收款方式：</span>");
        html.append("<span class=\"value\">").append(receiptData.getPaymentMethod()).append("</span>");
        html.append("</div>");
        html.append("<div class=\"row\">");
        html.append("<span class=\"label\">收款人：</span>");
        html.append("<span class=\"value\">").append(receiptData.getReceiver()).append("</span>");
        html.append("</div>");
        html.append("</div>");

        // 备注
        if (StrUtil.isNotBlank(receiptData.getRemark())) {
            html.append("<div class=\"section\">");
            html.append("<div class=\"row\">");
            html.append("<span class=\"label\">备注：</span>");
            html.append("<span class=\"value\">").append(receiptData.getRemark()).append("</span>");
            html.append("</div>");
            html.append("</div>");
        }

        // 签字
        html.append("<div class=\"footer\">");
        html.append("<div class=\"signature\">");
        html.append("<div class=\"signature-item\">");
        html.append("<div>收款单位（盖章）</div>");
        html.append("<div class=\"signature-line\"></div>");
        html.append("</div>");
        html.append("<div class=\"signature-item\">");
        html.append("<div>经办人（签字）</div>");
        html.append("<div class=\"signature-line\"></div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");

        // 机构信息
        html.append("<div class=\"institution-info\">");
        html.append("<div>").append(receiptData.getInstitutionInfo().getName()).append("</div>");
        if (StrUtil.isNotBlank(receiptData.getInstitutionInfo().getCampusName())) {
            html.append("<div>校区：").append(receiptData.getInstitutionInfo().getCampusName()).append("</div>");
        }
        html.append("<div>地址：").append(receiptData.getInstitutionInfo().getAddress()).append("</div>");
        html.append("<div>电话：").append(receiptData.getInstitutionInfo().getPhone()).append("</div>");
        html.append("</div>");

        html.append("</div>"); // receipt

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 将数字转换为中文大写
     */
    private String convertToChineseNumber(BigDecimal amount) {
        if (amount == null) {
            return "零元整";
        }

        String[] chineseNumbers = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[] units = {"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿"};
        String[] decimalUnits = {"角", "分"};

        long integerPart = amount.longValue();
        int decimalPart = amount.subtract(new BigDecimal(integerPart)).multiply(new BigDecimal(100)).intValue();

        if (integerPart == 0 && decimalPart == 0) {
            return "零元整";
        }

        StringBuilder result = new StringBuilder();

        // 处理整数部分
        if (integerPart == 0) {
            result.append("零");
        } else {
            String integerStr = String.valueOf(integerPart);
            int length = integerStr.length();
            boolean lastIsZero = false;

            for (int i = 0; i < length; i++) {
                int digit = integerStr.charAt(i) - '0';
                int unitIndex = length - i - 1;

                if (digit == 0) {
                    if (!lastIsZero && unitIndex != 0 && unitIndex != 4) {
                        result.append(chineseNumbers[0]);
                    }
                    lastIsZero = true;
                } else {
                    result.append(chineseNumbers[digit]);
                    result.append(units[unitIndex]);
                    lastIsZero = false;
                }

                if (unitIndex == 4 && !lastIsZero) {
                    result.append("万");
                }
            }
        }

        result.append("元");

        // 处理小数部分
        if (decimalPart == 0) {
            result.append("整");
        } else {
            int jiao = decimalPart / 10;
            int fen = decimalPart % 10;

            if (jiao > 0) {
                result.append(chineseNumbers[jiao]).append(decimalUnits[0]);
            } else {
                result.append("零");
            }

            if (fen > 0) {
                result.append(chineseNumbers[fen]).append(decimalUnits[1]);
            }
        }

        return result.toString();
    }

    /**
     * 获取支付方式名称
     */
    private String getPaymentMethodName(String paymentMethod) {
        if (StrUtil.isBlank(paymentMethod)) {
            return "现金";
        }

        switch (paymentMethod.toLowerCase()) {
            case "wechat":
                return "微信支付";
            case "alipay":
                return "支付宝";
            case "unionpay":
                return "银联支付";
            case "cash":
                return "现金";
            case "pos":
                return "POS机";
            case "bank_transfer":
                return "银行转账";
            default:
                return paymentMethod;
        }
    }
}
