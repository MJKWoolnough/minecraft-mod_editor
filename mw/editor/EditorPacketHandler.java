package mw.editor;

import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

import mw.library.PacketData;
import mw.library.PacketHandler;


public class EditorPacketHandler extends PacketHandler {

	public static final String CHANNEL = "editormod";
	private static final byte MODECHANGE = 0;
	private static final byte BLOCKCHANGE = 1;
	private static final byte STARTPOSCHANGE = 2;
	private static final byte ENDPOSCHANGE = 3;
	private static final byte RESETAREA = 4;
	private static final byte SNEAKING = 5;
	
	private static EditorPacketHandler instance;
	
	public EditorPacketHandler() {
		super(CHANNEL);
		this.instance = this;
	}

	@Override
	public void handlePacket(int packetId, ByteArrayDataInput in, Player player) {
		switch (packetId) {
		case MODECHANGE:
			handleModeChange(in);
			break;
		case BLOCKCHANGE:
			handleBlockChange(in);
			break;
		case STARTPOSCHANGE:
			handleStartPosChange(in);
			break;
		case ENDPOSCHANGE:
			handleEndPosChange(in);
			break;
		case RESETAREA:
			handleResetArea();
			break;
		case SNEAKING:
			handleSneaking(in);
			break;
		}
		return;
	}
	
	private void handleModeChange(ByteArrayDataInput in) {
		int modeId = in.readInt();
		ModEditor.instance.bam.setMode(modeId);
	}
	
	private void handleBlockChange(ByteArrayDataInput in) {
		try {
			ModEditor.instance.bam.setBlockData(new BlockData().load(in));
		} catch (IOException e) {}
	}
	
	private void handleStartPosChange(ByteArrayDataInput in) {
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		ModEditor.instance.bam.addStartPos(x, y, z);
	}
	
	private void handleEndPosChange(ByteArrayDataInput in) {
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		ModEditor.instance.bam.addEndPos(x, y, z);
	}
	
	private void handleResetArea() {
		ModEditor.instance.bam.resetArea();
	}
	
	private void handleSneaking(ByteArrayDataInput in) {
		ModEditor.instance.isSneaking = in.readBoolean();
	}
	
	public static void sendModeChange(Player player, int mode) {
		PacketData pd = new PacketData(1 + 4);
		try {
			pd.writeByte(MODECHANGE);
			pd.writeInt(mode);
		} catch (IOException e) {
			return;
		}
		EditorPacketHandler.instance.sendPacket(pd, true, player);
	}
	
	public static void sendBlockChange(Player player, BlockData bd) {
		PacketData pd = new PacketData(1 + 4 + 1); // + ???
		try {
			pd.writeByte(BLOCKCHANGE);
			bd.write(pd);
		} catch (IOException e) {
			return;
		}
		EditorPacketHandler.instance.sendPacket(pd, true, player);
	}
	
	public static void sendStartPosChange(Player player, int x, int y, int z) {
		PacketData pd = new PacketData(1 + 4 + 4 + 4);
		try {
			pd.writeByte(STARTPOSCHANGE);
			pd.writeInt(x);
			pd.writeInt(y);
			pd.writeInt(z);
		} catch (IOException e) {
			return;
		}
		EditorPacketHandler.instance.sendPacket(pd, true, player);
	}
	
	public static void sendEndPosChange(Player player, int x, int y, int z) {
		PacketData pd = new PacketData(1 + 4 + 4 + 4);
		try {
			pd.writeByte(ENDPOSCHANGE);
			pd.writeInt(x);
			pd.writeInt(y);
			pd.writeInt(z);
		} catch (IOException e) {
			return;
		}
		EditorPacketHandler.instance.sendPacket(pd, true, player);
	}
	
	public static void sendResetArea(Player player, int x, int y, int z) {
		PacketData pd = new PacketData(1);
		try {
			pd.writeByte(RESETAREA);
		} catch (IOException e) {
			return;
		}
		EditorPacketHandler.instance.sendPacket(pd, true, player);
	}
	
	public static void sendSneaking(boolean isSneaking) {
		PacketData pd = new PacketData(1 + 1);
		try {
			pd.writeByte(SNEAKING);
			pd.writeBoolean(isSneaking);
		} catch (IOException e) {
			return;
		}
		EditorPacketHandler.instance.sendPacket(pd);
	}
}