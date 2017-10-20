package proekspert.log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class LogEntry implements Comparable<LogEntry> {
    private LocalDate date;
    private LocalTime timestamp;
    private String threadId;
    private Map<String, String> userContext;
    private String requestURI;
    private int requestDurationMs;

    private LogEntry(Builder builder) {
        date = builder.date;
        timestamp = builder.timestamp;
        threadId = builder.threadId;
        userContext = builder.userContext;
        requestURI = builder.requestURI;
        requestDurationMs = builder.requestDurationMs;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public int compareTo(LogEntry otherEntry) {
        return otherEntry.requestDurationMs - this.requestDurationMs;
    }


    public static final class Builder {
        private LocalDate date;
        private LocalTime timestamp;
        private String threadId;
        private Map<String, String> userContext;
        private String requestURI;
        private int requestDurationMs;

        private Builder() {
        }

        public Builder date(LocalDate val) {
            date = val;
            return this;
        }

        public Builder timestamp(LocalTime val) {
            timestamp = val;
            return this;
        }

        public Builder threadId(String val) {
            threadId = val;
            return this;
        }

        public Builder userContext(Map<String, String> val) {
            userContext = val;
            return this;
        }

        public Builder requestURI(String val) {
            requestURI = val;
            return this;
        }

        public Builder requestDurationMs(int val) {
            requestDurationMs = val;
            return this;
        }

        public LogEntry build() {
            return new LogEntry(this);
        }
    }
}
