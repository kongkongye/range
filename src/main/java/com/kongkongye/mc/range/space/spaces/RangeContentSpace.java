package com.kongkongye.mc.range.space.spaces;

import com.kongkongye.mc.range.Rangable;
import com.kongkongye.mc.range.RangeManager;
import com.kongkongye.mc.range.hashlist.HashList;
import com.kongkongye.mc.range.hashlist.HashListImpl;
import com.kongkongye.mc.range.space.ContentSpace;
import com.kongkongye.mc.range.space.Pos;
import com.kongkongye.mc.range.space.Range;

import java.util.Collection;

/**
 * 范围内容维度空间
 *
 * (就是一个对象列表吧)
 */
public class RangeContentSpace<T extends Rangable> implements ContentSpace<T> {
    private HashList<T> contents = new HashListImpl<>();

    @Override
    public boolean add(T rangable) {
        return contents.add(rangable);
    }

    @Override
    public boolean del(T rangable) {
        return contents.remove(rangable);
    }

    @Override
    public Collection<T> getValues() {
        return contents.getList();
    }

    /**
     * @see RangeManager#getRanges(Pos)
     * @return 没有可返回null
     */
    public HashList<T> getRanges(Pos pos) {
        //遍历所有范围(因为这些范围没法再区分了,只能一个个检测过去)
        HashList<T> result = null;

        for (T rangable:contents) {
            if (rangable.getRange().checkPos(pos)) {
                if (result == null) {
                    result = new HashListImpl<>();
                }
                result.add(rangable);
            }
        }

        return result;
    }

    /**
     * 获取与指定范围冲突的所有范围对象
     * @return 可能为null
     */
    public HashList<T> getConflicts(Range range) {
        HashList<T> result = null;
        for (T check:contents) {
            if (check.getRange().isConflictWith(range)) {
                if (result == null) {
                    result = new HashListImpl<>();
                }
                result.add(check);
            }
        }
        return result;
    }
}
