package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemScannerDoor extends Item
{
	/**
	 * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
	 * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
	 */
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		else if(side != EnumFacing.UP)
			return false;
		else
		{
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if(!block.isReplaceable(world, pos))
				pos = pos.offset(side);

			if(!player.canPlayerEdit(pos, side, stack))
				return false;
			else if(!SCContent.scannerDoor.canPlaceBlockAt(world, pos))
				return false;
			else
			{
				placeDoor(world, pos, EnumFacing.fromAngle(player.rotationYaw), SCContent.scannerDoor);                    //TERD.getOwner().set(player.getGameProfile().getId().toString(), player.getName());
				((TileEntityOwnable) world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
				((TileEntityOwnable) world.getTileEntity(pos.up())).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
				stack.stackSize--;
				return true;
			}
		}
	}

	public static void placeDoor(World world, BlockPos pos, EnumFacing facing, Block door)
	{
		BlockPos left = pos.offset(facing.rotateY());
		BlockPos right = pos.offset(facing.rotateYCCW());
		int rightNormalCubeAmount = (world.getBlockState(right).getBlock().isNormalCube() ? 1 : 0) + (world.getBlockState(right.up()).getBlock().isNormalCube() ? 1 : 0);
		int leftNormalCubeAmount = (world.getBlockState(left).getBlock().isNormalCube() ? 1 : 0) + (world.getBlockState(left.up()).getBlock().isNormalCube() ? 1 : 0);
		boolean isRightDoor = world.getBlockState(right).getBlock() == door || world.getBlockState(right.up()).getBlock() == door;
		boolean isLeftDoor = world.getBlockState(left).getBlock() == door || world.getBlockState(left.up()).getBlock() == door;
		boolean hingeRight = false;

		if(isRightDoor && !isLeftDoor || leftNormalCubeAmount > rightNormalCubeAmount)
			hingeRight = true;

		BlockPos posAbove = pos.up();
		IBlockState stateToSet = SCContent.scannerDoor.getDefaultState().withProperty(BlockScannerDoor.FACING, facing).withProperty(BlockScannerDoor.HINGE, hingeRight ? BlockScannerDoor.EnumHingePosition.RIGHT : BlockScannerDoor.EnumHingePosition.LEFT);

		world.setBlockState(pos, stateToSet.withProperty(BlockDoor.HALF, BlockScannerDoor.EnumDoorHalf.LOWER), 2);
		world.setBlockState(posAbove, stateToSet.withProperty(BlockDoor.HALF, BlockScannerDoor.EnumDoorHalf.UPPER), 2);
		world.notifyNeighborsOfStateChange(pos, door);
		world.notifyNeighborsOfStateChange(posAbove, door);
	}
}
