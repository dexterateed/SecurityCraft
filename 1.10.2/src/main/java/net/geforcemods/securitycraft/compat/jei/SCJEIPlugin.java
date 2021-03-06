package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class SCJEIPlugin implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		registry.addAdvancedGuiHandlers(new SlotMover());
		registry.addDescription(new ItemStack(SCContent.adminTool), "gui.securitycraft:scManual.recipe.adminTool");
		IPasswordConvertible.BLOCKS.forEach((pc) ->  {
			registry.addDescription(new ItemStack(pc), "gui.securitycraft:scManual.recipe." + pc.getRegistryName().getResourcePath());
		});
		IReinforcedBlock.BLOCKS.forEach((rb) -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

			reinforcedBlock.getVanillaBlocks().forEach((vanillaBlock) -> {
				if(reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount())
					registry.addDescription(new ItemStack(rb, 1, reinforcedBlock.getVanillaBlocks().indexOf(vanillaBlock)), "jei.securitycraft:reinforcedBlock.info", "", vanillaBlock.getUnlocalizedName() + ".name");
				else
				{
					for(int i = 0; i < reinforcedBlock.getAmount(); i++)
					{
						registry.addDescription(new ItemStack(rb, 1, i), "jei.securitycraft:reinforcedBlock.info", "", new ItemStack(vanillaBlock, 1, i).getUnlocalizedName() + ".name");
					}
				}
			});
		});
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime){}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {}
}
