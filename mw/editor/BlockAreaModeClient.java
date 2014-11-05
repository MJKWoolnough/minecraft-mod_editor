package mw.editor;

import java.io.IOException;

import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;

import mw.library.Area;

public class BlockAreaModeClient {
	protected BlockData block = new BlockData();
	protected Area area = new Area(null, 0, 0, 0, 0, 0, 0);
	
	protected boolean startSet = false;
	protected boolean endSet = false;
	
	protected final int[] coords = new int[6];
	
	protected int mode = -1;
	protected int rmode = -1;
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void setRotatorMode(int mode) {
		this.rmode = mode;
	}
	
	public void addStartPos(int x, int y, int z) {
		this.coords[0] = x;
		this.coords[1] = y;
		this.coords[2] = z;
		this.startSet = true;
		if (this.endSet) {
			this.area.setCoords(null, x, y, z, this.coords[3], this.coords[4], this.coords[5]);
		}
	}
	public void addEndPos(int x, int y, int z) {
		this.coords[3] = x;
		this.coords[4] = y;
		this.coords[5] = z;
		this.endSet = true;
		if (this.startSet) {
			this.area.setCoords(null, this.coords[0], this.coords[1], this.coords[2], x, y, z);
		}
	}
	
	public void resetArea() {
		this.startSet = false;
		this.endSet = false;
	}
	
	
	public void setBlockData(ByteArrayDataInput in) {
		this.block.load(in);
	}

	public ForgeDirection fillAreaDirection(int x, int y, int z) {
		int[] coords = this.area.getCoords();
		if (x >= coords[0] && x <= coords[3] && y >= coords[1] && y <= coords[4]) {
			if (z < coords[2]) {
				return ForgeDirection.NORTH;
			} else if (z > coords[5]) {
				return ForgeDirection.SOUTH;
			}
		} else if (x >= coords[0] && x <= coords[3] && z >= coords[2] && z <= coords[5]) {
			if (y < coords[1]) {
				return ForgeDirection.DOWN;
			} else if (y > coords[4]) {
				return ForgeDirection.UP;
			}
		} else if (y >= coords[1] && y <= coords[4] && z >= coords[2] && z <= coords[5]) {
			if (x < coords[0]) {
				return ForgeDirection.WEST;
			} else if (x > coords[3]) {
				return ForgeDirection.EAST;
			}
		}
		return null;
	}
}
