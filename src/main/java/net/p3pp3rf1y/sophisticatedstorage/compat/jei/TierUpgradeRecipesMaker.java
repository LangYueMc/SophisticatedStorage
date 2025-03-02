package net.p3pp3rf1y.sophisticatedstorage.compat.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.p3pp3rf1y.sophisticatedcore.compat.common.ClientRecipeHelper;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;
import net.p3pp3rf1y.sophisticatedstorage.crafting.StorageTierUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedstorage.crafting.StorageTierUpgradeShapelessRecipe;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageBlockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class TierUpgradeRecipesMaker {
	private TierUpgradeRecipesMaker() {}

	public static List<CraftingRecipe> getShapedCraftingRecipes() {
		RecipeConstructor<StorageTierUpgradeRecipe> constructRecipe = (originalRecipe, id, ingredients, result) -> new ShapedRecipe(id, "", CraftingBookCategory.MISC, originalRecipe.getWidth(), originalRecipe.getHeight(), ingredients, result);
		return getCraftingRecipes(constructRecipe, StorageTierUpgradeRecipe.REGISTERED_RECIPES, StorageTierUpgradeRecipe.class);
	}

	public static List<CraftingRecipe> getShapelessCraftingRecipes() {
		RecipeConstructor<StorageTierUpgradeShapelessRecipe> constructRecipe = (originalRecipe, id, ingredients, result) -> new ShapelessRecipe(id, "", CraftingBookCategory.MISC, result, ingredients);
		return getCraftingRecipes(constructRecipe, StorageTierUpgradeShapelessRecipe.REGISTERED_RECIPES, StorageTierUpgradeShapelessRecipe.class);
	}

	@NotNull
	private static <T extends CraftingRecipe> List<CraftingRecipe> getCraftingRecipes(RecipeConstructor<T> constructRecipe, Set<ResourceLocation> registeredRecipes, Class<T> originalRecipeClass) {
		return ClientRecipeHelper.getAndTransformAvailableItemGroupRecipes(registeredRecipes, originalRecipeClass, recipe -> {
			List<CraftingRecipe> itemGroupRecipes = new ArrayList<>();
			getStorageItems(recipe).forEach(storageItem -> {
				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				CraftingContainer craftinginventory = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
					@Override
					public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
						return ItemStack.EMPTY;
					}

					public boolean stillValid(Player playerIn) {
						return false;
					}
				}, 3, 3);

				NonNullList<Ingredient> ingredientsCopy = NonNullList.createWithCapacity(ingredients.size());
				int i = 0;
				for (Ingredient ingredient : ingredients) {
					ItemStack[] ingredientItems = ingredient.getItems();
					if (ingredientItems.length == 1 && storageItem.getItem() == ingredientItems[0].getItem()) {
						ingredientsCopy.add(i, Ingredient.of(storageItem));
						craftinginventory.setItem(i, storageItem.copy());
					} else {
						ingredientsCopy.add(i, ingredient);
						craftinginventory.setItem(i, ingredientItems[0]);
					}
					i++;
				}

				ItemStack result = ClientRecipeHelper.assemble(recipe, craftinginventory);
				//noinspection ConstantConditions
				ResourceLocation id = new ResourceLocation(SophisticatedStorage.ID, "tier_upgrade_" + BuiltInRegistries.ITEM.getKey(storageItem.getItem()).getPath() + result.getOrCreateTag().toString().toLowerCase(Locale.ROOT).replaceAll("[{\",}: ]", "_"));
				itemGroupRecipes.add(constructRecipe.construct(recipe, id, ingredientsCopy, result));
			});
			return itemGroupRecipes;
		});
	}

	private static List<ItemStack> getStorageItems(CraftingRecipe recipe) {
		NonNullList<ItemStack> storageItems = NonNullList.create();
		for (Ingredient ingredient : recipe.getIngredients()) {
			ItemStack[] ingredientItems = ingredient.getItems();

			for (ItemStack ingredientItem : ingredientItems) {
				Item item = ingredientItem.getItem();
				if (item instanceof StorageBlockItem storageBlockItem) {
					storageBlockItem.addCreativeTabItems(storageItems::add);
				}
			}
		}

		return storageItems;
	}

	private interface RecipeConstructor<T extends Recipe<?>> {
		CraftingRecipe construct(T originalRecipe, ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result);
	}
}
