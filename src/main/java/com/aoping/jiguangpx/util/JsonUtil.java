package com.aoping.jiguangpx.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Date;

public class JsonUtil {

    public static String toJson(Object obj) {
        SerializeFilter filter = new ValueFilter() {
            public Object process(Object object, String propertyName, Object propertyValue) {
                if (propertyValue instanceof Date) {
                    propertyValue = ((Date) propertyValue).getTime();
                }
                return propertyValue;
            }
        };

        return JSON.toJSONString(obj, filter, SerializerFeature.PrettyFormat);
    }

    public static <T> T toObject(String json, Class<T> tClass) {
        if (StringUtils.isEmpty(json)) return null;
        return JSON.parseObject(json, tClass);
    }

    public static <T> T toObject(String json, Type type) {
        if (StringUtils.isEmpty(json)) return null;
        return JSON.parseObject(json, type);
    }
    
    /**
     * 转换成jsonObject，如果非json串，返回null
     * @param json
     * @return
     */
	public static JSONObject toJsonObject(String json) {
		try {
			return JSONObject.parseObject(json);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static <T> T parseObject(String json, Class<T> clazz) {
		try {
			T t = JSONObject.parseObject(json, clazz);
			if (t == null) {
				t = clazz.newInstance();
			}
			return t;
		} catch (Exception e) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				return null;
			}
		}
	}
	
}
