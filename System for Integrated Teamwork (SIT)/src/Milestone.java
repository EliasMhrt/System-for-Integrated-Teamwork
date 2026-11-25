/** Holds information about one milestone. */
public class Milestone {
    /** The kind checkin value. */
    public static final String KIND_CHECKIN = "Progress Check-in";
    /** The kind milestone value. */
    public static final String KIND_MILESTONE = "Milestone";

    private String title;
    private String date;
    private String kind;

    /**
     * Creates a new Milestone.
     * @param title the title
     * @param date the date
     * @param kind the kind
     */
    public Milestone(String title, String date, String kind) {
        this.title = title;
        this.date = date;
        this.kind = kind;
    }

    /**
     * Gets title.
     * @return the result
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets date.
     * @return the result
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Gets kind.
     * @return the result
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Sets kind.
     * @param kind the kind
     */
    public void setKind(String kind) {
        boolean allowed = false;
        if (kind.equals(KIND_CHECKIN) || kind.equals(KIND_MILESTONE)) {
            allowed = true;
        }
        if (allowed) {
            this.kind = kind;
        }
    }

    /**
     * Returns a short text for this object.
     * @return the result
     */
    public String toString() {
        String text = this.date + " | " + this.kind + ": " + this.title;
        return text;
    }
}
