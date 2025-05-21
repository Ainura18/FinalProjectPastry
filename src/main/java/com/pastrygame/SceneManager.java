package com.pastrygame;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SceneManager {
    private static SceneManager instance;
    private Stage stage;
    private SoundManager soundManager;

    private SceneManager(Stage stage) {
        this.stage = stage;
        this.soundManager = SoundManager.getInstance();
        System.out.println("SceneManager.getInstance: Initialized with stage and SoundManager");
    }

    public static SceneManager getInstance(Stage stage) {
        if (instance == null) {
            instance = new SceneManager(stage);
            System.out.println("SceneManager.getInstance: Initialized with stage");
        }
        return instance;
    }

    public void showWelcomeScene() {
        System.out.println("SceneManager.showWelcomeScene: Switching to Welcome Scene");
        soundManager.stopWinMusic(); // Ensure win music is stopped
        Main main = new Main();
        main.start(stage);
    }

    public void showSelectionScene() {
        System.out.println("SceneManager.showSelectionScene: Switching to Selection Scene");
        Main main = new Main();
        main.showSelectionScreen(stage, this);
    }

    public void showRecipeScene(String pastry) {
        System.out.println("SceneManager.showRecipeScene: Switching to Recipe Scene for " + pastry);
        Main main = new Main();
        main.showRecipeScreen(stage, pastry, this);
    }

    public void showIngredientScene(String pastry) {
        System.out.println("SceneManager.showIngredientScene: Switching to Ingredient Scene for " + pastry);
        IngredientScene ingredientScene = new IngredientScene(pastry, this);
        Scene scene = ingredientScene.createScene();
        stage.setScene(scene);
        stage.show();
        System.out.println("SceneManager.showIngredientScene: Ingredient Scene displayed");
    }

    public void showPuzzleScene() {
        System.out.println("SceneManager.showPuzzleScene: Switching to Puzzle Scene");
        PuzzleScene puzzleScene = new PuzzleScene(GameState.currentPastry, this);
        Scene scene = puzzleScene.createScene();
        if (scene == null) {
            System.err.println("SceneManager.showPuzzleScene: Error: Puzzle Scene is null");
            showGameOverScene();
            return;
        }
        stage.setScene(scene);
        stage.show();
        GameState.currentLevel = 1;
        System.out.println("SceneManager.showPuzzleScene: Puzzle Scene displayed");
    }

    public void showMemoryMatchScene() {
        System.out.println("SceneManager.showMemoryMatchScene: Switching to Memory Match Scene");
        MemoryMatchScene memoryScene = new MemoryMatchScene(this);
        Scene scene = memoryScene.createScene();
        if (scene == null) {
            System.err.println("SceneManager.showMemoryMatchScene: Error: Memory Match Scene is null");
            showGameOverScene();
            return;
        }
        stage.setScene(scene);
        stage.show();
        GameState.currentLevel = 2;
        System.out.println("SceneManager.showMemoryMatchScene: Memory Match Scene displayed");
    }

    public void showHangmanScene() {
        System.out.println("SceneManager.showHangmanScene: Switching to Hangman Scene");
        HangmanScene hangmanScene = new HangmanScene(this);
        Scene scene = hangmanScene.createScene();
        if (scene == null) {
            System.err.println("SceneManager.showHangmanScene: Error: Hangman Scene is null");
            showGameOverScene();
            return;
        }
        stage.setScene(scene);
        stage.show();
        GameState.currentLevel = 3;
        System.out.println("SceneManager.showHangmanScene: Hangman Scene displayed");
    }

    public void showGameOverScene() {
        System.out.println("SceneManager.showGameOverScene: Switching to Game Over Scene");
        soundManager.stopWinMusic(); // Ensure win music is stopped
        Label label = new Label("Game Over! Mistakes: " + GameState.mistakes);
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        label.setTextFill(Color.DARKMAGENTA);
        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        restartButton.setOnAction(e -> restartGame());
        VBox layout = new VBox(20, label, restartButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #fff0f5;");
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
        System.out.println("SceneManager.showGameOverScene: Game Over Scene displayed");
    }

    public void showWinScene() {
        System.out.println("SceneManager.showWinScene: Switching to Win Scene");
        soundManager.stopBackgroundMusic(); // Stop background music
        soundManager.playWinMusic(); // Play win music

        Label label = new Label("Game Over, You Are Winner!");
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        label.setTextFill(Color.YELLOW);

        Button restartButton = new Button("Play Again");
        restartButton.setStyle("-fx-background-color: #ffb6c1; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16;");
        restartButton.setOnAction(e -> {
            System.out.println("SceneManager.showWinScene: Play Again button clicked");
            soundManager.stopWinMusic(); // Stop win music on restart
            restartGame();
        });

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        List<FireworkParticle> particles = new ArrayList<>();
        Random random = new Random();

        System.out.println("SceneManager.showWinScene: Initializing canvas with size 800x600");

        AnimationTimer fireworksTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            private long lastFirework = 0;
            private int particleCount = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_666_666) { // ~60 FPS
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                    if (now - lastFirework >= 300_000_000) { // Every 0.3 seconds
                        double x = random.nextDouble() * 600 + 100;
                        double y = random.nextDouble() * 300 + 150;
                        for (int i = 0; i < 100; i++) {
                            double angle = random.nextDouble() * 2 * Math.PI;
                            double speed = random.nextDouble() * 5 + 3;
                            Color color = switch (random.nextInt(5)) {
                                case 0 -> Color.RED;
                                case 1 -> Color.YELLOW;
                                case 2 -> Color.BLUE;
                                case 3 -> Color.GREEN;
                                case 4 -> Color.MAGENTA;
                                default -> Color.WHITE;
                            };
                            particles.add(new FireworkParticle(x, y, speed * Math.cos(angle), speed * Math.sin(angle), color));
                            particleCount++;
                        }
                        System.out.println("SceneManager.showWinScene: Spawned fireworks at (" + x + ", " + y + "), total particles: " + particleCount);
                        lastFirework = now;
                    }

                    particles.removeIf(p -> !p.update());
                    for (FireworkParticle p : particles) {
                        p.draw(gc);
                    }
                    System.out.println("SceneManager.showWinScene: Drawing " + particles.size() + " particles");
                    lastUpdate = now;
                }
            }
        };
        fireworksTimer.start();
        System.out.println("SceneManager.showWinScene: Fireworks timer started");

        VBox layout = new VBox(20, label, restartButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: transparent;");

        StackPane root = new StackPane(canvas, layout);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
        System.out.println("SceneManager.showWinScene: Win Scene displayed with fireworks and win music");
    }

    public void restartGame() {
        System.out.println("SceneManager.restartGame: Restarting game");
        soundManager.stopWinMusic(); // Stop win music
        GameState.reset();
        showWelcomeScene();
    }

    private static class FireworkParticle {
        private double x, y;
        private double vx, vy;
        private Color color;
        private double life;

        public FireworkParticle(double x, double y, double vx, double vy, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.life = 1.0;
        }

        public boolean update() {
            x += vx;
            y += vy;
            vy += 0.03; // Gravity
            life -= 0.015; // Slower fade-out
            return life > 0 && x >= 0 && x <= 800 && y >= 0 && y <= 600;
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(color.deriveColor(0, 1, 1, life));
            gc.fillOval(x, y, 6, 6);
        }
    }
}
