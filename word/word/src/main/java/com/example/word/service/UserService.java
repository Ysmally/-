package com.example.word.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.word.common.Result;
import com.example.word.entity.User;
import com.example.word.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils; // 用于MD5加密，此处为示例，生产环境建议更强算法

/**
 * 用户服务实现类
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    /**
     * 用户登录逻辑
     */
    public Result<User> login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", username);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 生产环境中强烈建议存储哈希后的密码。
        // 此处为了匹配你的数据库示例，直接比对明文。
        if (!user.getPassword().equals(password)) {
            return Result.error("密码错误");
        }

        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 用户注册逻辑
     */
    public Result<String> register(User user) {
        QueryWrapper<User> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("user_name", user.getUserName());
        if (this.count(checkWrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // **安全建议**: 在实际保存前，应该对密码进行哈希加密
        // 例如：user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        if (user.getNeedCount() == null) {
            user.setNeedCount(20);
        }
        if (user.getTargetBook() == null) {
            user.setTargetBook("basic");
        }

        this.save(user);
        return Result.success("注册成功");
    }
}