import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Holds one entry for a project log. */
public class LogEntry {
    private String author;
    private String time;
    private String message;

    /**
     * Creates a new LogEntry.
     * @param author the author
     * @param message the message
     */
    public LogEntry(String author, String message) {
        this.author = author;
        this.message = message;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Creates a new LogEntry.
     * @param author the author
     * @param time the time
     * @param message the message
     */
    public LogEntry(String author, String time, String message) {
        this.author = author;
        this.time = time;
        this.message = message;
        if (this.author.length() == 0) {
            this.author = "Unknown";
        }
        if (this.time.length() == 0) {
            this.time = "unknown time";
        }
        if (this.message.length() == 0) {
            this.message = "No message";
        }
    }

    /**
     * Gets author.
     * @return the result
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Gets time.
     * @return the result
     */
    public String getTime() {
        return this.time;
    }

    /**
     * Gets message.
     * @return the result
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets a short form of the time.
     * @return the result
     */
    public String getShortTime() {
        String shortTime = this.time;
        if (shortTime.length() > 10) {
            shortTime = shortTime.substring(0, 10);
        }
        return shortTime;
    }

    /**
     * Returns a short text for this object.
     * @return the result
     */
    public String toString() {
        String text = "[" + this.time + "] " + this.author + ": " + this.message;
        return text;
    }
}
