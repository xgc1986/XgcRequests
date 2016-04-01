package com.xgc1986.xgcRequests;

import org.apache.http.NameValuePair;

/**
 * Created by xgc1986 on 1/04/16.
 */
public class RequestParameter implements NameValuePair {

    private String key;

    private String value;

    public RequestParameter(String key, Object value) {
        if (key == null || value == null) {
            throw new Error("neither key or value cannot be null");
        }

        this.key = key;
        this.value = value.toString();

    }

    public String getKey() {
        return key;
    }

    @Override
    public String getName() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
