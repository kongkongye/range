package com.kongkongye.mc.range.space.spaces;

import com.kongkongye.mc.range.DebugListener;
import com.kongkongye.mc.range.Rangable;
import com.kongkongye.mc.range.RangeConsts;
import com.kongkongye.mc.range.RangeManager;
import com.kongkongye.mc.range.dim.dims.LevelDim;
import com.kongkongye.mc.range.hashlist.HashList;
import com.kongkongye.mc.range.hashlist.HashListImpl;
import com.kongkongye.mc.range.space.MultiSpace;
import com.kongkongye.mc.range.space.Pos;
import com.kongkongye.mc.range.space.Range;
import com.kongkongye.mc.range.util.Util;

import java.util.*;

/**
 * (关键层)
 *
 * 等级子维度空间
 *
 * 这层的levels等级划分是影响效率的两个关键因素之一
 * 等级划分目标: 第0层的对象数量尽量少
 */
public class LevelMultiSpace<T extends Rangable> implements MultiSpace<LevelDim, RangeMultiSpace<T>, T> {
    //分层,假设分四层,第0层是整个地图范围,第1层是1000*1000,第2层是100*100,第3层是10*10
    //第0层为整个地图,固定用0来标记
    //除第0层外,其它层可以随意添加

    private Map<LevelDim, RangeMultiSpace<T>> dims;

    //与dims同步
    //index 对应的LevelDim
    private Map<Integer, LevelDim> levels;

    public LevelMultiSpace() {
        init(Util.newArrayList(0));
    }

    public LevelMultiSpace(List<Integer> levels) {
        init(levels);
    }

    @Override
    public Map<LevelDim, RangeMultiSpace<T>> getDims() {
        return dims;
    }

    @Override
    public boolean add(T rangable) {
        return dims.get(getSaveLevel(rangable.getRange())).add(rangable);
    }

    @Override
    public boolean del(T rangable) {
        return dims.get(getSaveLevel(rangable.getRange())).del(rangable);
    }

    @Override
    public Collection<T> getValues() {
        List<T> result = new ArrayList<>();
        for (RangeMultiSpace<T> rangeMultiSpace:dims.values()) {
            result.addAll(rangeMultiSpace.getValues());
        }
        return result;
    }

    /**
     * @see RangeManager#getRanges(Pos)
     * @return 没有可返回null
     */
    public HashList<T> getRanges(Pos pos) {
        //遍历所有等级(因为都可能包含指定位置)
        HashList<T> result = null;
        HashList<T> list;
        for (RangeMultiSpace<T> rangeMultiSpace:dims.values()) {
            list = rangeMultiSpace.getRanges(pos);
            if (list != null) {
                if (result == null) {
                    result = new HashListImpl<>();
                }
                result.convert(list, false);
            }
        }
        return result;
    }

    /**
     * 自动优化!!!
     * 非常牛B的方法哦~~~
     *
     * 在不达到上限的情况下,尽量减少下限的数量
     */
    public void autoOptimize() {
        //先缓存所有的值
        Collection<T> values = getValues();

        List<Integer> levels = new ArrayList<>();
        levels.add(0);

        int start = -1;//下限,-1表示无下限
        int end = -1;//上限,-1表示无上限
        int sel = 1;//选择的值

        boolean over = false;

        while (true) {
            while (true) {
                //检测已经达到最大值,不能再增加了
                if (sel >= RangeConsts.POS_OPTIMIZE_MAX) {
                    over = true;
                    break;
                }
                //增加等级
                levels.add(1, sel);
                //重新初始化
                init(levels);
                //把全部值都添加上去
                for (T value:values) {
                    add(value);
                }
                //已经没有更多值了(值都是从根等级里分出来的)
                RangeMultiSpace<T> rootLevelRangeMultiSpace = dims.get(new LevelDim(0, 0));
                if (rootLevelRangeMultiSpace.getValues().isEmpty()) {
                    over = true;
                    break;
                }
                //检测
                RangeMultiSpace<T> rangeMultiSpace = dims.get(this.levels.get(1));
                if (rangeMultiSpace.checkThreshold()) {//满足阀值
                    if (end == -1) {
                        start = sel;
                        sel = sel*2;
                    }else {
                        if (end - sel <= 1) {//没法增加了,确定这一等级了(虽然不太满足,但是没有办法呀)
                            break;
                        }else {//可以增加
                            start = sel;
                            sel = (sel+end)/2;
                        }
                    }
//                        if (rangeMultiSpace.checkMinThreshold()) {//最小情况满足,增大值再试(因为在满足条件情况下尽量减少层次)
//                        }else {//最小情况不满足,必须增加值
//
//                        }
                }else {//阀值不满足,必须降低值
                    if (sel <= 1) {
                        continue;//sel已经最小,没法再下降了
                    }
                    end = sel;
                    if (start == -1) {
                        sel = sel/2;
                    } else if (sel - start <= 1) {
                        break;//没法减小了,确定这一等级了(虽然不太满足,但是没有办法呀)
                    } else {
                        sel = (start+sel)/2;
                    }
                }

                //把最后一级删除(因为要重新设置)
                levels.remove(1);
            }

            //检测已经结束了
            if (over) {
                break;
            }

            //开始增加下一级
            start = sel+1;
            end = -1;
            sel = sel*2;
        }
    }

    public void debug(DebugListener listener) {
        for (Map.Entry<LevelDim, RangeMultiSpace<T>> entry:dims.entrySet()) {
            listener.debug(RangeConsts.TYPE_LEVEL_MULTI_SPACE, "Level:"+entry.getKey().getLevel()+" Size:"+entry.getKey().getSize()+" Amount:"+entry.getValue().getValues().size());
            //下级
            entry.getValue().debug(listener);
        }
    }

    public Map<Integer, LevelDim> getLevels() {
        return levels;
    }

    /**
     * 按照传入的levels更新此等级空间
     */
    public void update(List<Integer> levels) {
        //先缓存所有的值
        Collection<T> values = getValues();

        //重新初始化
        init(levels);

        //再加回所有的值
        for (T value:values) {
            add(value);
        }
    }

    /**
     * (重新)初始化
     */
    private void init(List<Integer> levels) {
        //初始化
        dims = new HashMap<>();
        this.levels = new HashMap<>();
        //设置
        for (int index=0;index<levels.size();index++) {
            LevelDim levelDim = new LevelDim(index, levels.get(index));
            dims.put(levelDim, new RangeMultiSpace<>(levelDim.getSize()));
            this.levels.put(index, levelDim);
        }
    }

    /**
     * 获取保存的层次
     * (很重要的一个方法!!!)
     * @param range 范围
     * @return 返回应该保存的层次
     */
    private LevelDim getSaveLevel(Range range) {
        //从高层向低层判断

        //选择的等级
        //[0, 最高等级]
        int selLevel = 0;

        for (int size, level=levels.size()-1;level>0;level--) {
            size = levels.get(level).getSize();
            //判断坐标
            if (
                d(range.getP1().getX(),size) == d(range.getP2().getX(),size) &&
                d(range.getP1().getY(),size) == d(range.getP2().getY(),size) &&
                d(range.getP1().getZ(),size) == d(range.getP2().getZ(),size)
            ) {
                selLevel = level;
                break;
            }
        }
        return levels.get(selLevel);
    }

    /**
     * n/size的坐标,如果x小于0则坐标减1
     * size不为0
     */
    private int d(int n, int size) {
        if (n >= 0) {
            return n/size;
        } else {
            return n/size-1;
        }
    }
}
