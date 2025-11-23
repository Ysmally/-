package com.example.word.common;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置MyBatis Plus拦截器
     * @Bean 注解表示将该方法的返回值注册为一个Spring Bean
     * @return 配置好的拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 1. 创建 MybatisPlusInterceptor 拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 2. 添加分页插件，并指定数据库类型为MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}