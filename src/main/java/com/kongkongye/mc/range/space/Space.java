package com.kongkongye.mc.range.space;

import java.util.Collection;

/**
 * 维度空间
 * 里面有这个维度保存的内容
 * @param <T> 保存的内容类型
 */
public interface Space<T> {
    /**
     * 增加对象
     * (增加到维度空间的哪里?这是维度空间需要判断的)
     * @param t 对象
     * @return 是否成功,如果原来已经有此对象,则返回false
     */
    boolean add(T t);

    /**
     * 删除对象
     * @param t 对象
     * @return 是否成功,如果原来没有此对象,则返回false
     */
    boolean del(T t);

    /**
     * 获取全部对象
     */
    Collection<T> getValues();
}
