package mw.editor;

import java.io.File;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="mw.ModEditor", name="ModEditor", version="1.1.1")
@NetworkMod(clientSideRequired=true, serverSideRequired=true, channels = { EditorPacketHandler.CHANNEL }, packetHandler = EditorPacketHandler.class)
public class ModEditor {
	
	@Instance(value = "mw.ModEditor")
	public static ModEditor instance;
	
	//@SideOnly(Side.CLIENT)
	protected BlockAreaMode bam = new BlockAreaMode();
	
	//@SideOnly(Side.SERVER)
	protected PlayerTracker pt = new PlayerTracker();
	
	protected int wandId = 4096;
	protected String wandCmd = "wand";
	protected Wand wand = new Wand(wandId);

	protected boolean isSneaking;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent) {
		Configuration Config = new Configuration(new File("config/EditorWandMod.cfg"));
		Config.load();
		this.wandId = Config.get("ItemId", "EditorWand", 4096).getInt();
		this.wandCmd = Config.get("Command", "EditorWand", "wand").getString();
		
		GameRegistry.registerItem(wand, wand.getUnlocalizedName());
		if (preInitEvent.getSide().isServer()) {
			GameRegistry.registerPlayerTracker(pt);
		} else {
			KeyBindingRegistry.registerKeyBinding(new NoFallKey());
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent preInitEvent) {
		if (preInitEvent.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(new WandGUI()); 
		}
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		Commands dc = new Commands();
		((ServerCommandManager) event.getServer().getCommandManager()).registerCommand(dc);
	}
}
