/** Holds information about one task. */
public class Task {
    /** The status to do value. */
    public static final String STATUS_TO_DO = "To Do";
    /** The status doing value. */
    public static final String STATUS_DOING = "In Progress";
    /** The status done value. */
    public static final String STATUS_DONE = "Done";
    /** The no student value. */
    public static final String NO_STUDENT = "-1";

    private int id;
    private String title;
    private String description;
    private String deadline;
    private String status;
    private String assignedStudentId;

    /**
     * Creates a new Task.
     * @param id the ID
     * @param title the title
     * @param description the description
     * @param deadline the deadline
     * @param assignedStudentId the assigned student ID
     */
    public Task(int id, String title, String description, String deadline, String assignedStudentId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = STATUS_TO_DO;
        this.assignedStudentId = assignedStudentId;
    }

    /**
     * Gets id.
     * @return the result
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets title.
     * @return the result
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets description.
     * @return the result
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets deadline.
     * @return the result
     */
    public String getDeadline() {
        return this.deadline;
    }

    /**
     * Gets status.
     * @return the result
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Gets assigned student id.
     * @return the result
     */
    public String getAssignedStudentId() {
        return this.assignedStudentId;
    }

    /**
     * Sets title.
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets description.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets deadline.
     * @param deadline the deadline
     */
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    /**
     * Sets status.
     * @param status the status
     */
    public void setStatus(String status) {
        boolean allowed = false;
        if (status.equals(STATUS_TO_DO) || status.equals(STATUS_DOING) || status.equals(STATUS_DONE)) {
            allowed = true;
        }
        if (allowed) {
            this.status = status;
        }
    }

    /**
     * Sets assigned student id.
     * @param assignedStudentId the assigned student ID
     */
    public void setAssignedStudentId(String assignedStudentId) {
        this.assignedStudentId = assignedStudentId;
    }

    /**
     * Checks if the task is done.
     * @return the result
     */
    public boolean isDone() {
        boolean answer = false;
        if (this.status.equals(STATUS_DONE)) {
            answer = true;
        }
        return answer;
    }

    /**
     * Returns a short text for this object.
     * @return the result
     */
    public String toString() {
        String text = "Task " + this.id + ": " + this.title;
        text = text + " | Deadline: " + this.deadline;
        text = text + " | Status: " + this.status;
        text = text + " | Assigned student ID: " + this.assignedStudentId;
        return text;
    }
}
