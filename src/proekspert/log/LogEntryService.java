package proekspert.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogEntryService {

    public Map<String, Integer> getHourlyRequests(List<LogEntry> logEntryList){
        Map<String, Integer> hourlyRequests = new HashMap<>();

        logEntryList.forEach(logEntry -> {
            hourlyRequests.computeIfPresent(String.valueOf(logEntry.getTimestamp().getHour()), (hour, value) -> value + 1);
            hourlyRequests.putIfAbsent(String.valueOf(logEntry.getTimestamp().getHour()), 1);
        });

        return hourlyRequests;
    }
}
