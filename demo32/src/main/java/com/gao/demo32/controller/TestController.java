package com.gao.demo32.controller;

import com.gao.demo32.common.CommonResponse;
import com.gao.demo32.service.TestService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/select")
    public CommonResponse<List<String>> select() {
        return CommonResponse.success(testService.select());
    }
}
