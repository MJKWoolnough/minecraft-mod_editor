package mw.editor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.Player;

public class Wand extends Item {
	
	public Wand(int id) {
		super(id);
		this.setMaxStackSize(1);
		this.setUnlocalizedName("EditorWand");
		this.setTextureName("arrow");
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote && player.capabilities.isCreativeMode) {
			if (ModEditor.instance.isSneaking) {
				MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
				int x;
				int y;
				int z;
				
				if (mop == null) {
					x = (int) Math.floor(player.posX);
			        y = (int) Math.floor(player.posY + (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()));
			        z = (int) Math.floor(player.posZ);
			        if (y > 255) {
						y = 255;
					} else if (y < 0) {
						y = 0;
					}
				} else {
					x = mop.blockX;
					y = mop.blockY;
					z = mop.blockZ;
				}
				
				BlockAreaMode bam = ModEditor.instance.pt.getPlayerData(player);
				switch(bam.getMode()) {
				case 0:
					//Select Block
					bam.getBlock(world, x, y, z);
					EditorPacketHandler.sendBlockChange((Player) player, bam.block);
					break;
				case 1:
					//Place Selected Block
					bam.setBlock(world, x, y, z);
					break;
				case 2:
					//Select Area Start
					bam.addStartPos(x, y, z);
					EditorPacketHandler.sendStartPosChange((Player) player, x, y, z);
					break;
				case 3:
					//Select Area End
					bam.addEndPos(x, y, z);
					EditorPacketHandler.sendEndPosChange((Player) player, x, y, z);
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
				case 7:
					//Copy Area to Selected Block
					bam.fillAreaTo(world, x, y, z);
					break;
				}
			} else { //change mode
				EditorPacketHandler.sendModeChange((Player) player, ModEditor.instance.pt.getPlayerData(player).changeMode());
			}
		}
		return stack;
	}
}
