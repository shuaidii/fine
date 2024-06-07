
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends Application {
    public static int pageNo = 1;

    public LocalDate startTime;
    public LocalDate endTime;
    public FirstPane firstPane;
    public Pane secondPane, thirdPane;

    public List<CovidData> covidDataList;
    private LocalDate earliest;
    private LocalDate latest;
    private Set<LocalDate> availableLocalDates;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // init data
        covidDataList = new CovidDataLoader().load();
        parseDateRange();

        // initialise pane
        AnchorPane root1 = new AnchorPane();
        Scene scene1 = new Scene(root1, 900, 700);
        scene1.getStylesheets().add("mainStyle.css");

        // initialise labels
        Label lbDateFrom = new Label();
        lbDateFrom.setLayoutX(170);
        lbDateFrom.setLayoutY(10);
        lbDateFrom.setText("From");

        firstPane = new FirstPane("#2BEBE8", earliest, latest);

        initSecondAndThirdPaneForTest();

        Label lbDateTo = new Label();
        lbDateTo.setLayoutX(470);
        lbDateTo.setLayoutY(10);
        lbDateTo.setText("To");

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
        previewBtn.setDisable(true);

        Button nextBtn = new Button(">>");
        nextBtn.setLayoutX(850);
        nextBtn.setLayoutY(660);
        nextBtn.setDisable(true);

        // from both datepickers, remove all dates before the earliest or after latest dates in data file
//        LocalDate earliest = LocalDate.of(2020, 2, 3);
//        LocalDate latest = LocalDate.of(2023, 12, 1);
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
            firstPane.setSelectDateRange("Date Selected = From: " + fromDataPicker.getValue() + " To: " + toDataPicker.getValue());
            startTime = fromDataPicker.getValue();
            fromDataPicker.setDisable(true);
        });

        toDataPicker.setOnAction(event -> {
            if (fromDataPicker.getValue() == null) {
                // do not allow selections where date "from" is after date "to"
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Select from date first");
                alert.showAndWait();
                return;
            }
            endTime = toDataPicker.getValue();
            boolean valid = !toDataPicker.getValue().isBefore(fromDataPicker.getValue());
            if (valid) {
                Set<LocalDate> hasDataDates = new HashSet<>();
                LocalDate tmp = startTime;
                while (!tmp.isAfter(endTime)) {
                    if (availableLocalDates.contains(tmp)) {
                        hasDataDates.add(tmp);
                    }
                    tmp = tmp.plusDays(1);
                }
                valid = hasDataDates.size() > 0;
            }
            if (valid) {
                firstPane.setSelectDateRange("Date Selected = From: " + fromDataPicker.getValue() + " To " + toDataPicker.getValue());
                toDataPicker.setDisable(true);
                previewBtn.setDisable(false);
                nextBtn.setDisable(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Selection");
                alert.showAndWait();
            }
        });

        previewBtn.setVisible(false);

        previewBtn.setOnAction(event -> {
            pageNo--;
            root1.getChildren().removeAll(firstPane, secondPane, thirdPane);

            if (pageNo == 1) {
                root1.getChildren().add(firstPane);
                previewBtn.setVisible(false);
            } else if (pageNo == 2) {
                root1.getChildren().add(secondPane);
                previewBtn.setVisible(true);
                nextBtn.setVisible(true);
            } else if (pageNo == 3) {
                root1.getChildren().add(thirdPane);
                nextBtn.setVisible(false);
            }
        });

        nextBtn.setOnAction(event -> {
            pageNo++;
            root1.getChildren().removeAll(firstPane, secondPane, thirdPane);

            if (pageNo == 1) {
                root1.getChildren().add(firstPane);
                previewBtn.setVisible(false);
            } else if (pageNo == 2) {
                root1.getChildren().add(secondPane);
                previewBtn.setVisible(true);
                nextBtn.setVisible(true);
            } else if (pageNo == 3) {
                root1.getChildren().add(thirdPane);
                nextBtn.setVisible(false);
            }
        });

//        root1.getChildren().addAll(lbWel, lbDate, fromDataPicker, toDataPicker, l1, l2, lbDateFrom, lbDateTo, previewBtn, nextBtn);
        root1.getChildren().addAll(firstPane, fromDataPicker, toDataPicker, l1, l2, lbDateFrom, lbDateTo, previewBtn, nextBtn);

        // show application window
        primaryStage.setScene(scene1);
        primaryStage.setTitle("LDN Shock - COVID19 in London");
        primaryStage.show();
    }

    /**
     * 切换测试用
     */
    private void initSecondAndThirdPaneForTest() {
        // for test
        secondPane = new SecondPane("#f4da66");

        Label showLabel1 = new Label();
        showLabel1.setLayoutX(80);
        showLabel1.setLayoutY(250);
        showLabel1.setText("Second Pane");
        secondPane.getChildren().add(showLabel1);

        thirdPane = new SecondPane("b40530");
        Label showLabel2 = new Label();
        showLabel2.setLayoutX(80);
        showLabel2.setLayoutY(250);
        showLabel2.setText("Third Pane");
        thirdPane.getChildren().add(showLabel2);
    }

    /**
     * parseDateRange
     */
    private void parseDateRange() {
        availableLocalDates = new HashSet<>();
        earliest = LocalDate.parse(covidDataList.get(0).getDate());
        latest = LocalDate.parse(covidDataList.get(0).getDate());
        for (CovidData covidData : covidDataList) {
            LocalDate localDate = LocalDate.parse(covidData.getDate());
            availableLocalDates.add(localDate);
            if (localDate.isBefore(earliest)) {
                earliest = localDate;
            }
            if (localDate.isAfter(latest)) {
                latest = localDate;
            }
        }
    }

}