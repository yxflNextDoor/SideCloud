package com.side.framework.core.tools;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class MapHelper {

    private MapHelper() {
    }

    /**
     * 将map转换为pojo
     *
     * @param map
     * @param pojoClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T convertToPojo(Map<String, Object> map, Class<T> pojoClass) {
        T pojo = null;
        try {
            pojo = pojoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("convert map to pojo error, class:{}, map:{}, error:{}", pojoClass, map, e.getMessage());
        }

        for (String key : map.keySet()) {
            Object value = map.get(key);
            Field field;
            try {
                field = pojoClass.getDeclaredField(key);
            } catch (NoSuchFieldException e) {
                continue;
            }
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers)) {
                //非final修饰的属性才尝试设置值
                field.setAccessible(true);
                try {
                    field.set(pojo, value);
                } catch (IllegalAccessException e) {
                    log.error("set value to pojo error, class:{}, field:{}, value:{}, error:{}", pojoClass, field, value, e.getMessage());
                }
            }
        }

        return pojo;
    }

    /**
     * 将pojo转换为map
     *
     * @param pojo
     * @param pojoClass
     * @param <T>
     * @return
     */
    public static <T extends Map<String,Object>> T convertToMap(Object pojo, Class<T> pojoClass) {
        if (pojo == null) {
            return null;
        } else {
            T map = null;
            try {
                map = pojoClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.error("convert pojo to map error, class:{}, pojo:{}, error:{}", pojoClass, pojo, e.getMessage());
            }

            for (Field field : pojo.getClass().getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers)) {
                    //非static修饰的属性才尝试设置值
                    field.setAccessible(true);
                    try {
                        map.put(field.getName(), field.get(pojo));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            return map;
        }
    }

    /**
     * 专用于
     * @param source
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T linkHashMapConvertToObj(Object source, Class<T> clazz){
        if(source instanceof LinkedHashMap){
            return (T) JsonHelper.jsonToObj(JsonHelper.objToJson(source), clazz);
        }
        throw new IllegalArgumentException("source is not LinkedHashMap");
    }
}
