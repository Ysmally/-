package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.word.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE user_name = #{userName}")
    User selectByUserName(@Param("userName") String userName);

    /**
     * 根据词书类型查询用户列表
     */
    @Select("SELECT * FROM user WHERE target_book = #{targetBook}")
    List<User> selectByTargetBook(@Param("targetBook") String targetBook);

    /**
     * 分页查询用户列表
     */
    IPage<User> selectUserPage(Page<User> page, @Param("userName") String userName);

    /**
     * 更新用户学习设置
     */
    @Select("UPDATE user SET target_book = #{targetBook}, need_count = #{needCount} WHERE userID = #{userID}")
    int updateUserSetting(@Param("userID") String userID,
                          @Param("targetBook") String targetBook,
                          @Param("needCount") Integer needCount);

    /**
     * 更新用户头像
     */
    @Select("UPDATE user SET avator = #{avator} WHERE userID = #{userID}")
    int updateUserAvatar(@Param("userID") String userID, @Param("avator") String avator);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE user_name = #{userName}")
    int checkUserNameExists(@Param("userName") String userName);

    /**
     * 用户登录验证
     */
    @Select("SELECT * FROM user WHERE user_name = #{userName} AND password = #{password}")
    User login(@Param("userName") String userName, @Param("password") String password);
}