package mw.editor;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;



@Mod(modid="mw.ModEditor", name="ModEditor", version="1.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=true, channels = { PacketHandler.CHANNEL }, packetHandler = PacketHandler.class)
public class ModEditor {
	
	@Instance(value = "mw.ModEditor")
	public static ModEditor instance;
	
	public boolean forgeMicroParts = false;
	
	//@SideOnly(Side.CLIENT)
	protected BlockAreaMode bam = new BlockAreaMode();
	
	//@SideOnly(Side.SERVER)
	protected PlayerTracker pt = new PlayerTracker();
	
	protected static int wandId = 4096;	
	protected Wand wand = new Wand(wandId);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent) {
		GameRegistry.registerItem(wand, wand.getUnlocalizedName());
		if (preInitEvent.getSide().isServer()) {
			GameRegistry.registerPlayerTracker(pt);
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent preInitEvent) {
		this.forgeMicroParts = Loader.isModLoaded("ForgeMultipart");
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
