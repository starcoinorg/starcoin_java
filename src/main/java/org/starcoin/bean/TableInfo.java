package org.starcoin.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TableInfo {
    @JSONField(name = "key_type")
    @JsonProperty("key_type")
    private String keyType;

    @JSONField(name = "value_type")
    @JsonProperty("value_type")
    private String valueType;

    public String getKeyType() {
        return keyType;
    }

    public String getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "keyType='" + keyType + '\'' +
                ", valueType='" + valueType + '\'' +
                '}';
    }
}
