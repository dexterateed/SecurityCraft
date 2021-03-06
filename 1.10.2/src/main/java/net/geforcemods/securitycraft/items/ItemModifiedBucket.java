package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemModifiedBucket extends ItemBucket {

	private Block containedBlock;

	public ItemModifiedBucket(Block containedBlock) {
		super(containedBlock);
		this.containedBlock = containedBlock;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		boolean isAir = containedBlock == Blocks.AIR;
		RayTraceResult rayTrace = rayTrace(world, player, isAir);
		ActionResult<ItemStack> eventResul = ForgeEventFactory.onBucketUse(player, world, stack, rayTrace);
		if (eventResul != null) return eventResul;

		if (rayTrace == null)
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		else if (rayTrace.typeOfHit != RayTraceResult.Type.BLOCK)
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		else
		{
			BlockPos pos = rayTrace.getBlockPos();

			if (!world.isBlockModifiable(player, pos))
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			else if (isAir)
			{
				if (!player.canPlayerEdit(pos.offset(rayTrace.sideHit), rayTrace.sideHit, stack))
					return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
				else
				{
					IBlockState state = world.getBlockState(pos);
					Material material = state.getMaterial();

					if (material == Material.WATER && state.getValue(BlockLiquid.LEVEL).intValue() == 0)
					{
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
						player.addStat(StatList.getObjectUseStats(this));
						player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, fillBucket(stack, player, SCContent.fWaterBucket));
					}
					else if (material == Material.LAVA && state.getValue(BlockLiquid.LEVEL).intValue() == 0)
					{
						player.playSound(SoundEvents.ITEM_BUCKET_FILL_LAVA, 1.0F, 1.0F);
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
						player.addStat(StatList.getObjectUseStats(this));
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, fillBucket(stack, player, SCContent.fLavaBucket));
					}
					else
						return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
				}
			}
			else
			{
				boolean isReplaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
				BlockPos offsetPos = isReplaceable && rayTrace.sideHit == EnumFacing.UP ? pos : pos.offset(rayTrace.sideHit);

				if (!player.canPlayerEdit(offsetPos, rayTrace.sideHit, stack))
					return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
				else if (this.tryPlaceContainedLiquid(player, world, offsetPos))
				{
					player.addStat(StatList.getObjectUseStats(this));
					return !player.capabilities.isCreativeMode ? new ActionResult<ItemStack>(EnumActionResult.SUCCESS, new ItemStack(Items.BUCKET)) : new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
				else
					return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
		}
	}

	private ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket)
	{
		if (player.capabilities.isCreativeMode)
			return emptyBuckets;
		else if (--emptyBuckets.stackSize <= 0)
			return new ItemStack(fullBucket);
		else
		{
			if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket)))
				player.dropItem(new ItemStack(fullBucket, 1, 0), false);

			return emptyBuckets;
		}
	}

	public boolean tryPlaceContainedLiquid(World world, BlockPos pos)
	{
		if (containedBlock == Blocks.AIR)
			return false;
		else
		{
			Material material = world.getBlockState(pos).getMaterial();
			boolean isNotSolid = !material.isSolid();

			if (!world.isAirBlock(pos) && !isNotSolid)
				return false;
			else
			{
				if (world.provider.doesWaterVaporize() && containedBlock == Blocks.FLOWING_WATER)
				{
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();

					for(EntityPlayer player : world.playerEntities)
						world.playSound(player, new BlockPos(x + 0.5F, y + 0.5F, z + 0.5F), SoundEvent.REGISTRY.getObject(new ResourceLocation("random.fizz")), SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l)
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
				}
				else
				{
					if (!world.isRemote && isNotSolid && !material.isLiquid())
						world.destroyBlock(pos, true);

					world.setBlockState(pos, containedBlock.getDefaultState(), 3);
				}

				return true;
			}
		}
	}
}
