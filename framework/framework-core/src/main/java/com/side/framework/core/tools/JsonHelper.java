package com.side.framework.core.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.side.framework.core.constants.CodeEnum;
import com.side.framework.core.constants.CoreConstant;
import com.side.framework.core.exception.BasicException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @param :
 * @author : yxfl
 * @date : 2022/5/3
 * @description :
 */
@Slf4j
public class JsonHelper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    static {
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(CoreConstant.DEFAULT_DATE_FORMAT));
        JSON_MAPPER.setDateFormat(new SimpleDateFormat(CoreConstant.DEFAULT_DATE_FORMAT));
    }

    private JsonHelper() {
    }

    /**
     * @param data data必须实现字段的get和set
     * @return
     */
    public static String objToJson(Object data) {
        String out = null;
        try {
            out = JSON_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new BasicException(CodeEnum.HELPER_ERROR, "json转换异常");
        }
        return out;
    }

    /**
     * * json转对象 不支持泛型
     *
     * @param json
     * @param cla
     * @param <T>
     * @return
     */
    public static <T> T jsonToPojo(String json, Class<T> cla) {
        T out = null;
        try {
            out = OBJECT_MAPPER.readValue(json, cla);
        } catch (JsonProcessingException e) {
            throw new BasicException(CodeEnum.HELPER_ERROR, "json convert fail");
        }
        return out;
    }

    /**
     * json转对象 支持泛型
     *
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T json2Pojo(String json, TypeReference<T> typeReference) {
        T out = null;
        try {
            out = OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new BasicException(CodeEnum.HELPER_ERROR, "json convert fail");
        }
        return out;
    }

    /**
     * json转对象
     *
     * @param json
     * @param cla
     * @return
     */
    public static Object jsonToObj(String json, Class<?> cla) {
        Object out = null;
        try {
            out = OBJECT_MAPPER.readValue(json, cla);
        } catch (JsonProcessingException e) {
            throw new BasicException(CodeEnum.HELPER_ERROR, "json convert fail");
        }
        return out;
    }

    /**
     * bytes转对象
     *
     * @param bytes
     * @param cla
     * @param <T>
     * @return
     */
    public static <T> T bytesToObj(byte[] bytes, Class<T> cla) {
        T out = null;
        try {
            out = OBJECT_MAPPER.readValue(bytes, cla);
        } catch (IOException e) {
            throw new BasicException(CodeEnum.HELPER_ERROR, "json convert fail");
        }
        return out;
    }

    /**
     * 将对象重新序列化成新对象，适用于通信中实际类型被转为linkHashMap的情况
     *
     * @param obj
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T reSerialization(Object obj, TypeReference<T> typeReference) {
        return json2Pojo(JsonHelper.objToJson(obj), typeReference);
    }

}
