package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockLaserField extends BlockContainer implements IIntersectable{

	public static final PropertyInteger BOUNDTYPE = PropertyInteger.create("boundtype", 1, 3);

	public BlockLaserField(Material material) {
		super(material);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos)
	{
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote && entity instanceof EntityLivingBase && !EntityUtils.doesMobHavePotionEffect((EntityLivingBase) entity, Potion.getPotionFromResourceLocation("invisibility")))
		{
			for(EnumFacing facing : EnumFacing.VALUES)
			{
				for(int i = 0; i < ConfigHandler.laserBlockRange; i++)
				{
					BlockPos offsetPos = pos.offset(facing, i);
					Block block = world.getBlockState(offsetPos).getBlock();

					if(block == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, offsetPos, BlockLaserBlock.POWERED))
					{
						TileEntity te = world.getTileEntity(offsetPos);

						if(te instanceof CustomizableSCTE && ((CustomizableSCTE)te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, offsetPos, EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getName().toLowerCase()))
							return;

						BlockUtils.setBlockProperty(world, offsetPos, BlockLaserBlock.POWERED, true, true);
						world.notifyNeighborsOfStateChange(offsetPos, SCContent.laserBlock, false);
						world.scheduleUpdate(offsetPos, SCContent.laserBlock, 50);

						if(te instanceof CustomizableSCTE && ((CustomizableSCTE)te).hasModule(EnumCustomModules.HARMING))
							((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					}
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, pos, state
	 */
	@Override
	public void onPlayerDestroy(World world, BlockPos pos, IBlockState state)
	{
		if(!world.isRemote)
		{
			for(EnumFacing facing : EnumFacing.VALUES)
			{
				for(int i = 0; i < ConfigHandler.laserBlockRange; i++)
				{
					if(BlockUtils.getBlock(world, pos.offset(facing, i)) == SCContent.laserBlock)
					{
						for(int j = 1; j < i; j++)
						{
							world.destroyBlock(pos.offset(facing, j), false);
						}
					}
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (source.getBlockState(pos).getValue(BOUNDTYPE).intValue() == 1)
			return new AxisAlignedBB(0.250F, 0.000F, 0.300F, 0.750F, 1.000F, 0.700F);
		else if (source.getBlockState(pos).getValue(BOUNDTYPE).intValue() == 2)
			return new AxisAlignedBB(0.325F, 0.300F, 0.000F, 0.700F, 0.700F, 1.000F);
		else if (source.getBlockState(pos).getValue(BOUNDTYPE).intValue() == 3)
			return new AxisAlignedBB(0.000F, 0.300F, 0.300F, 1.000F, 0.700F, 0.700F);
		return new AxisAlignedBB(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(BOUNDTYPE, 1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(BOUNDTYPE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(BOUNDTYPE).intValue();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {BOUNDTYPE});
	}

	@Override
	@OnlyIn(Dist.CLIENT)

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySCTE().intersectsEntities();
	}

}
