package mw.editor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	public static final String CHANNEL = "editormod";
	private static final byte MODECHANGE = 0;
	private static final byte BLOCKCHANGE = 1;
	private static final byte STARTPOSCHANGE = 2;
	private static final byte ENDPOSCHANGE = 3;
	private static final byte RESETAREA = 4;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput in = ByteStreams.newDataInput(packet.data);
		int packetId = in.readUnsignedByte();
		try {
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
			}
		} catch(Exception e) {}
		return;
	}
	
	private void handleModeChange(ByteArrayDataInput in) {
		int modeId = in.readInt();
		ModEditor.instance.bam.setMode(modeId);
	}
	
	private void handleBlockChange(ByteArrayDataInput in) {
		int blockId = in.readInt();
		int metadata = in.readInt();
		try {
			NBTTagCompound nbtData = (NBTTagCompound) NBTTagCompound.func_130104_b(in, 0);
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
	
	private static void sendPacket(ByteArrayOutputStream bos, boolean toPlayer, Player player) {
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = CHANNEL;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		if (toPlayer) {
			if (player == null) {
				PacketDispatcher.sendPacketToAllPlayers(packet);
			} else {
				PacketDispatcher.sendPacketToPlayer(packet, player);				
			}
		} else {
			PacketDispatcher.sendPacketToServer(packet);
		}
	}
	
	public static void sendModeChange(Player player, int mode) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(5);
		DataOutputStream os = new DataOutputStream(bos);
		try {
			os.writeByte(MODECHANGE);
			os.writeInt(mode);
		} catch (IOException e) {
			return;
		}
		PacketHandler.sendPacket(bos, true, player);
	}
	
	public static void sendBlockChange(Player player, BlockData bd) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(17);
		DataOutputStream os = new DataOutputStream(bos);
		try {
			os.writeByte(BLOCKCHANGE);
			bd.write(os);
		} catch (IOException e) {
			return;
		}
		PacketHandler.sendPacket(bos, true, player);
	}
	
	public static void sendStartPosChange(Player player, int x, int y, int z) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(17);
		DataOutputStream os = new DataOutputStream(bos);
		try {
			os.writeByte(STARTPOSCHANGE);
			os.writeInt(x);
			os.writeInt(y);
			os.writeInt(z);
		} catch (IOException e) {
			return;
		}
		PacketHandler.sendPacket(bos, true, player);
	}
	
	public static void sendEndPosChange(Player player, int x, int y, int z) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(17);
		DataOutputStream os = new DataOutputStream(bos);
		try {
			os.writeByte(ENDPOSCHANGE);
			os.writeInt(x);
			os.writeInt(y);
			os.writeInt(z);
		} catch (IOException e) {
			return;
		}
		PacketHandler.sendPacket(bos, true, player);
	}
	
	public static void sendResetArea(Player player, int x, int y, int z) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1);
		DataOutputStream os = new DataOutputStream(bos);
		try {
			os.writeByte(RESETAREA);
		} catch (IOException e) {
			return;
		}
		PacketHandler.sendPacket(bos, true, player);
	}
}