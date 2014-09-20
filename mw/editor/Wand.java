package mw.editor;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.Player;

public class Wand extends Item {
	
	protected static final int EDITOR = 0;
	protected static final int ROTATOR = 1;
	
	private Icon rotatorIcon;
	
	public Wand(int id) {
		super(id);
		this.setMaxStackSize(1)
		.setUnlocalizedName("EditorWand")
		.setTextureName(ModEditor.getModId() + ":wand");
	}
	
	protected static void useWand(EntityPlayer player, int x, int y, int z) {
		if (!player.worldObj.isRemote) {
			ItemStack stack = player.inventory.getCurrentItem();
			if (player.capabilities.isCreativeMode) {
				switch (stack.getItemDamage()) {
				case EDITOR:
					editor(player.worldObj, player, x, y, z);
					break;
				case ROTATOR:
					rotator(player.worldObj, player);
					break;
				default:
					stack.setItemDamage(EDITOR);
				}
			} else {
				stack.stackSize = 0;
			}
		}
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			EditorPacketHandler.sendUseWand(x, y, z);
		}
		return true;
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (player.capabilities.isCreativeMode) {
				switch (stack.getItemDamage()) {
				case EDITOR:
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
					this.editor(world, player, x, y, z);
					break;
				case ROTATOR:
					this.rotator(world, player);
					break;
				default:
					stack.setItemDamage(EDITOR);
				}
			} else {
				stack.stackSize = 0;
			}
		}
		return stack;
	}
	
	private static void editor(World world, EntityPlayer player, int x, int y, int z) {
		if (ModEditor.instance.isSneaking) {
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
	
	private static void rotator(World world, EntityPlayer player) {
		if (ModEditor.instance.isSneaking) {
			BlockAreaMode bam = ModEditor.instance.pt.getPlayerData(player);
			switch(bam.getRotatorMode()) {
			case 0:
				//Mirror X
				bam.mirrorX(world);
				break;
			case 1:
				//Mirror Z
				bam.mirrorZ(world);
				break;
			case 2:
				//Rotate 180
				bam.rotate180(world);
				break;
			case 3:
				//Rotate 90
				bam.rotate90(world);
				break;
			case 4:
				//Rotate 270
				bam.rotate270(world);
				break;
			}
		} else {
			EditorPacketHandler.sendRotatorModeChange((Player) player, ModEditor.instance.pt.getPlayerData(player).changeRotatorMode());
		}
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister) {
		super.registerIcons(iconRegister);
		this.rotatorIcon = iconRegister.registerIcon(ModEditor.getModId() + ":rwand");
	}
	
	@Override
	public Icon getIconFromDamage(int damage) {
		switch (damage) {
		case ROTATOR:
			return this.rotatorIcon;
		default:
			return super.getIconFromDamage(damage);
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + Integer.toString(stack.getItemDamage());
	}
}
