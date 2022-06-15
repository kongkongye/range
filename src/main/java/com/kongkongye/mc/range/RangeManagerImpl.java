package com.kongkongye.mc.range;

import com.kongkongye.mc.range.annotation.Nullable;
import com.kongkongye.mc.range.hashlist.HashList;
import com.kongkongye.mc.range.hashlist.HashListImpl;
import com.kongkongye.mc.range.space.Pos;
import com.kongkongye.mc.range.space.Range;
import com.kongkongye.mc.range.space.spaces.WorldSpace;
import com.kongkongye.mc.range.util.Util;

import java.util.*;

/**
 * 处理包含指定位置的范围
 */
public class RangeManagerImpl<T extends Rangable> implements RangeManager<T> {
    private WorldSpace<T> space;

    public int posMaxBufferSize = RangeConsts.POS_MAX_BUFFER_SIZE;
    public int posDropPer = RangeConsts.POS_DROP_PER;

    public int conflictMaxBufferSize = RangeConsts.CONFLICT_MAX_BUFFER_SIZE;
    public int conflictDropPer = RangeConsts.CONFLICT_DROP_PER;

    //检测包含位置的范围列表用
    private Map<Pos, HashList<T>> posBuffer = new HashMap<>();
    //检测与指定范围冲突的范围列表用
    private Map<Range, HashList<T>> conflictBuffer = new HashMap<>();

    public RangeManagerImpl() {
        this.space = new WorldSpace<>();
    }

    @Override
    public WorldSpace<T> getSpace() {
        return space;
    }

    @Override
    public void addWorld(String world) {
        Util.checkArgument(space.addWorld(world));
    }

    @Override
    public void addWorld(String world, List<Integer> levels) {
        Util.checkArgument(space.addWorld(world, levels));
    }

    @Override
    public void delWorld(String world) {
        Util.checkArgument(space.delWorld(world));
    }

    @Override
    public void add(T rangable) {
        space.add(rangable);
    }

    @Override
    public void del(T rangable) {
        space.del(rangable);
    }

    @Override
    public Collection<T> getValues() {
        return space.getValues();
    }

    @Override
    public void resetBuffer() {
        posBuffer.clear();
        conflictBuffer.clear();
    }

    @Override
    public HashList<T> getRanges(@Nullable Pos pos) {
        if (pos == null) {
            return new HashListImpl<>();
        }
        //缓冲优先检测
        if (posBuffer.containsKey(pos)) {
            return posBuffer.get(pos);
        }
        //正常检测
        HashList<T> result = space.getRanges(pos);
        if (result == null) {
            result = new HashListImpl<>();
        }
        //加入缓冲(必须克隆!!!)
        posBuffer.put(pos.clone(), result);
        //检测缓冲过大,清除一些
        if (posBuffer.size() > posMaxBufferSize) {
            int count = 0;
            Iterator<Pos> it = posBuffer.keySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
                count ++;
                if (count >= posDropPer) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public HashList<T> getConflicts(Range range) {
        //缓冲优先检测
        if (conflictBuffer.containsKey(range)) {
            return conflictBuffer.get(range);
        }
        //正常检测
        HashList<T> result = space.getConflicts(range);
        if (result == null) {
            result = new HashListImpl<>();
        }
        //加入缓冲(范围必须克隆!)
        conflictBuffer.put(range.clone(), result);
        //检测缓冲过大,清除一些
        if (conflictBuffer.size() > conflictMaxBufferSize) {
            int count = 0;
            Iterator<Range> it = conflictBuffer.keySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
                count ++;
                if (count >= conflictDropPer) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void autoOptimize() {
        space.autoOptimize();
    }

    @Override
    public void debug(DebugListener listener) {
        space.debug(listener);
    }
}
