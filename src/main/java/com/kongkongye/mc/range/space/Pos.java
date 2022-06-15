package com.kongkongye.mc.range.space;

import java.io.Serializable;

public class Pos implements Serializable, Cloneable{
	private static final int YMAX = 256;//包含
	private static final int YMIN = 0;//包含

	private static final long serialVersionUID = 1L;
	private String world;
	private int x,y,z;

	public Pos(String world,int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * 比较两个点
	 * @param p 比较的点,要求世界相同
	 * @return 如果此点每个坐标都<=目标点p的相应坐标,则返回true,否则返回false
	 */
	public boolean compare(Pos p) {
		return x <= p.getX() && y <= p.getY() && z <= p.getZ();
	}
	
	public String getWorld() {
		return world;
	}

	public void setWorld(String world){
		this.world = world;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		if (y > YMAX) {
			this.y = YMAX;
		}
		if (y < YMIN) {
			this.y = YMIN;
		}
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

    /**
     * 从保存数据中读取位置信息
     * @param data 数据
     * @return 异常返回null
     */
    public static Pos loadFromString(String data) {
        try {
            String[] args = data.split("/", 4);
            String world = args[0];
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            return new Pos(world, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存位置到字符串
     */
    public static String saveToString(Pos pos) {
		return pos.world+"/"+pos.x+"/"+pos.y+"/"+pos.z;
	}

	@Override
	public int hashCode() {
		return world.hashCode()+x+y+z;
	}

	@Override
	public boolean equals(Object obj) {
		Pos pos = (Pos)obj;
		return pos.world.equals(world) && pos.x == x && pos.y == y && pos.z == z;
	}

	@Override
	public String toString() {
		return "Pos{" +
				"world='" + world + '\'' +
				", x=" + x +
				", y=" + y +
				", z=" + z +
				'}';
	}

    @Override
	public Pos clone() {
		return new Pos(world,x,y,z);
	}
}
