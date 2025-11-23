package com.example.word.controller;

import com.example.word.common.Result;
import com.example.word.entity.MemoryRecord;
import com.example.word.service.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 记忆控制器
 * @author DaY1zz
 */
@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    @Autowired
    private MemoryService memoryService;

    @GetMapping("/records")
    public Result<List<MemoryRecord>> getMemoryRecords() {
        try {
            List<MemoryRecord> records = memoryService.getAllMemoryRecords();
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取记忆记录失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<String> addMemoryRecord(@RequestBody MemoryRecord record) {
        try {
            memoryService.addMemoryRecord(record);
            return Result.success("记忆记录添加成功");
        } catch (Exception e) {
            return Result.error("添加记忆记录失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<String> updateMemoryRecord(@RequestBody MemoryRecord record) {
        try {
            memoryService.updateMemoryRecord(record);
            return Result.success("记忆记录更新成功");
        } catch (Exception e) {
            return Result.error("更新记忆记录失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteMemoryRecord(@PathVariable Long id) {
        try {
            memoryService.deleteMemoryRecord(id);
            return Result.success("记忆记录删除成功");
        } catch (Exception e) {
            return Result.error("删除记忆记录失败: " + e.getMessage());
        }
    }
}