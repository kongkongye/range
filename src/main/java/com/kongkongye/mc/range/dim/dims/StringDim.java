package com.kongkongye.mc.range.dim.dims;

import com.kongkongye.mc.range.dim.Dim;

/**
 * 字符串维度
 * (精确匹配大小写)
 */
public class StringDim implements Dim {
    private String key;

    public StringDim(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StringDim stringDim = (StringDim) o;

        return key.equals(stringDim.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
