package com.kongkongye.mc.range.dim.dims;

import com.kongkongye.mc.range.dim.Dim;

/**
 * 范围维度
 */
public class RangeDim implements Dim {
    private int x;
    private int y;
    private int z;

    public RangeDim(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RangeDim rangeDim = (RangeDim) o;

        if (x != rangeDim.x) {
            return false;
        }
        if (y != rangeDim.y) {
            return false;
        }
        return z == rangeDim.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "RangeDim{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
