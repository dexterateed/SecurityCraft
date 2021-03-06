package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.Utils;

public class PacketSetISType implements IMessage{

	private int x, y, z;
	private String type;

	public PacketSetISType(){

	}

	public PacketSetISType(int x, int y, int z, String type){
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		type = ByteBufUtils.readUTF8String(buf);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, type);

	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetISType, IMessage> {

		@Override
		public IMessage onMessage(PacketSetISType packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;

			((TileEntityInventoryScanner) getWorld(context.getServerHandler().playerEntity).getTileEntity(x, y, z)).setType(packet.type);
			SecurityCraft.log("Setting type to " + packet.type);
			getWorld(context.getServerHandler().playerEntity).scheduleBlockUpdate(x, y, z, getWorld(context.getServerHandler().playerEntity).getBlock(x, y, z), 1);
			Utils.setISinTEAppropriately(getWorld(context.getServerHandler().playerEntity), x, y, z, ((TileEntityInventoryScanner) getWorld(context.getServerHandler().playerEntity).getTileEntity(x, y, z)).getContents(), ((TileEntityInventoryScanner) getWorld(context.getServerHandler().playerEntity).getTileEntity(x, y, z)).getType());
			return null;
		}

	}

}
