package org.jeecg.modules.chaindatapackage.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.chaindatapackage.consts.ChainDataPackageConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @projectName: industrychain
 * @package: org.jeecg.modules.chaindatapackage.controller
 * @className: DataPackageImportController
 * @author: lhx
 * @description: 数据包导入
 * @date: 2025/6/24 10:17
 * @version: 1.0
 */
@Api(tags = "数据包导入")
@RestController
@RequestMapping("/datapackage")
@Slf4j
public class DataPackageController {
    @Value("${accessFile.location}")
    private String location;


    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Result importDataPackage(@RequestParam("file") MultipartFile file) {
        try {
//            dataPackageImportService.importDataPackage(file);
            // 创建目录
            Files.createDirectories(Paths.get(location)); // 使用NIO方式创建目录:cite[7]
            // 生成唯一文件名等后续操作
            long timestamp = System.currentTimeMillis();
            // 使用SimpleDateFormat将时间戳格式化为可读的日期和时间字符串（默认本地时区）
            Date dateDefaultZone = new Date(timestamp);
            SimpleDateFormat sdfDefaultZone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDateDefaultZone = sdfDefaultZone.format(dateDefaultZone);
            formattedDateDefaultZone = formattedDateDefaultZone.replaceAll(" ", "_");
            formattedDateDefaultZone = formattedDateDefaultZone.replace(":", "-");
            String filename = file.getOriginalFilename();
            assert filename != null;
            String[] strings = filename.split("\\.");
            String uniqueFileName = strings[0] + formattedDateDefaultZone + "." + strings[1];
            File dest = new File(location + File.separator + uniqueFileName);
            file.transferTo(dest); // 保存文件
            return Result.OK("文件上传成功:  + uniqueFileName");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(ChainDataPackageConst.ERROR);
        }
    }
}
