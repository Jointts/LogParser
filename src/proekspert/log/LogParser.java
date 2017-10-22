package proekspert.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class responsible for parsing the log file
 */
class LogParser {

    /**
     * The amount of tokens (whitespace separated words)
     * in a single line of the log file which does not contain any
     * payload parameters. Used as there may be 0...n of the parameters,
     * the amount of these is calculated as tokens.size() - BASE_TOKEN_LENGTH
     */
    private final int BASE_TOKEN_LENGTH = 7;

    private final int TOKEN_DATE = 0;

    private final int TIMESTAMP_WITH_MS = 1;

    private final int THREAD_ID = 2;

    private final int USER_CONTEXT = 3;

    private final int REQUEST_URI = 4;

    private final int PAYLOAD_PARAMETERS = 5;

    /**
     * Wrapper function for parsing the log file
     * @param fileURI String of the file path to read
     * @return The List of parsed LogEntry objects of the scanned file
     */
    List<LogEntry> parse(String fileURI) {
        List<String> lines = readFile(fileURI);
        return convertLines(lines);
    }

    /**
     * Read a file into a String buffer
     * @param fileName String of the file path to read
     * @return List of strings, in the form of the lines of the parsed log
     */
    private List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get("./" + fileName))) {
            lines = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Could not open file, log file may be corrupted or missing");
            System.exit(-1);
        }

        return lines;
    }

    /**
     * Serialize list of Strings to LogEntry objects
     * @param lines List of strings, in the form of the lines of the parsed log
     * @return List of LogEntry objects that are converted from the String list
     */
    private List<LogEntry> convertLines(List<String> lines) {
        List<LogEntry> logEntryList = new ArrayList<>();

        lines.forEach(line -> {
            Map<String, String> userContextMap = new HashMap<>();
            List<String> tokens = Arrays.asList(line.split(" "));
            List<String> payloadParams = new ArrayList<>();

            int tokenSize = tokens.size();
            int requestDuration = tokenSize - 1;
            int requestDurationMs = Integer.parseInt(tokens.get(requestDuration));

            String[] userContext = tokens.get(USER_CONTEXT).replaceAll("[\\[\\]]", "").split(":");
            if (!userContext[0].isEmpty()) {
                userContextMap.put(userContext[0], userContext[1]);
            }

            // If not equal then log entry contains payload parameters
            if (tokens.size() != BASE_TOKEN_LENGTH) {
                int numOfPayloadElements = tokens.size() - BASE_TOKEN_LENGTH;
                for (int tokenPos = PAYLOAD_PARAMETERS; tokenPos <= REQUEST_URI + numOfPayloadElements; tokenPos++) {
                    payloadParams.add(tokens.get(tokenPos));
                }
            }

            String requestURI = tokens.get(REQUEST_URI);

            String threadId = tokens.get(THREAD_ID);

            String[] time = tokens.get(TIMESTAMP_WITH_MS).split(",");

            LocalTime timestamp = LocalTime.parse(time[0]);

            timestamp = timestamp.plusNanos(Long.parseLong(time[1]) * 1000000);

            LocalDate date = LocalDate.parse(tokens.get(TOKEN_DATE));

            LogEntry logEntry = LogEntry.newBuilder()
                    .requestDurationMs(requestDurationMs)
                    .requestURI(requestURI)
                    .threadId(threadId)
                    .userContext(userContextMap)
                    .payloadParams(payloadParams)
                    .date(date)
                    .timestamp(timestamp)
                    .build();

            logEntryList.add(logEntry);
        });

        return logEntryList;
    }
}
