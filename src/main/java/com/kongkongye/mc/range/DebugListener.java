package com.kongkongye.mc.range;

import com.kongkongye.mc.range.annotation.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 调试监听器
 */
public abstract class DebugListener {
    private Set<String> types = new HashSet<>();
    private String[] filters;

    /**
     * @param types 类型过滤,null或空表示不过滤类型,否则只显示指定的类型
     * @param contains 包含过滤,null或空表示不过滤字符串,否则只显示指定的包含
     */
    public DebugListener(@Nullable String[] types, @Nullable String[] contains) {
        if (types != null && types.length > 0) {
            this.types.addAll(Arrays.asList(types));
        }
        this.filters = contains;
    }

    /**
     * 调试
     * @param type 类型
     */
    public void debug(String type, String msg) {
        if (types.contains(type)) {
            boolean result = true;
            if (filters != null && filters.length > 0) {
                result = false;
                for (String filter:filters) {
                    if (msg.contains(filter)) {
                        result = true;
                        break;
                    }
                }
            }
            if (result) {
                onDebug(type, msg);
            }
        }
    }

    /**
     * 需要调试的时候调用
     */
    protected abstract void onDebug(String type, String msg);
}
