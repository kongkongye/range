package com.kongkongye.mc.range.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
    public static void checkArgument(boolean value) {
        if (!value) {
            throw new RuntimeException();
        }
    }

    public static <T> List<T> newArrayList(T... ts) {
        List<T> list = new ArrayList<>();
        if (ts != null) {
            Collections.addAll(list, ts);
        }
        return list;
    }
}
