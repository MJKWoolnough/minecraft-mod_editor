package mw.editor;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import codechicken.multipart.MultipartHelper;

import com.google.common.io.ByteArrayDataInput;

public class BlockData {
	protected int blockId;
	protected int metadata;
	protected NBTTagCompound nbtData;
	
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
	
	public void set(World world, int x, int y, int z) {
		world.setBlock(x, y, z, blockId, metadata, 2);
		TileEntity te = this.getTileEntity(world, x, y, z);
		if (te != null) {
            world.setBlockTileEntity(x, y, z, te);
			if (ModEditor.instance.forgeMicroParts && nbtData.getString("id").equals("savedMultipart")) {
				MultipartHelper.sendDescPacket(world, te);
			}
			world.markBlockForUpdate(x, y, z);
		}
		world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
	}
	
	protected TileEntity getTileEntity(World world, int x, int y, int z) {
		if (this.nbtData != null) {
			NBTTagCompound nbtData = (NBTTagCompound) this.nbtData.copy();
			nbtData.setInteger("x", x);
			nbtData.setInteger("y", y);
	        nbtData.setInteger("z", z);
			if (ModEditor.instance.forgeMicroParts && nbtData.getString("id").equals("savedMultipart")) {
				return MultipartHelper.createTileFromNBT(world, nbtData);
			}
			return TileEntity.createAndLoadEntity(nbtData);
		}
		return null;
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
		os.writeInt(this.metadata);
		if (this.nbtData != null) {
			NBTBase.writeNamedTag(this.nbtData, os);
		}
	}

	public BlockData load(ByteArrayDataInput in) throws IOException {
		this.blockId = in.readInt();
		this.metadata = in.readInt();
		this.nbtData = (NBTTagCompound) NBTBase.readNamedTag(in);
		return this;
	}
}
