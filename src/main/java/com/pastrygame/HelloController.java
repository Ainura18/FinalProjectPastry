package com.pastrygame;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
git add . — барлық өзгерістерді индексқа қосып,
git commit -m "commit туралы қысқаша түсінік" — локал commit жасап,
git pull --rebase origin main — GitHub-тағы соңғы өзгерістерді алып, өз коммиттеріңді соның үстіне қою,
git push origin main —