package mw.editor;

import java.util.Iterator;

import mw.library.Area;
import mw.library.BlockManipulator;
import mw.library.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockAreaMode {
	
	protected BlockData block = new BlockData();
	protected Area area;
	protected World world;
	
	protected boolean startSet = false;
	protected boolean endSet = false;
	
	protected final int[] coords = new int[6];
	
	protected int mode = -1;
	protected int rmode = -1;
	
	public int changeMode() {
		this.mode++;
		if (this.mode > 7 || ((!this.startSet || !this.endSet) && this.mode > 3)) {
			this.mode = 0;
		}
		return this.mode;
	}
	
	public int changeRotatorMode() {
		if (this.startSet && this.endSet) {
			this.rmode++;
			if (this.rmode > 4 || (this.rmode > 2 && this.area.width() != this.area.depth())) {
				this.rmode = 0;
			}
		}
		return this.rmode;
	}
	
	public void setMode(int modeId) {
		this.mode = modeId;
	}
	
	public void setRotatorMode(int modeId) {
		this.rmode = modeId;
	}
	
	public int getMode() {
		return this.mode;
	}
	
	public int getRotatorMode() {
		return this.rmode;
	}
	
	public void getBlock(World world, int x, int y, int z) {
		this.block = new BlockData().get(world, x, y, z);
	}
	
	public boolean setBlock(World world, int x, int y, int z) {
		if (this.block != null) {
			this.block.set(world, x, y, z);
			return true;
		}
		return false;
	}
	
	public void addStartPos(World world, int x, int y, int z) {
		this.resetWorld(world);
		this.coords[0] = x;
		this.coords[1] = y;
		this.coords[2] = z;
		this.startSet = true;
		if (this.endSet) {
			this.setArea(world);
			if (this.rmode > 2 && this.area.width() != this.area.depth()) {
				this.rmode = -1;
			}
		}
	}
	
	public void addEndPos(World world, int x, int y, int z) {
		this.resetWorld(world);
		this.coords[3] = x;
		this.coords[4] = y;
		this.coords[5] = z;
		this.endSet = true;
		if (this.startSet) {
			this.setArea(world);
			if (this.rmode > 2 && this.area.width() != this.area.depth()) {
				this.rmode = -1;
			}
		}
	}
	
	private void resetWorld(World world) {
		if (this.world != world) {
			this.area = null;
			this.startSet = false;
			this.endSet = false;
			this.rmode = -1;
			if (this.mode > 3) {
				this.mode = 0;
			}
			this.world = world;
		}
	}
	
	private void setArea(World world) {
		if (this.area == null) {
			this.area = new Area(world, this.coords[0], this.coords[1], this.coords[2], this.coords[3], this.coords[4], this.coords[5]);
		} else {
			this.area.setCoords(world, this.coords[0], this.coords[1], this.coords[2], this.coords[3], this.coords[4], this.coords[5]);
		}
	}
	
	public void resetArea() {
		this.startSet = false;
		this.endSet = false;
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

	public void fillArea() {
		if (this.startSet && this.endSet) {
			this.area.fill(this.block);
		}
	}

	public void setBlockInArea(World world, int x, int y, int z) {
		if (this.startSet && this.endSet) {
			this.area.replace(new Blocks().get(world, x, y, z), this.block);
		}
	}
	
	public void copyArea(World world, int x, int y, int z) {
		if (this.startSet && this.endSet) {
			this.area.copyTo(new Area(world, x, y, z, x + this.coords[3] - this.coords[0], y + this.coords[4] - this.coords[1], z + this.coords[5] - this.coords[2]));
		}
	}

	public void fillAreaTo(World world, int x, int y, int z) {
		if (this.startSet && this.endSet && this.area.sameWorld(world)) {
			ForgeDirection direction = this.fillAreaDirection(x, y, z);
			if (direction != null) {
				int times;
				int[] coords = this.area.getCoords();
				if ((direction.ordinal() & 1) == 0) {
					times = direction.offsetX * (x - coords[0] - this.area.width() + 1) / this.area.width() + direction.offsetY * (y - coords[1] - this.area.height() + 1) / this.area.height() + direction.offsetZ * (z - coords[2] - this.area.depth() + 1) / this.area.depth();
				} else {
					times = direction.offsetX * (x - coords[0]) / this.area.width() + direction.offsetY * (y - coords[1]) / this.area.height() + direction.offsetZ * (z - coords[2]) / this.area.depth();
				}
				this.area.copyInDirection(direction, times);
			}
		}
	}

	public void mirrorX() {
		if (this.startSet && this.endSet) {
			this.area.mirrorX();
		}
	}
	
	public void mirrorZ() {
		if (this.startSet && this.endSet) {
			this.area.mirrorZ();
		}
	}

	public void rotate90() {
		if (this.startSet && this.endSet) {
			this.area.rotate90();
		}
	}
	
	public void rotate180() {
		if (this.startSet && this.endSet) {
			this.area.rotate180();
		}
	}
	
	public void rotate270() {
		if (this.startSet && this.endSet) {
			this.area.rotate270();
		}
	}
}
