package com.gao.demo32.controller;

import com.gao.demo32.common.CommonResponse;
import com.gao.demo32.service.KnowledgeService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/knowledge")
@AllArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping("/ingest")
    public CommonResponse<Integer> ingest() {
        Resource resource = new FileSystemResource("F:\\ai_projects\\ai-study-repo\\demo32\\src\\main\\resources\\data\\knowledge.md");
        HashMap<String, Object> metaMap = new HashMap<>();
        metaMap.put("format", "md");
        return CommonResponse.success(knowledgeService.ingest(resource, metaMap));
    }

    @GetMapping("/clear")
    public CommonResponse<Void> clear() {
        knowledgeService.clear();
        return CommonResponse.success(null);
    }
}
