package com.gao.demo32.repository;

import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TestRepository {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<String> select() {
        return new ArrayList<>();
    }
}
