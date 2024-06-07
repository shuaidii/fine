
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

public class Main extends Application {
    public static int pageNo = 1;
    public String strWel = "Welcome\n" +
            "This app will allow you to see Covid data in the London area for a chosen date range.\n" +
            "To navigate between the Panels use the buttons in the bottom corners.\n" +
            "Please enter desired date range above. Earliest = 03/02/2020. Latest = 09/02/2023";
    public String strDate = "";
    public LocalDate startTime;
    public LocalDate endTime;
    boolean isPrime1 = false;
    boolean isPrime2 = false;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // initialise pane
        AnchorPane root1 = new AnchorPane();
        Scene scene1 = new Scene(root1, 900, 700);
        scene1.getStylesheets().add("mainStyle.css");

        // initialise labels
        Label lbDateFrom = new Label();
        lbDateFrom.setLayoutX(170);
        lbDateFrom.setLayoutY(10);
        lbDateFrom.setText("From");

        Label lbDateTo = new Label();
        lbDateTo.setLayoutX(470);
        lbDateTo.setLayoutY(10);
        lbDateTo.setText("To");

        Label lbWel = new Label();
        lbWel.setLayoutX(200);
        lbWel.setLayoutY(300);
        lbWel.setText(strWel);

        Label lbDate = new Label();
        lbDate.setLayoutX(200);
        lbDate.setLayoutY(400);
        lbDate.setText(strDate);

        // initialise datepickers
        DatePicker fromDataPicker = new DatePicker();
        fromDataPicker.setLayoutX(220);
        fromDataPicker.setLayoutY(10);

        DatePicker toDataPicker = new DatePicker();
        toDataPicker.setLayoutX(500);
        toDataPicker.setLayoutY(10);

        // initialise buttons & other nodes

        Line l1 = new Line(0, 50, 1000, 50);
        Line l2 = new Line(0, 650, 1000, 650);

        Button previewBtn = new Button("<<");
        previewBtn.setLayoutX(20);
        previewBtn.setLayoutY(660);

        Button nextBtn = new Button(">>");
        nextBtn.setLayoutX(850);
        nextBtn.setLayoutY(660);

        // from both datepickers, remove all dates before the earliest or after latest dates in data file
        LocalDate earliest = LocalDate.of(2020, 2, 3);
        LocalDate latest = LocalDate.of(2023, 12, 1);
        fromDataPicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean blank) {
                super.updateItem(date, blank);
                setDisable(blank || date.compareTo(earliest) < 0 || date.compareTo(latest) > 0);
            }
        });
        toDataPicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean blank) {
                super.updateItem(date, blank);
                setDisable(blank || date.compareTo(earliest) < 0 || date.compareTo(latest) > 0);
            }
        });

        // datepicker actions when date is selected
        fromDataPicker.setOnAction(event -> {
            strDate = "Date Selected = From: " + fromDataPicker.getValue() + " To: " + toDataPicker.getValue();
            lbDate.setText(strDate);
            startTime = fromDataPicker.getValue();
            lbDate.setText(strDate);
            startTime = fromDataPicker.getValue();
            fromDataPicker.setDisable(true);
            isPrime1 = true;
            if (isPrime1 && isPrime2) {
                previewBtn.setDisable(false);
                nextBtn.setDisable(false);
            }

        });

        toDataPicker.setOnAction(event -> {
            if (toDataPicker.getValue().isAfter(fromDataPicker.getValue())) {
                strDate = "Date Selected = From: " + fromDataPicker.getValue() + " To " + toDataPicker.getValue();
                lbDate.setText(strDate);
            } else { // do not allow selections where date "from" is after date "to"
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Selection");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    toDataPicker.setValue(fromDataPicker.getValue().plusDays(1));
                }
                if (result.get() == ButtonType.NO) {
                    toDataPicker.setValue(fromDataPicker.getValue().plusDays(1));
                }
            }
            endTime = toDataPicker.getValue();
            toDataPicker.setDisable(true);
            isPrime2 = true;
            if (isPrime2 && isPrime1) {
                previewBtn.setDisable(false);
                nextBtn.setDisable(false);
            }
        });

        previewBtn.setVisible(false);

        previewBtn.setOnAction(event -> {
            pageNo--;
            lbWel.setText("");

            lbDate.setText("");

            if (pageNo == 1) {
                lbWel.setText(strWel);

                lbDate.setText(strDate);
                previewBtn.setVisible(false);
            }
            if (pageNo == 2) {
                previewBtn.setVisible(true);
                nextBtn.setVisible(true);
            }
            if (pageNo == 3) {
                nextBtn.setVisible(false);
            }
        });

        nextBtn.setOnAction(event -> {
            pageNo++;
            lbWel.setText("");

            lbDate.setText("");

            if (pageNo == 1) {
                lbWel.setText(strWel);

                lbDate.setText(strDate);
                previewBtn.setVisible(false);
            }
            if (pageNo == 2) {
                previewBtn.setVisible(true);
                nextBtn.setVisible(true);
            }
            if (pageNo == 3) {
                nextBtn.setVisible(false);
            }
        });

        root1.getChildren().addAll(lbWel, lbDate, fromDataPicker, toDataPicker, l1, l2, lbDateFrom, lbDateTo, previewBtn, nextBtn);

        // show application window
        primaryStage.setScene(scene1);
        primaryStage.setTitle("LDN Shock - COVID19 in London");
        primaryStage.show();
    }
}