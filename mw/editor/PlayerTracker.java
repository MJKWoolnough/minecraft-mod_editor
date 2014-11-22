package mw.editor;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker {

	protected HashMap<String, BlockAreaMode>	l	= new HashMap<String, BlockAreaMode>();

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if (!this.l.containsKey(player.username)) {
			this.l.put(player.username, new BlockAreaMode());
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		this.l.remove(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		this.onPlayerLogin(player);
		this.l.get(player.username).resetArea();
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}

	public BlockAreaMode getPlayerData(EntityPlayer player) {
		this.onPlayerLogin(player);
		return this.l.get(player.username);
	}

}
