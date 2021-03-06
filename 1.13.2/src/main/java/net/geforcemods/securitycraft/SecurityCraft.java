package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.CommandModule;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCDecoration;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCExplosives;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCTechnical;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.ServerProxy;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

@Mod(SecurityCraft.MODID)
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Finally! Cameras!";
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static ServerProxy serverProxy;
	public static SecurityCraft instance;
	public static SimpleNetworkWrapper network;
	public static SCEventHandler eventHandler = new SCEventHandler();
	private GuiHandler guiHandler = new GuiHandler();
	public HashMap<String, Object[]> cameraUsePositions = new HashMap<String, Object[]>();
	public ArrayList<SCManualPage> manualPages = new ArrayList<SCManualPage>();
	private NBTTagCompound savedModule;
	public static ItemGroup tabSCTechnical = new ItemGroupSCTechnical();
	public static ItemGroup tabSCMine = new ItemGroupSCExplosives();
	public static ItemGroup tabSCDecoration = new ItemGroupSCDecoration();

	public SecurityCraft()
	{
		instance = this;
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandSC());
		event.registerServerCommand(new CommandModule());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		log("Starting to load....");
		log("Loading config file....");
		log(SecurityCraft.VERSION + " of SecurityCraft is for a post MC-1.6.4 version! Configuration files are useless for setting anything besides options.");
		log("Config file loaded.");
		log("Setting up network....");
		SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(SecurityCraft.MODID);
		RegistrationHandler.registerPackets(SecurityCraft.network);
		log("Network setup.");

		log("Loading mod content....");
		SetupHandler.setupBlocks();
		SetupHandler.setupMines();
		SetupHandler.setupItems();
		log("Finished loading mod content.");
		log("Regisering mod content... (PT 1/2)");
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		log("Setting up inter-mod stuff...");
		FMLInterModComms.sendMessage("waila", "register", "net.geforcemods.securitycraft.compat.waila.WailaDataProvider.callbackRegister");

		if(ConfigHandler.checkForUpdates) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();
			if(vcUpdateTag != null)
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
		}

		log("Registering mod content... (PT 2/2)");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		EnumCustomModules.refresh();
		serverProxy.registerRenderThings();
		FMLCommonHandler.instance().getDataFixer().init(SecurityCraft.MODID, TileEntityIDDataFixer.VERSION).registerFix(FixTypes.BLOCK_ENTITY, new TileEntityIDDataFixer());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(SecurityCraft.eventHandler);
		DataSerializers.registerSerializer(Owner.SERIALIZER);

		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					IReinforcedBlock.BLOCKS.add((Block)field.get(null));
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		log("Mod finished loading correctly! :D");
	}

	public Object[] getUsePosition(String playerName) {
		return cameraUsePositions.get(playerName);
	}

	public void setUsePosition(String playerName, double x, double y, double z, float yaw, float pitch) {
		cameraUsePositions.put(playerName, new Object[]{x, y, z, yaw, pitch});
	}

	public boolean hasUsePosition(String playerName) {
		return cameraUsePositions.containsKey(playerName);
	}

	public void removeUsePosition(String playerName){
		cameraUsePositions.remove(playerName);
	}

	public NBTTagCompound getSavedModule() {
		return savedModule;
	}

	public void setSavedModule(NBTTagCompound savedModule) {
		this.savedModule = savedModule;
	}

	/**
	 * Prints a String to the console. Only will print if SecurityCraft is in debug mode.
	 */
	public static void log(String line) {
		log(line, false);
	}

	public static void log(String line, boolean isSevereError) {
		if(ConfigHandler.debug)
			System.out.println(isSevereError ? "{SecurityCraft} {" + FMLCommonHandler.instance().getEffectiveSide() + "} {Severe}: " + line : "[SecurityCraft] [" + FMLCommonHandler.instance().getEffectiveSide() + "] " + line);
	}
}
