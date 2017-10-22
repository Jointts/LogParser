package proekspert.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class LogParser {

    private final int BASE_TOKEN_LENGTH = 7;

    public List<LogEntry> parse(String fileURI){
        List<String> lines = readFile(fileURI);
        return convertLines(lines);
    }

    private List<String> readFile(String fileName){
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get("./" + fileName))) {
            lines = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    private List<LogEntry> convertLines(List<String> lines){
        List<LogEntry> logEntryList = new ArrayList<>();

        lines.forEach(line -> {
            Map<String, String> userContextMap = new HashMap<>();
            List<String> tokens = Arrays.asList(line.split(" "));
            List<String> payloadParams = new ArrayList<>();
            int tokenSize = tokens.size();

            int requestDurationMs = Integer.parseInt(tokens.get(tokenSize - 1));

            String[] userContext = tokens.get(3).replaceAll("[\\[\\]]", "").split(":");
            if(!userContext[0].isEmpty()){
                userContextMap.put(userContext[0], userContext[1]);
            }

            // If not equal then log entry contains payload parameters
            if(tokens.size() != BASE_TOKEN_LENGTH){
                int numOfPayloadElements = tokens.size() - BASE_TOKEN_LENGTH;
                for(int tokenPos = 5; tokenPos <= 4 + numOfPayloadElements; tokenPos++){
                    payloadParams.add(tokens.get(tokenPos));
                }
            }

            String requestURI = tokens.get(4);

            String threadId = tokens.get(2);

            String[] time = tokens.get(1).split(",");

            LocalTime timestamp = LocalTime.parse(time[0]);

            timestamp = timestamp.plusNanos(Long.parseLong(time[1]) * 1000000);

            LocalDate date = LocalDate.parse(tokens.get(0));

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
