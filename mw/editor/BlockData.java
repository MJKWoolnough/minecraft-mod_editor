package mw.editor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import mw.library.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import codechicken.multipart.MultipartHelper;

public class BlockData extends Blocks {
	
	@Override
	public BlockData get(World world, int x, int y, int z) {
		return (BlockData) super.get(world, x, y,  z);
	}
	
	public void write(DataOutput os) throws IOException {
		os.writeInt(this.blockId);
		os.writeByte(this.metadata);
		if (this.nbtData != null) {
			NBTBase.writeNamedTag(this.nbtData, os);
		}
	}

	public BlockData load(DataInput in) throws IOException {
		this.blockId = in.readInt();
		this.metadata = in.readByte();
		if (this.blockId > 0 && Block.blocksList[this.blockId].hasTileEntity(this.metadata)) {
			this.nbtData = (NBTTagCompound) NBTBase.readNamedTag(in);
		}
		return this;
	}
}
