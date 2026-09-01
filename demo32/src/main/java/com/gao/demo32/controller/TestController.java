package com.gao.demo32.controller;

import com.gao.demo32.common.CommonResponse;
import com.gao.demo32.entity.bo.VectorStoreBO;
import com.gao.demo32.entity.vo.VectorStoreVO;
import com.gao.demo32.service.TestService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    public CommonResponse<List<VectorStoreVO>> select() {
        List<VectorStoreBO> boList = testService.select();
        if (CollectionUtils.isEmpty(boList)) {
            return CommonResponse.success(new ArrayList<>());
        }
        ArrayList<VectorStoreVO> resultList = new ArrayList<>();
        for (VectorStoreBO bo : boList) {
            VectorStoreVO vo = bo.toVO();
            resultList.add(vo);
        }
        return CommonResponse.success(resultList);
    }
}
