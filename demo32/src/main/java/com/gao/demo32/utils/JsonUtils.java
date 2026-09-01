package com.gao.demo32.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;  // 如果项目中有 commons-lang3 可用，否则自行判断

import java.text.SimpleDateFormat;

/**
 * Jackson JSON 工具类（静态方法，内部持有单例 ObjectMapper）
 * 线程安全，含防御性校验。
 */
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 日期格式
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 忽略未知属性（反序列化时）
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许序列化空对象（不抛异常）
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 注册 Java 8 时间类型支持
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 禁用时间戳格式，使用 ISO-8601
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonUtils() {}

    /**
     * 对象转 JSON 字符串
     * @param obj 待序列化的对象（允许为 null，此时返回 "null"）
     * @return JSON 字符串
     * @throws RuntimeException 序列化失败时抛出
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化 JSON 失败，对象类型：" + (obj != null ? obj.getClass().getName() : "null"), e);
        }
    }

    /**
     * JSON 字符串转对象（普通类）
     * @param json  JSON 字符串（不能为空或空白）
     * @param clazz 目标 Class
     * @param <T>   目标类型
     * @return 反序列化后的对象
     * @throws IllegalArgumentException 当 json 为 null 或仅包含空白字符时抛出
     * @throws RuntimeException 反序列化失败时抛出
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {  // 如果 commons-lang3 不可用，可用 json == null || json.trim().isEmpty()
            throw new IllegalArgumentException("JSON 字符串不能为 null 或空白");
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化 JSON 失败，目标类型：" + clazz.getName(), e);
        }
    }

    /**
     * JSON 字符串转对象（支持泛型，如 List<MyClass>、Map<String, Object>）
     * @param json    JSON 字符串（不能为空或空白）
     * @param typeRef TypeReference 泛型类型（例如 new TypeReference<List<User>>(){}）
     * @param <T>     目标泛型类型
     * @return 反序列化后的对象
     * @throws IllegalArgumentException 当 json 为 null 或仅包含空白字符时抛出
     * @throws RuntimeException 反序列化失败时抛出
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (StringUtils.isBlank(json)) {
            throw new IllegalArgumentException("JSON 字符串不能为 null 或空白");
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化 JSON 失败，目标类型：" + typeRef.getType().getTypeName(), e);
        }
    }

    /**
     * 转换为格式化的 JSON（多行缩进）
     * @param obj 待序列化的对象（允许为 null，返回 "null"）
     * @return 美化后的 JSON 字符串
     * @throws RuntimeException 序列化失败时抛出
     */
    public static String toPrettyJson(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化为美化 JSON 失败，对象类型：" + (obj != null ? obj.getClass().getName() : "null"), e);
        }
    }

    /**
     * 获取内部 ObjectMapper 实例（便于进行高级配置）
     * @return ObjectMapper 单例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}