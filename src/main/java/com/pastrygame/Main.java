package com.pastrygame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.List;

public class Main extends Application {
    public static Stage mainStage;
    private SoundManager soundManager;
    private boolean isMusicPlaying = true;

    private static class SoundManager {
        private static SoundManager instance;
        private Clip backgroundMusic;

        private SoundManager() {
        }

        public static SoundManager getInstance() {
            if (instance == null) {
                instance = new SoundManager();
                System.out.println("SoundManager.getInstance: Initialized SoundManager");
            }
            return instance;
        }

        public void startBackgroundMusic() {
            try {
                File audioFile = new File(Main.class.getResource("/sounds/Fkj-Ylang Ylang (slowed + reverb).wav").toURI());
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInputStream);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusic.start();
                System.out.println("SoundManager.startBackgroundMusic: Background music started");
            } catch (Exception e) {
                System.err.println("SoundManager.startBackgroundMusic: Error playing music: " + e.getMessage());
            }
        }

        public void stopBackgroundMusic() {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
                System.out.println("SoundManager.stopBackgroundMusic: Background music stopped");
            }
        }

        public void resumeBackgroundMusic() {
            if (backgroundMusic != null && !backgroundMusic.isRunning()) {
                backgroundMusic.start();
                System.out.println("SoundManager.resumeBackgroundMusic: Background music resumed");
            }
        }

        public void closeBackgroundMusic() {
            if (backgroundMusic != null) {
                backgroundMusic.close();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        SceneManager sceneManager = SceneManager.getInstance(primaryStage);
        System.out.println("Main.start: Initializing Welcome Scene");

        soundManager = SoundManager.getInstance();
        soundManager.startBackgroundMusic();

        Text welcomeText = new Text("Are you ready to start the game?");
        welcomeText.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        welcomeText.setFill(Color.DARKMAGENTA);

        Button startButton = new Button("START");
        startButton.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 40));
        startButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 15 30 15 30; -fx-background-radius: 15;");

        startButton.setOnAction(e -> {
            System.out.println("Main.start: START button clicked");
            sceneManager.showSelectionScene();
        });

        Button musicButton = new Button("♪");
        musicButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        musicButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 5 10 5 10; -fx-background-radius: 10;");
        musicButton.setOnAction(e -> {
            if (isMusicPlaying) {
                soundManager.stopBackgroundMusic();
                musicButton.setText("▶");
                isMusicPlaying = false;
                System.out.println("Main.start: Music paused");
            } else {
                soundManager.resumeBackgroundMusic();
                musicButton.setText("⏸");
                isMusicPlaying = true;
                System.out.println("Main.start: Music resumed");
            }
        });

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(welcomeText, startButton);

        StackPane startRoot = new StackPane();
        startRoot.setStyle("-fx-background-color: #ffe4e1;");
        startRoot.getChildren().add(vbox);

        StackPane.setAlignment(musicButton, Pos.TOP_LEFT);
        StackPane.setMargin(musicButton, new Insets(30));
        startRoot.getChildren().add(musicButton);

        Scene startScene = new Scene(startRoot, 800, 600);
        primaryStage.setTitle("Pastry Game Project");
        primaryStage.setScene(startScene);
        primaryStage.show();
        System.out.println("Main.start: Welcome Scene displayed");
    }

    public void showSelectionScreen(Stage stage, SceneManager sceneManager) {
        System.out.println("Main.showSelectionScreen: Initializing Selection Scene");

        Text chooseText = new Text("What would you like to bake?");
        chooseText.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        chooseText.setFill(Color.DARKMAGENTA);

        ImageView cupcakeImage = loadImageView("/images/muffin.png", "Cupcake");
        ImageView cakeImage = loadImageView("/images/cake.png", "Cake");
        ImageView donutImage = loadImageView("/images/donut.png", "Donut");

        cupcakeImage.setFitWidth(180);
        cupcakeImage.setFitHeight(180);
        cakeImage.setFitWidth(180);
        cakeImage.setFitHeight(180);
        donutImage.setFitWidth(180);
        donutImage.setFitHeight(180);

        cupcakeImage.setOnMouseClicked(e -> {
            System.out.println("Main.showSelectionScreen: Selected Cupcake");
            showRecipeScreen(stage, "Cupcake", sceneManager);
        });
        cakeImage.setOnMouseClicked(e -> {
            System.out.println("Main.showSelectionScreen: Selected Cake");
            showRecipeScreen(stage, "Cake", sceneManager);
        });
        donutImage.setOnMouseClicked(e -> {
            System.out.println("Main.showSelectionScreen: Selected Donut");
            showRecipeScreen(stage, "Donut", sceneManager);
        });

        HBox imageBox = new HBox(30, cupcakeImage, cakeImage, donutImage);
        imageBox.setAlignment(Pos.CENTER);

        VBox selectionLayout = new VBox(76, chooseText, imageBox);
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setStyle("-fx-background-color: #fff0f5;");

        Button musicButton = new Button("♪");
        musicButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        musicButton.setStyle("-fx-background-color: #ffb6d9; -fx-text-fill: white; -fx-padding: 5 10 5 10; -fx-background-radius: 10;");
        musicButton.setOnAction(e -> {
            if (isMusicPlaying) {
                soundManager.stopBackgroundMusic();
                musicButton.setText("▶");
                isMusicPlaying = false;
                System.out.println("Main.showSelectionScreen: Music paused");
            } else {
                soundManager.resumeBackgroundMusic();
                musicButton.setText("⏸");
                isMusicPlaying = true;
                System.out.println("Main.showSelectionScreen: Music resumed");
            }
        });

        StackPane selectionRoot = new StackPane();
        selectionRoot.getChildren().add(selectionLayout);
        StackPane.setAlignment(musicButton, Pos.TOP_LEFT);
        StackPane.setMargin(musicButton, new Insets(30));
        selectionRoot.getChildren().add(musicButton);

        Scene selectionScene = new Scene(selectionRoot, 800, 600);
        stage.setScene(selectionScene);
        System.out.println("Main.showSelectionScreen: Selection Scene displayed");
    }

    private ImageView loadImageView(String path, String fallbackText) {
        try {
            Image image = new Image(getClass().getResourceAsStream(path));
            if (image.isError()) {
                System.err.println("Main.loadImageView: Error loading image: " + path);
                return createFallbackImageView(fallbackText);
            }
            return new ImageView(image);
        } catch (NullPointerException e) {
            System.err.println("Main.loadImageView: Image not found: " + path);
            return createFallbackImageView(fallbackText);
        }
    }

    private ImageView createFallbackImageView(String text) {
        Text fallbackText = new Text(text);
        fallbackText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        fallbackText.setFill(Color.BLACK);
        StackPane stackPane = new StackPane(fallbackText);
        stackPane.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1;");
        stackPane.setPrefSize(180, 180);
        return new ImageView(stackPane.snapshot(null, null));
    }

    public void showRecipeScreen(Stage stage, String selectedPastry, SceneManager sceneManager) {
        System.out.println("Main.showRecipeScreen: Initializing Recipe Scene for " + selectedPastry);
        GameState.currentPastry = selectedPastry;

        VBox recipeLayout = new VBox(15);
        recipeLayout.setAlignment(Pos.CENTER);
        recipeLayout.setPadding(new Insets(10, 20, 20, 20));
        recipeLayout.setStyle("-fx-background-color: #fff0f5;");

        Text recipeTitle = new Text("Recipe");
        recipeTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        recipeTitle.setFill(Color.DARKMAGENTA);

        Text pastryName = new Text(selectedPastry);
        pastryName.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
        pastryName.setFill(Color.DARKMAGENTA);

        Text timerText = new Text("Time Left: 10 seconds");
        timerText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        timerText.setFill(Color.DARKBLUE);

        StringBuilder recipeDetails = new StringBuilder();
        switch (selectedPastry) {
            case "Cupcake":
                recipeDetails.append("Flour 250 g\n")
                        .append("Milk 150 ml\n")
                        .append("Water 150 ml\n")
                        .append("Baking Powder 25 g\n")
                        .append("Sugar 75 g\n")
                        .append("Salt 10 g\n")
                        .append("Butter 75 g");
                GameState.correctIngredients = List.of("Flour", "Milk", "Water", "Baking Powder", "Sugar", "Salt", "Butter");
                break;
            case "Cake":
                recipeDetails.append("Flour 300 g\n")
                        .append("Sugar 200 g\n")
                        .append("Eggs 3 units\n")
                        .append("Milk 200 ml\n")
                        .append("Butter 100 g\n")
                        .append("Baking Powder 20 g");
                GameState.correctIngredients = List.of("Flour", "Sugar", "Eggs", "Milk", "Butter", "Baking Powder");
                break;
            case "Donut":
                recipeDetails.append("Flour 400 g\n")
                        .append("Milk 250 ml\n")
                        .append("Eggs 2 units\n")
                        .append("Butter 80 g\n")
                        .append("Salt 5 g\n")
                        .append("Chocolate 100 g");
                GameState.correctIngredients = List.of("Flour", "Milk", "Eggs", "Butter", "Salt", "Chocolate");
                break;
            default:
                recipeDetails.append("Recipe not found");
                GameState.correctIngredients = List.of();
                break;
        }

        Text recipeDetailsText = new Text(recipeDetails.toString());
        recipeDetailsText.setFont(Font.font("Gabriola", FontWeight.NORMAL, 28));
        recipeDetailsText.setFill(Color.DARKSLATEBLUE);

        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int timeLeft = Integer.parseInt(timerText.getText().replaceAll("[^0-9]", ""));
            timeLeft--;
            timerText.setText("Time Left: " + timeLeft + " seconds");
            if (timeLeft <= 0) {
                System.out.println("Main.showRecipeScreen: Timer finished, switching to Ingredient Scene");
                sceneManager.showIngredientScene(selectedPastry);
            }
        }));
        timer.setCycleCount(10);
        timer.play();

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        backButton.setOnAction(e -> {
            System.out.println("Main.showRecipeScreen: Back to Menu clicked");
            timer.stop();
            soundManager.stopBackgroundMusic();
            sceneManager.showWelcomeScene();
        });

        Button musicButton = new Button("♪");
        musicButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        musicButton.setStyle("-fx-background-color: #ffb6d9; -fx-text-fill: white; -fx-padding: 5 10 5 10; -fx-background-radius: 10;");
        musicButton.setOnAction(e -> {
            if (isMusicPlaying) {
                soundManager.stopBackgroundMusic();
                musicButton.setText("▶");
                isMusicPlaying = false;
                System.out.println("Main.showRecipeScreen: Music paused");
            } else {
                soundManager.resumeBackgroundMusic();
                musicButton.setText("⏸");
                isMusicPlaying = true;
                System.out.println("Main.showRecipeScreen: Music resumed");
            }
        });

        recipeLayout.getChildren().addAll(recipeTitle, pastryName, timerText, recipeDetailsText, backButton);
        StackPane recipeRoot = new StackPane();
        recipeRoot.getChildren().add(recipeLayout);
        StackPane.setAlignment(musicButton, Pos.TOP_LEFT);
        StackPane.setMargin(musicButton, new Insets(30));
        recipeRoot.getChildren().add(musicButton);

        Scene recipeScene = new Scene(recipeRoot, 800, 600);
        stage.setScene(recipeScene);
        System.out.println("Main.showRecipeScreen: Recipe Scene displayed");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Бұл тек commit жасау үшін енгізілген жол

