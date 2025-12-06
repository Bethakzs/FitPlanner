package com.example.sonya.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum FoodItem {
    CHICKEN_BREAST("Chicken Breast", FoodCategory.PROTEIN),
    TURKEY("Turkey", FoodCategory.PROTEIN),
    LEAN_BEEF("Lean Beef", FoodCategory.PROTEIN),
    SALMON("Salmon", FoodCategory.PROTEIN, AllergyType.SEAFOOD),
    TUNA("Tuna", FoodCategory.PROTEIN, AllergyType.SEAFOOD),
    EGGS("Eggs", FoodCategory.PROTEIN, AllergyType.EGGS),
    GREEK_YOGURT("Greek Yogurt", FoodCategory.PROTEIN, AllergyType.LACTOSE),
    COTTAGE_CHEESE("Cottage Cheese", FoodCategory.PROTEIN, AllergyType.LACTOSE),
    TOFU("Tofu", FoodCategory.PROTEIN, AllergyType.SOY),
    TEMPEH("Tempeh", FoodCategory.PROTEIN, AllergyType.SOY),
    LENTILS("Lentils", FoodCategory.PROTEIN),
    CHICKPEAS("Chickpeas", FoodCategory.PROTEIN),
    QUINOA("Quinoa", FoodCategory.PROTEIN),
    BLACK_BEANS("Black Beans", FoodCategory.PROTEIN),
    PORK_TENDERLOIN("Pork Tenderloin", FoodCategory.PROTEIN),

    AVOCADO("Avocado", FoodCategory.FATS),
    OLIVE_OIL("Olive Oil", FoodCategory.FATS),
    ALMONDS("Almonds", FoodCategory.FATS, AllergyType.NUTS),
    WALNUTS("Walnuts", FoodCategory.FATS, AllergyType.NUTS),
    CASHEWS("Cashews", FoodCategory.FATS, AllergyType.NUTS),
    PEANUT_BUTTER("Peanut Butter", FoodCategory.FATS, AllergyType.NUTS),
    CHIA_SEEDS("Chia Seeds", FoodCategory.FATS),
    FLAX_SEEDS("Flax Seeds", FoodCategory.FATS),
    COCONUT_OIL("Coconut Oil", FoodCategory.FATS),
    DARK_CHOCOLATE("Dark Chocolate (70%+)", FoodCategory.FATS),
    BUTTER("Butter", FoodCategory.FATS, AllergyType.LACTOSE),
    CHEESE("Cheese", FoodCategory.FATS, AllergyType.LACTOSE),
    MACKEREL("Mackerel", FoodCategory.FATS, AllergyType.SEAFOOD),
    SARDINES("Sardines", FoodCategory.FATS, AllergyType.SEAFOOD),
    PUMPKIN_SEEDS("Pumpkin Seeds", FoodCategory.FATS),

    BROWN_RICE("Brown Rice", FoodCategory.CARBS),
    OATMEAL("Oatmeal", FoodCategory.CARBS),
    WHOLE_WHEAT_BREAD("Whole Wheat Bread", FoodCategory.CARBS, AllergyType.GLUTEN),
    WHOLE_WHEAT_PASTA("Whole Wheat Pasta", FoodCategory.CARBS, AllergyType.GLUTEN),
    SWEET_POTATO("Sweet Potato", FoodCategory.CARBS),
    REGULAR_POTATO("Potato", FoodCategory.CARBS),
    QUINOA_CARB("Quinoa", FoodCategory.CARBS),
    BUCKWHEAT("Buckwheat", FoodCategory.CARBS),
    BANANA("Banana", FoodCategory.CARBS),
    APPLE("Apple", FoodCategory.CARBS),
    BERRIES("Berries (mixed)", FoodCategory.CARBS),
    BROCCOLI("Broccoli", FoodCategory.CARBS),
    SPINACH("Spinach", FoodCategory.CARBS),
    CARROTS("Carrots", FoodCategory.CARBS),
    BEANS("Beans", FoodCategory.CARBS);

    private final String displayName;
    private final FoodCategory category;
    private final Set<AllergyType> allergens;

    FoodItem(String displayName, FoodCategory category, AllergyType... allergens) {
        this.displayName = displayName;
        this.category = category;
        this.allergens = Set.of(allergens);
    }

    public static List<FoodItem> getByCategory(FoodCategory category) {
        return Arrays.stream(values())
                .filter(item -> item.category == category)
                .collect(Collectors.toList());
    }

    public static List<FoodItem> getByCategoryFiltered(FoodCategory category, Set<AllergyType> userAllergies) {
        if (userAllergies == null || userAllergies.isEmpty()) {
            return getByCategory(category);
        }

        return Arrays.stream(values())
                .filter(item -> item.category == category)
                .filter(item -> {
                    for (AllergyType allergy : userAllergies) {
                        if (item.allergens.contains(allergy)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public boolean hasAllergen(AllergyType allergen) {
        return allergens.contains(allergen);
    }
}

