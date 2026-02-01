package com.edu.common.report;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.edu.common.exception.BusinessException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报表导出服务
 * 支持 Excel 和 PDF 格式导出
 */
@Slf4j
@Service
public class ReportExportService {

    private static final String DEFAULT_FONT = "STSong-Light";
    private static final String FONT_ENCODING = "UniGB-UCS2-H";

    /**
     * 导出 Excel 报表
     *
     * @param response 响应对象
     * @param fileName 文件名（不含扩展名）
     * @param config   Excel 导出配置
     */
    public void exportExcel(HttpServletResponse response, String fileName, ExcelExportConfig config) {
        try {
            setResponseHeaders(response, fileName, ExportFormat.EXCEL);

            // 创建 ExcelWriter
            var writerBuilder = EasyExcel.write(response.getOutputStream());

            // 配置自动列宽
            if (config.getAutoColumnWidth()) {
                writerBuilder.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
            }

            // 配置样式
            writerBuilder.registerWriteHandler(createCellStyleStrategy());

            // 写入多个 Sheet
            var writer = writerBuilder.build();
            for (int i = 0; i < config.getSheets().size(); i++) {
                var sheetConfig = config.getSheets().get(i);
                var sheet = EasyExcel.writerSheet(i, sheetConfig.getSheetName())
                        .head(sheetConfig.getDataClass())
                        .build();
                writer.write(sheetConfig.getData(), sheet);
            }
            writer.finish();

            log.info("Excel report exported successfully: {}", fileName);

        } catch (IOException e) {
            log.error("Failed to export Excel report: {}", fileName, e);
            throw new BusinessException("Excel 导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出 Excel 到字节数组（用于异步导出）
     *
     * @param config Excel 导出配置
     * @return 字节数组
     */
    public byte[] exportExcelToBytes(ExcelExportConfig config) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            var writerBuilder = EasyExcel.write(outputStream);

            if (config.getAutoColumnWidth()) {
                writerBuilder.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
            }

            writerBuilder.registerWriteHandler(createCellStyleStrategy());

            var writer = writerBuilder.build();
            for (int i = 0; i < config.getSheets().size(); i++) {
                var sheetConfig = config.getSheets().get(i);
                var sheet = EasyExcel.writerSheet(i, sheetConfig.getSheetName())
                        .head(sheetConfig.getDataClass())
                        .build();
                writer.write(sheetConfig.getData(), sheet);
            }
            writer.finish();

            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to export Excel to bytes", e);
            throw new BusinessException("Excel 导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出 PDF 报表
     *
     * @param response 响应对象
     * @param fileName 文件名（不含扩展名）
     * @param config   PDF 导出配置
     */
    public void exportPdf(HttpServletResponse response, String fileName, PdfExportConfig config) {
        try {
            setResponseHeaders(response, fileName, ExportFormat.PDF);
            exportPdfToStream(response.getOutputStream(), config);
            log.info("PDF report exported successfully: {}", fileName);

        } catch (IOException e) {
            log.error("Failed to export PDF report: {}", fileName, e);
            throw new BusinessException("PDF 导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出 PDF 到字节数组（用于异步导出）
     *
     * @param config PDF 导出配置
     * @return 字节数组
     */
    public byte[] exportPdfToBytes(PdfExportConfig config) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            exportPdfToStream(outputStream, config);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Failed to export PDF to bytes", e);
            throw new BusinessException("PDF 导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出 PDF 到输出流
     */
    private void exportPdfToStream(OutputStream outputStream, PdfExportConfig config) throws IOException {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // 设置中文字体
            PdfFont font = PdfFontFactory.createFont(DEFAULT_FONT, FONT_ENCODING);
            document.setFont(font);

            // 添加标题
            if (config.getTitle() != null) {
                Paragraph title = new Paragraph(config.getTitle())
                        .setFontSize(18)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(title);
            }

            // 添加页眉
            if (config.getHeaderText() != null) {
                Paragraph header = new Paragraph(config.getHeaderText())
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(10);
                document.add(header);
            }

            // 添加表格
            for (PdfExportConfig.TableConfig tableConfig : config.getTables()) {
                addTableToPdf(document, tableConfig, font);
            }

            // 添加页脚
            if (config.getFooterText() != null || config.getShowPageNumber()) {
                addFooter(document, config, font);
            }

        } finally {
            document.close();
        }
    }

    /**
     * 添加表格到 PDF
     */
    private void addTableToPdf(Document document, PdfExportConfig.TableConfig tableConfig, PdfFont font) {
        // 添加表格标题
        if (tableConfig.getTitle() != null) {
            Paragraph tableTitle = new Paragraph(tableConfig.getTitle())
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(15)
                    .setMarginBottom(10);
            document.add(tableTitle);
        }

        // 创建表格
        float[] columnWidths = tableConfig.getColumnWidths();
        if (columnWidths == null) {
            columnWidths = new float[tableConfig.getHeaders().size()];
            for (int i = 0; i < columnWidths.length; i++) {
                columnWidths[i] = 1;
            }
        }

        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        // 添加表头
        for (String header : tableConfig.getHeaders()) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setFont(font))
                    .setBackgroundColor(new DeviceRgb(200, 200, 200))
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5);
            table.addHeaderCell(cell);
        }

        // 添加数据行
        for (List<String> row : tableConfig.getData()) {
            for (String cellData : row) {
                Cell cell = new Cell()
                        .add(new Paragraph(cellData != null ? cellData : "").setFont(font))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5);
                table.addCell(cell);
            }
        }

        document.add(table);
    }

    /**
     * 添加页脚
     */
    private void addFooter(Document document, PdfExportConfig config, PdfFont font) {
        String footerText = "";
        if (config.getFooterText() != null) {
            footerText = config.getFooterText();
        }
        if (config.getShowPageNumber()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            footerText += (footerText.isEmpty() ? "" : " | ") + "生成时间: " + timestamp;
        }

        Paragraph footer = new Paragraph(footerText)
                .setFont(font)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setFontColor(ColorConstants.GRAY);
        document.add(footer);
    }

    /**
     * 创建单元格样式策略
     */
    private HorizontalCellStyleStrategy createCellStyleStrategy() {
        // 表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 数据行样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * 设置响应头
     */
    private void setResponseHeaders(HttpServletResponse response, String fileName, ExportFormat format) {
        response.setContentType(format.getMimeType());
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition",
                "attachment;filename*=utf-8''" + encodedFileName + format.getExtension());
    }
}
