package mw.editor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mw.library.Blocks;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class BlockData extends Blocks {
	
	@Override
	public BlockData get(World world, int x, int y, int z) {
		return (BlockData) super.get(world, x, y,  z);
	}
	
	public void write(DataOutput os) {
		try {
			os.writeInt(this.blockId);
			os.writeByte(this.metadata);
			if (this.nbtData != null) {
				NBTBase.writeNamedTag(this.nbtData, os);
			}
		} catch(IOException e) {}
	}

	public BlockData load(DataInput in) {
		try {
			this.blockId = in.readInt();
			this.metadata = in.readByte();
			if (this.blockId > 0 && Block.blocksList[this.blockId].hasTileEntity(this.metadata)) {
				this.nbtData = (NBTTagCompound) NBTBase.readNamedTag(in);
			}
		} catch (IOException e) {}
		return this;
	}
}
