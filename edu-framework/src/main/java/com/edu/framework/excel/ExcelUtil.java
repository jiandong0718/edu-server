package com.edu.framework.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.edu.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Excel 导入导出工具类
 */
@Slf4j
public class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * 导出 Excel
     *
     * @param response  响应对象
     * @param fileName  文件名（不含扩展名）
     * @param sheetName 工作表名
     * @param clazz     数据类型
     * @param data      数据列表
     */
    public static <T> void export(HttpServletResponse response, String fileName,
                                  String sheetName, Class<T> clazz, List<T> data) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (IOException e) {
            log.error("Excel导出失败", e);
            throw new BusinessException("Excel导出失败: " + e.getMessage());
        }
    }

    /**
     * 导入 Excel（一次性读取全部数据）
     *
     * @param inputStream 输入流
     * @param clazz       数据类型
     * @return 数据列表
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        EasyExcel.read(inputStream, clazz, new PageReadListener<T>(result::addAll))
                .sheet()
                .doRead();
        return result;
    }

    /**
     * 导入 Excel（分批处理）
     *
     * @param inputStream 输入流
     * @param clazz       数据类型
     * @param batchSize   每批数量
     * @param consumer    批量处理函数
     */
    public static <T> void importExcel(InputStream inputStream, Class<T> clazz,
                                       int batchSize, Consumer<List<T>> consumer) {
        EasyExcel.read(inputStream, clazz, new PageReadListener<>(consumer, batchSize))
                .sheet()
                .doRead();
    }

    /**
     * 导出 Excel 模板
     *
     * @param response  响应对象
     * @param fileName  文件名（不含扩展名）
     * @param sheetName 工作表名
     * @param clazz     数据类型
     */
    public static <T> void exportTemplate(HttpServletResponse response, String fileName,
                                          String sheetName, Class<T> clazz) {
        export(response, fileName, sheetName, clazz, new ArrayList<>());
    }
}
