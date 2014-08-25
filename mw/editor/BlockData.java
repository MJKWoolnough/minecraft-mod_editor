package mw.editor;

import java.io.DataOutputStream;
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

import com.google.common.io.ByteArrayDataInput;

public class BlockData extends Blocks {
	
	private static final Vec3Pool vecPool = new Vec3Pool(3, 20);
	
	public BlockData get(World world, int x, int y, int z) {
		this.blockId = world.getBlockId(x, y, z);
		this.metadata = world.getBlockMetadata(x, y, z);
		if (this.blockId > 0 && Block.blocksList[this.blockId].hasTileEntity(this.metadata)) {
			this.nbtData = new NBTTagCompound();
			TileEntity te =	world.getBlockTileEntity(x, y, z);
			te.writeToNBT(this.nbtData);
			this.nbtData.removeTag("x");
			this.nbtData.removeTag("y");
			this.nbtData.removeTag("z");
		} else {
			this.nbtData = null;
		}
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BlockData)) {
			return false;
		} 
		BlockData b = (BlockData) o;
		if (b.blockId == this.blockId && b.metadata == this.metadata) {
			if (b.nbtData == null && this.nbtData == null) {
				return true;
			} else if (b.nbtData != null && this.nbtData != null) {
				return b.nbtData.equals(this.nbtData);
			}
		}
		return false;
	}
	
	public void write(DataOutputStream os) throws IOException {
		os.writeInt(this.blockId);
		os.writeByte(this.metadata);
		if (this.nbtData != null) {
			NBTBase.writeNamedTag(this.nbtData, os);
		}
	}

	public BlockData load(ByteArrayDataInput in) throws IOException {
		this.blockId = in.readInt();
		this.metadata = in.readByte();
		if (this.blockId > 0 && Block.blocksList[this.blockId].hasTileEntity(this.metadata)) {
			this.nbtData = (NBTTagCompound) NBTBase.readNamedTag(in);
		}
		return this;
	}

}
