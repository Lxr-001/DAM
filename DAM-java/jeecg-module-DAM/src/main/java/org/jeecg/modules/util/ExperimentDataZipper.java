package org.jeecg.modules.util;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 实验数据导出为ZIP压缩包工具类
 */
public class ExperimentDataZipper {

    // 四个子文件夹名称
    private static final String FOLDER_OUTLINE = "试验大纲信息";
    private static final String FOLDER_STARTUP = "实验启动信息";
    private static final String FOLDER_PROCESS = "实验过程信息";
    private static final String FOLDER_ACCEPTANCE = "实验验收信息";

    /**
     * 导出实验数据为ZIP压缩包
     *
     * @param zipFileName           压缩包名称(不含.zip后缀)
     * @param outlineExcel          试验大纲Excel
     * @param outlineAttachments    试验大纲附件列表
     * @param startupExcel          实验启动Excel
     * @param startupAttachments    实验启动附件列表
     * @param processExcel          实验过程Excel
     * @param processAttachments    实验过程附件列表
     * @param acceptanceExcel       实验验收Excel
     * @param acceptanceAttachments 实验验收附件列表
     * @param outputDir             输出目录
     * @throws IOException 当IO操作失败时抛出
     */
    public static void exportToZip(String zipFileName,
                                   XSSFWorkbook outlineExcel, List<File> outlineAttachments,
                                   XSSFWorkbook startupExcel, List<File> startupAttachments,
                                   XSSFWorkbook processExcel, List<File> processAttachments,
                                   XSSFWorkbook acceptanceExcel, List<File> acceptanceAttachments,
                                   String outputDir) throws IOException {

        // 创建输出目录(如果不存在)
        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        // 压缩包完整路径
        String zipFilePath = outputDir + File.separator + zipFileName + ".zip";

        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFilePath));

            // 添加试验大纲信息
            addFolderToZip(zos, FOLDER_OUTLINE, outlineExcel, "试验大纲信息.xlsx", outlineAttachments);

            // 添加实验启动信息
            addFolderToZip(zos, FOLDER_STARTUP, startupExcel, "实验启动信息.xlsx", startupAttachments);

            // 添加实验过程信息
            addFolderToZip(zos, FOLDER_PROCESS, processExcel, "实验过程信息.xlsx", processAttachments);

            // 添加实验验收信息
            addFolderToZip(zos, FOLDER_ACCEPTANCE, acceptanceExcel, "实验验收信息.xlsx", acceptanceAttachments);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向ZIP中添加一个文件夹及其内容
     */
    private static void addFolderToZip(ZipOutputStream zos, String folderName,
                                       XSSFWorkbook excelFile, String excelFileName,
                                       List<File> attachments) throws IOException {
        // 添加Excel文件
        if (excelFile != null) {
            String excelEntryName = folderName + File.separator + excelFileName;
            ZipEntry excelEntry = new ZipEntry(excelEntryName);
            zos.putNextEntry(excelEntry);

            // 写入Excel内容
            excelFile.write(zos);
            zos.closeEntry();
        }

        // 添加附件
        if (attachments != null && !attachments.isEmpty()) {
            for (int i = 0; i < attachments.size(); i++) {
                File attachment = attachments.get(i);
                if (attachment.exists() && attachment.isFile()) {
                    String entryName = folderName + File.separator + attachment.getName();
                    ZipEntry attachmentEntry = new ZipEntry(entryName);
                    zos.putNextEntry(attachmentEntry);

                    // 写入附件内容
                    InputStream is = null;
                    try {
                        is = new FileInputStream(attachment);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            zos.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    /**
     * 测试主方法
     */
    public static void main(String[] args) {
        try {

            // String outlineExcelPath ="D:/实验数据/试验大纲.xlsx";
            // XSSFWorkbook outlineExcel = ExcelUtils.getWorkbookFromPath(outlineExcelPath);

            // 创建测试Excel文件
            XSSFWorkbook outlineExcel = createTestExcel("试验大纲数据");
            XSSFWorkbook startupExcel = createTestExcel("实验启动数据");
            XSSFWorkbook processExcel = createTestExcel("实验过程数据");
            XSSFWorkbook acceptanceExcel = createTestExcel("实验验收数据");

            // 创建测试附件文件
            File attachment1 = createTestFile("附件1.txt", "这是试验大纲的附件1内容");
            File attachment2 = createTestFile("附件2.pdf", "这是实验过程的PDF附件内容".getBytes());
            File attachment3 = createTestFile("验收报告.doc", "这是实验验收的文档内容");

            // 组织附件列表（使用ArrayList替代List.of()）
            List<File> outlineAttachments = new ArrayList<File>();
            outlineAttachments.add(attachment1);

            List<File> processAttachments = new ArrayList<File>();
            processAttachments.add(attachment2);

            List<File> acceptanceAttachments = new ArrayList<File>();
            acceptanceAttachments.add(attachment3);

            // 导出为ZIP
            String zipName = "实验数据汇总_" + System.currentTimeMillis();
            String outputDir = System.getProperty("user.dir") + File.separator + "export_test";

            exportToZip(zipName,
                    outlineExcel, outlineAttachments,
                    startupExcel, null,  // 实验启动信息没有附件
                    processExcel, processAttachments,
                    acceptanceExcel, acceptanceAttachments,
                    outputDir);

            System.out.println("ZIP文件已生成: " + outputDir + File.separator + zipName + ".zip");

            // 清理测试文件
            cleanupTestFiles(attachment1, attachment2, attachment3);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建测试Excel文件
     */
    private static XSSFWorkbook createTestExcel(String sheetName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("测试数据列1");
        row.createCell(1).setCellValue("测试数据列2");

        row = sheet.createRow(1);
        row.createCell(0).setCellValue("数据1");
        row.createCell(1).setCellValue("数据2");
        return workbook;
    }

    /**
     * 创建测试文件
     */
    private static File createTestFile(String fileName, String content) throws IOException {
        File file = File.createTempFile(fileName, null);
        file.deleteOnExit(); // 程序退出时自动删除
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(content);
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
        return file;
    }

    /**
     * 创建测试文件(字节内容)
     */
    private static File createTestFile(String fileName, byte[] content) throws IOException {
        File file = File.createTempFile(fileName, null);
        file.deleteOnExit(); // 程序退出时自动删除
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return file;
    }

    /**
     * 清理测试文件
     */
    private static void cleanupTestFiles(File... files) {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.exists() && file.delete()) {
                System.out.println("已清理测试文件: " + file.getName());
            }
        }
    }
}
