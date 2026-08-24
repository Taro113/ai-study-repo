package com.gao.demo03.chat.controller;

import com.gao.demo03.chat.service.ChatService;
import com.gao.demo03.chat.vo.param.ChatParam;
import com.gao.demo03.chat.vo.result.ChatResult;
import com.gao.demo03.common.model.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    /**
     * 输出示例：
     * {
     *     "code": 0,
     *     "message": "success",
     *     "data": {
     *         "content": "我是一个名为"LLaMA"的语言模型 AI 小助手。我的主要功能是帮助您提供信息、回答问题、转换语言和生成文本。
     *         我是基于Transformer模型的，使用了深度学习技术来处理自然语言。我的训练数据源自大量的文本数据，包括但不限于书籍、文章、新闻和互联网上的内容。
     *         我可以帮助您：
     *         1. **回答问题**：我可以根据您的输入回答问题，提供相关的信息和答案。
     *         2. **转换语言**：我可以帮助您转换不同的语言，例如从英文转换为中文。
     *         3. **生成文本**：我可以根据您的要求生成文本，例如写作日记、生成故事或创作诗歌。
     *         4. **提供信息**：我可以提供您需要的信息，包括但不限于历史、科学、文化和娱乐等领域。
     *         5. **帮助写作**：我可以帮助您写作，提供建议和提示，帮助您完善您的写作。
     *         我是一个不断学习和改进的 AI 小助手，我的能力和知识会随着时间的推移而不断增强。",
     *         "promptTokens": 36,
     *         "completionTokens": 255,
     *         "finishReason": "stop"
     *     }
     * }
     */
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/prod", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<ChatResult> streamChat(
            @RequestParam(defaultValue = "你好，请介绍一下你自己") String message) {
        ChatParam chatParam = new ChatParam(message, "你是一个AI小助手", 0.5d, 300);
        return CommonResult.success(chatService.chat(chatParam));
    }
}
