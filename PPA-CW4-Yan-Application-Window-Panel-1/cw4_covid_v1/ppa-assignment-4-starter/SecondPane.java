import javafx.scene.layout.Pane;

public class SecondPane extends Pane {

    public SecondPane() {
        this.setLayoutX(75);
        this.setLayoutY(50);
        this.setPrefWidth(750);
        this.setPrefHeight(600);
    }

    public SecondPane(String color) {
        this();
        this.setStyle("-fx-background-color: " + color + ";");
    }

}
