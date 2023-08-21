package kr.co.seedit.global.utils;

import java.util.HashMap;

import org.springframework.jdbc.support.JdbcUtils;

@SuppressWarnings("serial")
public class CamelHashMap extends HashMap<String, Object> {

    @Override
    public Object put(String key, Object value) {
    	if (null == value) return null;
        return super.put(JdbcUtils.convertUnderscoreNameToPropertyName(key), value);
    }
}
