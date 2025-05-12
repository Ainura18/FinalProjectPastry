package com.pastrygame;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardMatchingScene {
    private Scene scene;
    private int matchesFound = 0;
    private ImageView firstCard = null;
    private int firstCardIndex = -1;
    private boolean isFlipping = false;
    private Timeline timer;
    private int timeLeft;

    public CardMatchingScene(String pastryName, SceneManager sceneManager) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fff0f5;");

        Text title = new Text("Match the Cards");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        title.setFill(Color.DARKMAGENTA);

        timeLeft = GameState.currentLevel == 1 ? 60 : 45;
        Text timerText = new Text("Time Left: " + timeLeft + " seconds");
        timerText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        timerText.setFill(Color.DARKBLUE);

        List<String> cardImages = GameState.currentLevel == 1 ?
                Arrays.asList(
                        "/images/cupcake.png", "/images/cupcake.png",
                        "/images/cake.png", "/images/cake.png",
                        "/images/donut.png", "/images/donut.png"
                ) :
                Arrays.asList(
                        "/ingredients/flour.png", "/ingredients/flour.png",
                        "/ingredients/sugar.png", "/ingredients/sugar.png",
                        "/ingredients/eggs.png", "/ingredients/eggs.png",
                        "/ingredients/milk.png", "/ingredients/milk.png",
                        "/ingredients/butter.png", "/ingredients/butter.png",
                        "/ingredients/chocolate.png", "/ingredients/chocolate.png"
                );
        Collections.shuffle(cardImages);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Image backImage = new Image(getClass().getResourceAsStream("/cards/back.png"));
        int rows = GameState.currentLevel == 1 ? 2 : 3;
        int cols = GameState.currentLevel == 1 ? 3 : 4;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;
                ImageView card = new ImageView(backImage);
                card.setFitWidth(80);
                card.setFitHeight(120);
                card.setUserData(cardImages.get(index));
                card.setOnMouseClicked(e -> handleCardClick(card, index, sceneManager));
                grid.add(card, j, i);
            }
        }

        root.getChildren().addAll(title, timerText, grid);
        scene = new Scene(root, 800, 600);

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerText.setText("Time Left: " + timeLeft + " seconds");
            if (timeLeft <= 0) {
                timer.stop();
                GameState.score = 80;
                sceneManager.showGameOverScene(80);
            }
        }));
        timer.setCycleCount(timeLeft);
        timer.play();
    }

    private void handleCardClick(ImageView card, int index, SceneManager sceneManager) {
        if (isFlipping || card.getOpacity() == 0 || firstCard == card) return;

        String cardImagePath = (String) card.getUserData();
        card.setImage(new Image(getClass().getResourceAsStream(cardImagePath)));

        if (firstCard == null) {
            firstCard = card;
            firstCardIndex = index;
        } else {
            isFlipping = true;
            String firstCardImagePath = (String) firstCard.getUserData();
            if (firstCardImagePath.equals(cardImagePath) && firstCard != card) {
                card.setOpacity(0);
                firstCard.setOpacity(0);
                matchesFound++;
                int totalPairs = GameState.currentLevel == 1 ? 3 : 6;
                if (matchesFound == totalPairs) {
                    timer.stop();
                    if (GameState.currentLevel == 1) {
                        GameState.currentLevel++;
                        Text advanceText = new Text("You advanced to the next level!");
                        advanceText.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
                        advanceText.setFill(Color.DARKMAGENTA);
                        VBox advanceLayout = new VBox(20, advanceText);
                        advanceLayout.setAlignment(Pos.CENTER);
                        advanceLayout.setStyle("-fx-background-color: #ffe4e1;");
                        scene.setRoot(advanceLayout);
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(e -> sceneManager.showCardMatchingScene(GameState.currentPastry));
                        pause.play();
                    } else {
                        GameState.score = 100;
                        Text winText = new Text("Game Over! Your score: 100");
                        winText.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
                        winText.setFill(Color.DARKMAGENTA);
                        Button restartButton = new Button("Restart");
                        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                        restartButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white;");
                        restartButton.setOnAction(e -> {
                            System.out.println("Restarting game from win screen...");
                            sceneManager.restartGame();
                        });
                        VBox winLayout = new VBox(20, winText, restartButton);
                        winLayout.setAlignment(Pos.CENTER);
                        winLayout.setStyle("-fx-background-color: #ffe4e1;");
                        scene.setRoot(winLayout);
                    }
                }
            } else {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> {
                    card.setImage(new Image(getClass().getResourceAsStream("/cards/back.png")));
                    firstCard.setImage(new Image(getClass().getResourceAsStream("/cards/back.png")));
                    isFlipping = false;
                });
                pause.play();
            }
            firstCard = null;
            firstCardIndex = -1;
            isFlipping = false;
        }
    }

    public Scene getScene() {
        return scene;
    }
}