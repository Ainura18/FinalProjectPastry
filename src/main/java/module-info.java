module org.example.erdtermproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.erdtermproject to javafx.fxml;
    exports org.example.erdtermproject;
}