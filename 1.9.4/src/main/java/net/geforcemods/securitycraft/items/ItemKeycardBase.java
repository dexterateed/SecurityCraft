package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings({"rawtypes"})
public class ItemKeycardBase extends Item{

	private final int level;

	public ItemKeycardBase(int level) {
		this.level = level;
		setMaxDamage(0);
		setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	public int getKeycardLvl(ItemStack stack){
		if(level == 0)
			return 1;
		else if(level == 1)
			return 2;
		else if(level == 2)
			return 3;
		else if(level == 3)
			return 6;
		else if(level == 4)
			return 4;
		else if(level == 5)
			return 5;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		if(level == 3){
			if(stack.getTagCompound() == null){
				stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("Uses", 5);
			}

			list.add(ClientUtils.localize("tooltip.securitycraft:keycard.uses") + " " + stack.getTagCompound().getInteger("Uses"));

		}
	}

}
