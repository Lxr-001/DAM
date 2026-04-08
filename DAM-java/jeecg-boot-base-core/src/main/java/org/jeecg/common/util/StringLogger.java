package org.jeecg.common.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringLogger {
    private final String logFile;
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public StringLogger(String logFile) {
        this.logFile = logFile;
    }

    /**
     * 记录字符串及其状态到文件
     * @param stringToLog 要记录的字符串
     * @param description 字符串描述（可选）
     */
    public void logString(String stringToLog, String description) {
        // 重点：全面的空值检查
        StringValidationResult validation = validateString(stringToLog);

        String logEntry = buildLogEntry(stringToLog, validation, description);

        writeToLogFile(logEntry);
    }

    /**
     * 验证字符串状态
     */
    private StringValidationResult validateString(String str) {
        if (str == null) {
            return new StringValidationResult(true, "NULL_VALUE", 0);
        } else if (str.isEmpty()) {
            return new StringValidationResult(true, "EMPTY_STRING", 0);
        } else if (str.trim().isEmpty()) {
            return new StringValidationResult(true, "BLANK_STRING", str.length());
        } else {
            return new StringValidationResult(false, "VALID_STRING", str.length());
        }
    }

    /**
     * 构建日志条目
     */
    private String buildLogEntry(String originalString, StringValidationResult validation,
                               String description) {
        return String.format(
            "[%s] | 描述: %s | 值: '%s' | 状态: %s | 长度: %d | 是否为空: %s%n",
            LocalDateTime.now().format(FORMATTER),
            description != null ? description : "未描述",
            originalString != null ? originalString : "null",
            validation.status,
            validation.length,
            validation.isEmpty ? "是" : "否"
        );
    }

    /**
     * 写入日志文件
     */
    private void writeToLogFile(String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("日志写入错误: " + e.getMessage());
        }
    }

    // 内部类用于存储验证结果
    private static class StringValidationResult {
        boolean isEmpty;
        String status;
        int length;

        StringValidationResult(boolean isEmpty, String status, int length) {
            this.isEmpty = isEmpty;
            this.status = status;
            this.length = length;
        }
    }

    // 使用示例
    public static void main(String[] args) {
        StringLogger logger = new StringLogger("application_log.txt");

        // 记录各种字符串情况
        logger.logString("重要数据", "用户输入");
        logger.logString("", "空用户名");
        logger.logString(null, "未设置密码");
        logger.logString("   ", "空白输入");
        logger.logString("Hello World!", "欢迎消息");
    }
}
