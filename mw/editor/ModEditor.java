package mw.editor;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "editorwand", name = "ModEditor", version = "3.0.0", dependencies = "required-after:MWLibrary")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { EditorPacketHandler.CHANNEL }, packetHandler = EditorPacketHandler.class)
public class ModEditor {

	@Instance(value = "editorwand")
	public static ModEditor		instance;

	protected BlockAreaModeClient	bam	= new BlockAreaModeClient();

	protected PlayerTracker		pt	= new PlayerTracker();

	protected int			wandId	= 4096;
	protected String		wandCmd	= "wand";
	protected Wand			wand;

	protected boolean		isSneaking;

	@EventHandler
	public void preInit(FMLPreInitializationEvent preInitEvent) {
		Configuration Config = new Configuration(preInitEvent.getSuggestedConfigurationFile());
		Config.load();
		this.wandId = Config.get("ItemId", "EditorWand", this.wandId).getInt();
		this.wandCmd = Config.get("Command", "EditorWand", this.wandCmd).getString();

		this.wand = new Wand(wandId);

		GameRegistry.registerItem(this.wand, this.wand.getUnlocalizedName());
		if (preInitEvent.getSide().isServer()) {
			GameRegistry.registerPlayerTracker(this.pt);
		} else {
			KeyBindingRegistry.registerKeyBinding(new NoFallKey());
			KeyBindingRegistry.registerKeyBinding(new SwitchFunction());
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent postInitEvent) {
		if (postInitEvent.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(new WandGUI());
		}
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		Commands dc = new Commands();
		((ServerCommandManager) event.getServer().getCommandManager()).registerCommand(dc);
	}

	public static String getModId() {
		return "editorwand";
	}
}
