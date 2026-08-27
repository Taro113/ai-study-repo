# ai-study-repo

## 第一章：Spring AI 的基础使用

### ai-parent

> 父工程，只包含一个 pom.xml 文件，提供依赖版本控制功能

### demo01

> 第一个 AI 测试工程，验证 AI 接口调用是否能成功

**问题：**

1. 同一个模型，同一个问题，通过终端对话和 API 调用这两者不同的方式的输出，感觉 API 调用方式更蠢一些<br/>
答：API 调用是“裸模型”，终端对话是“带包装的模型”。优化手段：加上 System Prompt、合理设置温度、使用正确的消息格式等
2. demo01的 API 调用方式，感觉像是一次性把答案组织好再输出，而不是常规的流式输出<br/>
答：call() 方法是非流式 API，call() 会等所有 token 都生成完、由服务端/客户端收集成完整结果后，才一次性返回输出。

### demo02

> 使用了流式 API，通过项目内 files/index.html 文件调用这俩流式 API 进行展示

**问题：**

1. 温度这一属性的本质是什么呢？结合大模型的输出是输出概率最大的那个词，那么可以说明温度字段是可以用来控制这个概率的吗？温度这个字段就是大模型微调的一小环节嘛？<br/>
答：https://chat.deepseek.com/share/g15b6vfh7p4pgrxuz4


### demo03

> 一个符合生产标准的完整示例，包含错误处理、日志记录和合理的分层设计
> - 流式 API 调用
> - 只有 service 层

### demo04

> Prompt 简单工程，通过文件保存提示词模板，在代码中注入变量以达到专业定制化 AI 助手目的

**问题：**

1. 真实的代码 CR 场景是什么样子的？肯定要以业务为主，所以肯定要有PRD和技术方案作为输入吧？<br/>
答：
2. 闭环思维：CR 结果 -> 用户反馈 -> 统计 -> 改进<br/>
答：

### demo05

> 一种比较规范的，可以在生产环境中应用的 ChatClient 流式 API，封装了 Advisor 链、默认配置、流式调用等复杂性，是**推荐在业务代码中使用的主要方式**

**问题：**

1. 调用不同的大模型呢？应该怎么处理？一个 ChatClient 是不是对应一个大模型？<br/>
   答：

### demo06

> Advisor 示例工程
> 
> 一些常用的官方 Advisor：
> 
> | 类名                     | 简述              | 作用                                                                                                                                               |
> |--------------------------|-------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
> | MessageChatMemoryAdvisor | 记忆管理类        | 从 ChatMemory 中读取历史对话消息，并将它们注入到 Prompt 中，使 AI 能够“记住”之前的对话上下文                                                       |
> | PromptChatMemoryAdvisor  | Prompt 管理类     | 与 MessageChatMemoryAdvisor 类似，但更底层，直接操作 Prompt 对象，允许更灵活地控制历史消息的插入位置和格式                                         |
> | QuestionAnswerAdvisor    | 检索增强（RAG）类 | 基于 VectorStore 进行向量检索，将用户问题与知识库中的文档片段进行匹配，并将检索到的相关文档作为上下文添加到 Prompt 中，从而实现检索增强生成（RAG） |
> | SafeGuardAdvisor         | 安全与过滤类      | 根据配置的关键词列表或正则表达式，对请求（Prompt）和响应（ChatResponse）进行敏感内容过滤，可以拦截或替换违规内容                                   |
> | SimpleLoggerAdvisor      | 日志与调试类      | 在调用前后打印请求和响应的简要日志，帮助开发调试和监控                                                                                             |
> | ReReadingAdvisor         | 推理增强类        | 实现“重复阅读”（Re-Reading）技术，将用户问题重复一次后发送给模型，以提升模型对问题的理解能力（尤其适用于复杂推理任务）                             |

除了官方 Advisor，我们还可以自定义实现定制化 Advisor。

### demo07

