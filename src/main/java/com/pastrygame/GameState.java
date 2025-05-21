package com.pastrygame;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.ImageView;

public class GameState {
    public static List<String> selectedIngredients = new ArrayList<>();
    public static List<String> correctIngredients = new ArrayList<>();
    public static int mistakes = 0;
    public static int score = 0;
    public static String currentPastry = "";
    public static ImageView stageImage;
    public static int currentLevel = 1;
    public static List<Integer> puzzleState = new ArrayList<>();

    private static final java.util.Map<String, List<String>> recipes = new java.util.HashMap<>();

    static {
        recipes.put("Cupcake", List.of("Flour 250 g", "Milk 150 ml", "Water 150 ml", "Baking Powder 25 g", "Sugar 75 g", "Salt 10 g", "Butter 75 g"));
        recipes.put("Cake", List.of("Flour 300 g", "Sugar 200 g", "Eggs 3 units", "Milk 200 ml", "Butter 100 g", "Baking Powder 20 g"));
        recipes.put("Donut", List.of("Flour 400 g", "Milk 250 ml", "Eggs 2 units", "Butter 80 g", "Salt 5 g", "Chocolate 100 g"));
    }

    public static List<String> getRecipe(String pastryName) {
        return recipes.getOrDefault(pastryName, new ArrayList<>());
    }

    public static void reset() {
        mistakes = 0;
        currentLevel = 1;
        currentPastry = null;
        correctIngredients = null;
    }

    public static List<String> getRemainingIngredients() {
        List<String> remaining = new ArrayList<>();
        if (correctIngredients != null) {
            for (String ingredient : correctIngredients) {
                if (!selectedIngredients.contains(ingredient)) {
                    remaining.add(ingredient);
                }
            }
        }
        return remaining;
    }

}