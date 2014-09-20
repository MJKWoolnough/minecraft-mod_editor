package mw.editor;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.TickType;

public class NoFallKey extends KeyHandler {
	
	public NoFallKey() {
		super(new KeyBinding[] { Minecraft.getMinecraft().gameSettings.keyBindSneak }, new boolean[] { false });
	}

	@Override
	public String getLabel() {
		return "No Fall Key";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		if (!player.capabilities.isCreativeMode) {
			return;
		}
		if (player.inventory.mainInventory[player.inventory.currentItem] != null && player.inventory.mainInventory[player.inventory.currentItem].itemID == ModEditor.instance.wand.itemID) {
			player.movementInput.sneak = false;
			kb.pressed = false;
		}
		EditorPacketHandler.sendSneaking(true);
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		EditorPacketHandler.sendSneaking(false);
		kb.pressTime = 0;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