> 结构化输出：按照指定类结构输出<br/>
> 结构化输出 + 复杂嵌套结构 + 失败重试 + 异常处理（返回默认值）

### demo08

> 结构化输出实际场景：简历信息提取

> **结构化输出是将 AI 能力集成到业务系统的关键桥梁**。优先使用 `ChatClient.call().entity()` 方式，它最简介、最易维护。
> 对于高可靠性要求的场景，务必添加重试机制和降级处理。
> 复杂嵌套 Record 的 JSON Schema 自动生成是 Spring AI 的强大特性，充分利用它可以处理任意复杂的数据提取需求

### 总结

demo01-demo08 主要是了解 Spring AI 框架的使用，下面章节，才是 AI 应用真正企业级核心技术

---

## 第二章：Spring AI 实战指南

> 这一部分是 Spring AI 的企业级实战部分，将深入 RAG（检索增强生成）、VectorStore、Advisor 机制、Tool Calling（函数调用）和多智能体编排。
> 这些是构建真正 “ 有用 ” AI 应用的核心技术。

### demo11

> 1. 文档分片：将文档按指定规则进行分片拆分
> 2. 向量生成：将拆分后的片段数据生成向量坐标
> 3. 数据存储：将片段及对应的向量坐标保存仅向量数据库中
> 
> - `BatchEmbeddingService` 类只用于 2- 对分片生成向量坐标
> - 而 `KnowledgeBaseService#addDocuments()` 包含了这三步
> 
> **最佳实践**：在企业知识库中，始终为文档添加 `source`（来源）、`department`（部门）、`create_at`（来源）
> 等元数据。这样可以实现按部门隔离、按时间范围过滤等精细化检索，大幅提升 RAG 答案质量

**问题：**

1. RAG 这个思想是怎么提出来的？怎样的背景下诞生的？具体的设计实现又是怎么想到的？<br/>
   答：

### demo12

> 一个完整的文档 ETL（Extract提取 -> Transform分块 -> Load入库）流程
> 
> - `DocumentReader`：提取源文件（txt、md、word、pdf等）根据不同实现类生成为文档 `Document`
> - `TextSplitter`：将文档根据不同实现类的分块规制进行分块
> - `VectorStore`：向量数据库（不同的具体实现：postgreSQL、Redis、ES 等）

### demo13

> 一个相对比较完整的 RAG 流程：
> 
> 1. 构造查询参数
> 2. 向量数据库查询相关文档
> 3. 大模型回答问题（Advisor 自动注入）
> 4. 将回答与关联文档建立引用

**问题**：

1. 向量数据库中存的数据不是向量维度的嘛，`vectorStore#similaritySearch()` 返回的为啥是文档维度的数据？<br/>
答：向量数据库中一条数据就是一个 `Document`，其中 `text` 就是分块后的内容，`metadata` 存的就是该分块片段的信息，比如文件名、页码、来源等等
2. 手动查一遍向量数据库得到的 `sources` 只用于建立与回答的引用关系嘛？那这不是查了两次向量数据库？<br/>
答：是的，`sources` 只用于最后一步的建立关系，所以这是一个可优化点，不需要 `QuestionAnswerAdvisor` 了，
每次的检索步骤都自己手动查一次，将结果 `sources` 添加到 `UserPrompt` 里
3. 既然调用了两次，那么 `sources` 和 `Advisor` 查到的 资料是同一份资料吗？有没有可能不同？<br/>
答：参数 `topK` 和 `similarityThreshold` 是一样的，只是查询时间不一致但间隔也很短很短，可以说是大概率是相同的
4. 其他的可优化点？
   1. 难道每次都需要手动检索 `sources` 嘛？答：除了每次手动检索，也可以在系统提示词里让AI的回答中带有引用
   2. 多轮对话中的查询改写：比如先问工资福利，再问“那实习生呢”，那么第二句的实习生去检索，查询出来的相关文档就会很差，可以引入记忆和查询改写组件，重写为一个独立问题，
   比如举例的这个场景就可以重改为：“实习生的工资福利”
   3. token 溢出风险、检索空结果集提前拦截


