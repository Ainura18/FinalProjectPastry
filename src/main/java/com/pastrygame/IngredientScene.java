package com.pastrygame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientScene {
    private String pastry;
    private SceneManager sceneManager;
    private List<CheckBox> ingredientChecks;

    public IngredientScene(String pastry, SceneManager sceneManager) {
        this.pastry = pastry;
        this.sceneManager = sceneManager;
        this.ingredientChecks = new ArrayList<>();
    }

    public Scene createScene() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fff0f5;");

        Text title = new Text("Ingredients: " + pastry);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        title.setFill(Color.DARKMAGENTA);

        VBox imagesContainer = new VBox(10);
        Map<String, String> ingredientImages = new HashMap<>();
        ingredientImages.put("Flour", "flour.png");
        ingredientImages.put("Sugar", "sugar.png");
        ingredientImages.put("Eggs", "eggs.png");
        ingredientImages.put("Milk", "milk.png");
        ingredientImages.put("Butter", "butter.png");
        ingredientImages.put("Salt", "salt.png");
        ingredientImages.put("Baking Powder", "baking_powder.png");
        ingredientImages.put("Vanilla", "vanilla.png");
        ingredientImages.put("Chocolate", "chocolate.png");
        ingredientImages.put("Water", "water.png");
        ingredientImages.put("Yeast", "yeast.png");
        ingredientImages.put("Baby Food", "baby-food.png");
        ingredientImages.put("Fried Egg", "fried-egg.png");
        ingredientImages.put("Banana", "banana.png");
        ingredientImages.put("Pet Food", "pet-food.png");
        ingredientImages.put("Snacks", "snacks.png");
        ingredientImages.put("Vegetables", "vegetables.png");
        ingredientImages.put("Oil", "olive_oil.png");

        List<String> ingredients = List.of(
                "Flour", "Sugar", "Eggs", "Milk", "Oil", "Butter",
                "Salt", "Baking Powder", "Vanilla", "Chocolate", "Water", "Yeast",
                "Baby Food", "Fried Egg", "Banana", "Pet Food", "Snacks", "Vegetables"
        );
        for (int i = 0; i < ingredients.size(); i += 6) {
            HBox row = new HBox(38);
            row.setAlignment(Pos.CENTER);
            int end = Math.min(i + 6, ingredients.size());
            for (int j = i; j < end; j++) {
                String ingredient = ingredients.get(j);
                VBox item = new VBox(5);
                item.setAlignment(Pos.CENTER);
                String imagePath = "/ingredients/" + ingredientImages.getOrDefault(ingredient, "salt.png");
                ImageView img;
                try {
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    if (image.isError()) {
                        throw new Exception("Image loading failed: " + imagePath);
                    }
                    img = new ImageView(image);
                } catch (Exception e) {
                    System.err.println("IngredientScene.createScene: Error loading image: " + imagePath + " - " + e.getMessage());
                    img = new ImageView(new Image(getClass().getResourceAsStream("/ingredients/salt.png")));
                }
                img.setFitWidth(80);
                img.setFitHeight(80);
                CheckBox cb = new CheckBox(ingredient);
                cb.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
                cb.setTextFill(Color.DARKBLUE);
                ingredientChecks.add(cb);
                item.getChildren().addAll(img, cb);
                row.getChildren().add(item);
            }
            imagesContainer.getChildren().add(row);
        }

        Button submit = new Button("Check");
        submit.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        submit.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10;");
        Text feedback = new Text();
        feedback.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        feedback.setFill(Color.CRIMSON);

        submit.setOnAction(e -> {
            System.out.println("IngredientScene.createScene: Check button clicked");
            List<String> selectedIngredients = new ArrayList<>();
            for (CheckBox cb : ingredientChecks) {
                if (cb.isSelected()) {
                    selectedIngredients.add(cb.getText());
                }
            }
            if (selectedIngredients.containsAll(GameState.correctIngredients) && GameState.correctIngredients.containsAll(selectedIngredients)) {
                feedback.setText("Correct!");
                feedback.setFill(Color.DARKGREEN);
                sceneManager.showPuzzleScene();
            } else {
                GameState.mistakes++;
                feedback.setText("Incorrect! Try again. Mistakes: " + GameState.mistakes);
                if (GameState.mistakes >= 3) {
                    sceneManager.showGameOverScene();
                } else {
                    ingredientChecks.forEach(cb -> cb.setSelected(false));
                }
            }
        });

        root.getChildren().addAll(title, imagesContainer, submit, feedback);
        Scene scene = new Scene(root, 800, 600);
        System.out.println("IngredientScene.createScene: Ingredient Scene created");
        return scene;
    }

    public void updateCorrectIngredients(List<String> newCorrectIngredients) {
        GameState.correctIngredients = newCorrectIngredients;
        System.out.println("IngredientScene.updateCorrectIngredients: Updated correct ingredients to " + newCorrectIngredients);
    }

}