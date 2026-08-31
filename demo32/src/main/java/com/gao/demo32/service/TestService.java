package com.gao.demo32.service;

import com.gao.demo32.repository.TestRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TestService {

    @Resource
    private TestRepository testRepository;

    public List<String> select() {
        return testRepository.select();
    }
}
