package proekspert.log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Object representation of a single log line
 */
public class LogEntry implements Comparable<LogEntry> {
    private LocalDate date;
    private LocalTime timestamp;
    private String threadId;
    private Map<String, String> userContext;
    private String requestURI;
    private List<String> payloadParams;
    private int requestDurationMs;

    private LogEntry(Builder builder) {
        date = builder.date;
        timestamp = builder.timestamp;
        threadId = builder.threadId;
        userContext = builder.userContext;
        requestURI = builder.requestURI;
        payloadParams = builder.payloadParams;
        requestDurationMs = builder.requestDurationMs;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getThreadId() {
        return threadId;
    }

    public Map<String, String> getUserContext() {
        return userContext;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public List<String> getPayloadParams() {
        return payloadParams;
    }

    public int getRequestDurationMs() {
        return requestDurationMs;
    }

    // Usually a resource is a REST endpoint without query params,
    // it is usually a model exposed via URI (for example: /users)
    // which can have CRUD endpoints <- however these are considered as
    // operations and should not count as separate resource entities
    // Identifying a resource as such is not feasible for automation
    // as the computer needs to understand the context of the request.

    /**
     * Converts the full query url to a resource URI
     * @return String without query params
     */
    public String extractResourceName() {
        if (requestURI.contains("?")) {
            return requestURI.split("\\?")[0];
        }
        return requestURI;
    }

    @Override
    public int compareTo(LogEntry otherEntry) {
        return otherEntry.requestDurationMs - this.requestDurationMs;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private LocalDate date;
        private LocalTime timestamp;
        private String threadId;
        private Map<String, String> userContext;
        private String requestURI;
        private List<String> payloadParams;
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

        public Builder payloadParams(List<String> val) {
            payloadParams = val;
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
