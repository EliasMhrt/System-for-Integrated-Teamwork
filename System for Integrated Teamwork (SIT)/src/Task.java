public class Task {

    public static final String LEVEL = "Level 1";

    public static final String STATUS_TO_DO = "To Do";
    public static final String STATUS_DOING = "In Progress";
    public static final String STATUS_DONE = "Done";
    public static final String NO_STUDENT = "-1";

    private int id;
    private String title;
    private String description;
    private String deadline;
    private String status;
    private String assignedStudentId;

    public Task(int id, String title, String description, String deadline, String assignedStudentId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = STATUS_TO_DO;
        this.assignedStudentId = assignedStudentId;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDeadline() {
        return this.deadline;
    }

    public String getStatus() {
        return this.status;
    }

    public String getAssignedStudentId() {
        return this.assignedStudentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedStudentId(String assignedStudentId) {
        this.assignedStudentId = assignedStudentId;
    }

    public boolean isDone() {
        boolean answer = false;
        if (this.status.equals(STATUS_DONE)) {
            answer = true;
        }
        return answer;
    }

    public String toString() {
        String text = "Task " + this.id + ": " + this.title;
        text = text + " | Deadline: " + this.deadline;
        text = text + " | Status: " + this.status;
        text = text + " | Assigned student ID: " + this.assignedStudentId;
        return text;
    }
}
