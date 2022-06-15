package com.kongkongye.mc.range;

public class RangeConsts {
    //Conflict
    public static final int CONFLICT_MAX_BUFFER_SIZE = 1000;
    public static final int CONFLICT_DROP_PER = 100;
    //初始分割大小
    //太大没有必要,浪费内存空间
    //太小会使检测耗时变长
    public static final int INIT_SIZE = 20000;
    //最小分割大小
    //太小会使内存使用增加(而且太小检测耗时提高有限,而且太小时耗时还会反弹)
    //太大会使检测耗时变长
    public static final int MIN_SIZE = 300;
    //减少速率,[1,99]
    //值越小,减小得越快,占用内存越少,但检测耗时变长
    //但不是绝对的,需要在占用内存与cpu耗时之间取得一个平衡
    public static final int CONFLICT_DESC_RATE = 60;

    //Pos

    //自动优化参数
    public static final int POS_OPTIMIZE_MAX = 1000000;//最大值
    public static final int POS_OPTIMIZE_MAX_THRESHOLD = 100;//最大阀值(这个是pos性能最关键参数!!!影响内存与检测效率!!!)

    public static final int POS_MAX_BUFFER_SIZE = 1000;
    public static final int POS_DROP_PER = 100;

    //debug
    public static final String TYPE_WORLD_SPACE = "World";
    public static final String TYPE_LEVEL_MULTI_SPACE = "Pos-Level";
    public static final String TYPE_RANGE_MULTI_SPACE = "Pos-Range";
    public static final String TYPE_DIV_SPACE = "Conflict-Div";
}
