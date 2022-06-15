package com.kongkongye.mc.range.space.spaces;

import com.kongkongye.mc.range.DebugListener;
import com.kongkongye.mc.range.Rangable;
import com.kongkongye.mc.range.RangeConsts;
import com.kongkongye.mc.range.RangeManager;
import com.kongkongye.mc.range.dim.dims.WorldDim;
import com.kongkongye.mc.range.hashlist.HashList;
import com.kongkongye.mc.range.space.MultiSpace;
import com.kongkongye.mc.range.space.Pos;
import com.kongkongye.mc.range.space.Range;
import com.kongkongye.mc.range.util.Util;

import java.util.*;

/**
 * 世界维度空间
 *
 * (就是以世界来将对象分区,简单有效)
 */
public class WorldSpace<T extends Rangable> implements MultiSpace<WorldDim, LevelMultiSpace<T>, T> {
    private Map<WorldDim, LevelMultiSpace<T>> dims = new HashMap<>();
    private Map<WorldDim, DivSpace<T>> conflictDims = new HashMap<>();
    //与dims同步
    //世界名 世界维度
    private Map<String, WorldDim> worldToDim = new HashMap<>();

    @Override
    public Map<WorldDim, LevelMultiSpace<T>> getDims() {
        return dims;
    }

    @Override
    public boolean add(T rangable) {
        if (dims.get(getDim(rangable.getRange().getWorld())).add(rangable)) {
            Util.checkArgument(conflictDims.get(getDim(rangable.getRange().getWorld())).add(rangable));
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean del(T rangable) {
        if (dims.get(getDim(rangable.getRange().getWorld())).del(rangable)) {
            Util.checkArgument(conflictDims.get(getDim(rangable.getRange().getWorld())).del(rangable));
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Collection<T> getValues() {
        List<T> result = new ArrayList<>();
        for (LevelMultiSpace<T> levelMultiSpace:dims.values()) {
            result.addAll(levelMultiSpace.getValues());
        }
        return result;
    }

    /**
     * @see RangeManager#getRanges(Pos)
     * @return 没有可返回null
     */
    public HashList<T> getRanges(Pos pos) {
        //位置只会在一个世界内(并且那个世界必然存在)
        return dims.get(getDim(pos.getWorld())).getRanges(pos);
    }

    public HashList<T> getConflicts(Range range) {
        //范围只会在一个世界内(并且那个世界必然存在)
        return conflictDims.get(getDim(range.getWorld())).getConflicts(range);
    }

    /**
     * 只包含一个根等级0(因此效率没有任何提高)
     * @see #addWorld(String, List)
     */
    public boolean addWorld(String world) {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        return addWorld(world, list);
    }

    /**
     * 增加世界
     * @return 是否成功
     */
    public boolean addWorld(String world, List<Integer> levels) {
        //已经存在
        if (getDim(world) != null) {
            return false;
        }

        WorldDim dim = new WorldDim(world);
        dims.put(dim, new LevelMultiSpace<>(levels));
        conflictDims.put(dim, new DivSpace<>(0));
        worldToDim.put(world, dim);
        return true;
    }

    /**
     * 删除世界
     * @return 是否成功
     */
    public boolean delWorld(String world) {
        WorldDim dim = worldToDim.remove(world);
        if (dim == null) {
            return false;
        }
        dims.remove(dim);
        conflictDims.remove(dim);
        return true;
    }

    /**
     * @see LevelMultiSpace#autoOptimize()
     */
    public void autoOptimize() {
        for (LevelMultiSpace<T> levelMultiSpace:dims.values()) {
            levelMultiSpace.autoOptimize();
        }
    }

    public void debug(DebugListener listener) {
        //Pos
        for(Map.Entry<WorldDim, LevelMultiSpace<T>> entry:dims.entrySet()) {
            //本级
            listener.debug(RangeConsts.TYPE_WORLD_SPACE, "Pos> World:"+entry.getKey().getKey()+" Amount:"+entry.getValue().getValues().size());
            //下级
            entry.getValue().debug(listener);
        }
        //Conflict
        for(Map.Entry<WorldDim, DivSpace<T>> entry:conflictDims.entrySet()) {
            //本级
            listener.debug(RangeConsts.TYPE_WORLD_SPACE, "Conflict> World:"+entry.getKey().getKey()+" Amount:"+entry.getValue().getValues().size());
            //下级
            entry.getValue().debug(listener);
        }
    }

    /**
     * @return 不存在返回null
     */
    private WorldDim getDim(String world) {
        return worldToDim.get(world);
    }
}
