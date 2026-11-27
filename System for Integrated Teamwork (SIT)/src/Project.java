import java.util.ArrayList;

/** Holds information about one project. */
public class Project {
    private int id;
    private String name;
    private String description;
    private String deadline;
    private ArrayList<Student> team;
    private ArrayList<Task> tasks;
    private ArrayList<Milestone> milestones;
    private ArrayList<LogEntry> log;
    private int nextTaskId;

    /**
     * Creates a new Project.
     * @param id the ID
     * @param name the name
     * @param description the description
     * @param deadline the deadline
     */
    public Project(int id, String name, String description, String deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.team = new ArrayList<Student>();
        this.tasks = new ArrayList<Task>();
        this.milestones = new ArrayList<Milestone>();
        this.log = new ArrayList<LogEntry>();
        this.nextTaskId = 1;
    }

    /**
     * Gets id.
     * @return the result
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets name.
     * @return the result
     */
    public String getName() {
        return this.name;
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
     * Gets team.
     * @return the result
     */
    public ArrayList<Student> getTeam() {
        return this.team;
    }

    /**
     * Gets tasks.
     * @return the result
     */
    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Gets milestones.
     * @return the result
     */
    public ArrayList<Milestone> getMilestones() {
        return this.milestones;
    }

    /**
     * Gets log.
     * @return the result
     */
    public ArrayList<LogEntry> getLog() {
        return this.log;
    }

    /**
     * Gets next task id.
     * @return the result
     */
    public int getNextTaskId() {
        return this.nextTaskId;
    }

    /**
     * Sets name.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets next task id.
     * @param nextTaskId the next task ID
     */
    public void setNextTaskId(int nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    /**
     * Adds a student to the project team.
     * @param student the student
     * @return the result
     */
    public boolean addTeamMember(Student student) {
        boolean added = false;
        boolean alreadyThere = false;

        int i = 0;
        while (i < this.team.size()) {
            Student current = this.team.get(i);
            if (current.getId().equals(student.getId())) {
                alreadyThere = true;
            }
            i = i + 1;
        }

        if (alreadyThere == false) {
            this.team.add(student);
            added = true;
        }

        return added;
    }

    /**
     * Adds a new task to the project.
     * @param title the title
     * @param description the description
     * @param deadline the deadline
     * @param assignedStudentId the assigned student ID
     * @return the result
     */
    public Task addTask(String title, String description, String deadline, String assignedStudentId) {
        if (title == null || title.length() == 0) {
            return null;
        }
        if (deadline == null || deadline.length() == 0) {
            return null;
        }
        Task newTask = new Task(this.nextTaskId, title, description, deadline, assignedStudentId);
        this.tasks.add(newTask);
        this.nextTaskId = this.nextTaskId + 1;
        return newTask;
    }

    /**
     * Adds a new milestone to the project.
     * @param title the title
     * @param date the date
     * @param kind the kind
     * @return the result
     */
    public Milestone addMilestone(String title, String date, String kind) {
        if (title == null || title.length() == 0) {
            return null;
        }
        if (date == null || date.length() == 0) {
            return null;
        }
        Milestone newMilestone = new Milestone(title, date, kind);
        this.milestones.add(newMilestone);
        return newMilestone;
    }

    /**
     * Adds a new text entry to the project log.
     * @param author the author
     * @param message the message
     */
    public void addLog(String author, String message) {
        if (message == null || message.length() == 0) {
            return;
        }
        LogEntry entry = new LogEntry(author, message);
        this.log.add(entry);
    }

    /**
     * Adds a saved text entry to the project log.
     * @param author the author
     * @param time the time
     * @param message the message
     */
    public void addSavedLog(String author, String time, String message) {
        LogEntry entry = new LogEntry(author, time, message);
        this.log.add(entry);
    }

    /**
     * Finds a task with the given ID.
     * @param taskId the task ID
     * @return the result
     */
    public Task findTaskById(int taskId) {
        Task found = null;
        int i = 0;
        while (i < this.tasks.size()) {
            Task current = this.tasks.get(i);
            if (current.getId() == taskId) {
                found = current;
            }
            i = i + 1;
        }
        return found;
    }

    /**
     * Checks if a student is in the project team.
     * @param studentId the student ID
     * @return the result
     */
    public boolean isStudentOnTeam(String studentId) {
        boolean found = false;
        int i = 0;
        while (i < this.team.size()) {
            Student current = this.team.get(i);
            if (current.getId().equals(studentId)) {
                found = true;
            }
            i = i + 1;
        }
        return found;
    }

    /**
     * Gets tasks and milestones that are coming soon.
     * @return the result
     */
    public ArrayList<String> getUpcomingItems() {
        ArrayList<String> items = new ArrayList<String>();
        int i = 0;
        while (i < this.milestones.size()) {
            items.add(this.milestones.get(i).toString());
            i = i + 1;
        }
        int j = 0;
        while (j < this.tasks.size()) {
            Task current = this.tasks.get(j);
            items.add(current.getDeadline() + " | " + current.getTitle());
            j = j + 1;
        }
        return items;
    }

    /**
     * Gets the number of tasks that are done.
     * @return the result
     */
    public int getDoneTaskCount() {
        int count = 0;
        int i = 0;
        while (i < this.tasks.size()) {
            Task current = this.tasks.get(i);
            if (current.isDone()) {
                count = count + 1;
            }
            i = i + 1;
        }
        return count;
    }

    /**
     * Gets the project progress as a percent.
     * @return the result
     */
    public int getProgressPercent() {
        int percent = 0;
        int total = this.tasks.size();
        if (total > 0) {
            int done = this.getDoneTaskCount();
            percent = (done * 100) / total;
        }
        return percent;
    }

    /**
     * Gets a short summary for the dashboard.
     * @return the result
     */
    public String getDashboardSummary() {
        String text = "Project " + this.id + ": " + this.name;
        text = text + " | Deadline: " + this.deadline;
        text = text + " | Tasks: " + this.tasks.size();
        text = text + " | Done: " + this.getDoneTaskCount();
        text = text + " | Progress: " + this.getProgressPercent() + "%";
        return text;
    }

    /**
     * Returns a short text for this object.
     * @return the result
     */
    public String toString() {
        String text = "Project " + this.id + ": " + this.name;
        text = text + " | Deadline: " + this.deadline;
        text = text + " | Team size: " + this.team.size();
        text = text + " | Tasks: " + this.tasks.size();
        text = text + " | Progress: " + this.getProgressPercent() + "%";
        return text;
    }
}
