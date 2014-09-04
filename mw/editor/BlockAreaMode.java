package mw.editor;

import java.util.Iterator;

import mw.library.BlockManipulator;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockAreaMode {
	
	protected BlockData block = new BlockData();
	
	protected boolean startSet = false;
	protected boolean endSet = false;
	
	protected final int[] area = new int[6];
	protected final int[] mmArea = new int[6];
	
	protected int mode = -1;
	protected int rmode = -1;
	
	public int changeMode() {
		this.mode++;
		if (this.mode > 7) {
			this.mode = 0;
		}
		return this.mode;
	}
	
	public int changeRotatorMode() {
		if (this.startSet && this.endSet) {
			this.rmode++;
			if (this.rmode > 4 || (this.rmode > 2 && this.mmArea[3] - this.mmArea[0] != this.mmArea[5] - this.mmArea[2])) {
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
	
	public void getAir() {
		this.block = new BlockData();
	}
	
	public boolean setBlock(World world, int x, int y, int z) {
		if (this.block != null) {
			this.block.set(world, x, y, z);
			return true;
		}
		return false;
	}
	
	public void setBlockData(BlockData bd) {
		this.block = bd;
	}
	
	public void addStartPos(int x, int y, int z) {
		this.area[0] = x;
		this.area[1] = y;
		this.area[2] = z;
		this.mmArea[0] = min(x, this.area[3]);
		this.mmArea[1] = min(y, this.area[4]);
		this.mmArea[2] = min(z, this.area[5]);
		this.mmArea[3] = max(x, this.area[3]);
		this.mmArea[4] = max(y, this.area[4]);
		this.mmArea[5] = max(z, this.area[5]);
		this.startSet = true;
		if (this.rmode > 2 && this.mmArea[3] - this.mmArea[0] != this.mmArea[5] - this.mmArea[2]) {
			this.rmode = -1;
		}
	}
	
	public void addEndPos(int x, int y, int z) {
		this.area[3] = x;
		this.area[4] = y;
		this.area[5] = z;
		this.mmArea[0] = min(this.area[0], x);
		this.mmArea[1] = min(this.area[1], y);
		this.mmArea[2] = min(this.area[2], z);
		this.mmArea[3] = max(this.area[0], x);
		this.mmArea[4] = max(this.area[1], y);
		this.mmArea[5] = max(this.area[2], z);
		this.endSet = true;
		if (this.rmode > 2 && this.mmArea[3] - this.mmArea[0] != this.mmArea[5] - this.mmArea[2]) {
			this.rmode = -1;
		}
	}
	
	public void resetArea() {
		this.startSet = false;
		this.endSet = false;
	}
	
	private boolean setBlockInArea(World world, BlockData matchBlock) {
		if (!this.startSet || !this.endSet) {
			return false;
		}
		
		BlockData bd = new BlockData();
		
		for (int i = this.mmArea[0]; i <= this.mmArea[3]; i++) {
			for (int j = this.mmArea[1]; j <= this.mmArea[4]; j++) {
				for (int k = this.mmArea[2]; k <= this.mmArea[5]; k++) {
					bd.get(world, i, j, k);
					if (matchBlock.equals(bd)) {
						this.block.set(world, i, j, k);
					}
				}
			}
		}
		return true;
	}
	
	public boolean setBlockInArea(World world) { //Replace Air
		return this.setBlockInArea(world, new BlockData());
	}
	
	public boolean setBlockInArea(World world, int x, int y, int z) {
		return this.setBlockInArea(world, new BlockData().get(world, x, y, z));
	}
	
	public boolean fillArea(World world) {
		if (this.block == null || !this.startSet || !this.endSet) {
			return false;
		}
		
		BlockData bd = new BlockData();
		
		for (int i = this.mmArea[0]; i <= this.mmArea[3]; i++) {
			for (int j = this.mmArea[1]; j <= this.mmArea[4]; j++) {
				for (int k = this.mmArea[2]; k <= this.mmArea[5]; k++) {
					this.block.set(world, i, j, k);
				}
			}
		}
		return true;
	}
	
	private class upRange implements Iterator<Integer> {
		
		private int start;
		private int on;
		private int stop;
		
		private upRange(int start, int stop) {
			this.start = start;
			this.on = start;
			this.stop = stop;
		}

		@Override
		public boolean hasNext() {
			return this.on <= this.stop;
		}

		@Override
		public Integer next() {
			int toRet = this.on;
			this.on++;
			return toRet;
		}

		@Override
		public void remove() {
			this.on = this.start;
		}
	}
	
	private class downRange implements Iterator<Integer> {
		
		private int start;
		private int on;
		private int stop;
		
		private downRange(int start, int stop) {
			this.start = start;
			this.on = start;
			this.stop = stop;
		}

		@Override
		public boolean hasNext() {
			return this.on >= this.stop;
		}

		@Override
		public Integer next() {
			int toRet = this.on;
			this.on--;
			return toRet;
		}

		@Override
		public void remove() {
			this.on = this.start;
		}
	}
	
	public boolean copyArea(World world, int x, int y, int z) {
		if (!this.startSet || !this.endSet) {
			return false;
		}
		
		BlockData bd = new BlockData();
		bd.notifyChange = false;
		
		int dx = x - this.area[0];
		int dy = y - this.area[1];
		int dz = z - this.area[2];
		
		Iterator<Integer> xI;
		Iterator<Integer> yI;
		Iterator<Integer> zI;
		
		if (dx > 0) {
			xI = new downRange(this.mmArea[3], this.mmArea[0]);
		} else {
			xI = new upRange(this.mmArea[0], this.mmArea[3]);
		}
		
		if (dy > 0) {
			yI = new downRange(this.mmArea[4], this.mmArea[1]);
		} else {
			yI = new upRange(this.mmArea[1], this.mmArea[4]);
		}
		
		if (dz > 0) {
			zI = new downRange(this.mmArea[5], this.mmArea[2]);
		} else {
			zI = new upRange(this.mmArea[2], this.mmArea[5]);
		}
		
		while(xI.hasNext()) {
			int i = xI.next().intValue();
			while(yI.hasNext()) {
				int j = yI.next().intValue();
				while(zI.hasNext()) {
					int k = zI.next().intValue();
					bd.get(world, i, j, k).set(world, i + dx, j + dy, k + dz);
				}
				zI.remove();
			}
			yI.remove();
		}
		
		this.notifyBlockChanges(world, mmArea[0] + dx, mmArea[1] + dy, mmArea[2] + dz, mmArea[3] + dx, mmArea[4] + dy, mmArea[5] + dz);
		return true;
	}
	
	private static int min(int a, int b) {
		if (a < b) {
			return a;
		}
		return b;
	}
	
	private static int max(int a, int b) {
		if (a < b) {
			return b;
		}
		return a;
	}
	
	public ForgeDirection fillAreaDirection(int x, int y, int z) {
		if (x >= this.mmArea[0] && x <= this.mmArea[3] && y >= this.mmArea[1] && y <= this.mmArea[4]) {
			if (z < this.mmArea[2]) {
				return ForgeDirection.NORTH;
			} else if (z > this.mmArea[5]) {
				return ForgeDirection.SOUTH;
			}
		} else if (x >= this.mmArea[0] && x <= this.mmArea[3] && z >= this.mmArea[2] && z <= this.mmArea[5]) {
			if (y < this.mmArea[1]) {
				return ForgeDirection.DOWN;
			} else if (y > this.mmArea[4]) {
				return ForgeDirection.UP;
			}
		} else if (y >= this.mmArea[1] && y <= this.mmArea[4] && z >= this.mmArea[2] && z <= this.mmArea[5]) {
			if (x < this.mmArea[0]) {
				return ForgeDirection.WEST;
			} else if (x > this.mmArea[3]) {
				return ForgeDirection.EAST;
			}
		}
		return null;
	}

	public boolean fillAreaTo(World world, int x, int y, int z) {
		if (!this.startSet || !this.endSet) {
			return false;
		}
		ForgeDirection direction = this.fillAreaDirection(x, y, z);
		if (direction == null) {
			return false;
		}
		
		int width  = this.mmArea[3] - this.mmArea[0] + 1;
		int height = this.mmArea[4] - this.mmArea[1] + 1;
		int depth  = this.mmArea[5] - this.mmArea[2] + 1;
		
		int count;
		
		if ((direction.ordinal() & 1) == 0) {
			count = direction.offsetX * (x - mmArea[0] - width + 1) / width + direction.offsetY * (y - mmArea[1] - height + 1) / height + direction.offsetZ * (z - mmArea[2] - depth + 1) / depth;
		} else {
			count = direction.offsetX * (x - mmArea[0]) / width + direction.offsetY * (y - mmArea[1]) / height + direction.offsetZ * (z - mmArea[2]) / depth;
		}
		
		x = area[0];
		y = area[1];
		z = area[2];
		
		BlockData bd = new BlockData();
		//bd.notifyChange = false;
		
		for (int i = mmArea[0]; i <= mmArea[3]; i++) {
			for (int j = mmArea[1]; j <= mmArea[4]; j++) {
				for (int k = mmArea[2]; k <= mmArea[5]; k++) {
					bd.get(world, i, j, k);
					int a = i;
					int b = j;
					int c = k;
					for (int d = count; d > 0; d--) {
						a += width  * direction.offsetX;
						b += height * direction.offsetY;
						c += depth  * direction.offsetZ;
						bd.set(world, a, b, c);
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean mirrorX(World world) {
		if (!this.startSet || !this.endSet) {
			return false;
		}
		
		BlockData left = new BlockData();
		BlockData right = new BlockData();
		
		left.notifyChange = false;
		right.notifyChange = false;
		
		for (int i = 0; i <= (mmArea[3] - mmArea[0]) >> 1; i++) {
			int leftPos = mmArea[0] + i;
			int rightPos = mmArea[3] - i;
			for (int j = mmArea[1]; j <= mmArea[4]; j++) {
				for (int k = mmArea[2]; k <= mmArea[5]; k++) {
					left.get(world, leftPos, j, k);
					if (leftPos != rightPos) {
						right.get(world, rightPos, j, k).mirrorX().set(world, leftPos, j, k);
					}
					left.mirrorX().set(world, rightPos, j, k);
				}
			}
		}
		this.notifyBlockChanges(world, mmArea[0], mmArea[1], mmArea[2], mmArea[3], mmArea[4], mmArea[5]);
		return true;
	}
	
	public boolean mirrorZ(World world) {
		if (!this.startSet || !this.endSet) {
			return false;
		}
		
		BlockData top = new BlockData();
		BlockData bottom = new BlockData();
		
		top.notifyChange = false;
		bottom.notifyChange = false;
		
		for (int k = 0; k <= (mmArea[5] - mmArea[2]) >> 1; k++) {
			int topPos = mmArea[2] + k;
			int bottomPos = mmArea[5] - k;
			for (int i = mmArea[0]; i <= mmArea[3]; i++) {
				for (int j = mmArea[1]; j <= mmArea[4]; j++) {
					top.get(world, i, j, topPos);
					if (topPos != bottomPos) {
						bottom.get(world, i, j, bottomPos).mirrorZ().set(world, i, j, topPos);
					}
					top.mirrorZ().set(world, i, j, bottomPos);
				}
			}
		}
		this.notifyBlockChanges(world, mmArea[0], mmArea[1], mmArea[2], mmArea[3], mmArea[4], mmArea[5]);
		return true;
	}
	
	public boolean rotate90(World world) {
		if (!this.startSet || !this.endSet || this.mmArea[3] - this.mmArea[0] != this.mmArea[5] - this.mmArea[2]) {
			return false;
		}
		
		BlockData topLeft = new BlockData();
		BlockData other = new BlockData();
		
		topLeft.notifyChange = false;
		other.notifyChange = false;
		
		int width  = this.mmArea[3] - this.mmArea[0];
		
		for (int i = 0; i <= width >> 1; i++) {
			int leftPos = i;
			int rightPos = width - i;
			for (int k = 0; k <= (width - 1) >> 1; k++) {
				int topPos = k;
				int bottomPos = width - k;
				for (int j = mmArea[1]; j <= mmArea[4]; j++) {
					topLeft.get(world, this.mmArea[0] + leftPos, j, this.mmArea[2] + topPos); //top-left ...
					if (leftPos != rightPos || topPos != bottomPos) {
						other.get(world, this.mmArea[0] + topPos, j, this.mmArea[2] + rightPos).rotate90().set(world, this.mmArea[0] + leftPos, j, this.mmArea[2] + topPos); //bottom-left -> top-left
						other.get(world, this.mmArea[0] + rightPos, j, this.mmArea[2] + bottomPos).rotate90().set(world, this.mmArea[0] + topPos, j, this.mmArea[2] + rightPos); //bottom-right -> bottom-left
						other.get(world, this.mmArea[0] + bottomPos, j, this.mmArea[2] + leftPos).rotate90().set(world,this.mmArea[0] +  rightPos, j, this.mmArea[2] + bottomPos); //top-right -> bottom-right
					}
					topLeft.rotate90().set(world, this.mmArea[0] + bottomPos, j, this.mmArea[2] + leftPos); //... -> top-right
				}
			}
		}
		
		if ((width & 1) == 0) { //middle blocks
			for (int j = mmArea[1]; j <= mmArea[4]; j++) {
				other.get(world, this.mmArea[0] + (width >> 1), j, this.mmArea[2] + (width >> 1)).rotate90().set(world, this.mmArea[0] + (width >> 1), j, this.mmArea[2] + (width >> 1));
			}
		}
		this.notifyBlockChanges(world, mmArea[0], mmArea[1], mmArea[2], mmArea[3], mmArea[4], mmArea[5]);
		return true;
	}
	
	public boolean rotate180(World world) {
		if (!this.startSet || !this.endSet) {
			return false;
		}
		BlockData left = new BlockData();
		BlockData right = new BlockData();
		
		left.notifyChange = false;
		right.notifyChange = false;
		
		for (int i = 0; i <= (mmArea[3] - mmArea[0]) >> 1; i++) {
			int leftPos = mmArea[0] + i;
			int rightPos = mmArea[3] - i;
			for (int k = 0; k <= mmArea[5] - mmArea[2]; k++) {
				int topPos = mmArea[2] + k;
				int bottomPos = mmArea[5] - k;
				for (int j = mmArea[1]; j <= mmArea[4]; j++) {
					left.get(world, leftPos, j, topPos);
					if (leftPos != rightPos || topPos != bottomPos) {
						right.get(world, rightPos, j, bottomPos).rotate180().set(world, leftPos, j, topPos);
					}
					left.rotate180().set(world, rightPos, j, bottomPos);
				}
				if (leftPos == rightPos && topPos == bottomPos) {
					break;
				}
			}
		}
		this.notifyBlockChanges(world, mmArea[0], mmArea[1], mmArea[2], mmArea[3], mmArea[4], mmArea[5]);
		return true;
	}
	
	public boolean rotate270(World world) {
		if (!this.startSet || !this.endSet || this.mmArea[3] - this.mmArea[0] != this.mmArea[5] - this.mmArea[2]) {
			return false;
		}
		
		BlockData topLeft = new BlockData();
		BlockData other = new BlockData();
		
		topLeft.notifyChange = false;
		other.notifyChange = false;
		
		int width  = this.mmArea[3] - this.mmArea[0];
		
		for (int i = 0; i <= width >> 1; i++) {
			int leftPos = i;
			int rightPos = width - i;
			for (int k = 0; k <= (width - 1)>> 1; k++) {
				int topPos = k;
				int bottomPos = width - k;
				for (int j = mmArea[1]; j <= mmArea[4]; j++) {
					topLeft.get(world, this.mmArea[0] + leftPos, j, this.mmArea[2] + topPos); //top-left ...
					if (leftPos != rightPos || topPos != bottomPos) {
						other.get(world, this.mmArea[0] + bottomPos, j, this.mmArea[2] + leftPos).rotate270().set(world, this.mmArea[0] + leftPos, j, this.mmArea[2] + topPos); //top-right -> top-left
						other.get(world, this.mmArea[0] + rightPos, j, this.mmArea[2] + bottomPos).rotate270().set(world, this.mmArea[0] + bottomPos, j, this.mmArea[2] + leftPos); //bottom-right -> top-right
						other.get(world, this.mmArea[0] + topPos, j, this.mmArea[2] + rightPos).rotate270().set(world,this.mmArea[0] +  rightPos, j, this.mmArea[2] + bottomPos); //bottom-left -> bottom-right
					}
					topLeft.rotate270().set(world, this.mmArea[0] + topPos, j, this.mmArea[2] + rightPos); //... -> bottom-left
				}
			}
		}
		
		if ((width & 1) == 0) { //middle blocks
			for (int j = mmArea[1]; j <= mmArea[4]; j++) {
				other.get(world, this.mmArea[0] + (width >> 1), j, this.mmArea[2] + (width >> 1)).rotate270().set(world, this.mmArea[0] + (width >> 1), j, this.mmArea[2] + (width >> 1));
			}
		}
		this.notifyBlockChanges(world, mmArea[0], mmArea[1], mmArea[2], mmArea[3], mmArea[4], mmArea[5]);
		return true;
	}

	private void notifyBlockChanges(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				for (int k = z1; k < z2; k++) {
					world.notifyBlockChange(i, j, k, world.getBlockId(i, j, k));
				}
			}
		}
	}
}
