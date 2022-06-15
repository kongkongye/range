package com.kongkongye.mc.range.space.spaces;

import com.kongkongye.mc.range.DebugListener;
import com.kongkongye.mc.range.Rangable;
import com.kongkongye.mc.range.RangeConsts;
import com.kongkongye.mc.range.RangeManager;
import com.kongkongye.mc.range.dim.dims.RangeDim;
import com.kongkongye.mc.range.hashlist.HashList;
import com.kongkongye.mc.range.space.MultiSpace;
import com.kongkongye.mc.range.space.Pos;

import java.util.*;

/**
 * (关键层)
 *
 * 范围子维度空间
 * 维度: 范围维度(x, y, z)
 * 内容维度空间: 范围内容维度空间(对象列表)
 *
 * 这层是影响效率的两个关键因素之一
 * 目标: 每个范围维度内包含的对象数量尽量少(范围维度多没事)
 */
public class RangeMultiSpace<T extends Rangable> implements MultiSpace<RangeDim, RangeContentSpace<T>, T> {
    //0表示根层(这种情况需要特殊考虑!)
    private int size;

    private Map<RangeDim, RangeContentSpace<T>> dims = new HashMap<>();

    public RangeMultiSpace(int size) {
        this.size = size;
    }

    @Override
    public Map<RangeDim, RangeContentSpace<T>> getDims() {
        return dims;
    }

    @Override
    public boolean add(T rangable) {
        return dims.computeIfAbsent(convert(rangable.getRange().getP1()), key -> new RangeContentSpace<>()).add(rangable);
    }

    @Override
    public boolean del(T rangable) {
        RangeContentSpace<T> rangeContentSpace = dims.get(convert(rangable.getRange().getP1()));
        return rangeContentSpace != null && rangeContentSpace.del(rangable);
    }

    @Override
    public Collection<T> getValues() {
        List<T> result = new ArrayList<>();
        for (RangeContentSpace<T> rangeContentSpace:dims.values()) {
            result.addAll(rangeContentSpace.getValues());
        }
        return result;
    }

    /**
     * 检测是否满足阀值要求
     */
    public boolean checkThreshold() {
        for (RangeContentSpace rangeContentSpace:dims.values()) {
            if (rangeContentSpace.getValues().size() > RangeConsts.POS_OPTIMIZE_MAX_THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see RangeManager#getRanges(Pos)
     * @return 没有可返回null
     */
    public HashList<T> getRanges(Pos pos) {
        //位置只会在一个`块`内
        RangeContentSpace<T> rangeContentSpace = dims.get(convert(pos));
        if (rangeContentSpace != null) {
            return rangeContentSpace.getRanges(pos);
        } else {
            return null;
        }
    }

    public void debug(DebugListener listener) {
        listener.debug(RangeConsts.TYPE_RANGE_MULTI_SPACE, "Size:"+size+" Dim Amount:"+dims.size());
        for (Map.Entry<RangeDim, RangeContentSpace<T>> entry:dims.entrySet()) {
            listener.debug(RangeConsts.TYPE_RANGE_MULTI_SPACE, "Range:"+entry.getKey()+" Amount:"+entry.getValue().getValues().size());
        }
    }

    /**
     * 转换
     */
    private RangeDim convert(Pos pos) {
        int x,y,z;
        if (size == 0) {
            x = y = z = 0;
        }else {
            x = d(pos.getX());
            y = d(pos.getY());
            z = d(pos.getZ());
        }
        return new RangeDim(x, y, z);
    }

    /**
     * n/size的坐标,如果x小于0则坐标减1
     * size为0时不会调用到此方法
     */
    private int d(int n) {
        if (n >= 0) {
            return n/size;
        } else {
            return n/size-1;
        }
    }
}
