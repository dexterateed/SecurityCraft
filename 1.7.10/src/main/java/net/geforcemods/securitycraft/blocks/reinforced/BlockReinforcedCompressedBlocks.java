package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedCompressedBlocks extends BlockOwnable implements ICustomWailaDisplay, IReinforcedBlock
{
	public BlockReinforcedCompressedBlocks()
	{
		super(Material.rock);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		switch(meta)
		{
			case 0: return Blocks.lapis_block.getIcon(side, meta);
			case 1: return Blocks.coal_block.getIcon(side, meta);
			default: return null; //won't happen
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x999999;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		return new ItemStack(Item.getItemFromBlock(this), 1, world.getBlockMetadata(x, y, z));
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z)
	{
		return new ItemStack(Item.getItemFromBlock(world.getBlock(x, y, z)), 1, world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[]{
				Blocks.lapis_block,
				Blocks.coal_block
		});
	}

	@Override
	public int getAmount()
	{
		return 2;
	}
}
