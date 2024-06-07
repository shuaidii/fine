import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FirstPane extends Pane {
    public String strWel = "Welcome\n" +
            "This app will allow you to see Covid data in the London area for a chosen date range.\n" +
            "To navigate between the Panels use the buttons in the bottom corners.\n" +
            "Please enter desired date range above. Earliest = %s. Latest = %s";

    public Label lbDate, lbWel;

    public FirstPane() {
        this.setLayoutX(75);
        this.setLayoutY(50);
        // 大小是750*600
        this.setPrefWidth(750);
        this.setPrefHeight(600);
    }

    public FirstPane(LocalDate earliest, LocalDate latest) {
        this();

        lbWel = new Label();
        lbWel.setLayoutX(80);
        lbWel.setLayoutY(250);
        lbWel.setText(String.format(strWel, earliest.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                latest.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        lbDate = new Label();
        lbDate.setLayoutX(80);
        lbDate.setLayoutY(350);
        lbDate.setText("");

        this.getChildren().addAll(lbWel, lbDate);
    }

    public FirstPane(String color, LocalDate earliest, LocalDate latest) {
        this(earliest, latest);
        this.setStyle("-fx-background-color: " + color + ";");
    }

    public void setSelectDateRange(String strDate) {
        lbDate.setText(strDate);
    }

}
