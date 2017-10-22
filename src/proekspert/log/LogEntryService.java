package proekspert.log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class responsible for sorting log entries
 */
class LogEntryService {

    /**
     * Calculates the number of hourly requests
     *
     * @param logEntryList Parsed List of LogEntry objects
     * @return Map, of which the first element contains the hour,
     * second parameter the amount of requests made on the specific hour
     */
    Map<String, Integer> getHourlyRequests(List<LogEntry> logEntryList) {
        Map<String, Integer> hourlyRequests = new HashMap<>();

        logEntryList.forEach(logEntry -> {
            hourlyRequests.computeIfPresent(String.valueOf(logEntry.getTimestamp().getHour()), (hour, value) -> value + 1);
            hourlyRequests.putIfAbsent(String.valueOf(logEntry.getTimestamp().getHour()), 1);
        });

        return hourlyRequests;
    }

    /**
     * Calculates N longest responding resources
     * (average of all requests to a single resource)
     *
     * @param logEntryList Parsed List of LogEntry objects
     * @param responsesToExtract The number of longest responding resources to show
     * @return Map of which the first parameter contains the resource URI,
     * second parameter the average duration of the requests made to the resource
     */
    Map<String, Integer> calculateAverageResourceTime(List<LogEntry> logEntryList, int responsesToExtract) {
        Map<String, Integer> averageTimeLogEntryMap = new HashMap<>();

        logEntryList.forEach(logEntry -> {
            averageTimeLogEntryMap.computeIfPresent(logEntry.extractResourceName(), (logEntryInstance, requestDuration) -> requestDuration + logEntry.getRequestDurationMs());
            averageTimeLogEntryMap.putIfAbsent(logEntry.extractResourceName(), logEntry.getRequestDurationMs());
        });

        return averageTimeLogEntryMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(responsesToExtract)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

}