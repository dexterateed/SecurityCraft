package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSToggleOption implements IMessage{

	private int x, y, z, id;

	public PacketSToggleOption(){ }

	public PacketSToggleOption(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSToggleOption, IMessage> {

		@Override
		public IMessage onMessage(PacketSToggleOption packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			int id = packet.id;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) != null && getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof CustomizableSCTE) {
				((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).customOptions()[id].toggle();
				((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).onOptionChanged(((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).customOptions()[id]);
				((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).sync();
			}

			return null;
		}
	}

}
