package mw.editor;

import mw.library.Area;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;

public class BlockAreaModeClient {

	protected final BlockData block        = new BlockData();
	protected final Area      area         = new Area(null, 0, 0, 0, 0, 0, 0);

	protected boolean         startSet     = false;
	protected boolean         endSet       = false;

	protected final int[]     coords       = new int[6];

	protected int             mode         = -1;
	protected int             rmode        = -1;
	protected int             tmode        = -1;
	
	protected int             sections     = 0; // will later store the section list
	protected byte            sectionType  = -1;
	protected int             sectionStart = -1;
	protected int             sectionEnd   = -1;

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setRotatorMode(int mode) {
		this.rmode = mode;
	}

	public void setTemplateMode(int mode) {
		this.tmode = mode;
	}

	public void addStartPos(int x, int y, int z) {
		this.coords[0] = x;
		this.coords[1] = y;
		this.coords[2] = z;
		this.startSet = true;
		if (this.endSet) {
			this.area.setCoords(null, x, y, z, this.coords[3], this.coords[4], this.coords[5]);
			this.resetTemplate();
		}
	}

	public void addEndPos(int x, int y, int z) {
		this.coords[3] = x;
		this.coords[4] = y;
		this.coords[5] = z;
		this.endSet = true;
		if (this.startSet) {
			this.area.setCoords(null, this.coords[0], this.coords[1], this.coords[2], x, y, z);
			this.resetTemplate();
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
	
	private void resetTemplate() {
		this.sections = 0; // reset the section list
		this.sectionType = -1;
		this.sectionStart = -1;
		this.sectionEnd = -1;
	}

	public boolean sectionSet(int x, int y, int z) {
		return false;
	}

	public int[][] selectedSectionLine() {
		return null;
	}

	public int[][] sectionLines() {
		switch (this.sectionType) {
		case -1:// all
			break;
		case 0: // x
			break;
		case 1: // y
			break;
		case 2: // z
			break;
		}
		return null;
	}

	// Convert world coords to corrected template coords
	public int[] coordsToTemplate(int x, int y, int z) {
		x -= this.coords[0];
		y -= this.area.getCoords()[1];
		z -= this.coords[2];
		if (this.coords[0] > this.coords[3]) {
			x = -x; // + 1 // ???
		}
		if (this.coords[2] > this.coords[5]) {
			z = -z; // + 1 // ???
		}
		return new int[]{x, y, z};
	}

	// Convert corrected template coords to world coords
	public int[] coordsToWorld(int x, int y, int z) {
		if (this.coords[0] > this.coords[3]) {
			x = -x; // - 1 // ???
		}
		if (this.coords[2] > this.coords[5]) {
			z = -z; // - 1 // ???
		}
		x += this.coords[0];
		y += this.area.getCoords()[1];
		z += this.coords[2];
		return new int[]{x, y, z};
	}
}
