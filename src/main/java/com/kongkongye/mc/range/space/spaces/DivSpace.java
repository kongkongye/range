package com.kongkongye.mc.range.space.spaces;

import com.kongkongye.mc.range.DebugListener;
import com.kongkongye.mc.range.Rangable;
import com.kongkongye.mc.range.RangeConsts;
import com.kongkongye.mc.range.dim.dims.RangeDim;
import com.kongkongye.mc.range.hashlist.HashList;
import com.kongkongye.mc.range.hashlist.HashListImpl;
import com.kongkongye.mc.range.space.MultiSpace;
import com.kongkongye.mc.range.space.Range;

import java.util.*;

/**
 * (关键层)
 *
 * 分割维度空间
 *
 * 这层是影响Conflict检测效率的关键因素
 */
public class DivSpace<T extends Rangable> implements MultiSpace<RangeDim, DivSpace<T>, T> {
    /**
     * 保存信息
     */
    private class SaveInfo {
        //保存的范围维度
        //不为null时subSize才有效
        RangeDim dim;
        //下一层大小
        int subSize;

        public SaveInfo() {
        }

        public SaveInfo(RangeDim dim, int subSize) {
            this.dim = dim;
            this.subSize = subSize;
        }

        /**
         * 检测是否能保存到子维度
         */
        public boolean isSaveSub() {
            return dim != null;
        }
    }

    //0表示根层(这种情况需要特殊考虑!)
    private int size;

    //子维度列表
    //初始为null
    private Map<RangeDim, DivSpace<T>> dims;
    //内容空间
    //初始为null
    //指无法保存到子维度的内容(或者已经达到下限,无法再分子维度)
    private RangeContentSpace<T> content;

    public DivSpace(int size) {
        this.size = size;
    }

    @Override
    public Map<RangeDim, DivSpace<T>> getDims() {
        return dims;
    }

    @Override
    public boolean add(T rangable) {
        SaveInfo saveInfo = getSaveDim(rangable.getRange());
        if (saveInfo.isSaveSub()) {
            return initDims().computeIfAbsent(saveInfo.dim, key -> new DivSpace<>(saveInfo.subSize)).add(rangable);//保存在下级
        } else {
            return initContent().add(rangable);//保存在本级
        }
    }

    @Override
    public boolean del(T rangable) {
        SaveInfo saveInfo = getSaveDim(rangable.getRange());
        if (saveInfo.isSaveSub()) {//保存在下级
            DivSpace<T> divSpace = null;
            if (dims != null) {
                divSpace = dims.get(saveInfo.dim);
            }
            return divSpace != null && divSpace.del(rangable);
        }else {//保存在本级
            return initContent().del(rangable);
        }
    }

    @Override
    public Collection<T> getValues() {
        List<T> result = new ArrayList<>();
        if (content != null) {
            result.addAll(content.getValues());
        }
        if (dims != null) {
            for (DivSpace<T> divSpace :dims.values()) {
                result.addAll(divSpace.getValues());
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
        //本空间
        if (content != null) {//无法保存到子空间的内容,肯定要遍历检测是否冲突
            HashList<T> list = content.getConflicts(range);
            if (list != null) {
                if (result == null) {
                    result = new HashListImpl<>();
                }
                result.convert(list, false);
            }
        }
        //子空间
        if (dims != null) {
            SaveInfo saveInfo = getSaveDim(range);
            if (saveInfo.isSaveSub()) {//可以保存到下级,太好了,可以忽略那些无关的子空间
                DivSpace<T> divSpace = dims.get(saveInfo.dim);
                if (divSpace != null) {
                    HashList<T> list = divSpace.getConflicts(range);
                    if (list != null) {
                        if (result == null) {
                            result = new HashListImpl<>();
                        }
                        result.convert(list, false);
                    }
                }
            }else {//无法保存到下级,只能获取全部子级
                HashList<T> list;
                for (DivSpace<T> divSpace :dims.values()) {
                    list = divSpace.getConflicts(range);
                    if (list != null) {
                        if (result == null) {
                            result = new HashListImpl<>();
                        }
                        result.convert(list, false);
                    }
                }
            }
        }
        return result;
    }

    public void debug(DebugListener listener) {
        listener.debug(RangeConsts.TYPE_DIV_SPACE, "Size:"+size+" Current Amount:"+(content!=null?content.getValues().size():0)+" Dim Amount:"+(dims != null?dims.size():0));
        if (dims != null) {
            for (Map.Entry<RangeDim, DivSpace<T>> entry:dims.entrySet()) {
                entry.getValue().debug(listener);
            }
        }
    }

    /**
     * 获取保存的范围维度
     * size可能为0
     */
    private SaveInfo getSaveDim(Range range) {
        if (size == 0) {
            return new SaveInfo(new RangeDim(0, 0, 0), RangeConsts.INIT_SIZE);
        }

        RangeDim dim = null;
        int xZone1 = d(range.getP1().getX());
        if (xZone1 == d(range.getP2().getX())) {
            int yZone1 = d(range.getP1().getY());
            if (yZone1 == d(range.getP2().getY())) {
                int zZone1 = d(range.getP1().getZ());
                if (zZone1 == d(range.getP2().getZ())) {
                    dim = new RangeDim(xZone1, yZone1, zZone1);
                }
            }
        }
        if (dim == null) {
            return new SaveInfo();
        }

        //下一层分割维度空间的大小
        int subSize = size* RangeConsts.CONFLICT_DESC_RATE/100;
        //可以保存到下一层分割维度空间
        if (subSize >= RangeConsts.MIN_SIZE) {
            return new SaveInfo(dim, subSize);
        } else {
            return new SaveInfo();
        }
    }

    private Map<RangeDim, DivSpace<T>> initDims() {
        if (dims == null) {
            dims = new HashMap<>();
        }
        return dims;
    }

    private RangeContentSpace<T> initContent() {
        if (content == null) {
            content = new RangeContentSpace<>();
        }
        return content;
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
