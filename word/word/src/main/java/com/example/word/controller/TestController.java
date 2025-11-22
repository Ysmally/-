package com.example.word.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test/db")
    public Object testDb() {
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap("SELECT * FROM user LIMIT 1");
            return row;
        } catch (Exception e) {
            return "DB test failed: " + e.getMessage();
        }
    }
}
