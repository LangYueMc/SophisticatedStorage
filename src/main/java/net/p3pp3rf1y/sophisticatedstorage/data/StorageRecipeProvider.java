package net.p3pp3rf1y.sophisticatedstorage.data;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.p3pp3rf1y.sophisticatedcore.crafting.ShapeBasedRecipeBuilder;
import net.p3pp3rf1y.sophisticatedcore.crafting.ShapelessBasedRecipeBuilder;
import net.p3pp3rf1y.sophisticatedcore.init.ModRecipes;
import net.p3pp3rf1y.sophisticatedcore.util.RegistryHelper;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;
import net.p3pp3rf1y.sophisticatedstorage.block.WoodStorageBlockBase;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedstorage.init.ModItems;
import net.p3pp3rf1y.sophisticatedstorage.item.WoodStorageBlockItem;

import java.util.function.Consumer;

public class StorageRecipeProvider extends FabricRecipeProvider {
	private static final String HAS_UPGRADE_BASE_CRITERION_NAME = "has_upgrade_base";
	private static final String HAS_REDSTONE_TORCH_CRITERION_NAME = "has_redstone_torch";
	private static final String HAS_SMELTING_UPGRADE = "has_smelting_upgrade";
	private static final String PLANK_SUFFIX = "_plank";

