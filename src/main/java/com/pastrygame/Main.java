package com.pastrygame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientScene {
    private Scene scene;

    public IngredientScene(String pastryName, Scene previousScene, SceneManager sceneManager) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Title centered in HBox
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("Ингредиенттер: " + pastryName);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        title.setFill(Color.DARKMAGENTA);
        titleBox.getChildren().add(title);

        VBox imagesContainer = new VBox(10);
        List<CheckBox> ingredientChecks = new ArrayList<>();

        // Ingredient images mapping
        Map<String, String> ingredientImages = new HashMap<>();
        ingredientImages.put("Ұн", "flour.png");
        ingredientImages.put("Қант", "sugar.png");
        ingredientImages.put("Жұмыртқа", "eggs.png");
        ingredientImages.put("Сүт", "milk.png");
        ingredientImages.put("Май", "olive_oil.png");
        ingredientImages.put("Сары май", "butter.png");
        ingredientImages.put("Тұз", "salt.png");
        ingredientImages.put("Қопсытқыш", "baking_powder.png");
        ingredientImages.put("Ванилин", "vanilla.png");
        ingredientImages.put("Шоколад", "chocolate.png");
        ingredientImages.put("Вода", "water.png");
        ingredientImages.put("Дрожжи", "yeast.png");
        ingredientImages.put("Балалар тағамы", "baby-food.png");
        ingredientImages.put("Қуырылған жұмыртқа", "fried-egg.png");
        ingredientImages.put("Банан", "banana.png");
        ingredientImages.put("Үй жануарларының тамағы", "pet-food.png");
        ingredientImages.put("Жеңіл тағамдар", "snacks.png");
        ingredientImages.put("Көкөністер", "vegetables.png");

        // All ingredients for selection
        List<String> ingredients = List.of(
                "Ұн", "Қант", "Жұмыртқа", "Сүт", "Май", "Сары май",
                "Тұз", "Қопсытқыш", "Ванилин", "Шоколад", "Вода", "Дрожжи",
                "Балалар тағамы", "Қуырылған жұмыртқа", "Банан", "Үй жануарларының тамағы",
                "Жеңіл тағамдар", "Көкөністер"
        );
        for (int i = 0; i < ingredients.size(); i += 6) { // 6 images per row for 3 rows
            HBox row = new HBox(38); // 1 cm ≈ 38 pixels
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
                    System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
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

        Button submit = new Button("Тексеру");
        submit.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        submit.setTextFill(Color.DARKGREEN);

        Text feedback = new Text();
        feedback.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        feedback.setFill(Color.CRIMSON);

        submit.setOnAction(e -> {
            GameState.selectedIngredients.clear();
            int incorrectSelections = 0;

            for (CheckBox cb : ingredientChecks) {
                if (cb.isSelected()) {
                    GameState.selectedIngredients.add(cb.getText());
                    if (!GameState.correctIngredients.contains(cb.getText())) {
                        incorrectSelections++;
                    }
                } else if (GameState.correctIngredients.contains(cb.getText())) {
                    incorrectSelections++; // Missing a required ingredient
                }
            }

            if (incorrectSelections > 0) {
                GameState.mistakes++;
                feedback.setText("Қате! Тағы таңдаңыз. Қалған мүмкіндік: " + (3 - GameState.mistakes));
            }

            if (GameState.mistakes >= 3) {
                feedback.setText("Проигрыш! Ойын қайта басталады.");
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(ev -> sceneManager.showResultScene(false));
                pause.play();
            } else if (incorrectSelections == 0 && GameState.selectedIngredients.containsAll(GameState.correctIngredients)) {
                feedback.setText("Дұрыс! Келесі кезеңге өтіңіз.");
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(ev -> sceneManager.showResultScene(true));
                pause.play();
            }
        });

        // Layout with 1 cm (38 pixels) between title and images
        VBox layout = new VBox(38, titleBox, imagesContainer, feedback, submit);
        layout.setAlignment(Pos.CENTER);
        root.getChildren().add(layout);

        scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }
}