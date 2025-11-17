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
            FoodBeverage(0, "Water", "ماء", "Eau", "beverage", 100f, "250ml", 0.25f, "ic_water_drop"),
            FoodBeverage(0, "Coffee", "قهوة", "Café", "beverage", 99.5f, "240ml", 0.24f, "ic_coffee"),
            FoodBeverage(0, "Tea (unsweetened)", "شاي", "Thé", "beverage", 99.7f, "240ml", 0.24f, "ic_tea"),
            FoodBeverage(0, "Green Tea", "شاي أخضر", "Thé vert", "beverage", 99.9f, "240ml", 0.24f, "ic_tea"),
            FoodBeverage(0, "Sparkling Water", "ماء فوار", "Eau gazeuse", "beverage", 99.9f, "250ml", 0.25f, "ic_water_drop"),
            FoodBeverage(0, "Coconut Water", "ماء جوز الهند", "Eau de coco", "beverage", 95f, "240ml", 0.23f, "ic_coconut"),
            FoodBeverage(0, "Sports Drink", "مشروب رياضي", "Boisson sportive", "beverage", 94f, "240ml", 0.225f, "ic_fitness"),

            // FRUIT JUICES (85-90% water)
            FoodBeverage(0, "Orange Juice", "عصير برتقال", "Jus d'orange", "beverage", 88f, "240ml", 0.21f, "ic_orange"),
            FoodBeverage(0, "Apple Juice", "عصير تفاح", "Jus de pomme", "beverage", 88f, "240ml", 0.21f, "ic_apple"),
            FoodBeverage(0, "Grape Juice", "عصير عنب", "Jus de raisin", "beverage", 84f, "240ml", 0.20f, "ic_grape"),
            FoodBeverage(0, "Watermelon Juice", "عصير بطيخ", "Jus de pastèque", "beverage", 92f, "240ml", 0.22f, "ic_watermelon"),

            // MILK & ALTERNATIVES (85-91% water)
            FoodBeverage(0, "Milk (whole)", "حليب كامل الدسم", "Lait entier", "beverage", 88f, "240ml", 0.21f, "ic_milk"),
            FoodBeverage(0, "Milk (skim)", "حليب خالي الدسم", "Lait écrémé", "beverage", 91f, "240ml", 0.22f, "ic_milk"),
            FoodBeverage(0, "Almond Milk", "حليب اللوز", "Lait d'amande", "beverage", 96f, "240ml", 0.23f, "ic_milk"),
            FoodBeverage(0, "Soy Milk", "حليب الصويا", "Lait de soja", "beverage", 93f, "240ml", 0.22f, "ic_milk"),

            // HIGH WATER FRUITS (90-96% water)
            FoodBeverage(0, "Watermelon", "بطيخ", "Pastèque", "fruit", 92f, "1 cup (154g)", 0.14f, "ic_watermelon"),
            FoodBeverage(0, "Strawberries", "فراولة", "Fraises", "fruit", 91f, "1 cup (152g)", 0.14f, "ic_strawberry"),
            FoodBeverage(0, "Cantaloupe", "شمام", "Cantaloup", "fruit", 90f, "1 cup (177g)", 0.16f, "ic_melon"),
            FoodBeverage(0, "Peaches", "خوخ", "Pêches", "fruit", 89f, "1 medium (150g)", 0.13f, "ic_peach"),
            FoodBeverage(0, "Oranges", "برتقال", "Oranges", "fruit", 87f, "1 medium (131g)", 0.11f, "ic_orange"),
            FoodBeverage(0, "Grapefruit", "جريب فروت", "Pamplemousse", "fruit", 91f, "1/2 fruit (123g)", 0.11f, "ic_grapefruit"),
            FoodBeverage(0, "Pineapple", "أناناس", "Ananas", "fruit", 86f, "1 cup (165g)", 0.14f, "ic_pineapple"),
            FoodBeverage(0, "Grapes", "عنب", "Raisins", "fruit", 81f, "1 cup (151g)", 0.12f, "ic_grape"),
            FoodBeverage(0, "Apples", "تفاح", "Pommes", "fruit", 84f, "1 medium (182g)", 0.15f, "ic_apple"),
            FoodBeverage(0, "Pears", "كمثرى", "Poires", "fruit", 84f, "1 medium (178g)", 0.15f, "ic_pear"),
            FoodBeverage(0, "Plums", "برقوق", "Prunes", "fruit", 87f, "1 medium (66g)", 0.06f, "ic_plum"),

            // MEDIUM WATER FRUITS (75-85% water)
            FoodBeverage(0, "Blueberries", "توت أزرق", "Myrtilles", "fruit", 84f, "1 cup (148g)", 0.12f, "ic_blueberry"),
            FoodBeverage(0, "Cherries", "كرز", "Cerises", "fruit", 82f, "1 cup (138g)", 0.11f, "ic_cherry"),
            FoodBeverage(0, "Mango", "مانجو", "Mangue", "fruit", 83f, "1 cup (165g)", 0.14f, "ic_mango"),
            FoodBeverage(0, "Papaya", "بابايا", "Papaye", "fruit", 88f, "1 cup (140g)", 0.12f, "ic_papaya"),
            FoodBeverage(0, "Kiwi", "كيوي", "Kiwi", "fruit", 83f, "1 medium (69g)", 0.06f, "ic_kiwi"),

            // HIGH WATER VEGETABLES (92-96% water)
            FoodBeverage(0, "Cucumber", "خيار", "Concombre", "vegetable", 96f, "1 cup (104g)", 0.10f, "ic_cucumber"),
            FoodBeverage(0, "Lettuce", "خس", "Laitue", "vegetable", 96f, "1 cup (47g)", 0.045f, "ic_lettuce"),
            FoodBeverage(0, "Celery", "كرفس", "Céleri", "vegetable", 95f, "1 cup (101g)", 0.096f, "ic_celery"),
            FoodBeverage(0, "Tomatoes", "طماطم", "Tomates", "vegetable", 94f, "1 medium (123g)", 0.12f, "ic_tomato"),
            FoodBeverage(0, "Zucchini", "كوسة", "Courgette", "vegetable", 95f, "1 cup (124g)", 0.12f, "ic_zucchini"),
            FoodBeverage(0, "Radishes", "فجل", "Radis", "vegetable", 95f, "1 cup (116g)", 0.11f, "ic_radish"),
            FoodBeverage(0, "Bell Peppers", "فلفل حلو", "Poivrons", "vegetable", 92f, "1 cup (149g)", 0.14f, "ic_pepper"),
            FoodBeverage(0, "Cauliflower", "قرنبيط", "Chou-fleur", "vegetable", 92f, "1 cup (107g)", 0.10f, "ic_cauliflower"),
            FoodBeverage(0, "Spinach", "سبانخ", "Épinards", "vegetable", 91f, "1 cup (30g)", 0.027f, "ic_spinach"),
            FoodBeverage(0, "Broccoli", "بروكلي", "Brocoli", "vegetable", 89f, "1 cup (91g)", 0.081f, "ic_broccoli"),
            FoodBeverage(0, "Cabbage", "ملفوف", "Chou", "vegetable", 92f, "1 cup (89g)", 0.082f, "ic_cabbage"),
            FoodBeverage(0, "Carrots", "جزر", "Carottes", "vegetable", 88f, "1 cup (128g)", 0.11f, "ic_carrot"),

            // SOUPS & BROTHS (85-95% water)
            FoodBeverage(0, "Chicken Soup", "شوربة دجاج", "Soupe de poulet", "meal", 92f, "1 cup (240ml)", 0.22f, "ic_soup"),
            FoodBeverage(0, "Vegetable Soup", "شوربة خضار", "Soupe de légumes", "meal", 90f, "1 cup (240ml)", 0.22f, "ic_soup"),
            FoodBeverage(0, "Tomato Soup", "شوربة طماطم", "Soupe de tomate", "meal", 85f, "1 cup (240ml)", 0.20f, "ic_soup"),
            FoodBeverage(0, "Vegetable Broth", "مرق خضار", "Bouillon de légumes", "meal", 96f, "1 cup (240ml)", 0.23f, "ic_soup"),

            // YOGURT & DAIRY (75-88% water)
            FoodBeverage(0, "Plain Yogurt", "زبادي", "Yaourt nature", "meal", 88f, "1 cup (245g)", 0.22f, "ic_yogurt"),
            FoodBeverage(0, "Greek Yogurt", "زبادي يوناني", "Yaourt grec", "meal", 80f, "1 cup (200g)", 0.16f, "ic_yogurt"),
            FoodBeverage(0, "Cottage Cheese", "جبن قريش", "Fromage cottage", "meal", 79f, "1 cup (226g)", 0.18f, "ic_cheese"),

            // SMOOTHIES (80-90% water depending on ingredients)
            FoodBeverage(0, "Fruit Smoothie", "عصير فواكه مخفوق", "Smoothie aux fruits", "beverage", 85f, "240ml", 0.20f, "ic_smoothie"),
            FoodBeverage(0, "Green Smoothie", "عصير أخضر", "Smoothie vert", "beverage", 88f, "240ml", 0.21f, "ic_smoothie"),

            // ICE CREAM & FROZEN (55-65% water)
            FoodBeverage(0, "Ice Cream", "آيس كريم", "Glace", "meal", 61f, "1/2 cup (66g)", 0.040f, "ic_icecream"),
            FoodBeverage(0, "Frozen Yogurt", "زبادي مجمد", "Yaourt glacé", "meal", 64f, "1/2 cup (72g)", 0.046f, "ic_icecream"),
            FoodBeverage(0, "Popsicle", "مصاصة ثلج", "Glace à l'eau", "meal", 80f, "1 bar (59g)", 0.047f, "ic_popsicle")
        )
    }

    // Helper function to calculate water from custom serving
    fun calculateWaterContent(waterPercentage: Float, servingSizeGrams: Float): Float {
        return (waterPercentage / 100f) * (servingSizeGrams / 1000f) // Convert to liters
    }
}