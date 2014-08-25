package mw.editor;

import java.util.Iterator;

import net.minecraft.world.World;

public class BlockAreaMode {
	
	protected BlockData block = new BlockData();
	
	protected boolean startSet = false;
	protected boolean endSet = false;
	
	protected final int[] area = new int[6];
	
	protected int mode = -1;
	
	public int changeMode() {
		this.mode++;
		if (this.mode > 6) {
			this.mode = 0;
		}
		return this.mode;
	}
	
	public void setMode(int modeId) {
		this.mode = modeId;
	}
	
	public int getMode() {
		return this.mode;
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
		this.startSet = true;
	}
	
	public void addEndPos(int x, int y, int z) {
		this.area[3] = x;
		this.area[4] = y;
		this.area[5] = z;
		this.endSet = true;
	}
	
	public void resetArea() {
		this.startSet = false;
		this.endSet = false;
	}
	
	private boolean setBlockInArea(World world, BlockData matchBlock) {
		if (!this.startSet || !this.endSet) {
			return false;
		}
		
		int minX = min(this.area[0], this.area[3]);
		int maxX = max(this.area[0], this.area[3]);
		int minY = min(this.area[1], this.area[4]);
		int maxY = max(this.area[1], this.area[4]);
		int minZ = min(this.area[2], this.area[5]);
		int maxZ = max(this.area[2], this.area[5]);
		
		BlockData bd = new BlockData();
		
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				for (int k = minZ; k <= maxZ; k++) {
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
		int minX = min(this.area[0], this.area[3]);
		int maxX = max(this.area[0], this.area[3]);
		int minY = min(this.area[1], this.area[4]);
		int maxY = max(this.area[1], this.area[4]);
		int minZ = min(this.area[2], this.area[5]);
		int maxZ = max(this.area[2], this.area[5]);
		
		BlockData bd = new BlockData();
		
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				for (int k = minZ; k <= maxZ; k++) {
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
		int minX = min(this.area[0], this.area[3]);
		int maxX = max(this.area[0], this.area[3]);
		int minY = min(this.area[1], this.area[4]);
		int maxY = max(this.area[1], this.area[4]);
		int minZ = min(this.area[2], this.area[5]);
		int maxZ = max(this.area[2], this.area[5]);
		
		BlockData bd = new BlockData();
		
		int dx = x - this.area[0];
		int dy = y - this.area[1];
		int dz = z - this.area[2];
		
		Iterator<Integer> xI;
		Iterator<Integer> yI;
		Iterator<Integer> zI;
		
		if (dx > 0) {
			xI = new downRange(maxX, minX);
		} else {
			xI = new upRange(minX, maxX);
		}
		
		if (dy > 0) {
			yI = new downRange(maxY, minY);
		} else {
			yI = new upRange(minY, maxY);
		}
		
		if (dz > 0) {
			zI = new downRange(maxZ, minZ);
		} else {
			zI = new upRange(minZ, maxZ);
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
}
