package com.pastrygame;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryMatchScene {
    private static final int GRID_SIZE = 4;
    private static final int CARD_SIZE = 100;
    private static final int DELAY = 1000;
    private SceneManager sceneManager;
    private List<Button> cards;
    private List<String> imagePaths;
    private Button firstCard;
    private Button secondCard;
    private int matchesFound;
    private Label statusLabel;
    private boolean isWaiting;
    private Button nextButton;

    public MemoryMatchScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.cards = new ArrayList<>();
        this.imagePaths = new ArrayList<>();
        this.matchesFound = 0;
        this.isWaiting = false;
        System.out.println("MemoryMatchScene.constructor: Initializing MemoryMatchScene");
        initializeGame();
    }

    private void initializeGame() {
        List<String> images = new ArrayList<>(List.of(
                "/images/cake.png",
                "/images/cake.png","/images/muffin.png","/images/muffin.png","/images/donut.png",
                "/images/donut.png","/ingredients/milk.png","/ingredients/milk.png","/ingredients/eggs.png",
                "/ingredients/eggs.png","/ingredients/sugar.png","/ingredients/sugar.png","/ingredients/vanilla.png",
                "/ingredients/vanilla.png","/ingredients/chocolate.png","/ingredients/chocolate.png"
        ));
        Collections.shuffle(images);
        imagePaths.addAll(images);
        System.out.println("MemoryMatchScene.initializeGame: Image paths initialized and shuffled");
    }

    public Scene createScene() {
        System.out.println("MemoryMatchScene.createScene: Creating Memory Match Scene");
        Text title = new Text("Memory Match Game (Level 2)");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        title.setFill(Color.DARKMAGENTA);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Button card = new Button();
                card.setMinSize(CARD_SIZE, CARD_SIZE);
                card.setStyle("-fx-background-color: #ffb6c1;");
                int index = row * GRID_SIZE + col;
                card.setOnAction(e -> handleCardClick(card, index));
                cards.add(card);
                grid.add(card, col, row);
            }
        }

        statusLabel = new Label("Pairs Found: 0 / 8");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nextButton = new Button("Next Level");
        nextButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        nextButton.setVisible(false);
        nextButton.setOnAction(e -> {
            System.out.println("MemoryMatchScene.createScene: Next button clicked, attempting to switch to Hangman");
            try {
                sceneManager.showHangmanScene();
                System.out.println("MemoryMatchScene.createScene: Called showHangmanScene successfully");
            } catch (Exception ex) {
                System.err.println("MemoryMatchScene.createScene: Error switching to Hangman: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        backButton.setOnAction(e -> {
            System.out.println("MemoryMatchScene.createScene: Back to Menu clicked");
            sceneManager.showWelcomeScene();
        });

        VBox layout = new VBox(20, title, grid, statusLabel, nextButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #fff0f5;");

        Scene scene = new Scene(layout, 800, 600);
        System.out.println("MemoryMatchScene.createScene: Memory Match Scene created");
        return scene;
    }

    private void handleCardClick(Button card, int index) {
        if (isWaiting || card.getGraphic() != null) return;
        System.out.println("MemoryMatchScene.handleCardClick: Card clicked at index " + index);
        ImageView imageView = loadImageView(imagePaths.get(index));
        imageView.setFitWidth(CARD_SIZE - 10);
        imageView.setFitHeight(CARD_SIZE - 10);
        card.setGraphic(imageView);
        if (firstCard == null) {
            firstCard = card;
            System.out.println("MemoryMatchScene.handleCardClick: First card selected");
        } else if (secondCard == null) {
            secondCard = card;
            isWaiting = true;
            System.out.println("MemoryMatchScene.handleCardClick: Second card selected, checking for match");
            checkForMatch();
        }
    }

    private void checkForMatch() {
        ImageView firstImage = (ImageView) firstCard.getGraphic();
        ImageView secondImage = (ImageView) secondCard.getGraphic();
        String firstPath = imagePaths.get(cards.indexOf(firstCard));
        String secondPath = imagePaths.get(cards.indexOf(secondCard));
        System.out.println("MemoryMatchScene.checkForMatch: Comparing " + firstPath + " with " + secondPath);
        if (firstPath.equals(secondPath)) {
            firstCard.setDisable(true);
            secondCard.setDisable(true);
            matchesFound++;
            statusLabel.setText("Pairs Found: " + matchesFound + " / 8");
            System.out.println("MemoryMatchScene.checkForMatch: Match found, pairs found: " + matchesFound);
            resetCards();
            if (matchesFound == 8) {
                System.out.println("MemoryMatchScene.checkForMatch: All 8 pairs found, enabling Next button");
                nextButton.setVisible(true);
                nextButton.requestFocus();
                cards.forEach(card -> card.setDisable(true));
                statusLabel.setText("Congratulations! All pairs found!");
            }
        } else {
            System.out.println("MemoryMatchScene.checkForMatch: No match, hiding cards after delay");
            PauseTransition pause = new PauseTransition(Duration.millis(DELAY));
            pause.setOnFinished(e -> {
                firstCard.setGraphic(null);
                secondCard.setGraphic(null);
                resetCards();
                System.out.println("MemoryMatchScene.checkForMatch: Cards hidden after mismatch");
            });
            pause.play();
        }
    }
    private void resetCards() {
        firstCard = null;
        secondCard = null;
        isWaiting = false;
        System.out.println("MemoryMatchScene.resetCards: Cards reset");
    }

    private ImageView loadImageView(String path) {
        try {
            Image image = new Image(getClass().getResourceAsStream(path));
            if (image.isError()) {
                System.err.println("MemoryMatchScene.loadImageView: Error loading image: " + path);
                return createFallbackImageView(path);
            }
            System.out.println("MemoryMatchScene.loadImageView: Image loaded: " + path);
            return new ImageView(image);
        } catch (NullPointerException e) {
            System.err.println("MemoryMatchScene.loadImageView: Image not found: " + path);
            return createFallbackImageView(path);
        }
    }

    private ImageView createFallbackImageView(String text) {
        Text fallbackText = new Text(text);
        fallbackText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        fallbackText.setFill(Color.BLACK);
        StackPane stackPane = new StackPane(fallbackText);
        stackPane.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1;");
        stackPane.setPrefSize(CARD_SIZE - 10, CARD_SIZE - 10);
        System.out.println("MemoryMatchScene.createFallbackImageView: Fallback image created for: " + text);
        return new ImageView(stackPane.snapshot(null, null));
    }

    private void resetCards() {
        firstCard = null;
        secondCard = null;
        isWaiting = false;
        System.out.println("MemoryMatchScene.resetCards: Cards reset");
    }

    // Осы жерден кейін қоса сал:
    public void sayHello() {
        System.out.println("Hello from MemoryMatchScene!");
    }
}