### demo14

> `Function Calling` 工具调用：`@Tool()` 注解

**问题**：

1. 可优化点？<br/>
答：异步调用、安全校验、敏感数据脱敏、写操作二次确认

## 第三章：高级特性与生产实践

> 本章是 Spring AI 系列第三部分，
> 
> 1. Advisors、Chat Memory、MCP 协议、图像/音频模型和可观测性
> 2. RAG 深度实践（文档解析 -> 分块策略 -> 向量检索优化 -> 重排序），以及 Function Calling 高级模式（并行调用、嵌套调用、工具链编排）
> 
> 没有  `demo21 ~ demo24` 的关于 1 的工程代码

### demo21

> 用于介绍 `Advisor` 的生产实践，其使用在上面的 `demo5`、`demo6`、`demo13` 中已经学习过了
> 
> `Advisor` 开发最佳企业实践：
> 
> 1. 调用顺序是同步的，按 `order` 顺序调用，但是其数据处理是异步的，`stream API` 也是异步的
> 2. `Order` 规划：安全过滤 -> 限流 -> 记忆/RAG -> 业务逻辑 -> 日志
> 3. 无状态设计：道理同1，`Advisor Bean` 通常是单例，避免在 `Advisor` 内存储请求级别的动态状态数据
> 4. `adviseContext` 传参：通过 `request.adviseContext()` 传递 `userId`、`traceId` 等元数据信息，禁止用 `ThreadLocal`
> 5. 流式同步：实现了 `CallAdvisor` 就一并实现 `StreamAdvisor`，保持行为一致
> 6. 异常处理：`Advisor` 内的异常不要吞掉，向上传播以便统一错误处理

### demo22

> 用于介绍 `ChatMemory` 的生产实践，其使用主要是通过几个实现了 `BaseChatMemoryAdvisor` 的特定的 `Advisor`
>
> `ChatMemoryAdvisor` 开发最佳企业实践：
>
> 1. 三个 `Advisor`：`MessageChatMemoryAdvisor`、`PromptChatMemoryAdvisor`（1.0 版本已废弃）、`VectorStoreChatMemoryAdvisor`
> 2. `.advisors()` 方法会将参数传递到 `adviseContext` 上下文对象中去，各 `Advisor` 按需取用

### demo23

> 多模态：多模态输入：、图片生成

### demo24

> 可观测性：
> 
> 完整的生产环境可观测性技术栈如下：
> 
> ```text
> Spring AI Application
> │
> ├── Metrics (Micrometer → Prometheus)
> │     └── Prometheus → Grafana Dashboard + AlertManager → PagerDuty/钉钉
> │
> ├── Tracing (Micrometer Tracing → Zipkin/Jaeger)
> │     └── 分布式链路追踪，关联 HTTP→LLM→VectorDB 完整调用链
> │
> ├── Logging (SLF4J → ELK / Loki)
> │     ├── SimpleLoggerAdvisor → 结构化 JSON 日志
> │     └── MDC 注入 traceId，日志可与 Trace 关联
> │
> └── Custom Observation (ObservationHandler)
> └── 写入业务数据库 → BI 分析 → 成本报告
> 
> ```
> 
> 生产可观测性 checkList：
> 
> 1. Prometheus + Grafana 监控 LLM 延迟、token 用量、错误率
> 2. 分布式追踪接入 Zipkin/Jaeger，traceId 注入所有日志
> 3. 成本告警：日预算超标立即通知，避免账单爆炸
> 4. `include-prompt: false`（生存环境禁止在 Span 中记录 Prompt，防止敏感信息泄露）
> 5. Prometheus 采样率生产环境设 0.1（10%），避免 Zipkin 数据过多
> 6. Token 用量按 userId/model 分维度统计，便于成本分摊和异常用户识别





