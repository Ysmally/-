package com.example.word.controller;

import com.example.word.common.Result;
import com.example.word.entity.User;
import com.example.word.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * @author DaY1zz
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<String> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return Result.success("用户注册成功");
        } catch (Exception e) {
            return Result.error("用户注册失败: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<String> loginUser(@RequestBody User user) {
        try {
            String token = userService.loginUser(user);
            return Result.success(token);
        } catch (Exception e) {
            return Result.error("用户登录失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return Result.success("用户信息更新成功");
        } catch (Exception e) {
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("用户删除成功");
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
}