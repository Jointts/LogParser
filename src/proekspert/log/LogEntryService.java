package proekspert.log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class responsible for sorting/calculating log entries
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
        Map<String, AbstractMap.SimpleEntry<Integer, Integer>> averageTimeLogEntryMap = new HashMap<>();
        Map<String, Integer> output = new HashMap<>();

        // This seems cryptic, but actually the principle is to have something along the
        // lines of a structure <requestURI> <numberOfRequests> <totalOfAllRequestsInMs>
        // so we can later on do <requestURI> <totalOfAllRequestsInMs/numberOfRequests>
        // to get the average length of requests to a specific requestURI/resource

        logEntryList.forEach(logEntry -> {
            averageTimeLogEntryMap.computeIfPresent(logEntry.extractResourceName(), (requestURI, simpleEntry) -> new AbstractMap.SimpleEntry<>(simpleEntry.getKey() + 1, simpleEntry.getValue() + logEntry.getRequestDurationMs()));
            averageTimeLogEntryMap.putIfAbsent(logEntry.extractResourceName(), new AbstractMap.SimpleEntry<>(1, logEntry.getRequestDurationMs()));
        });

        averageTimeLogEntryMap.forEach((requestURI, simpleEntry) -> output.put(requestURI, simpleEntry.getValue() / simpleEntry.getKey()));

        return output.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(responsesToExtract)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

}