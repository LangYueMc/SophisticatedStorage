package net.p3pp3rf1y.sophisticatedstorage.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;
import net.p3pp3rf1y.sophisticatedstorage.client.particle.CustomTintTerrainParticle;
import net.p3pp3rf1y.sophisticatedstorage.client.particle.CustomTintTerrainParticleData;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.StorageContainerMenu;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedstorage.item.BarrelBlockItem;
import net.p3pp3rf1y.sophisticatedstorage.item.WoodStorageBlockItem;

import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class BarrelBlock extends WoodStorageBlockBase {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty FLAT_TOP = BooleanProperty.create("flat_top");
	private static final VoxelShape ITEM_ENTITY_COLLISION_SHAPE = box(0.05, 0.05, 0.05, 15.95, 15.95, 15.95);

	public BarrelBlock(Supplier<Integer> numberOfInventorySlotsSupplier, Supplier<Integer> numberOfUpgradeSlotsSupplier, Properties properties) {
		this(numberOfInventorySlotsSupplier, numberOfUpgradeSlotsSupplier, properties, stateDef -> stateDef.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(TICKING, false).setValue(FLAT_TOP, false));
	}

	public BarrelBlock(Supplier<Integer> numberOfInventorySlotsSupplier, Supplier<Integer> numberOfUpgradeSlotsSupplier, Properties properties, Function<StateDefinition<Block, BlockState>, BlockState> getDefaultState) {
		super(properties.noOcclusion(), numberOfInventorySlotsSupplier, numberOfUpgradeSlotsSupplier);
		registerDefaultState(getDefaultState.apply(stateDefinition));
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		super.addCreativeTabItems(itemConsumer);
		if (this != ModBlocks.BARREL) {
			return;
		}

		ItemStack flatBarrel = WoodStorageBlockItem.setWoodType(new ItemStack(this), WoodType.ACACIA);
		BarrelBlockItem.toggleFlatTop(flatBarrel);
		itemConsumer.accept(flatBarrel);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

	@Override
	public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		level.sendParticles(new CustomTintTerrainParticleData(state1, pos), entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15D);
		return true;
	}

	@Override
	public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
		Vec3 vec3 = entity.getDeltaMovement();
		level.addParticle(new CustomTintTerrainParticleData(state, pos),
				entity.getX() + (level.random.nextDouble() - 0.5D) * entity.getBbWidth(), entity.getY() + 0.1D, entity.getZ() + (level.random.nextDouble() - 0.5D) * entity.getBbWidth(),
				vec3.x * -4.0D, 1.5D, vec3.z * -4.0D);
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
		if (!(level instanceof ClientLevel clientLevel) || !(target instanceof BlockHitResult blockHitResult)) {
			return false;
		}
		Direction sideHit = blockHitResult.getDirection();
		BlockPos pos = blockHitResult.getBlockPos();
		if (state.getRenderShape() != RenderShape.INVISIBLE) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			AABB aabb = state.getShape(level, pos).bounds();
			Random random = new Random();
			double d0 = i + random.nextDouble() * (aabb.maxX - aabb.minX - 0.2F) + 0.1F + aabb.minX;
			double d1 = j + random.nextDouble() * (aabb.maxY - aabb.minY - 0.2F) + 0.1F + aabb.minY;
			double d2 = k + random.nextDouble() * (aabb.maxZ - aabb.minZ - 0.2F) + 0.1F + aabb.minZ;
			if (sideHit == Direction.DOWN) {
				d1 = j + aabb.minY - 0.1F;
			}

			if (sideHit == Direction.UP) {
				d1 = j + aabb.maxY + 0.1F;
			}

			if (sideHit == Direction.NORTH) {
				d2 = k + aabb.minZ - 0.1F;
			}

			if (sideHit == Direction.SOUTH) {
				d2 = k + aabb.maxZ + 0.1F;
			}

			if (sideHit == Direction.WEST) {
				d0 = i + aabb.minX - 0.1F;
			}

			if (sideHit == Direction.EAST) {
				d0 = i + aabb.maxX + 0.1F;
			}

			manager.add((new CustomTintTerrainParticle(clientLevel, d0, d1, d2, 0.0D, 0.0D, 0.0D, state, pos).updateSprite(state, pos)).setPower(0.2F).scale(0.6F));
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
		if (!(level instanceof ClientLevel clientLevel)) {
			return false;
		}

		VoxelShape voxelshape = state.getShape(level, pos);
		voxelshape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
			double d1 = Math.min(1.0D, maxX - minX);
			double d2 = Math.min(1.0D, maxY - minY);
			double d3 = Math.min(1.0D, maxZ - minZ);
			int i = Math.max(2, Mth.ceil(d1 / 0.25D));
			int j = Math.max(2, Mth.ceil(d2 / 0.25D));
			int k = Math.max(2, Mth.ceil(d3 / 0.25D));

			for (int l = 0; l < i; ++l) {
				for (int i1 = 0; i1 < j; ++i1) {
					for (int j1 = 0; j1 < k; ++j1) {
						double d4 = (l + 0.5D) / i;
						double d5 = (i1 + 0.5D) / j;
						double d6 = (j1 + 0.5D) / k;
						double d7 = d4 * d1 + minX;
						double d8 = d5 * d2 + minY;
						double d9 = d6 * d3 + minZ;
						manager.add(new CustomTintTerrainParticle(clientLevel, pos.getX() + d7, pos.getY() + d8, pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state, pos).updateSprite(state, pos));
					}
				}
			}
		});
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return WorldHelper.getBlockEntity(level, pos, WoodStorageBlockEntity.class).map(b -> {
			if (b.isPacked()) {
				return InteractionResult.PASS;
			}
			if (level.isClientSide || hand == InteractionHand.OFF_HAND) {
				return InteractionResult.SUCCESS;
			}

			ItemStack stackInHand = player.getItemInHand(hand);
			if (tryItemInteraction(player, hand, b, stackInHand, getFacing(state), hitResult)) {
				return InteractionResult.SUCCESS;
			}

			player.awardStat(Stats.OPEN_BARREL);

			player.openMenu(MenuProviderHelper.createMenuProvider((w, ctx, pl) -> instantiateContainerMenu(w, pl, pos), buffer -> buffer.writeBlockPos(pos),
					WorldHelper.getBlockEntity(level, pos, StorageBlockEntity.class).map(StorageBlockEntity::getDisplayName).orElse(Component.empty())));
			PiglinAi.angerNearbyPiglins(player, true);
			return InteractionResult.CONSUME;
		}).orElse(InteractionResult.PASS);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		WorldHelper.getBlockEntity(level, pos, BarrelBlockEntity.class).ifPresent(barrel -> {
			Map<BarrelMaterial, ResourceLocation> materials = BarrelBlockItem.getMaterials(stack);
			if (!materials.isEmpty()) {
				barrel.setMaterials(materials);
			}
		});
	}

	protected StorageContainerMenu instantiateContainerMenu(int w, Player pl, BlockPos pos) {
		return new StorageContainerMenu(w, pl, pos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		WorldHelper.getBlockEntity(level, pos, StorageBlockEntity.class).ifPresent(StorageBlockEntity::recheckOpen);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, TICKING, FLAT_TOP);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		ItemStack cloneItemStack = super.getCloneItemStack(level, pos, state);
		BarrelBlockItem.setFlatTop(cloneItemStack, state.getValue(FLAT_TOP));
		WorldHelper.getBlockEntity(level, pos, BarrelBlockEntity.class).ifPresent(barrelBlockEntity -> {
			Map<BarrelMaterial, ResourceLocation> materials = barrelBlockEntity.getMaterials();
			if (!materials.isEmpty()) {
				BarrelBlockItem.setMaterials(cloneItemStack, materials);
			}
		});
		return cloneItemStack;
	}

	@Override
	public void addDropData(ItemStack stack, StorageBlockEntity be) {
		super.addDropData(stack, be);
		BlockState state = be.getBlockState();
		BarrelBlockItem.setFlatTop(stack, state.getValue(FLAT_TOP));
		if (be instanceof BarrelBlockEntity barrelBlockEntity) {
			Map<BarrelMaterial, ResourceLocation> materials = barrelBlockEntity.getMaterials();
			if (!materials.isEmpty()) {
				BarrelBlockItem.setMaterials(stack, materials);
			}
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		return defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite()).setValue(FLAT_TOP, BarrelBlockItem.isFlatTop(blockPlaceContext.getItemInHand()));
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof ItemEntity || isCalledByCollisionCacheLogic(level, pos) ? ITEM_ENTITY_COLLISION_SHAPE : super.getCollisionShape(state, level, pos, context);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return Shapes.block();
	}

	private boolean isCalledByCollisionCacheLogic(BlockGetter level, BlockPos pos) {
		return level instanceof EmptyBlockGetter && pos == BlockPos.ZERO;
	}

	@Nullable
	@Override
	public StorageBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BarrelBlockEntity(pos, state);
	}

	@Override
	protected BlockEntityType<? extends StorageBlockEntity> getBlockEntityType() {
		return ModBlocks.BARREL_BLOCK_ENTITY_TYPE;
	}

	@Override
	public Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}
}
