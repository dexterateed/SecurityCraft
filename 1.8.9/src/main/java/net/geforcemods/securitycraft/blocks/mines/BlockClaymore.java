package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockClaymore extends BlockContainer implements IExplosive {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockClaymore(Material material) {
		super(material);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
	{
		return null;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getBlock().isSideSolid(world, pos.down(), EnumFacing.UP);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote)
			if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == SCContent.wireCutters){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().withProperty(FACING, state.getValue(FACING)).withProperty(DEACTIVATED, true));
				return true;
			}else if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().withProperty(FACING, state.getValue(FACING)).withProperty(DEACTIVATED, false));
				return true;
			}

		return false;
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
		if (!world.isRemote && !world.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED).booleanValue())
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}

		return super.removedByPlayer(world, pos, player, willHarvest);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isRemote && BlockUtils.hasBlockProperty(world, pos, BlockClaymore.DEACTIVATED) && !world.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED).booleanValue())
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(DEACTIVATED, false);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.5F, true);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
	{
		if (world.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH)
			setBlockBounds(0.225F, 0.000F, 0.175F, 0.775F, 0.325F, 0.450F);
		else if (world.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
			setBlockBounds(0.225F, 0.000F, 0.550F, 0.775F, 0.325F, 0.825F);
		else if (world.getBlockState(pos).getValue(FACING) == EnumFacing.EAST)
			setBlockBounds(0.550F, 0.0F, 0.225F, 0.825F, 0.335F, 0.775F);
		else
			setBlockBounds(0.175F, 0.0F, 0.225F, 0.450F, 0.335F, 0.775F);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(DEACTIVATED, true);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(DEACTIVATED, false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getValue(DEACTIVATED).booleanValue())
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING, DEACTIVATED});
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED).booleanValue();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityClaymore();
	}

}
