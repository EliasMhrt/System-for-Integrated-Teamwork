public class Milestone {

    public static final String LEVEL = "Level 1";

    public static final String KIND_CHECKIN = "Progress Check-in";
    public static final String KIND_MILESTONE = "Milestone";

    private String title;
    private String date;
    private String kind;

    public Milestone(String title, String date, String kind) {
        this.title = title;
        this.date = date;
        this.kind = kind;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDate() {
        return this.date;
    }

    public String getKind() {
        return this.kind;
    }

    public String toString() {
        String text = this.date + " | " + this.kind + ": " + this.title;
        return text;
    }
}
