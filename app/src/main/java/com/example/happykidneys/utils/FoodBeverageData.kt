package com.example.happykidneys.utils

import com.example.happykidneys.data.database.entities.FoodBeverage

/**
 * Food and Beverage water content data based on:
 * - USDA FoodData Central Database
 * - Scientific nutritional research
 * - WHO guidelines
 */
object FoodBeverageData {

    fun getAllItems(): List<FoodBeverage> {
        return listOf(
            // BEVERAGES (95-100% water)
            // Beverages get volume measurements
            FoodBeverage(0, "Water", "ماء", "Eau", "beverage", 100f, "ic_water_drop", "250ml", 0.25f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Coffee", "قهوة", "Café", "beverage", 99.5f, "ic_coffee", "240ml", 0.24f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Tea (unsweetened)", "شاي", "Thé", "beverage", 99.7f, "ic_tea", "240ml", 0.24f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Green Tea", "شاي أخضر", "Thé vert", "beverage", 99.9f, "ic_tea", "240ml", 0.24f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Sparkling Water", "ماء فوار", "Eau gazeuse", "beverage", 99.9f, "ic_water_drop", "250ml", 0.25f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Coconut Water", "ماء جوز الهند", "Eau de coco", "beverage", 95f, "ic_coconut", "240ml", 0.23f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Sports Drink", "مشروب رياضي", "Boisson sportive", "beverage", 94f, "ic_fitness", "240ml", 0.225f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),

            // FRUIT JUICES (85-90% water)
            // Also beverages, get volume measurements
            FoodBeverage(0, "Orange Juice", "عصير برتقال", "Jus d'orange", "beverage", 88f, "ic_orange", "240ml", 0.21f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Apple Juice", "عصير تفاح", "Jus de pomme", "beverage", 88f, "ic_apple", "240ml", 0.21f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Grape Juice", "عصير عنب", "Jus de raisin", "beverage", 84f, "ic_grape", "240ml", 0.20f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Watermelon Juice", "عصير بطيخ", "Jus de pastèque", "beverage", 92f, "ic_watermelon", "240ml", 0.22f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),

            // MILK & ALTERNATIVES (85-91% water)
            // Also beverages, get volume measurements
            FoodBeverage(0, "Milk (whole)", "حليب كامل الدسم", "Lait entier", "beverage", 88f, "ic_milk", "240ml", 0.21f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Milk (skim)", "حليب خالي الدسم", "Lait écrémé", "beverage", 91f, "ic_milk", "240ml", 0.22f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Almond Milk", "حليب اللوز", "Lait d'amande", "beverage", 96f, "ic_milk", "240ml", 0.23f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Soy Milk", "حليب الصويا", "Lait de soja", "beverage", 93f, "ic_milk", "240ml", 0.22f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),

            // HIGH WATER FRUITS (90-96% water)
            // Fruits get weight measurements
            FoodBeverage(0, "Watermelon", "بطيخ", "Pastèque", "fruit", 92f, "ic_watermelon", "1 cup (154g)", 0.14f,
                weightSmall_g = 100f, weightMedium_g = 154f, weightLarge_g = 200f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Strawberries", "فراولة", "Fraises", "fruit", 91f, "ic_strawberry", "1 cup (152g)", 0.14f,
                weightSmall_g = 100f, weightMedium_g = 152f, weightLarge_g = 200f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Cantaloupe", "شمام", "Cantaloup", "fruit", 90f, "ic_melon", "1 cup (177g)", 0.16f,
                weightSmall_g = 120f, weightMedium_g = 177f, weightLarge_g = 220f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Peaches", "خوخ", "Pêches", "fruit", 89f, "ic_peach", "1 medium (150g)", 0.13f,
                weightSmall_g = 100f, weightMedium_g = 150f, weightLarge_g = 200f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Oranges", "برتقال", "Oranges", "fruit", 87f, "ic_orange", "1 medium (131g)", 0.11f,
                weightSmall_g = 90f, weightMedium_g = 131f, weightLarge_g = 180f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Grapefruit", "جريب فروت", "Pamplemousse", "fruit", 91f, "ic_grapefruit", "1/2 fruit (123g)", 0.11f,
                weightSmall_g = 80f, weightMedium_g = 123f, weightLarge_g = 160f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Pineapple", "أناناس", "Ananas", "fruit", 86f, "ic_pineapple", "1 cup (165g)", 0.14f,
                weightSmall_g = 100f, weightMedium_g = 165f, weightLarge_g = 220f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Grapes", "عنب", "Raisins", "fruit", 81f, "ic_grape", "1 cup (151g)", 0.12f,
                weightSmall_g = 100f, weightMedium_g = 151f, weightLarge_g = 200f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Apples", "تفاح", "Pommes", "fruit", 84f, "ic_apple", "1 medium (182g)", 0.15f,
                weightSmall_g = 120f, weightMedium_g = 182f, weightLarge_g = 240f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Pears", "كمثرى", "Poires", "fruit", 84f, "ic_pear", "1 medium (178g)", 0.15f,
                weightSmall_g = 120f, weightMedium_g = 178f, weightLarge_g = 220f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Plums", "برقوق", "Prunes", "fruit", 87f, "ic_plum", "1 medium (66g)", 0.06f,
                weightSmall_g = 50f, weightMedium_g = 66f, weightLarge_g = 80f, weightUnit_g = 1f, volumeUnit_ml = null),

            // MEDIUM WATER FRUITS (75-85% water)
            FoodBeverage(0, "Blueberries", "توت أزرق", "Myrtilles", "fruit", 84f, "ic_blueberry", "1 cup (148g)", 0.12f,
                weightSmall_g = 100f, weightMedium_g = 148f, weightLarge_g = 200f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Cherries", "كرز", "Cerises", "fruit", 82f, "ic_cherry", "1 cup (138g)", 0.11f,
                weightSmall_g = 100f, weightMedium_g = 138f, weightLarge_g = 180f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Mango", "مانجو", "Mangue", "fruit", 83f, "ic_mango", "1 cup (165g)", 0.14f,
                weightSmall_g = 100f, weightMedium_g = 165f, weightLarge_g = 220f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Papaya", "بابايا", "Papaye", "fruit", 88f, "ic_papaya", "1 cup (140g)", 0.12f,
                weightSmall_g = 100f, weightMedium_g = 140f, weightLarge_g = 180f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Kiwi", "كيوي", "Kiwi", "fruit", 83f, "ic_kiwi", "1 medium (69g)", 0.06f,
                weightSmall_g = 50f, weightMedium_g = 69f, weightLarge_g = 90f, weightUnit_g = 1f, volumeUnit_ml = null),

            // HIGH WATER VEGETABLES (92-96% water)
            // Vegetables get weight measurements
            FoodBeverage(0, "Cucumber", "خيار", "Concombre", "vegetable", 96f, "ic_cucumber", "1 cup (104g)", 0.10f,
                weightSmall_g = 50f, weightMedium_g = 104f, weightLarge_g = 150f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Lettuce", "خس", "Laitue", "vegetable", 96f, "ic_lettuce", "1 cup (47g)", 0.045f,
                weightSmall_g = 30f, weightMedium_g = 47f, weightLarge_g = 60f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Celery", "كرفس", "Céleri", "vegetable", 95f, "ic_celery", "1 cup (101g)", 0.096f,
                weightSmall_g = 50f, weightMedium_g = 101f, weightLarge_g = 150f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Tomatoes", "طماطم", "Tomates", "vegetable", 94f, "ic_tomato", "1 medium (123g)", 0.12f,
                weightSmall_g = 80f, weightMedium_g = 123f, weightLarge_g = 180f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Zucchini", "كوسة", "Courgette", "vegetable", 95f, "ic_zucchini", "1 cup (124g)", 0.12f,
                weightSmall_g = 80f, weightMedium_g = 124f, weightLarge_g = 180f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Radishes", "فجل", "Radis", "vegetable", 95f, "ic_radish", "1 cup (116g)", 0.11f,
                weightSmall_g = 80f, weightMedium_g = 116f, weightLarge_g = 150f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Bell Peppers", "فلفل حلو", "Poivrons", "vegetable", 92f, "ic_pepper", "1 cup (149g)", 0.14f,
                weightSmall_g = 100f, weightMedium_g = 149f, weightLarge_g = 200f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Cauliflower", "قرنبيط", "Chou-fleur", "vegetable", 92f, "ic_cauliflower", "1 cup (107g)", 0.10f,
                weightSmall_g = 80f, weightMedium_g = 107f, weightLarge_g = 150f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Spinach", "سبانخ", "Épinards", "vegetable", 91f, "ic_spinach", "1 cup (30g)", 0.027f,
                weightSmall_g = 20f, weightMedium_g = 30f, weightLarge_g = 50f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Broccoli", "بروكلي", "Brocoli", "vegetable", 89f, "ic_broccoli", "1 cup (91g)", 0.081f,
                weightSmall_g = 50f, weightMedium_g = 91f, weightLarge_g = 120f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Cabbage", "ملفوف", "Chou", "vegetable", 92f, "ic_cabbage", "1 cup (89g)", 0.082f,
                weightSmall_g = 50f, weightMedium_g = 89f, weightLarge_g = 120f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Carrots", "جزر", "Carottes", "vegetable", 88f, "ic_carrot", "1 cup (128g)", 0.11f,
                weightSmall_g = 80f, weightMedium_g = 128f, weightLarge_g = 180f, weightUnit_g = 1f, volumeUnit_ml = null),

            // SOUPS & BROTHS (85-95% water)
            // Meals, can be measured in weight (g) or volume (ml). Let's provide both.
            FoodBeverage(0, "Chicken Soup", "شوربة دجاج", "Soupe de poulet", "meal", 92f, "ic_soup", "1 cup (240ml)", 0.22f,
                weightSmall_g = 150f, weightMedium_g = 240f, weightLarge_g = 350f, weightUnit_g = 1f, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Vegetable Soup", "شوربة خضار", "Soupe de légumes", "meal", 90f, "ic_soup", "1 cup (240ml)", 0.22f,
                weightSmall_g = 150f, weightMedium_g = 240f, weightLarge_g = 350f, weightUnit_g = 1f, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Tomato Soup", "شوربة طماطم", "Soupe de tomate", "meal", 85f, "ic_soup", "1 cup (240ml)", 0.20f,
                weightSmall_g = 150f, weightMedium_g = 240f, weightLarge_g = 350f, weightUnit_g = 1f, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Vegetable Broth", "مرق خضار", "Bouillon de légumes", "meal", 96f, "ic_soup", "1 cup (240ml)", 0.23f,
                weightSmall_g = 150f, weightMedium_g = 240f, weightLarge_g = 350f, weightUnit_g = 1f, volumeCup_ml = 240f, volumeUnit_ml = 1f),

            // YOGURT & DAIRY (75-88% water)
            // Meals, measured in weight
            FoodBeverage(0, "Plain Yogurt", "زبادي", "Yaourt nature", "meal", 88f, "ic_yogurt", "1 cup (245g)", 0.22f,
                weightSmall_g = 100f, weightMedium_g = 245f, weightLarge_g = 350f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Greek Yogurt", "زبادي يوناني", "Yaourt grec", "meal", 80f, "ic_yogurt", "1 cup (200g)", 0.16f,
                weightSmall_g = 100f, weightMedium_g = 200f, weightLarge_g = 300f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Cottage Cheese", "جبن قريش", "Fromage cottage", "meal", 79f, "ic_cheese", "1 cup (226g)", 0.18f,
                weightSmall_g = 100f, weightMedium_g = 226f, weightLarge_g = 300f, weightUnit_g = 1f, volumeUnit_ml = null),

            // SMOOTHIES (80-90% water depending on ingredients)
            FoodBeverage(0, "Fruit Smoothie", "عصير فواكه مخفوق", "Smoothie aux fruits", "beverage", 85f, "ic_smoothie", "240ml", 0.20f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),
            FoodBeverage(0, "Green Smoothie", "عصير أخضر", "Smoothie vert", "beverage", 88f, "ic_smoothie", "240ml", 0.21f,
                weightUnit_g = null, volumeCup_ml = 240f, volumeUnit_ml = 1f),

            // ICE CREAM & FROZEN (55-65% water)
            FoodBeverage(0, "Ice Cream", "آيس كريم", "Glace", "meal", 61f, "ic_icecream", "1/2 cup (66g)", 0.040f,
                weightSmall_g = 50f, weightMedium_g = 66f, weightLarge_g = 100f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Frozen Yogurt", "زبادي مجمد", "Yaourt glacé", "meal", 64f, "ic_icecream", "1/2 cup (72g)", 0.046f,
                weightSmall_g = 50f, weightMedium_g = 72f, weightLarge_g = 100f, weightUnit_g = 1f, volumeUnit_ml = null),
            FoodBeverage(0, "Popsicle", "مصاصة ثلج", "Glace à l'eau", "meal", 80f, "ic_popsicle", "1 bar (59g)", 0.047f,
                weightSmall_g = 30f, weightMedium_g = 59f, weightLarge_g = 80f, weightUnit_g = 1f, volumeUnit_ml = null)
        )
    }

    // Helper function to calculate water from custom serving
    fun calculateWaterContent(waterPercentage: Float, servingSizeGrams: Float): Float {
        return (waterPercentage / 100f) * (servingSizeGrams / 1000f) // Convert to liters
    }
}