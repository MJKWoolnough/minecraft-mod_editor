package mw.editor;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.TickType;

public class SwitchFunction extends KeyHandler {
	
	public SwitchFunction() {
		super(new KeyBinding[] { new KeyBinding("Wand Function Switch", Keyboard.KEY_R) }, new boolean[] { false });
	}

	@Override
	public String getLabel() {
		return "Wand Function Switch";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (tickEnd && Minecraft.getMinecraft().inGameHasFocus) {
			EditorPacketHandler.sendFunctionChange();
		}
	}
	
	protected static void onFunctionChange(EntityPlayerMP player) {
		ItemStack is = player.inventory.getCurrentItem();
		if (is.itemID == ModEditor.instance.wandId + 256) {
			switch (is.getItemDamage()) {
			case Wand.EDITOR:
				is.setItemDamage(Wand.ROTATOR);
				break;
			case Wand.ROTATOR:
				is.setItemDamage(Wand.EDITOR);
				break;
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
