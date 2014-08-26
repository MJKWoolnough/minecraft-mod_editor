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
	
	public BlockData get(World world, int x, int y, int z) {
		return (BlockData) super.get(world, x, y,  z);
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
