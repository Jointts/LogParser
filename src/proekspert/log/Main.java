package proekspert.log;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.List;

public class Main extends Application {

    private static final String HELP_TEXT = "Usage: proekspert.jar <filename:string> <n top resources:int>";
    private static final String NOT_ENOUGH_PARAMS = "Not enough parameters supplied";
    private static final String SECOND_PARAM_MUST_BE_NUMBER = "Second paramater should be a number";

    private LogParser logParser = new LogParser();

    @Override
    public void init() throws Exception {
        long timeBefore = System.currentTimeMillis();

        if(getParameters().getUnnamed().contains("-h")){
            System.out.println(HELP_TEXT);
            System.exit(0);
        }

        if (getParameters().getUnnamed().size() != 2) throw new Exception(NOT_ENOUGH_PARAMS);

        String fileURI = getParameters().getUnnamed().get(0);
        int resourcesNum;

        try {
            resourcesNum = Integer.parseInt(getParameters().getUnnamed().get(1));
        } catch (NumberFormatException e) {
            throw new Exception(SECOND_PARAM_MUST_BE_NUMBER);
        }

        List<LogEntry> logEntries = logParser.parse(fileURI);

        Collections.sort(logEntries);

        super.init();

        long deltaTime = System.currentTimeMillis() - timeBefore;

        System.out.println("Program ran for " + deltaTime + " milliseconds");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        primaryStage.setTitle("ProEkspert - Log parser");
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.show();
    }
}
