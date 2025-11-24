package com.example.word;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 单词学习应用主程序启动类
 * @author DaY1zz
 */
@SpringBootApplication
@MapperScan("com.example.word.mapper")
public class WordTreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordTreeApplication.class, args);
    }

}

