
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {

    public static final String LEVEL = "Level 1";

    private String author;
    private String time;
    private String message;

    public LogEntry(String author, String message) {
        this.author = author;
        this.message = message;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public LogEntry(String author, String time, String message) {
        this.author = author;
        this.time = time;
        this.message = message;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getTime() {
        return this.time;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        String text = "[" + this.time + "] " + this.author + ": " + this.message;
        return text;
    }
}
