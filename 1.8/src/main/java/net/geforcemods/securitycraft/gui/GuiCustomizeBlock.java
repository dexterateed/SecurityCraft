package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.network.packets.PacketSToggleOption;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomizeBlock extends GuiContainer{

	private CustomizableSCTE tileEntity;
	private GuiPictureButton[] descriptionButtons = new GuiPictureButton[5];
	private GuiButton[] optionButtons = new GuiButton[5];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private boolean jei = Loader.isModLoaded("JEI");

	private final String blockName;

	public GuiCustomizeBlock(InventoryPlayer inventory, CustomizableSCTE te)
	{
		super(new ContainerCustomizeBlock(inventory, te));
		tileEntity = te;
		blockName = BlockUtils.getBlock(Minecraft.getMinecraft().theWorld, tileEntity.getPos()).getUnlocalizedName().substring(5);
	}

	@Override
	public void initGui(){
		super.initGui();

		for(int i = 0; i < tileEntity.getNumberOfCustomizableOptions(); i++){
			descriptionButtons[i] = new GuiPictureButton(i, jei ? guiLeft + 16 : guiLeft + 130, (guiTop + 10) + (i * 25), 20, 20, itemRender, new ItemStack(tileEntity.acceptedModules()[i].getItem()));
			buttonList.add(descriptionButtons[i]);
			hoverCheckers[i] = new HoverChecker(descriptionButtons[i], 20);
		}

		if(tileEntity.customOptions() != null)
			for(int i = 0; i < tileEntity.customOptions().length; i++){
				Option option = tileEntity.customOptions()[i];
				int buttonX = jei ? guiLeft - 122 : guiLeft + 178;

				if(option instanceof OptionDouble && ((OptionDouble)option).isSlider())
				{
					optionButtons[i] = new GuiSlider((StatCollector.translateToLocal("option." + blockName + "." + option.getName()) + " ").replace("#", option.toString()), blockName, i, buttonX, (guiTop + 10) + (i * 25), 120, 20, "", "", (Double)option.getMin(), (Double)option.getMax(), (Double)option.getValue(), true, true, (OptionDouble)option);
					optionButtons[i].packedFGColour = 14737632;
				}
				else
				{
					optionButtons[i] = new GuiButton(i, buttonX, (guiTop + 10) + (i * 25), 120, 20, getOptionButtonTitle(option));
					optionButtons[i].packedFGColour = option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632;
				}

				buttonList.add(optionButtons[i]);
				hoverCheckers[i + tileEntity.getNumberOfCustomizableOptions()] = new HoverChecker(optionButtons[i], 20);
			}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY))
				if(i < tileEntity.getNumberOfCustomizableOptions())
					this.drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(getModuleDescription(i), 150), mouseX, mouseY, mc.fontRendererObj);
				else
					this.drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(getOptionDescription(i), 150), mouseX, mouseY, mc.fontRendererObj);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String name = tileEntity.hasCustomName() ? tileEntity.getName() : I18n.format(tileEntity.getName(), new Object[0]);
		String inventory = I18n.format("container.inventory");

		fontRendererObj.drawString(name, xSize / 2 - fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
		fontRendererObj.drawString(inventory, jei && tileEntity.acceptedModules().length == 3 ? xSize / 2 - fontRendererObj.getStringWidth(inventory) / 2 : 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(new ResourceLocation("securitycraft:textures/gui/container/customize" + tileEntity.getNumberOfCustomizableOptions() + ".png"));
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(!(button instanceof GuiPictureButton)) {
			Option<?> tempOption = tileEntity.customOptions()[button.id];
			tempOption.toggle();
			button.packedFGColour = tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632;
			button.displayString = getOptionButtonTitle(tempOption);
			SecurityCraft.network.sendToServer(new PacketSToggleOption(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), button.id));
		}
	}

	private String getModuleDescription(int buttonID) {
		String moduleDescription = "module." + blockName + "." + descriptionButtons[buttonID].getItemStack().getUnlocalizedName().substring(5).replace("securitycraft:", "") + ".description";

		return StatCollector.translateToLocal(descriptionButtons[buttonID].getItemStack().getUnlocalizedName() + ".name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal(moduleDescription);
	}

	private String getOptionDescription(int buttonID) {
		String optionDescription = "option." + blockName + "." + tileEntity.customOptions()[buttonID - tileEntity.getNumberOfCustomizableOptions()].getName() + ".description";

		return StatCollector.translateToLocal(optionDescription);
	}

	private String getOptionButtonTitle(Option<?> option) {
		return (StatCollector.translateToLocal("option." + blockName + "." + option.getName()) + " ").replace("#", option.toString());
	}

}