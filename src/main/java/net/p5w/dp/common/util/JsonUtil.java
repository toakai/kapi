package net.p5w.dp.common.util;

import com.alibaba.fastjson2.JSON;

public class JsonUtil {
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
}