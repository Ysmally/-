package com.example.word.controller;

import com.example.word.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件控制器
 * @author DaY1zz
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        try {
            String fileName = file.getOriginalFilename();
            // 这里添加文件上传逻辑
            return Result.success("文件上传成功: " + fileName);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileName}")
    public Result<String> downloadFile(@PathVariable String fileName) {
        try {
            // 这里添加文件下载逻辑
            return Result.success("文件下载成功: " + fileName);
        } catch (Exception e) {
            return Result.error("文件下载失败: " + e.getMessage());
        }
    }
}