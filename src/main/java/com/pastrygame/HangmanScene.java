package com.pastrygame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HangmanScene {
    private static final List<String> PHRASES = List.of(
            "banana", "butter", "chocolate", "milk", "salt",
            "sugar", "vanilla", "flour", "eggs", "olive-oil"
    );
    private static final int MAX_TRIES = 6;
    private static final int MAX_HINTS = 3;

    private SceneManager sceneManager;
    private String phrase;
    private char[] guessedLetters;
    private int triesLeft;
    private int hintsUsed;
    private List<Character> guessedChars;
    private Label phraseLabel;
    private Label triesLabel;
    private Label guessedLettersLabel;
    private Canvas hangmanCanvas;
    private TextField guessField;
    private Button guessButton;
    private Button hintButton;

    public HangmanScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        System.out.println("HangmanScene.constructor: Initializing HangmanScene");
        initializeGame();
    }

    private void initializeGame() {
        Random random = new Random();
        phrase = PHRASES.get(random.nextInt(PHRASES.size())).toLowerCase();
        guessedLetters = new char[phrase.length()];
        for (int i = 0; i < guessedLetters.length; i++) {
            guessedLetters[i] = (phrase.charAt(i) == ' ') ? '-' : '_';
        }
        triesLeft = MAX_TRIES;
        hintsUsed = 0;
        guessedChars = new ArrayList<>();
        System.out.println("HangmanScene.initializeGame: Initialized with phrase: " + phrase);
    }

    public Scene createScene() {
        System.out.println("HangmanScene.createScene: Creating Hangman Scene");
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #fff0f5;");

        Label title = new Label("Hangman Game (Level 3)");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        title.setTextFill(Color.DARKMAGENTA);

        hangmanCanvas = new Canvas(300, 300);
        updateHangmanDrawing(0);

        phraseLabel = new Label(formatPhraseDisplay(guessedLetters));
        phraseLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 24));

        triesLabel = new Label("Tries left: " + triesLeft);
        triesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        guessedLettersLabel = new Label("Guessed letters: ");
        guessedLettersLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        guessField = new TextField();
        guessField.setMaxWidth(50);
        guessField.setPromptText("Letter");

        guessButton = new Button("Guess");
        guessButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        guessButton.setOnAction(e -> {
            System.out.println("HangmanScene.createScene: Guess button clicked");
            processGuess();
        });

        hintButton = new Button("Hint (" + (MAX_HINTS - hintsUsed) + ")");
        hintButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        hintButton.setOnAction(e -> {
            System.out.println("HangmanScene.createScene: Hint button clicked");
            processHint();
        });
        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        backButton.setOnAction(e -> {
            System.out.println("HangmanScene.createScene: Back to Menu clicked");
            sceneManager.showWelcomeScene();
        });

        layout.getChildren().addAll(title, hangmanCanvas, phraseLabel, triesLabel, guessedLettersLabel, guessField, guessButton, hintButton, backButton);

        Scene scene = new Scene(layout, 800, 600);
        System.out.println("HangmanScene.createScene: Hangman Scene created successfully");
        return scene;
    }

    private String formatPhraseDisplay(char[] letters) {
        StringBuilder formatted = new StringBuilder();
        for (char c : letters) {
            if (c == '-') {
                formatted.append("- ");
            } else {
                formatted.append(c).append(" ");
            }
        }
        String result = formatted.toString().trim();
        System.out.println("HangmanScene.formatPhraseDisplay: Formatted phrase: " + result);
        return result;
    }

    private void processGuess() {
        String guessText = guessField.getText().toLowerCase().trim();
        System.out.println("HangmanScene.processGuess: Processing guess: " + guessText);
        if (guessText.isEmpty()) {
            showAlert("Please enter a letter.");
            return;
        }
        if (guessText.length() > 1) {
            showAlert("Please enter only one letter.");
            return;
        }
        char guess = guessText.charAt(0);
        if (!Character.isLetter(guess)) {
            showAlert("Please enter a valid letter.");
            return;
        }
        if (guessedChars.contains(guess)) {
            showAlert("You already guessed that letter!");
            return;
        }
        guessedChars.add(guess);
        boolean correctGuess = false;
        for (int i = 0; i < phrase.length(); i++) {
            if (phrase.charAt(i) == guess) {
                guessedLetters[i] = guess;
                correctGuess = true;
            }
        }
        if (!correctGuess) {
            triesLeft--;
            updateHangmanDrawing(MAX_TRIES - triesLeft);
            GameState.mistakes++;
            System.out.println("HangmanScene.processGuess: Incorrect guess, tries left: " + triesLeft + ", mistakes: " + GameState.mistakes);
        }
        updateGameState();
        guessField.setText("");
    }

    private void processHint() {
        System.out.println("HangmanScene.processHint: Processing hint request");
        if (hintsUsed >= MAX_HINTS) {
            showAlert("No more hints available!");
            hintButton.setDisable(true);
            System.out.println("HangmanScene.processHint: Max hints reached");
            return;
        }
        char hintLetter = getLeastFrequentLetter();
        if (hintLetter == '\0') {
            showAlert("No more hints available!");
            System.out.println("HangmanScene.processHint: No valid hint letter found");
            return;
        }
        guessedChars.add(hintLetter);
        for (int i = 0; i < phrase.length(); i++) {
            if (phrase.charAt(i) == hintLetter) {
                guessedLetters[i] = hintLetter;
            }
        }
        hintsUsed++;
        hintButton.setText("Hint (" + (MAX_HINTS - hintsUsed) + ")");
        if (hintsUsed >= MAX_HINTS) {
            hintButton.setDisable(true);
        }
        System.out.println("HangmanScene.processHint: Hint provided: " + hintLetter + ", hints used: " + hintsUsed);
        updateGameState();
    }
    private char getLeastFrequentLetter() {
        Map<Character, Integer> letterCounts = new HashMap<>();
        for (char c : phrase.toCharArray()) {
            if (c != ' ' && !guessedChars.contains(c) && !String.valueOf(guessedLetters).contains(String.valueOf(c))) {
                letterCounts.put(c, letterCounts.getOrDefault(c, 0) + 1);
            }
        }
        char leastFrequent = '\0';
        int minCount = Integer.MAX_VALUE;
        for (Map.Entry<Character, Integer> entry : letterCounts.entrySet()) {
            if (entry.getValue() < minCount) {
                minCount = entry.getValue();
                leastFrequent = entry.getKey();
            }
        }
        System.out.println("HangmanScene.getLeastFrequentLetter: Selected hint letter: " + leastFrequent);
        return leastFrequent;
    }

    private void updateGameState() {
        phraseLabel.setText(formatPhraseDisplay(guessedLetters));
        triesLabel.setText("Tries left: " + triesLeft);
        guessedLettersLabel.setText("Guessed letters: " + guessedChars);
        String currentGuess = new String(guessedLetters).replace("-", " ");
        if (currentGuess.equals(phrase)) {
            System.out.println("HangmanScene.updateGameState: Phrase guessed correctly");
            showWinMessage();
        } else if (triesLeft <= 0 || GameState.mistakes >= 6) {
            System.out.println("HangmanScene.updateGameState: Game over, tries left: " + triesLeft + ", mistakes: " + GameState.mistakes);
            showAlert("Game Over! The phrase was: " + phrase);
            sceneManager.showGameOverScene();
        }
    }

    private void updateHangmanDrawing(int stage) {
        System.out.println("HangmanScene.updateHangmanDrawing: Drawing stage: " + stage);
        GraphicsContext gc = hangmanCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, hangmanCanvas.getWidth(), hangmanCanvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);

        if (stage >= 1) {
            gc.strokeLine(50, 250, 150, 250);
            gc.strokeLine(100, 250, 100, 50);
            gc.strokeLine(100, 50, 200, 50);
            gc.strokeLine(200, 50, 200, 80);
        }

        if (stage >= 2) {
            gc.strokeOval(190, 80, 20, 20);
        }

        if (stage >= 3) {
            gc.strokeLine(200, 100, 200, 150);
        }

        if (stage >= 4) {
            gc.strokeLine(200, 110, 180, 130);
        }

        if (stage >= 5) {
            gc.strokeLine(200, 110, 220, 130);
        }

        if (stage >= 6) {
            gc.strokeLine(200, 150, 180, 170);
        }

        if (stage >= 7) {
            gc.strokeLine(200, 150, 220, 170);
        }
    }

    private void showAlert(String message) {
        System.out.println("HangmanScene.showAlert: Showing alert: " + message);
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Hangman");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWinMessage() {
        System.out.println("HangmanScene.showWinMessage: Showing win message");
        guessButton.setDisable(true);
        hintButton.setDisable(true);
        guessField.setDisable(true);
        showAlert("Congratulations! You guessed the phrase: " + phrase);
        sceneManager.showWinScene(); // Changed from showWelcomeScene to showWinScene
    }

    

}