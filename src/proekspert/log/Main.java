package proekspert.log;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main extends Application {

    private static final String HELP_TEXT = "Usage: proekspert.jar <filename:string> <n top resources:int>";
    private static final String NOT_ENOUGH_PARAMS = "Not enough parameters supplied";
    private static final String SECOND_PARAM_MUST_BE_NUMBER = "Second paramater should be a number";

    private LogParser logParser = new LogParser();

    private LogEntryService logEntryService = new LogEntryService();

    private List<LogEntry> logEntryList = new ArrayList<>();

    private String fileURI;

    private int resourcesNum;

    long timeBefore;

    @Override
    public void init() throws Exception {
        timeBefore = System.currentTimeMillis();
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        getParams();
        logEntryList = logParser.parse(fileURI);

        drawHistogram(primaryStage);
    }

    private void getParams() throws Exception {

        if(getParameters().getUnnamed().contains("-h")){
            System.out.println(HELP_TEXT);
            System.exit(0);
        }

        if (getParameters().getUnnamed().size() != 2) throw new Exception(NOT_ENOUGH_PARAMS);

        fileURI = getParameters().getUnnamed().get(0);

        try {
            resourcesNum = Integer.parseInt(getParameters().getUnnamed().get(1));
        } catch (NumberFormatException e) {
            throw new Exception(SECOND_PARAM_MUST_BE_NUMBER);
        }
    }

    public void drawHistogram(Stage primaryStage){
        Map<String, Integer> hourlyRequests = logEntryService.getHourlyRequests(logEntryList);

        GridPane root = new GridPane();
        primaryStage.setTitle("ProEkspert - Log parser");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        Collections.sort(logEntryList);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        barChart.setPadding(new Insets(25, 0, 0, 0));

        xAxis.setLabel("Hour");
        yAxis.setLabel("Requests");

        XYChart.Series xyChart = new XYChart.Series();
        xyChart.setName("Hourly requests");

        hourlyRequests.forEach((hour, requests) -> xyChart.getData().add(new XYChart.Data<>(hour, requests)));

        barChart.getData().addAll(xyChart);

        VBox vBox = new VBox();
        VBox vBox2 = new VBox();
        VBox vBox3 = new VBox();
        vBox3.setFillWidth(true);

        TableView tableView = new TableView();

        List<LogEntry> nTopResources = logEntryList.subList(0, resourcesNum);

        TableColumn<LogEntry, String> timeColumn = new TableColumn<>("Time");
        TableColumn<LogEntry, String> resourceColumn = new TableColumn<>("Resource");

        timeColumn.setCellValueFactory(resource -> new SimpleStringProperty(String.valueOf(resource.getValue().getRequestDurationMs())));
        resourceColumn.setCellValueFactory(resource -> new SimpleStringProperty(resource.getValue().getRequestURI()));

        tableView.getColumns().addAll(timeColumn, resourceColumn);
        tableView.setItems(new ObservableListWrapper<>(nTopResources));

        vBox2.getChildren().add(tableView);

        vBox.setPadding(new Insets(0, 50, 25, 50));
        vBox2.setPadding(new Insets(0,50,0,50));
        vBox3.setPadding(new Insets(0,50,0,50));

        vBox3.getChildren().add(barChart);

        long deltaTime = System.currentTimeMillis() - timeBefore;

        Text timeTaken = new Text("Time taken - " + deltaTime + "ms");
        vBox.getChildren().add(timeTaken);

        root.add(vBox3, 0, 0, 1, 1);
        root.add(vBox, 0, 2, 1, 1);
        root.add(vBox2, 0, 1, 1, 1);

        root.setVgap(25);

        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.show();
    }
}
