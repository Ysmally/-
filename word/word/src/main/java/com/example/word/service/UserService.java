package com.example.word.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.word.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * @author DaY1zz
 */
public interface UserService extends IService<User> {

    List<User> getAllUsers();

    User getUserById(Long id);

    void registerUser(User user);

    String loginUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);

    User getUserByUsername(String username);

    boolean checkPassword(String rawPassword, String encodedPassword);

    String encodePassword(String password);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}