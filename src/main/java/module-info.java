module org.example.payingcards {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.payingcards to javafx.fxml;
    exports org.example.payingcards;
}