package net.geforcemods.securitycraft.util;

import java.net.URI;
import java.net.URISyntaxException;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientUtils{

	@OnlyIn(Dist.CLIENT)
	public static void closePlayerScreen(){
		Minecraft.getInstance().player.closeScreen();
	}

	/**
	 * Takes a screenshot, and sends the player a notification. <p>
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static void takeScreenshot() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Dist.CLIENT)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(Minecraft.getInstance().gameDir, Minecraft.getInstance().displayWidth, Minecraft.getInstance().displayHeight, Minecraft.getInstance().getFramebuffer()));
	}

	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static String getFormattedMinecraftTime(){
		Long time = Long.valueOf(Minecraft.getInstance().world.getDayTime());

		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (time.longValue() / 16.666666F % 60.0F);

		return String.format("%02d:%02d %s", new Object[]{Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM"});
	}

	/**
	 * Sends the client-side NBTTagCompound of a block's TileEntity to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static void syncTileEntity(TileEntity tileEntity){
		NBTTagCompound tag = new NBTTagCompound();
		tileEntity.write(tag);
		SecurityCraft.network.sendToServer(new PacketSSyncTENBTTag(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), tag));
	}

	/**
	 * Sends the client-side NBTTagCompound of a player's held item to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static void syncItemNBT(ItemStack item){
		SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(item));
	}

	@OnlyIn(Dist.CLIENT)
	public static void openURL(String url) {
		URI uri = null;

		try {
			uri = new URI(url);
		}
		catch(URISyntaxException e) {
			e.printStackTrace();
		}

		if(uri == null) return;

		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
		}

		catch (Throwable throwable) {}
	}

	/**
	 * Localizes a String with the given format
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static String localize(String key, Object... params)
	{
		return I18n.format(key, params);
	}
}