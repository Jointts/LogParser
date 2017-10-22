package proekspert.log;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class Main extends Application {

    private static final String HELP_TEXT = "Usage: proekspert.jar <filename:string> <n top resources:int>";
    private static final String NOT_ENOUGH_PARAMS = "Not enough parameters supplied";
    private static final String SECOND_PARAM_MUST_BE_NUMBER = "Second paramater should be a number";

    private LogParser logParser = new LogParser();

    private LogEntryService logEntryService = new LogEntryService();

    private LogEntryPresenter logEntryPresenter = new LogEntryPresenter();

    private String fileURI;

    private int resourcesNum;

    private long timeLaunched;

    @Override
    public void init() throws Exception {
        timeLaunched = System.currentTimeMillis();
        super.init();
    }

    /**
     * Application entry point
     * @param primaryStage JavaFX supplied Stage object
     * @throws Exception Throws an exception if file reading or parameter checking fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        getParams();

        List<LogEntry> logEntryList = logParser.parse(fileURI);

        Map<String, Integer> hourlyRequests = logEntryService.getHourlyRequests(logEntryList);
        Map<String, Integer> nTopResources = logEntryService.calculateAverageResourceTime(logEntryList, resourcesNum);
        long applicationDuration = getApplicationDuration(timeLaunched);

        logEntryPresenter.init(primaryStage);
        logEntryPresenter.drawHourlyRequestsHistogram(hourlyRequests);
        logEntryPresenter.drawNTopResources(nTopResources);
        logEntryPresenter.drawApplicationDuration(applicationDuration);

        logEntryPresenter.drawScene();
    }

    /**
     * Acquires the CLI parameters and checks if they are supplied
     *
     * @throws Exception Throws an exception if not enough parameters are supplied
     *                   or they are the wrong type
     */
    private void getParams() throws Exception {

        if (getParameters().getUnnamed().contains("-h")) {
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

    /**
     * @param launchTime The time in ms, when the application was launched as long
     * @return Long to indicate how long the application ran
     */
    private long getApplicationDuration(long launchTime) {
        return System.currentTimeMillis() - launchTime;
    }
}
