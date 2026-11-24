import java.time.LocalDate;
import java.util.ArrayList;

/** Builds reminder messages for one student. */
public class Notifications {
    private SystemManager manager;

    /**
     * Creates a new Notifications.
     * @param manager the system manager
     */
    public Notifications(SystemManager manager) {
        this.manager = manager;
    }

    /**
     * Builds reminder messages for a student.
     * @param student the student
     * @return the result
     */
    public ArrayList<String> buildFor(Student student) {
        ArrayList<String> messages = new ArrayList<String>();

        if (student != null) {
            if (student.getNotifyEnabled() == false) {
                messages.add("Notifications are turned off in your settings.");
            } else {
                ArrayList<Project> projects = this.manager.getProjects();
                int i = 0;
                while (i < projects.size()) {
                    Project project = projects.get(i);
                    if (project.isStudentOnTeam(student.getId())) {
                        int projectDays = daysUntil(project.getDeadline());
                        if (projectDays <= student.getNotifyDays()) {
                            this.addMessage(messages, "Project deadline: " + project.getName(),
                                    project.getDeadline(), projectDays);
                        }

                        int j = 0;
                        while (j < project.getMilestones().size()) {
                            Milestone milestone = project.getMilestones().get(j);
                            int milestoneDays = daysUntil(milestone.getDate());
                            if (milestoneDays <= student.getNotifyDays()) {
                                this.addMessage(messages, milestone.getKind() + ": "
                                        + milestone.getTitle() + " (" + project.getName() + ")",
                                        milestone.getDate(), milestoneDays);
                            }
                            j = j + 1;
                        }

                        int k = 0;
                        while (k < project.getTasks().size()) {
                            Task task = project.getTasks().get(k);
                            if (task.getAssignedStudentId().equals(student.getId())
                                    && task.isDone() == false) {
                                int taskDays = daysUntil(task.getDeadline());
                                if (taskDays <= student.getNotifyDays()) {
                                    this.addMessage(messages, "Your task: " + task.getTitle()
                                            + " (" + project.getName() + ")",
                                            task.getDeadline(), taskDays);
                                }
                            }
                            k = k + 1;
                        }
                    }
                    i = i + 1;
                }

                if (messages.size() == 0) {
                    messages.add("No deadlines need your attention right now.");
                }
            }
        }

        return messages;
    }

    private void addMessage(ArrayList<String> messages, String label, String date, int days) {
        String when = "";
        if (days < 0) {
            int late = days * -1;
            when = "OVERDUE by " + late + " day(s)";
        } else if (days == 0) {
            when = "DUE TODAY";
        } else {
            when = "due in " + days + " day(s)";
        }
        messages.add(label + " | " + date + " | " + when);
    }

    /**
     * Gets the number of days until a date.
     * @param date the date
     * @return the result
     */
    public static int daysUntil(String date) {
        int answer = 9999;
        try {
            long today = LocalDate.now().toEpochDay();
            long target = LocalDate.parse(date).toEpochDay();
            long difference = target - today;
            answer = (int) difference;
        } catch (Exception error) {
            answer = 9999;
        }
        return answer;
    }
}
