module org.example.erdtermproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.pastrygame to javafx.fxml;
    exports com.pastrygame;
}