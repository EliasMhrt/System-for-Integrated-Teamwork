import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class SystemManager {

    public static final String LEVEL = "Level 5";

    private ArrayList<Student> students;
    private ArrayList<Project> projects;
    private int nextProjectId;
    private String dataFileName;

    public SystemManager(String dataFileName) {
        this.students = new ArrayList<Student>();
        this.projects = new ArrayList<Project>();
        this.nextProjectId = 1;
        this.dataFileName = dataFileName;
    }

    public void clear() {
        this.students.clear();
        this.projects.clear();
        this.nextProjectId = 1;
    }

    public ArrayList<Student> getStudents() {
        return this.students;
    }

    public ArrayList<Project> getProjects() {
        return this.projects;
    }

    public Student addStudent(String id, String name, String studentNumber, String email, String role) {
        String chosenRole = role;
        if (chosenRole.equals(Student.ROLE_LEAD) == false
                && chosenRole.equals(Student.ROLE_COORDINATOR) == false
                && chosenRole.equals(Student.ROLE_MEMBER) == false) {
            chosenRole = Student.ROLE_MEMBER;
        }
        Student newStudent = new Student(id, name, studentNumber, email, chosenRole);
        this.students.add(newStudent);
        return newStudent;
    }

    public Project addProject(String name, String description, String deadline) {
        Project newProject = new Project(this.nextProjectId, name, description, deadline);
        this.projects.add(newProject);
        this.nextProjectId = this.nextProjectId + 1;
        return newProject;
    }

    public Student findStudentById(String studentId) {
        Student found = null;
        int i = 0;
        while (i < this.students.size()) {
            Student current = this.students.get(i);
            if (current.getId().equals(studentId)) {
                found = current;
            }
            i = i + 1;
        }
        return found;
    }

    public Student findStudentByNumber(String studentNumber) {
        Student found = null;
        int i = 0;
        while (i < this.students.size()) {
            Student current = this.students.get(i);
            if (current.getStudentNumber().equals(studentNumber)) {
                found = current;
            }
            i = i + 1;
        }
        return found;
    }

    public Student findStudentByIdOrNumber(String text) {
        Student found = this.findStudentById(text);
        if (found == null) {
            found = this.findStudentByNumber(text);
        }
        return found;
    }

    public Project findProjectById(int projectId) {
        Project found = null;
        int i = 0;
        while (i < this.projects.size()) {
            Project current = this.projects.get(i);
            if (current.getId() == projectId) {
                found = current;
            }
            i = i + 1;
        }
        return found;
    }

    public Project findProjectByName(String name) {
        Project found = null;
        int i = 0;
        while (i < this.projects.size()) {
            Project current = this.projects.get(i);
            if (current.getName().equals(name)) {
                found = current;
            }
            i = i + 1;
        }
        return found;
    }

    public ArrayList<Project> findProjectsForStudent(String studentId) {
        ArrayList<Project> found = new ArrayList<Project>();
        int i = 0;
        while (i < this.projects.size()) {
            Project current = this.projects.get(i);
            if (current.isStudentOnTeam(studentId)) {
                found.add(current);
            }
            i = i + 1;
        }
        return found;
    }

    public boolean addStudentToProject(int projectId, String studentId) {
        boolean added = false;
        Project project = this.findProjectById(projectId);
        Student student = this.findStudentById(studentId);

        if (project != null && student != null) {
            added = project.addTeamMember(student);
        }

        return added;
    }

    public Task addTaskToProject(int projectId, String title, String description,
                                 String deadline, String assignedStudentId) {
        Task newTask = null;
        Project project = this.findProjectById(projectId);

        if (project != null) {
            String chosenId = assignedStudentId;
            if (chosenId == null || chosenId.length() == 0) {
                chosenId = Task.NO_STUDENT;
            }
            if (chosenId.equals(Task.NO_STUDENT)) {
                newTask = project.addTask(title, description, deadline, Task.NO_STUDENT);
            } else {
                boolean onTeam = project.isStudentOnTeam(chosenId);
                if (onTeam) {
                    newTask = project.addTask(title, description, deadline, chosenId);
                }
            }
        }

        return newTask;
    }

    public boolean addMilestone(int projectId, String title, String date, String kind) {
        boolean added = false;
        Project project = this.findProjectById(projectId);
        if (project != null) {
            project.addMilestone(title, date, kind);
            added = true;
        }
        return added;
    }

    public boolean addLog(int projectId, String author, String message) {
        boolean added = false;
        Project project = this.findProjectById(projectId);
        if (project != null) {
            project.addLog(author, message);
            added = true;
        }
        return added;
    }

    public String getDashboardForStudent(String studentId) {
        String text = "Projects: " + this.findProjectsForStudent(studentId).size();
        return text;
    }

    public boolean saveToFile() {
        boolean saved = false;

        try {
            FileWriter writer = new FileWriter(this.dataFileName, false);

            int i = 0;
            while (i < this.students.size()) {
                Student student = this.students.get(i);
                String line = "STUDENT|" + this.makeSafe(student.getId()) + "|"
                        + this.makeSafe(student.getName())
                        + "|" + this.makeSafe(student.getStudentNumber())
                        + "|" + this.makeSafe(student.getEmail())
                        + "|" + this.makeSafe(student.getRole())
                        + "|" + student.getNotifyDays()
                        + "|" + student.getNotifyEnabled();
                writer.write(line + "\n");
                i = i + 1;
            }

            int j = 0;
            while (j < this.projects.size()) {
                Project project = this.projects.get(j);
                String projectLine = "PROJECT|" + project.getId() + "|" + this.makeSafe(project.getName())
                        + "|" + this.makeSafe(project.getDescription())
                        + "|" + this.makeSafe(project.getDeadline());
                writer.write(projectLine + "\n");

                int k = 0;
                while (k < project.getTeam().size()) {
                    Student member = project.getTeam().get(k);
                    String teamLine = "TEAM|" + project.getId() + "|" + this.makeSafe(member.getId());
                    writer.write(teamLine + "\n");
                    k = k + 1;
                }

                int m = 0;
                while (m < project.getTasks().size()) {
                    Task task = project.getTasks().get(m);
                    String taskLine = "TASK|" + project.getId() + "|" + task.getId()
                            + "|" + this.makeSafe(task.getTitle())
                            + "|" + this.makeSafe(task.getDescription())
                            + "|" + this.makeSafe(task.getDeadline())
                            + "|" + this.makeSafe(task.getStatus())
                            + "|" + this.makeSafe(task.getAssignedStudentId());
                    writer.write(taskLine + "\n");
                    m = m + 1;
                }

                int n = 0;
                while (n < project.getMilestones().size()) {
                    Milestone milestone = project.getMilestones().get(n);
                    String milestoneLine = "MILESTONE|" + project.getId()
                            + "|" + this.makeSafe(milestone.getTitle())
                            + "|" + this.makeSafe(milestone.getDate())
                            + "|" + this.makeSafe(milestone.getKind());
                    writer.write(milestoneLine + "\n");
                    n = n + 1;
                }

                int p = 0;
                while (p < project.getLog().size()) {
                    LogEntry entry = project.getLog().get(p);
                    String logLine = "LOG|" + project.getId()
                            + "|" + this.makeSafe(entry.getAuthor())
                            + "|" + this.makeSafe(entry.getTime())
                            + "|" + this.makeSafe(entry.getMessage());
                    writer.write(logLine + "\n");
                    p = p + 1;
                }

                j = j + 1;
            }

            writer.close();
            saved = true;
        } catch (Exception error) {
            System.out.println("Could not save data: " + error.getMessage());
        }

        return saved;
    }

    public boolean loadFromFile() {
        boolean loaded = false;
        this.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFileName));
            String line = reader.readLine();

            while (line != null) {
                String[] parts = this.splitLine(line);

                if (parts.length >= 5 && parts[0].equals("STUDENT")) {
                    String role = Student.ROLE_MEMBER;
                    int notifyDays = 3;
                    boolean notifyEnabled = true;
                    if (parts.length >= 8) {
                        role = parts[5];
                        notifyDays = Integer.parseInt(parts[6]);
                        notifyEnabled = parts[7].equals("true");
                    }
                    Student student = new Student(parts[1], parts[2], parts[3], parts[4], role);
                    student.setNotifyDays(notifyDays);
                    student.setNotifyEnabled(notifyEnabled);
                    this.students.add(student);
                } else if (parts.length >= 5 && parts[0].equals("PROJECT")) {
                    int id = Integer.parseInt(parts[1]);
                    Project project = new Project(id, parts[2], parts[3], parts[4]);
                    this.projects.add(project);
                    if (id >= this.nextProjectId) {
                        this.nextProjectId = id + 1;
                    }
                } else if (parts.length >= 3 && parts[0].equals("TEAM")) {
                    int projectId = Integer.parseInt(parts[1]);
                    String studentId = parts[2];
                    Project project = this.findProjectById(projectId);
                    Student student = this.findStudentById(studentId);
                    if (project != null && student != null) {
                        project.addTeamMember(student);
                    }
                } else if (parts.length >= 8 && parts[0].equals("TASK")) {
                    int projectId = Integer.parseInt(parts[1]);
                    int taskId = Integer.parseInt(parts[2]);
                    Project project = this.findProjectById(projectId);
                    if (project != null) {
                        Task task = new Task(taskId, parts[3], parts[4], parts[5], parts[7]);
                        task.setStatus(parts[6]);
                        project.getTasks().add(task);
                        if (taskId >= project.getNextTaskId()) {
                            project.setNextTaskId(taskId + 1);
                        }
                    }
                } else if (parts.length >= 5 && parts[0].equals("MILESTONE")) {
                    int projectId = Integer.parseInt(parts[1]);
                    Project project = this.findProjectById(projectId);
                    if (project != null) {
                        project.addMilestone(parts[2], parts[3], parts[4]);
                    }
                } else if (parts.length >= 5 && parts[0].equals("LOG")) {
                    int projectId = Integer.parseInt(parts[1]);
                    Project project = this.findProjectById(projectId);
                    if (project != null) {
                        project.addSavedLog(parts[2], parts[3], parts[4]);
                    }
                }

                line = reader.readLine();
            }

            reader.close();
            loaded = true;
        } catch (Exception error) {
            System.out.println("Could not load data: " + error.getMessage());
            loaded = false;
        }

        return loaded;
    }

    private String makeSafe(String value) {
        String result = "";
        int i = 0;
        while (i < value.length()) {
            char current = value.charAt(i);
            if (current == '|' || current == '\n' || current == '\r') {
                result = result + ' ';
            } else {
                result = result + current;
            }
            i = i + 1;
        }
        return result;
    }

    private String[] splitLine(String line) {
        ArrayList<String> pieces = new ArrayList<String>();
        String currentPiece = "";
        int i = 0;

        while (i < line.length()) {
            char current = line.charAt(i);
            if (current == '|') {
                pieces.add(currentPiece);
                currentPiece = "";
            } else {
                currentPiece = currentPiece + current;
            }
            i = i + 1;
        }
        pieces.add(currentPiece);

        String[] answer = new String[pieces.size()];
        int j = 0;
        while (j < pieces.size()) {
            answer[j] = pieces.get(j);
            j = j + 1;
        }
        return answer;
    }
}
