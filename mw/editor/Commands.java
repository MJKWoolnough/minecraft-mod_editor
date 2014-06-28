package mw.editor;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Commands implements ICommand {

	@Override
	public int compareTo(Object o) {
		return o instanceof Commands ? 0 : 1;
	}

	@Override
	public String getCommandName() {
		return "wand";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/wand\n";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var1 instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) var1;
			player.inventory.addItemStackToInventory(new ItemStack(ModEditor.instance.wand));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		if (var1 instanceof EntityPlayer) {
    			EntityPlayer player = (EntityPlayer) var1;
    			return player.capabilities.isCreativeMode;
    		}
    		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}

}
