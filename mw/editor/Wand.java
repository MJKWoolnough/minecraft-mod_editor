package mw.editor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.Player;

public class Wand extends Item {
	
	public Wand(int id) {
		super(id);
		this.setMaxStackSize(1);
		this.setUnlocalizedName("EditorWand");
		this.setTextureName("arrow");
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote || !player.capabilities.isCreativeMode) {
			return true;
		}
		BlockAreaMode bam = ModEditor.instance.pt.getPlayerData(player);
		if (player.isSneaking()) {
			switch(bam.getMode()) {
			case 0:
				//Select Block
				bam.getBlock(world, x, y, z);
				PacketHandler.sendBlockChange((Player) player, bam.block);
				break;
			case 1:
				//Place Selected Block
				bam.setBlock(world, x, y, z);
				break;
			case 2:
				//Select Area Start
				bam.addStartPos(x, y, z);
				PacketHandler.sendStartPosChange((Player) player, x, y, z);
				break;
			case 3:
				//Select Area End
				bam.addEndPos(x, y, z);
				PacketHandler.sendEndPosChange((Player) player, x, y, z);
				break;
			case 4:
				//Fill Area with Selected Block
				bam.fillArea(world);
				break;
			case 5:
				//Replaced Matching Blocks in Area with Selected Block
				bam.setBlockInArea(world, x, y, z);
				break;
			case 6:
				//Copy Area to Selected Block
				bam.copyArea(world, x, y, z);
				break;
			default:
				PacketHandler.sendModeChange((Player) player, bam.changeMode());
				return false;
			}
		}
		return true;
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote && player.capabilities.isCreativeMode) {
			BlockAreaMode bam = ModEditor.instance.pt.getPlayerData(player);
			if (player.isSneaking()) {
				switch(bam.getMode()) {
				case 0:
					//Select Air
					bam.getAir();
					PacketHandler.sendBlockChange((Player) player, bam.block);
					//send block to client
					break;
				case 4:
					//Fill Area with Air
					bam.fillArea(world);
					break;
				case 5:
					//Replaced Matching Blocks in Area with Air
					bam.setBlockInArea(world);
					break;
				}
			} else {
				//change mode
				PacketHandler.sendModeChange((Player) player, bam.changeMode());
			}
		}
		return stack;
	}
}
