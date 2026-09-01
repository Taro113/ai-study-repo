package com.gao.demo32.service;

import com.gao.demo32.entity.bo.VectorStoreBO;
import com.gao.demo32.entity.po.VectorStorePO;
import com.gao.demo32.repository.TestRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TestService {

    @Resource
    private TestRepository testRepository;

    public List<VectorStoreBO> select() {
        List<VectorStorePO> poList = testRepository.select();
        List<VectorStoreBO> boList = new ArrayList<>();
        if (poList == null || poList.isEmpty()) {
            return boList;
        }
        for (VectorStorePO po : poList) {
            boList.add(po.toBO());
        }
        return boList;
    }
}
