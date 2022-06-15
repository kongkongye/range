package com.kongkongye.mc.range.dim.dims;

import com.kongkongye.mc.range.dim.Dim;

/**
 * 等级维度
 */
public class LevelDim implements Dim {
    //0级是根维度(即容纳所有其它维度保存不下的内容)
    private int level;

    private int size;

    public LevelDim(int level, int size) {
        this.level = level;
        this.size = size;
    }

    public int getLevel() {
        return level;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LevelDim levelDim = (LevelDim) o;

        return level == levelDim.level;
    }

    @Override
    public int hashCode() {
        return level;
    }
}
