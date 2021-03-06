package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCSetPlayerPositionAndRotation implements IMessage{

	private double x, y, z;
	private float rotationYaw, rotationPitch;

	public PacketCSetPlayerPositionAndRotation(){

	}

	public PacketCSetPlayerPositionAndRotation(double x, double y, double z, float yaw, float pitch){
		this.x = x;
		this.y = y;
		this.z = z;
		rotationYaw = yaw;
		rotationPitch = pitch;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		rotationYaw = buf.readFloat();
		rotationPitch = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeFloat(rotationYaw);
		buf.writeFloat(rotationPitch);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCSetPlayerPositionAndRotation, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCSetPlayerPositionAndRotation message, MessageContext ctx) {
			Minecraft.getMinecraft().player.setPositionAndRotation(message.x, message.y, message.z, message.rotationYaw, message.rotationPitch);
			return null;
		}

	}

}
