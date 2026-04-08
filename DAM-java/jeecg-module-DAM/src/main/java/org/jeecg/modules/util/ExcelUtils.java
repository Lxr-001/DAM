package org.jeecg.modules.util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Excel工具类：用于通过文件路径获取XSSFWorkbook对象
 */
public class ExcelUtils {

    /**
     * 通过Excel文件路径创建XSSFWorkbook（仅支持.xlsx格式）
     *
     * @param excelFilePath Excel文件的完整路径（例如：D:/test/实验数据.xlsx）
     * @return XSSFWorkbook对象
     * @throws IOException 当文件不存在、路径错误或格式不支持时抛出
     */
    public static XSSFWorkbook getWorkbookFromPath(String excelFilePath) throws IOException {
        // 验证文件是否存在
        File excelFile = new File(excelFilePath);
        if (!excelFile.exists()) {
            throw new IOException("Excel文件不存在：" + excelFilePath);
        }
        if (!excelFile.isFile()) {
            throw new IOException("路径不是有效的文件：" + excelFilePath);
        }
        // 验证文件格式（仅支持.xlsx）
        if (!excelFilePath.toLowerCase().endsWith(".xlsx")) {
            throw new IOException("不支持的文件格式，仅支持.xlsx：" + excelFilePath);
        }

        // 通过文件输入流加载Excel并返回XSSFWorkbook
        InputStream inputStream = new FileInputStream(excelFile);
        return new XSSFWorkbook(inputStream);
    }
}
