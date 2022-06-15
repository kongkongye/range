package com.kongkongye.mc.range;

import com.kongkongye.mc.range.annotation.Nullable;
import com.kongkongye.mc.range.hashlist.HashList;
import com.kongkongye.mc.range.space.Pos;
import com.kongkongye.mc.range.space.Range;
import com.kongkongye.mc.range.space.spaces.WorldSpace;

import java.util.Collection;
import java.util.List;

/**
 * 处理包含指定位置的范围
 */
public interface RangeManager<T extends Rangable> {
    /**
     * 获取世界纬度空间
     */
    WorldSpace<T> getSpace();

    /**
     * 增加世界时调用
     * @see WorldSpace#addWorld(String)
     */
    void addWorld(String world);

    /**
     * 增加世界时调用
     * @see WorldSpace#addWorld(String, List)
     */
    void addWorld(String world, List<Integer> levels);

    /**
     * 删除世界时调用
     */
    void delWorld(String world);

    /**
     * 增加范围对象时调用
     */
    void add(T rangable);

    /**
     * 删除范围对象时调用
     */
    void del(T rangable);

    /**
     * 获取全部保存的对象
     * @return 不为null可为空
     */
    Collection<T> getValues();

    /**
     * 重置缓存时调用
     * (当范围增加,删除,修改大小时需要重置缓存)
     */
    void resetBuffer();

    /**
     * 获取包含指定位置的所有范围对象
     * @param pos null时返回空列表
     * @return 不为null可为空列表
     */
    HashList<T> getRanges(@Nullable Pos pos);

    /**
     * 获取与指定范围冲突的所有范围对象列表
     * @param range null时返回空列表
     * @return 不为null可为空列表
     */
    HashList<T> getConflicts(Range range);

    /**
     * 自动优化
     * @see WorldSpace#autoOptimize()
     */
    void autoOptimize();

    /**
     * 设置调试监听器
     */
    void debug(DebugListener listener);
}