	public StorageRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> consumer) {
		SpecialRecipeBuilder.special(ModBlocks.STORAGE_DYE_RECIPE_SERIALIZER).save(consumer, SophisticatedStorage.getRegistryName("storage_dye"));
		SpecialRecipeBuilder.special(ModBlocks.FLAT_TOP_BARREL_TOGGLE_RECIPE_SERIALIZER).save(consumer, SophisticatedStorage.getRegistryName("flat_top_barrel_toggle"));
		SpecialRecipeBuilder.special(ModBlocks.BARREL_MATERIAL_RECIPE_SERIALIZER).save(consumer, SophisticatedStorage.getRegistryName("barrel_material"));

		addBarrelRecipes(consumer);
		addLimitedBarrelRecipes(consumer);
		addChestRecipes(consumer);
		addShulkerBoxRecipes(consumer);
		addControllerRelatedRecipes(consumer);
		addUpgradeRecipes(consumer);
		addTierUpgradeItemRecipes(consumer);

		ShapelessBasedRecipeBuilder.shapeless(ModItems.PACKING_TAPE)
				.requires(Tags.Items.SLIMEBALLS)
				.requires(Items.PAPER)
				.unlockedBy("has_slime", has(Tags.Items.SLIMEBALLS))
				.save(consumer);
	}

	private void addLimitedBarrelRecipes(Consumer<FinishedRecipe> consumer) {
		WoodStorageBlockBase.CUSTOM_TEXTURE_WOOD_TYPES.forEach((woodType, blockFamily) -> {
			limitedWoodBarrel1Recipe(consumer, woodType, blockFamily.getBaseBlock(), blockFamily.get(BlockFamily.Variant.SLAB));
			limitedWoodBarrel2Recipe(consumer, woodType, blockFamily.getBaseBlock(), blockFamily.get(BlockFamily.Variant.SLAB));
			limitedWoodBarrel3Recipe(consumer, woodType, blockFamily.getBaseBlock(), blockFamily.get(BlockFamily.Variant.SLAB));
			limitedWoodBarrel4Recipe(consumer, woodType, blockFamily.getBaseBlock(), blockFamily.get(BlockFamily.Variant.SLAB));
		});

		addStorageTierUpgradeRecipes(consumer, ModBlocks.LIMITED_BARREL_1_ITEM, ModBlocks.LIMITED_IRON_BARREL_1_ITEM, ModBlocks.LIMITED_GOLD_BARREL_1_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_1_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_1_ITEM);
		addStorageTierUpgradeRecipes(consumer, ModBlocks.LIMITED_BARREL_2_ITEM, ModBlocks.LIMITED_IRON_BARREL_2_ITEM, ModBlocks.LIMITED_GOLD_BARREL_2_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_2_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_2_ITEM);
		addStorageTierUpgradeRecipes(consumer, ModBlocks.LIMITED_BARREL_3_ITEM, ModBlocks.LIMITED_IRON_BARREL_3_ITEM, ModBlocks.LIMITED_GOLD_BARREL_3_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_3_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_3_ITEM);
		addStorageTierUpgradeRecipes(consumer, ModBlocks.LIMITED_BARREL_4_ITEM, ModBlocks.LIMITED_IRON_BARREL_4_ITEM, ModBlocks.LIMITED_GOLD_BARREL_4_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_4_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_4_ITEM);
	}

	@SuppressWarnings("removal")
	private void addStorageTierUpgradeRecipes(Consumer<FinishedRecipe> consumer, BlockItem baseTierItem, BlockItem ironTierItem, BlockItem goldTierItem, BlockItem diamondTierItem, BlockItem netheriteTierItem) {
		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ironTierItem, ModBlocks.STORAGE_TIER_UPGRADE_RECIPE_SERIALIZER)
				.pattern("III")
				.pattern("ISI")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('S', baseTierItem)
				.unlockedBy("has_" + RegistryHelper.getItemKey(baseTierItem).getPath(), has(baseTierItem))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, goldTierItem, ModBlocks.STORAGE_TIER_UPGRADE_RECIPE_SERIALIZER)
				.pattern("GGG")
				.pattern("GSG")
				.pattern("GGG")
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('S', ironTierItem)
				.unlockedBy("has_iron_" + RegistryHelper.getItemKey(ironTierItem).getPath(), has(ironTierItem))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, diamondTierItem, ModBlocks.STORAGE_TIER_UPGRADE_RECIPE_SERIALIZER)
				.pattern("DDD")
				.pattern("DSD")
				.pattern("DDD")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('S', goldTierItem)
				.unlockedBy("has_gold_" + RegistryHelper.getItemKey(goldTierItem).getPath(), has(goldTierItem))
				.save(consumer);

		ShapelessBasedRecipeBuilder.shapeless(netheriteTierItem, ModBlocks.STORAGE_TIER_UPGRADE_SHAPELESS_RECIPE_SERIALIZER)
				.requires(Ingredient.of(diamondTierItem))
				.requires(Tags.Items.INGOTS_NETHERITE)
				.unlockedBy("has_diamond_" + RegistryHelper.getItemKey(baseTierItem).getPath(), has(diamondTierItem))
				.save(consumer, RegistryHelper.getItemKey(netheriteTierItem));
	}

	private void addControllerRelatedRecipes(Consumer<FinishedRecipe> consumer) {
		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CONTROLLER_ITEM)
				.pattern("SCS")
				.pattern("PBP")
				.pattern("SCS")
				.define('S', Tags.Items.STONE)
				.define('C', Items.COMPARATOR)
				.define('P', ItemTags.PLANKS)
				.define('B', ModBlocks.BASE_TIER_WOODEN_STORAGE_TAG)
				.unlockedBy("has_base_tier_wooden_storage", has(ModBlocks.BASE_TIER_WOODEN_STORAGE_TAG))
				.save(consumer);

		ShapelessBasedRecipeBuilder.shapeless(ModBlocks.STORAGE_LINK_ITEM, 3)
				.requires(ModBlocks.CONTROLLER_ITEM)
				.requires(Tags.Items.ENDER_PEARLS)
				.unlockedBy("has_controller", has(ModBlocks.CONTROLLER_ITEM))
				.save(consumer, SophisticatedStorage.getRL("storage_link_from_controller"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STORAGE_LINK_ITEM)
				.pattern("EP")
				.pattern("RS")
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('P', ItemTags.PLANKS)
				.define('R', Items.REPEATER)
				.define('S', Tags.Items.STONE)
				.unlockedBy("has_repeater", has(Items.REPEATER))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STORAGE_TOOL)
				.pattern(" EI")
				.pattern(" SR")
				.pattern("S  ")
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('S', Tags.Items.RODS_WOODEN)
				.define('R', Items.REDSTONE_TORCH)
				.unlockedBy(HAS_REDSTONE_TORCH_CRITERION_NAME, has(Items.REDSTONE_TORCH))
				.save(consumer);
	}

	private void addShulkerBoxRecipes(Consumer<FinishedRecipe> consumer) {
		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SHULKER_BOX_ITEM)
				.pattern(" S")
				.pattern("RC")
				.pattern(" S")
				.define('R', Items.REDSTONE_TORCH)
				.define('S', Items.SHULKER_SHELL)
				.define('C', Tags.Items.CHESTS)
				.unlockedBy("has_shulker_shell", has(Items.SHULKER_SHELL))
				.save(consumer);

		ShapelessBasedRecipeBuilder.shapeless(ModBlocks.SHULKER_BOX_ITEM)
				.requires(Items.SHULKER_BOX).requires(Items.REDSTONE_TORCH)
				.save(consumer, "shulker_box_from_vanilla_shulker_box");

		tintedShulkerBoxRecipe(consumer, Blocks.BLACK_SHULKER_BOX, DyeColor.BLACK);
		tintedShulkerBoxRecipe(consumer, Blocks.BLUE_SHULKER_BOX, DyeColor.BLUE);
		tintedShulkerBoxRecipe(consumer, Blocks.BROWN_SHULKER_BOX, DyeColor.BROWN);
		tintedShulkerBoxRecipe(consumer, Blocks.CYAN_SHULKER_BOX, DyeColor.CYAN);
		tintedShulkerBoxRecipe(consumer, Blocks.GRAY_SHULKER_BOX, DyeColor.GRAY);
		tintedShulkerBoxRecipe(consumer, Blocks.GREEN_SHULKER_BOX, DyeColor.GREEN);
		tintedShulkerBoxRecipe(consumer, Blocks.LIGHT_BLUE_SHULKER_BOX, DyeColor.LIGHT_BLUE);
		tintedShulkerBoxRecipe(consumer, Blocks.LIGHT_GRAY_SHULKER_BOX, DyeColor.LIGHT_GRAY);
		tintedShulkerBoxRecipe(consumer, Blocks.LIME_SHULKER_BOX, DyeColor.LIME);
		tintedShulkerBoxRecipe(consumer, Blocks.MAGENTA_SHULKER_BOX, DyeColor.MAGENTA);
		tintedShulkerBoxRecipe(consumer, Blocks.ORANGE_SHULKER_BOX, DyeColor.ORANGE);
		tintedShulkerBoxRecipe(consumer, Blocks.PINK_SHULKER_BOX, DyeColor.PINK);
		tintedShulkerBoxRecipe(consumer, Blocks.PURPLE_SHULKER_BOX, DyeColor.PURPLE);
		tintedShulkerBoxRecipe(consumer, Blocks.RED_SHULKER_BOX, DyeColor.RED);
		tintedShulkerBoxRecipe(consumer, Blocks.WHITE_SHULKER_BOX, DyeColor.WHITE);
		tintedShulkerBoxRecipe(consumer, Blocks.YELLOW_SHULKER_BOX, DyeColor.YELLOW);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SHULKER_BOX_ITEM, ModBlocks.SHULKER_BOX_FROM_CHEST_RECIPE_SERIALIZER)
				.pattern("S")
				.pattern("C")
				.pattern("S")
				.define('C', ModBlocks.CHEST_ITEM)
				.define('S', Items.SHULKER_SHELL)
				.unlockedBy("has_chest", has(ModBlocks.CHEST_ITEM))
				.save(consumer, SophisticatedStorage.getRL("shulker_from_chest"));

		addStorageTierUpgradeRecipes(consumer, ModBlocks.SHULKER_BOX_ITEM, ModBlocks.IRON_SHULKER_BOX_ITEM, ModBlocks.GOLD_SHULKER_BOX_ITEM, ModBlocks.DIAMOND_SHULKER_BOX_ITEM, ModBlocks.NETHERITE_SHULKER_BOX_ITEM);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.IRON_SHULKER_BOX_ITEM, ModBlocks.SHULKER_BOX_FROM_CHEST_RECIPE_SERIALIZER)
				.pattern("S")
				.pattern("C")
				.pattern("S")
				.define('C', ModBlocks.IRON_CHEST_ITEM)
				.define('S', Items.SHULKER_SHELL)
				.unlockedBy("has_iron_chest", has(ModBlocks.IRON_CHEST_ITEM))
				.save(consumer, SophisticatedStorage.getRL("iron_shulker_from_iron_chest"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GOLD_SHULKER_BOX_ITEM, ModBlocks.SHULKER_BOX_FROM_CHEST_RECIPE_SERIALIZER)
				.pattern("S")
				.pattern("C")
				.pattern("S")
				.define('C', ModBlocks.GOLD_CHEST_ITEM)
				.define('S', Items.SHULKER_SHELL)
				.unlockedBy("has_gold_chest", has(ModBlocks.GOLD_CHEST_ITEM))
				.save(consumer, SophisticatedStorage.getRL("gold_shulker_from_gold_chest"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DIAMOND_SHULKER_BOX_ITEM, ModBlocks.SHULKER_BOX_FROM_CHEST_RECIPE_SERIALIZER)
				.pattern("S")
				.pattern("C")
				.pattern("S")
				.define('C', ModBlocks.DIAMOND_CHEST_ITEM)
				.define('S', Items.SHULKER_SHELL)
				.unlockedBy("has_diamond_chest", has(ModBlocks.DIAMOND_CHEST_ITEM))
				.save(consumer, SophisticatedStorage.getRL("diamond_shulker_from_diamond_chest"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.NETHERITE_SHULKER_BOX_ITEM, ModBlocks.SHULKER_BOX_FROM_CHEST_RECIPE_SERIALIZER)
				.pattern("S")
				.pattern("C")
				.pattern("S")
				.define('C', ModBlocks.NETHERITE_CHEST_ITEM)
				.define('S', Items.SHULKER_SHELL)
				.unlockedBy("has_netherite_chest", has(ModBlocks.NETHERITE_CHEST_ITEM))
				.save(consumer, SophisticatedStorage.getRL("netherite_shulker_from_netherite_chest"));
	}

	private void addTierUpgradeItemRecipes(Consumer<FinishedRecipe> consumer) {
		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASIC_TIER_UPGRADE)
				.pattern(" S ")
				.pattern("SRS")
				.pattern(" S ")
				.define('R', Items.REDSTONE_TORCH)
				.define('S', Items.STICK)
				.unlockedBy(HAS_REDSTONE_TORCH_CRITERION_NAME, has(Items.REDSTONE_TORCH))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASIC_TO_IRON_TIER_UPGRADE)
				.pattern("III")
				.pattern("IRI")
				.pattern("III")
				.define('R', Items.REDSTONE_TORCH)
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy(HAS_REDSTONE_TORCH_CRITERION_NAME, has(Items.REDSTONE_TORCH))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASIC_TO_GOLD_TIER_UPGRADE)
				.pattern("GGG")
				.pattern("GTG")
				.pattern("GGG")
				.define('T', ModItems.BASIC_TO_IRON_TIER_UPGRADE)
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_basic_to_iron_tier_upgrade", has(ModItems.BASIC_TO_IRON_TIER_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASIC_TO_DIAMOND_TIER_UPGRADE)
				.pattern("DDD")
				.pattern("DTD")
				.pattern("DDD")
				.define('T', ModItems.BASIC_TO_GOLD_TIER_UPGRADE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_basic_to_gold_tier_upgrade", has(ModItems.BASIC_TO_GOLD_TIER_UPGRADE))
				.save(consumer);

		ShapelessBasedRecipeBuilder.shapeless(ModItems.BASIC_TO_NETHERITE_TIER_UPGRADE)
				.requires(ModItems.BASIC_TO_DIAMOND_TIER_UPGRADE)
				.requires(Tags.Items.INGOTS_NETHERITE)
				.unlockedBy("has_basic_to_diamond_tier_upgrade", has(ModItems.BASIC_TO_DIAMOND_TIER_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_TO_GOLD_TIER_UPGRADE)
				.pattern("GGG")
				.pattern("GRG")
				.pattern("GGG")
				.define('R', Items.REDSTONE_TORCH)
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy(HAS_REDSTONE_TORCH_CRITERION_NAME, has(Items.REDSTONE_TORCH))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_TO_DIAMOND_TIER_UPGRADE)
				.pattern("DDD")
				.pattern("DTD")
				.pattern("DDD")
				.define('T', ModItems.IRON_TO_GOLD_TIER_UPGRADE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_iron_to_gold_tier_upgrade", has(ModItems.IRON_TO_GOLD_TIER_UPGRADE))
				.save(consumer);

		ShapelessBasedRecipeBuilder.shapeless(ModItems.IRON_TO_NETHERITE_TIER_UPGRADE)
				.requires(ModItems.IRON_TO_DIAMOND_TIER_UPGRADE)
				.requires(Tags.Items.INGOTS_NETHERITE)
				.unlockedBy("has_iron_to_diamond_tier_upgrade", has(ModItems.IRON_TO_DIAMOND_TIER_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_TO_DIAMOND_TIER_UPGRADE)
				.pattern("DDD")
				.pattern("DRD")
				.pattern("DDD")
				.define('R', Items.REDSTONE_TORCH)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy(HAS_REDSTONE_TORCH_CRITERION_NAME, has(Items.REDSTONE_TORCH))
				.save(consumer);

		ShapelessBasedRecipeBuilder.shapeless(ModItems.GOLD_TO_NETHERITE_TIER_UPGRADE)
				.requires(ModItems.GOLD_TO_DIAMOND_TIER_UPGRADE)
				.requires(Tags.Items.INGOTS_NETHERITE)
				.unlockedBy("has_gold_to_diamond_tier_upgrade", has(ModItems.GOLD_TO_DIAMOND_TIER_UPGRADE))
				.save(consumer);

		ShapelessBasedRecipeBuilder.shapeless(ModItems.DIAMOND_TO_NETHERITE_TIER_UPGRADE)
				.requires(Items.REDSTONE_TORCH)
				.requires(Tags.Items.INGOTS_NETHERITE)
				.unlockedBy(HAS_REDSTONE_TORCH_CRITERION_NAME, has(Items.REDSTONE_TORCH))
				.save(consumer);
	}

	private void addUpgradeRecipes(Consumer<FinishedRecipe> consumer) {
		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UPGRADE_BASE)
				.pattern("PIP")
				.pattern("IPI")
				.pattern("PIP")
				.define('P', ItemTags.PLANKS)
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PICKUP_UPGRADE)
				.pattern(" P ")
				.pattern("LBL")
				.pattern("RRR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('L', ItemTags.PLANKS)
				.define('P', Blocks.STICKY_PISTON)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_PICKUP_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GPG")
				.pattern("RRR")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('P', ModItems.PICKUP_UPGRADE)
				.unlockedBy("has_pickup_upgrade", has(ModItems.PICKUP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FILTER_UPGRADE)
				.pattern("RSR")
				.pattern("SBS")
				.pattern("RSR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('S', Tags.Items.STRING)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_FILTER_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("GPG")
				.pattern("RRR")
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('P', ModItems.FILTER_UPGRADE)
				.unlockedBy("has_filter_upgrade", has(ModItems.FILTER_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MAGNET_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("EIE")
				.pattern("IPI")
				.pattern("R L")
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('L', Tags.Items.GEMS_LAPIS)
				.define('P', ModItems.PICKUP_UPGRADE)
				.unlockedBy("has_pickup_upgrade", has(ModItems.PICKUP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_MAGNET_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("EIE")
				.pattern("IPI")
				.pattern("R L")
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('L', Tags.Items.GEMS_LAPIS)
				.define('P', ModItems.ADVANCED_PICKUP_UPGRADE)
				.unlockedBy("has_advanced_pickup_upgrade", has(ModItems.ADVANCED_PICKUP_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_MAGNET_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GMG")
				.pattern("RRR")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('M', ModItems.MAGNET_UPGRADE)
				.unlockedBy("has_magnet_upgrade", has(ModItems.MAGNET_UPGRADE))
				.save(consumer, SophisticatedStorage.getRL("advanced_magnet_upgrade_from_basic"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FEEDING_UPGRADE)
				.pattern(" C ")
				.pattern("ABM")
				.pattern(" E ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Items.GOLDEN_CARROT)
				.define('A', Items.GOLDEN_APPLE)
				.define('M', Items.GLISTERING_MELON_SLICE)
				.define('E', Tags.Items.ENDER_PEARLS)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COMPACTING_UPGRADE)
				.pattern("IPI")
				.pattern("PBP")
				.pattern("RPR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('P', Items.PISTON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_COMPACTING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GCG")
				.pattern("RRR")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('C', ModItems.COMPACTING_UPGRADE)
				.unlockedBy("has_compacting_upgrade", has(ModItems.COMPACTING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VOID_UPGRADE)
				.pattern(" E ")
				.pattern("OBO")
				.pattern("ROR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('O', Tags.Items.OBSIDIAN)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_VOID_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('V', ModItems.VOID_UPGRADE)
				.unlockedBy("has_void_upgrade", has(ModItems.VOID_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SMELTING_UPGRADE)
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RFR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('F', Items.FURNACE)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUTO_SMELTING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('H', Items.HOPPER)
				.define('S', ModItems.SMELTING_UPGRADE)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CRAFTING_UPGRADE)
				.pattern(" T ")
				.pattern("IBI")
				.pattern(" C ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('C', Tags.Items.CHESTS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('T', Items.CRAFTING_TABLE)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONECUTTER_UPGRADE)
				.pattern(" S ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('S', Items.STONECUTTER)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STACK_UPGRADE_TIER_1)
				.pattern("LLL")
				.pattern("LBL")
				.pattern("LLL")
				.define('B', ModItems.UPGRADE_BASE)
				.define('L', ItemTags.LOGS)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STACK_UPGRADE_TIER_2)
				.pattern("III")
				.pattern("ISI")
				.pattern("BIB")
				.define('S', ModItems.STACK_UPGRADE_TIER_1)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', Tags.Items.STORAGE_BLOCKS_IRON)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.STACK_UPGRADE_TIER_1))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STACK_UPGRADE_TIER_3)
				.pattern("GGG")
				.pattern("GSG")
				.pattern("BGB")
				.define('S', ModItems.STACK_UPGRADE_TIER_2)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('B', Tags.Items.STORAGE_BLOCKS_GOLD)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.STACK_UPGRADE_TIER_2))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STACK_UPGRADE_TIER_4)
				.pattern("DDD")
				.pattern("DSD")
				.pattern("BDB")
				.define('S', ModItems.STACK_UPGRADE_TIER_3)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('B', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.STACK_UPGRADE_TIER_3))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.JUKEBOX_UPGRADE)
				.pattern(" J ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('J', Items.JUKEBOX)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_FEEDING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GVG")
				.pattern("RRR")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('V', ModItems.FEEDING_UPGRADE)
				.unlockedBy("has_feeding_upgrade", has(ModItems.FEEDING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SMOKING_UPGRADE)
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RSR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('S', Items.SMOKER)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SMOKING_UPGRADE)
				.pattern(" L ")
				.pattern("LSL")
				.pattern(" L ")
				.define('S', ModItems.SMELTING_UPGRADE)
				.define('L', ItemTags.LOGS)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE))
				.save(consumer, SophisticatedStorage.getRL("smoking_upgrade_from_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUTO_SMOKING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('H', Items.HOPPER)
				.define('S', ModItems.SMOKING_UPGRADE)
				.unlockedBy("has_smoking_upgrade", has(ModItems.SMOKING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUTO_SMOKING_UPGRADE)
				.pattern(" L ")
				.pattern("LSL")
				.pattern(" L ")
				.define('S', ModItems.AUTO_SMELTING_UPGRADE)
				.define('L', ItemTags.LOGS)
				.unlockedBy("has_auto_smelting_upgrade", has(ModItems.AUTO_SMELTING_UPGRADE))
				.save(consumer, SophisticatedStorage.getRL("auto_smoking_upgrade_from_auto_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLASTING_UPGRADE)
				.pattern("RIR")
				.pattern("IBI")
				.pattern("RFR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('F', Items.BLAST_FURNACE)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLASTING_UPGRADE)
				.pattern("III")
				.pattern("ISI")
				.pattern("TTT")
				.define('S', ModItems.SMELTING_UPGRADE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('T', Items.SMOOTH_STONE)
				.unlockedBy(HAS_SMELTING_UPGRADE, has(ModItems.SMELTING_UPGRADE))
				.save(consumer, SophisticatedStorage.getRL("blasting_upgrade_from_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUTO_BLASTING_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern("DHD")
				.pattern("RSH")
				.pattern("GHG")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('H', Items.HOPPER)
				.define('S', ModItems.BLASTING_UPGRADE)
				.unlockedBy("has_blasting_upgrade", has(ModItems.BLASTING_UPGRADE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUTO_BLASTING_UPGRADE)
				.pattern("III")
				.pattern("ISI")
				.pattern("TTT")
				.define('S', ModItems.AUTO_SMELTING_UPGRADE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('T', Items.SMOOTH_STONE)
				.unlockedBy("has_auto_smelting_upgrade", has(ModItems.AUTO_SMELTING_UPGRADE))
				.save(consumer, SophisticatedStorage.getRL("auto_blasting_upgrade_from_auto_smelting_upgrade"));

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COMPRESSION_UPGRADE)
				.pattern(" I ")
				.pattern("PBP")
				.pattern("RIR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('P', Items.PISTON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HOPPER_UPGRADE)
				.pattern(" H ")
				.pattern("IBI")
				.pattern("RRR")
				.define('B', ModItems.UPGRADE_BASE)
				.define('H', Items.HOPPER)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE))
				.save(consumer);

		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_HOPPER_UPGRADE, ModRecipes.UPGRADE_NEXT_TIER_SERIALIZER)
				.pattern(" D ")
				.pattern("GHG")
				.pattern("ROR")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('G', Tags.Items.INGOTS_GOLD)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('O', Items.DROPPER)
				.define('H', ModItems.HOPPER_UPGRADE)
				.unlockedBy("has_feeding_upgrade", has(ModItems.HOPPER_UPGRADE))
				.save(consumer);

		// addChippedUpgradeRecipes(consumer);
	}

	/*private static void addChippedUpgradeRecipes(Consumer<FinishedRecipe> consumer) {
		addChippedUpgradeRecipe(consumer, ChippedCompat.BOTANIST_WORKBENCH_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.BOTANIST_WORKBENCH.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.GLASSBLOWER_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.GLASSBLOWER.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.CARPENTERS_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.CARPENTERS_TABLE.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.LOOM_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.LOOM_TABLE.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.MASON_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.MASON_TABLE.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.ALCHEMY_BENCH_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.ALCHEMY_BENCH.get());
		addChippedUpgradeRecipe(consumer, ChippedCompat.TINKERING_TABLE_UPGRADE.get(), earth.terrarium.chipped.common.registry.ModBlocks.TINKERING_TABLE.get());
	}

	private static void addChippedUpgradeRecipe(Consumer<FinishedRecipe> consumer, BlockTransformationUpgradeItem upgrade, Block workbench) {
		ShapeBasedRecipeBuilder.shaped(upgrade)
				.pattern(" W ")
				.pattern("IBI")
				.pattern(" R ")
				.define('B', ModItems.UPGRADE_BASE.get())
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('W', workbench)
				.unlockedBy(HAS_UPGRADE_BASE_CRITERION_NAME, has(ModItems.UPGRADE_BASE.get()))
				.condition(new ModLoadedCondition(CompatModIds.CHIPPED))
				.save(consumer);
	}*/

	private void addChestRecipes(Consumer<FinishedRecipe> consumer) {
		WoodStorageBlockBase.CUSTOM_TEXTURE_WOOD_TYPES.forEach((woodType, blockFamily) -> woodChestRecipe(consumer, woodType, blockFamily.getBaseBlock()));

		ShapelessBasedRecipeBuilder.shapeless(WoodStorageBlockItem.setWoodType(new ItemStack(ModBlocks.CHEST_ITEM), WoodType.OAK))
				.requires(Blocks.CHEST)
				.requires(Blocks.REDSTONE_TORCH)
				.unlockedBy("has_vanilla_chest", has(Blocks.CHEST))
				.save(consumer, SophisticatedStorage.getRL("oak_chest_from_vanilla_chest"));

		addStorageTierUpgradeRecipes(consumer, ModBlocks.CHEST_ITEM, ModBlocks.IRON_CHEST_ITEM, ModBlocks.GOLD_CHEST_ITEM, ModBlocks.DIAMOND_CHEST_ITEM, ModBlocks.NETHERITE_CHEST_ITEM);

//		addQuarkChestRecipes(consumer);
	}

/*	private void addQuarkChestRecipes(Consumer<FinishedRecipe> consumer) {
		addQuarkChestRecipe(consumer, "oak_chest", WoodType.OAK);
		addQuarkChestRecipe(consumer, "acacia_chest", WoodType.ACACIA);
		addQuarkChestRecipe(consumer, "birch_chest", WoodType.BIRCH);
		addQuarkChestRecipe(consumer, "crimson_chest", WoodType.CRIMSON);
		addQuarkChestRecipe(consumer, "dark_oak_chest", WoodType.DARK_OAK);
		addQuarkChestRecipe(consumer, "jungle_chest", WoodType.JUNGLE);
		addQuarkChestRecipe(consumer, "mangrove_chest", WoodType.MANGROVE);
		addQuarkChestRecipe(consumer, "spruce_chest", WoodType.SPRUCE);
		addQuarkChestRecipe(consumer, "warped_chest", WoodType.WARPED);
	}

	private void addQuarkChestRecipe(Consumer<FinishedRecipe> consumer, String name, WoodType woodType) {
		String chestRegistryName = "quark:" + name;
		Block chestBlock = getBlock(chestRegistryName);
		ShapelessBasedRecipeBuilder.shapeless(RecipeCategory.MISC, WoodStorageBlockItem.setWoodType(new ItemStack(ModBlocks.CHEST_ITEM), woodType))
				.requires(chestBlock)
				.requires(Blocks.REDSTONE_TORCH)
				.condition(new ItemExistsCondition(chestRegistryName))
				.save(consumer, SophisticatedStorage.getRL(woodType.name() + "_chest_from_quark_" + name));
	}

	private Block getBlock(String registryName) {
		//noinspection ConstantConditions - could only fail in dev environment and crashing is preferred here to fix issues early
		return BuiltInRegistries.BLOCK.get(new ResourceLocation(registryName));
	}*/

	private void addBarrelRecipes(Consumer<FinishedRecipe> consumer) {
		WoodStorageBlockBase.CUSTOM_TEXTURE_WOOD_TYPES.forEach((woodType, blockFamily) -> woodBarrelRecipe(consumer, woodType, blockFamily.getBaseBlock(), blockFamily.get(BlockFamily.Variant.SLAB)));

		ShapelessBasedRecipeBuilder.shapeless(WoodStorageBlockItem.setWoodType(new ItemStack(ModBlocks.BARREL_ITEM), WoodType.SPRUCE))
				.requires(Blocks.BARREL)
				.requires(Blocks.REDSTONE_TORCH)
				.unlockedBy("has_vanilla_barrel", has(Blocks.BARREL))
				.save(consumer, SophisticatedStorage.getRL("spruce_barrel_from_vanilla_barrel"));

		addStorageTierUpgradeRecipes(consumer, ModBlocks.BARREL_ITEM, ModBlocks.IRON_BARREL_ITEM, ModBlocks.GOLD_BARREL_ITEM, ModBlocks.DIAMOND_BARREL_ITEM, ModBlocks.NETHERITE_BARREL_ITEM);
	}

	private void woodBarrelRecipe(Consumer<FinishedRecipe> consumer, WoodType woodType, Block planks, Block slab) {
		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, WoodStorageBlockItem.setWoodType(new ItemStack(ModBlocks.BARREL_ITEM), woodType))
				.pattern("PSP")
				.pattern("PRP")
				.pattern("PSP")
				.define('P', planks)
				.define('S', slab)
				.define('R', Blocks.REDSTONE_TORCH)
				.unlockedBy("has_" + woodType.name() + PLANK_SUFFIX, has(planks))
				.save(consumer, SophisticatedStorage.getRL(woodType.name() + "_barrel"));
	}

	private void limitedWoodBarrelRecipe(Consumer<FinishedRecipe> consumer, WoodType woodType, Block planks, Block slab, Consumer<ShapeBasedRecipeBuilder> addPattern, BlockItem item) {
		ShapeBasedRecipeBuilder builder = ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, WoodStorageBlockItem.setWoodType(new ItemStack(item), woodType))
				.define('P', planks)
				.define('S', slab)
				.define('R', Blocks.REDSTONE_TORCH)
				.unlockedBy("has_" + woodType.name() + PLANK_SUFFIX, has(planks));
		addPattern.accept(builder);
		builder.save(consumer, SophisticatedStorage.getRL(woodType.name() + "_" + RegistryHelper.getItemKey(item).getPath()));
	}

	private void limitedWoodBarrel1Recipe(Consumer<FinishedRecipe> consumer, WoodType woodType, Block planks, Block slab) {
		limitedWoodBarrelRecipe(consumer, woodType, planks, slab, builder ->
						builder.pattern("PSP")
								.pattern("PRP")
								.pattern("PPP")
				, ModBlocks.LIMITED_BARREL_1_ITEM);
	}

	private void limitedWoodBarrel2Recipe(Consumer<FinishedRecipe> consumer, WoodType woodType, Block planks, Block slab) {
		limitedWoodBarrelRecipe(consumer, woodType, planks, slab, builder ->
						builder.pattern("PPP")
								.pattern("SRS")
								.pattern("PPP")
				, ModBlocks.LIMITED_BARREL_2_ITEM);
	}

	private void limitedWoodBarrel3Recipe(Consumer<FinishedRecipe> consumer, WoodType woodType, Block planks, Block slab) {
		limitedWoodBarrelRecipe(consumer, woodType, planks, slab, builder ->
						builder.pattern("PSP")
								.pattern("PRP")
								.pattern("SPS")
				, ModBlocks.LIMITED_BARREL_3_ITEM);
	}

	private void limitedWoodBarrel4Recipe(Consumer<FinishedRecipe> consumer, WoodType woodType, Block planks, Block slab) {
		limitedWoodBarrelRecipe(consumer, woodType, planks, slab, builder ->
						builder.pattern("SPS")
								.pattern("PRP")
								.pattern("SPS")
				, ModBlocks.LIMITED_BARREL_4_ITEM);
	}

	private void woodChestRecipe(Consumer<FinishedRecipe> consumer, WoodType woodType, Block planks) {
		ShapeBasedRecipeBuilder.shaped(RecipeCategory.MISC, WoodStorageBlockItem.setWoodType(new ItemStack(ModBlocks.CHEST_ITEM), woodType))
				.pattern("PPP")
				.pattern("PRP")
				.pattern("PPP")
				.define('P', planks)
				.define('R', Blocks.REDSTONE_TORCH)
				.unlockedBy("has_" + woodType.name() + PLANK_SUFFIX, has(planks))
				.save(consumer, SophisticatedStorage.getRL(woodType.name() + "_chest"));
	}

	private void tintedShulkerBoxRecipe(Consumer<FinishedRecipe> consumer, Block vanillaShulkerBox, DyeColor dyeColor) {
		//noinspection ConstantConditions
		String vanillaShulkerBoxName = BuiltInRegistries.BLOCK.getKey(vanillaShulkerBox).getPath();
		ShapelessBasedRecipeBuilder.shapeless(ModBlocks.SHULKER_BOX.getTintedStack(dyeColor)).requires(vanillaShulkerBox).requires(Items.REDSTONE_TORCH)
				.unlockedBy("has_" + vanillaShulkerBoxName, has(vanillaShulkerBox))
				.save(consumer, SophisticatedStorage.getRL(vanillaShulkerBoxName + "_to_sophisticated"));
	}
}
