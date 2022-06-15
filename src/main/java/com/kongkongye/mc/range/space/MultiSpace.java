package com.kongkongye.mc.range.space;

import com.kongkongye.mc.range.dim.Dim;

import java.util.Map;

/**
 * 多维度空间
 * 指里面的内容仍然以维度分割
 * @param <T> 保存的内容类型
 * @param <D> 维度类型
 * @param <S> 维度空间类型
 */
public interface MultiSpace<D extends Dim, S extends Space<T>, T> extends Space<T> {
    /**
     * 获取所有维度映射列表
     * @return 维度与维度空间的映射
     */
    Map<D, S> getDims();
}
