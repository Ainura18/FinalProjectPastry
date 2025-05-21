package com.pastrygame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzleScene {
    private SceneManager sceneManager;
    private String pastryName;
    private int timeLeft = 60; // 60 seconds
    private Timeline timer;
    private List<ImageView> puzzlePieces = new ArrayList<>();
    private List<StackPane> slots = new ArrayList<>();
    private int piecesPlacedCorrectly = 0;

    public PuzzleScene(String pastryName, SceneManager sceneManager) {
        this.pastryName = pastryName;
        this.sceneManager = sceneManager;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fff0f5;");

        Text title = new Text("Assemble the " + pastryName + " Puzzle (Level 1)");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        title.setFill(Color.DARKMAGENTA);

        Text timerText = new Text("Time Left: " + timeLeft + " seconds");
        timerText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        timerText.setFill(Color.DARKBLUE);

        String imagePath = switch (pastryName) {
            case "Cupcake" -> "/images/muffin.png";
            case "Cake" -> "/images/cake.png";
            case "Donut" -> "/images/donut.png";
            default -> "/images/muffin.png";
        };
        Image pastryImage = loadImage(imagePath, pastryName);

        Pane puzzleArea = new Pane();
        puzzleArea.setPrefSize(400, 400);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                StackPane slot = new StackPane();
                slot.setPrefSize(150, 150);
                slot.setStyle("-fx-border-color: gray; -fx-border-width: 2;");
                slot.setLayoutX(j * 160 + 80);
                slot.setLayoutY(i * 160 + 80);
                slot.getProperties().put("correctPiece", i * 2 + j);
                slots.add(slot);
                puzzleArea.getChildren().add(slot);
            }
        }

        List<Integer> positions = new ArrayList<>(List.of(0, 1, 2, 3));
        Collections.shuffle(positions);
        for (int i = 0; i < 4; i++) {
            ImageView piece = new ImageView(createPuzzlePiece(pastryImage, i % 2, i / 2));
            piece.setFitWidth(150);
            piece.setFitHeight(150);
            piece.setUserData(i);
            puzzlePieces.add(piece);
            piece.setLayoutX(50 + (positions.get(i) % 2) * 500);
            piece.setLayoutY(50 + (positions.get(i) / 2) * 300);
            // Drag-and-drop
            final double[] startPos = {0, 0};
            piece.setOnMousePressed(e -> {
                startPos[0] = e.getSceneX() - piece.getLayoutX();
                startPos[1] = e.getSceneY() - piece.getLayoutY();
            });
            piece.setOnMouseDragged(e -> {
                piece.setLayoutX(e.getSceneX() - startPos[0]);
                piece.setLayoutY(e.getSceneY() - startPos[1]);
            });
            piece.setOnMouseReleased(e -> {
                for (StackPane slot : slots) {
                    if (slot.getBoundsInParent().contains(e.getSceneX(), e.getSceneY() - 50)) {
                        piece.setLayoutX(slot.getLayoutX());
                        piece.setLayoutY(slot.getLayoutY());
                        int pieceIndex = (int) piece.getUserData();
                        int correctIndex = (int) slot.getProperties().get("correctPiece");
                        if (pieceIndex == correctIndex) {
                            piecesPlacedCorrectly++;
                            piece.setDisable(true);
                            if (piecesPlacedCorrectly == 4) {
                                timer.stop();
                                sceneManager.showMemoryMatchScene();
                            }
                        }
                        break;
                    }
                }
            });
            puzzleArea.getChildren().add(piece);
        }

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerText.setText("Time Left: " + timeLeft + " seconds");
            if (timeLeft <= 0) {
                timer.stop();
                showAlert("You Lost!");
                GameState.reset();
                sceneManager.showWelcomeScene();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        root.getChildren().addAll(title, timerText, puzzleArea);
        return new Scene(root, 800, 600);
    }

    private Image loadImage(String path, String fallbackText) {
        try {
            Image image = new Image(getClass().getResourceAsStream(path));
            if (image.isError()) {
                System.err.println("Error loading image: " + path);
                return createFallbackImage(fallbackText);
            }
            return image;
        } catch (NullPointerException e) {
            System.err.println("Image not found: " + path);
            return createFallbackImage(fallbackText);
        }
    }

    private Image createFallbackImage(String text) {
        Text fallbackText = new Text(text);
        fallbackText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        fallbackText.setFill(Color.BLACK);
        StackPane stackPane = new StackPane(fallbackText);
        stackPane.setStyle("-fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 1;");
        stackPane.setPrefSize(150, 150);
        return stackPane.snapshot(null, null);
    }

    private Image createPuzzlePiece(Image fullImage, int x, int y) {
        int pieceWidth = (int) (fullImage.getWidth() / 2);
        int pieceHeight = (int) (fullImage.getHeight() / 2);
        WritableImage piece = new WritableImage(pieceWidth, pieceHeight);
        piece.getPixelWriter().setPixels(0, 0, pieceWidth, pieceHeight,
                fullImage.getPixelReader(), x * pieceWidth, y * pieceHeight);
        return piece;
    }

    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }




    private void showWinMessage() {
        // Фонға жарқыраған мәтін
        Text victoryText = new Text("🎉 ЖЕҢІС! 🎉");
        victoryText.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 40));
        victoryText.setFill(Color.HOTPINK);
        victoryText.setOpacity(0);

        StackPane overlay = new StackPane(victoryText);
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7);");
        overlay.setPrefSize(800, 600);

        Scene currentScene = sceneManager.getStage().getScene();
        if (currentScene.getRoot() instanceof VBox root) {
            root.getChildren().add(overlay);

            // Fade-in анимациясы
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.seconds(1), victoryText);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setCycleCount(3);
            ft.setAutoReverse(true);
            ft.play();
        }

        // Жалғастыру сұрауы
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("ЖЕҢІС!");
        alert.setHeaderText("Сіз барлық бөліктерді дұрыс құрастырдыңыз 🎉");
        alert.setContentText("Келесі раундқа өтесіз бе, әлде басты бетке қайтасыз ба?");

        javafx.scene.control.ButtonType nextLevel = new javafx.scene.control.ButtonType("Келесі раунд");
        javafx.scene.control.ButtonType backToMenu = new javafx.scene.control.ButtonType("Басты бетке");

        alert.getButtonTypes().setAll(nextLevel, backToMenu);

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == nextLevel) {
            sceneManager.showMemoryMatchScene();
        } else {
            GameState.reset();
            sceneManager.showWelcomeScene();
        }
    }

}