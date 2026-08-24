package com.gao.demo04.prompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ResourceBasedPromptService {

    private final ChatModel chatModel;

    public ResourceBasedPromptService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 从 classpath 资源文件加载 Prompt 模板
     * PromptTemplate 构造器可以接受 Resource 对象
     */
    public String reviewCodeFromFile(String language, String code) {
        // 从 resources/prompts/code-review.st 加载模板
        var resource = new ClassPathResource("prompts/code-review.st");
        var promptTemplate = new PromptTemplate(resource);

        Prompt prompt = promptTemplate.create(Map.of(
                "language", language,
                "domain", "企业级应用",
                "code", code,
                "reviewPoints", "- 线程安全\n- 异常处理\n- 资源释放"
        ));

        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}
