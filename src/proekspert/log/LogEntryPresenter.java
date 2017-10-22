package proekspert.log;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class responsible for rendering the application
 */
class LogEntryPresenter {

    private Stage primaryStage;

    private GridPane root;

    /**
     * Initiates the root Node and the Stage, must be called before invoking
     * any other methods of this class
     *
     * @param primaryStage The JavaFX Stage that is supplied through the start fn
     */
    void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("ProEkspert - Log parser");
        root = new GridPane();
    }

    /**
     * Draws the hourly request histogram
     *
     * @param hourlyRequests Accepts a map of which the first parameter is the hour,
     *                       second parameter is the amount of requests on that specific hour
     */
    void drawHourlyRequestsHistogram(Map<String, Integer> hourlyRequests) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Hour");
        yAxis.setLabel("Requests");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series xyChart = new XYChart.Series();
        xyChart.setName("Hourly requests");

        hourlyRequests.forEach((hour, requests) -> xyChart.getData().add(new XYChart.Data<>(hour, requests)));

        barChart.getData().addAll(xyChart);

        VBox vBox = new VBox(barChart);

        GridPane.setHgrow(vBox, Priority.ALWAYS);
        vBox.setPadding(new Insets(0, 50, 0, 50));
        barChart.setPadding(new Insets(25, 0, 0, 0));

        root.add(vBox, 0, 0, 1, 1);
    }

    /**
     * Draws the top N resources that have the longest average request duration
     *
     * @param nTopResources Accepts a map of which the first parameter is the average
     *                      request duration, second parameter is the URI/name of the resource
     */
    void drawNTopResources(Map<String, Integer> nTopResources) {
        TableView tableView = new TableView();

        TableColumn<Map.Entry, String> timeColumn = new TableColumn<>("Time");
        TableColumn<Map.Entry, String> resourceColumn = new TableColumn<>("Resource");

        timeColumn.setCellValueFactory(resource -> new SimpleStringProperty(String.valueOf(resource.getValue().getKey())));
        resourceColumn.setCellValueFactory(resource -> new SimpleStringProperty(String.valueOf(resource.getValue().getValue())));

        tableView.getColumns().addAll(timeColumn, resourceColumn);
        tableView.setItems(new ObservableListWrapper(new ArrayList(nTopResources.entrySet())));

        VBox vBox = new VBox(tableView);

        vBox.setPadding(new Insets(25, 50, 0, 50));

        root.add(vBox, 0, 1, 1, 1);
    }

    /**
     * Draws the text to indicate how long the application ran
     *
     * @param applicationDuration The time the application was launched
     */
    void drawApplicationDuration(long applicationDuration) {
        Text timeTaken = new Text("Time taken - " + applicationDuration + "ms");

        VBox vBox = new VBox(timeTaken);
        vBox.setPadding(new Insets(25, 50, 25, 50));

        root.add(vBox, 0, 2, 1, 1);
    }

    /**
     * Draw/show the scene
     */
    void drawScene() {
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.show();
    }
}
