import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static final String LEVEL = "Level 1";

    private static final String DATA_FILE = "data.txt";
    private static final String PORTAL_FILE = "sit_portal.txt";

    public static void main(String[] args) {
        SystemManager manager = new SystemManager(DATA_FILE);
        boolean loaded = manager.loadFromFile();
        if (loaded) {
            System.out.println("Saved data was loaded.");
        } else {
            System.out.println("No saved data found. Starting with an empty system.");
        }

        PortalSync portal = new PortalSync(PORTAL_FILE, manager);
        boolean sampleCreated = portal.createSampleIfMissing();
        if (sampleCreated) {
            System.out.println("A sample SIT portal file was created: " + PORTAL_FILE);
        }

        Scanner scanner = new Scanner(System.in);

        if (manager.getStudents().size() == 0) {
            System.out.println("No users yet. Create the first account (Team Lead).");
            addStudentFlow(scanner, manager, Student.ROLE_LEAD);
        }

        Student currentUser = login(scanner, manager);
        boolean running = true;

        while (running) {
            printMenu(currentUser);
            int option = readMenuOption(scanner);

            if (option == -99) {
                System.out.println();
                System.out.println("Input closed. Saving and exiting.");
                manager.saveToFile();
                running = false;
            } else if (option == 1) {
                printMyDashboard(currentUser, manager);
            } else if (option == 2) {
                printProjects(manager);
            } else if (option == 3) {
                projectDashboardFlow(scanner, manager, currentUser);
            } else if (option == 4) {
                printCalendar(manager);
            } else if (option == 5) {
                if (canManage(currentUser)) {
                    addStudentFlow(scanner, manager, "");
                } else {
                    printDenied(currentUser);
                }
            } else if (option == 6) {
                printStudents(manager);
            } else if (option == 7) {
                if (canManage(currentUser)) {
                    addProjectFlow(scanner, manager, currentUser);
                } else {
                    printDenied(currentUser);
                }
            } else if (option == 8) {
                if (canManage(currentUser)) {
                    addMemberFlow(scanner, manager, currentUser);
                } else {
                    printDenied(currentUser);
                }
            } else if (option == 9) {
                if (canAssign(currentUser)) {
                    addTaskFlow(scanner, manager, currentUser);
                } else {
                    printDenied(currentUser);
                }
            } else if (option == 10) {
                if (canManage(currentUser)) {
                    addMilestoneFlow(scanner, manager, currentUser);
                } else {
                    printDenied(currentUser);
                }
            } else if (option == 11) {
                postUpdateFlow(scanner, manager, currentUser);
            } else if (option == 12) {
                changeStatusFlow(scanner, manager, currentUser);
            } else if (option == 13) {
                printAllTasks(manager);
            } else if (option == 14) {
                printNotifications(currentUser, manager);
            } else if (option == 15) {
                notificationSettingsFlow(scanner, currentUser);
            } else if (option == 16) {
                if (canManage(currentUser)) {
                    portalSyncFlow(portal);
                } else {
                    printDenied(currentUser);
                }
            } else if (option == 17) {
                boolean saved = manager.saveToFile();
                if (saved) {
                    System.out.println("Data saved to " + DATA_FILE);
                }
            } else if (option == 18) {
                currentUser = login(scanner, manager);
            } else if (option == 0) {
                boolean saved = manager.saveToFile();
                if (saved) {
                    System.out.println("Data saved. Goodbye!");
                }
                running = false;
            } else {
                System.out.println("That is not a menu option. Type a number from 0 to 18.");
            }
        }

        scanner.close();
    }

    private static void printMenu(Student user) {
        System.out.println();
        System.out.println("===== SYSTEM FOR INTEGRATED TEAMWORK (SIT) =====");
        System.out.println("Signed in as " + user.getName() + " | Role: " + user.getRole());
        System.out.println("1. My dashboard");
        System.out.println("2. View all projects");
        System.out.println("3. Open project dashboard");
        System.out.println("4. Calendar of all deadlines");
        System.out.println("5. Add a student (Lead/Coordinator)");
        System.out.println("6. View all students");
        System.out.println("7. Create a project (Lead/Coordinator)");
        System.out.println("8. Add a member to a project (Lead/Coordinator)");
        System.out.println("9. Add a task to a project (Team Lead)");
        System.out.println("10. Add a progress check-in (Lead/Coordinator)");
        System.out.println("11. Post a project update");
        System.out.println("12. Change a task status");
        System.out.println("13. View all tasks");
        System.out.println("14. My notifications");
        System.out.println("15. Notification settings");
        System.out.println("16. Sync with SIT portal (Lead/Coordinator)");
        System.out.println("17. Save data");
        System.out.println("18. Switch user");
        System.out.println("0. Save and exit");
        printPrompt("Choose an option: ");
    }

    private static int readMenuOption(Scanner scanner) {
        int option = -1;

        if (scanner.hasNextLine()) {
            String choice = scanner.nextLine().trim();
            while (choice.length() > 0 && choice.charAt(choice.length() - 1) == '.') {
                choice = choice.substring(0, choice.length() - 1).trim();
            }
            String lower = choice.toLowerCase();
            if (lower.equals("q") || lower.equals("quit") || lower.equals("exit")) {
                option = 0;
            } else {
                try {
                    option = Integer.parseInt(choice);
                } catch (Exception error) {
                    option = -1;
                }
            }
        } else {
            option = -99;
        }

        return option;
    }

    private static void printPrompt(String prompt) {
        System.out.print(prompt);
        System.out.flush();
    }

    private static void printDenied(Student user) {
        System.out.println("Your role (" + user.getRole() + ") cannot use that option.");
    }

    private static boolean canManage(Student user) {
        boolean allowed = false;
        if (user.getRole().equals(Student.ROLE_LEAD)
                || user.getRole().equals(Student.ROLE_COORDINATOR)) {
            allowed = true;
        }
        return allowed;
    }

    private static boolean canAssign(Student user) {
        boolean allowed = false;
        if (user.getRole().equals(Student.ROLE_LEAD)) {
            allowed = true;
        }
        return allowed;
    }

    private static Student login(Scanner scanner, SystemManager manager) {
        System.out.println();
        System.out.println("===== LOGIN =====");
        printStudents(manager);
        Student chosen = null;

        while (chosen == null) {
            String id = readText(scanner, "Your student ID (or student number): ");
            chosen = manager.findStudentByIdOrNumber(id);
            if (chosen == null) {
                System.out.println("No student with that ID. Try again.");
            }
        }

        System.out.println("Welcome, " + chosen.getName() + " (" + chosen.getRole() + ")");
        printNotifications(chosen, manager);
        return chosen;
    }

    private static void addStudentFlow(Scanner scanner, SystemManager manager, String forcedRole) {
        System.out.println();
        System.out.println("--- Add a student ---");
        String id = "";
        boolean uniqueId = false;
        while (uniqueId == false) {
            id = readText(scanner, "Student ID (letters and/or numbers, e.g. S100): ");
            if (manager.findStudentById(id) == null) {
                uniqueId = true;
            } else {
                System.out.println("That student ID is already in use.");
            }
        }

        String name = readText(scanner, "Name: ");
        String number = "";
        boolean uniqueNumber = false;
        while (uniqueNumber == false) {
            number = readText(scanner, "Student number: ");
            if (manager.findStudentByNumber(number) == null) {
                uniqueNumber = true;
            } else {
                System.out.println("That student number is already in use.");
            }
        }
        String email = readText(scanner, "Email: ");
        String role = forcedRole;

        if (role.equals("")) {
            boolean roleChosen = false;
            while (roleChosen == false) {
                System.out.println("1. " + Student.ROLE_LEAD);
                System.out.println("2. " + Student.ROLE_COORDINATOR);
                System.out.println("3. " + Student.ROLE_MEMBER);
                int roleChoice = readNumber(scanner, "Choose a role: ");
                if (roleChoice == 1) {
                    role = Student.ROLE_LEAD;
                    roleChosen = true;
                } else if (roleChoice == 2) {
                    role = Student.ROLE_COORDINATOR;
                    roleChosen = true;
                } else if (roleChoice == 3) {
                    role = Student.ROLE_MEMBER;
                    roleChosen = true;
                } else {
                    System.out.println("Choose 1, 2, or 3.");
                }
            }
        }

        Student student = manager.addStudent(id, name, number, email, role);
        System.out.println("Student added. Their ID is " + student.getId());
    }

    private static void addProjectFlow(Scanner scanner, SystemManager manager, Student user) {
        System.out.println();
        System.out.println("--- Create a project ---");
        String name = readText(scanner, "Project name: ");
        String description = readText(scanner, "Description: ");
        String deadline = readDate(scanner, "Deadline (yyyy-mm-dd): ");
        Project project = manager.addProject(name, description, deadline);
        project.addTeamMember(user);
        project.addLog(user.getName(), "Project created by " + user.getName() + ".");
        System.out.println("Project created. Its ID is " + project.getId());
        System.out.println("You were added to the team automatically.");
    }

    private static void addMemberFlow(Scanner scanner, SystemManager manager, Student user) {
        System.out.println();
        System.out.println("--- Add a member to a project ---");
        printStudents(manager);
        printProjects(manager);
        int projectId = readNumber(scanner, "Project ID: ");
        String studentId = readText(scanner, "Student ID: ");
        boolean added = manager.addStudentToProject(projectId, studentId);

        if (added) {
            Project project = manager.findProjectById(projectId);
            Student member = manager.findStudentById(studentId);
            project.addLog(user.getName(), member.getName() + " joined the team.");
            System.out.println("Student " + studentId + " was added to project " + projectId);
        } else {
            System.out.println("Could not add that student. Check both IDs and try again.");
        }
    }

    private static void addTaskFlow(Scanner scanner, SystemManager manager, Student user) {
        System.out.println();
        System.out.println("--- Add a task ---");
        printProjects(manager);
        int projectId = readNumber(scanner, "Project ID: ");
        Project project = manager.findProjectById(projectId);

        if (project == null) {
            System.out.println("No project with that ID.");
        } else {
            printTeam(project);
            String title = readText(scanner, "Task title: ");
            String description = readText(scanner, "Task description: ");
            String deadline = readDate(scanner, "Task deadline (yyyy-mm-dd): ");
            String studentId = readText(scanner, "Assign to student ID (-1 for nobody): ");
            Task task = manager.addTaskToProject(projectId, title, description, deadline, studentId);

            if (task == null) {
                System.out.println("That student is not on the project team. Task was not added.");
            } else {
                String assignedName = "nobody";
                Student assigned = manager.findStudentById(studentId);
                if (assigned != null) {
                    assignedName = assigned.getName();
                }
                project.addLog(user.getName(), "Task '" + title + "' was assigned to "
                        + assignedName + ".");
                System.out.println("Task " + task.getId() + " was added.");
            }
        }
    }

    private static void addMilestoneFlow(Scanner scanner, SystemManager manager, Student user) {
        System.out.println();
        System.out.println("--- Add a progress check-in or milestone ---");
        printProjects(manager);
        int projectId = readNumber(scanner, "Project ID: ");
        Project project = manager.findProjectById(projectId);

        if (project == null) {
            System.out.println("No project with that ID.");
        } else {
            String title = readText(scanner, "Title: ");
            String date = readDate(scanner, "Date (yyyy-mm-dd): ");
            System.out.println("1. " + Milestone.KIND_CHECKIN);
            System.out.println("2. " + Milestone.KIND_MILESTONE);
            int kindChoice = readNumber(scanner, "Choose a type: ");
            String kind = Milestone.KIND_CHECKIN;
            if (kindChoice == 2) {
                kind = Milestone.KIND_MILESTONE;
            }
            manager.addMilestone(projectId, title, date, kind);
            project.addLog(user.getName(), kind + " '" + title + "' was added for " + date + ".");
            System.out.println("Added: " + date + " | " + kind + ": " + title);
        }
    }

    private static void postUpdateFlow(Scanner scanner, SystemManager manager, Student user) {
        System.out.println();
        System.out.println("--- Post a project update ---");
        printProjects(manager);
        int projectId = readNumber(scanner, "Project ID: ");
        Project project = manager.findProjectById(projectId);

        if (project == null) {
            System.out.println("No project with that ID.");
        } else if (project.isStudentOnTeam(user.getId()) == false) {
            System.out.println("You are not on that project's team.");
        } else {
            String message = readText(scanner, "Your update: ");
            project.addLog(user.getName(), message);
            System.out.println("Your update was added to the project log.");
        }
    }

    private static void changeStatusFlow(Scanner scanner, SystemManager manager, Student user) {
        System.out.println();
        System.out.println("--- Change a task status ---");
        printProjects(manager);
        int projectId = readNumber(scanner, "Project ID: ");
        Project project = manager.findProjectById(projectId);

        if (project == null) {
            System.out.println("No project with that ID.");
        } else {
            printTasks(project);
            if (project.getTasks().size() == 0) {
                System.out.println("This project has no tasks yet.");
            } else {
                int taskId = readNumber(scanner, "Task ID: ");
                Task task = project.findTaskById(taskId);
                if (task == null) {
                    System.out.println("No task with that ID in this project.");
                } else {
                    boolean allowed = canManage(user)
                            || task.getAssignedStudentId().equals(user.getId());
                    if (allowed == false) {
                        System.out.println("That task is not assigned to you.");
                    } else {
                        System.out.println("1. " + Task.STATUS_TO_DO);
                        System.out.println("2. " + Task.STATUS_DOING);
                        System.out.println("3. " + Task.STATUS_DONE);
                        int statusChoice = readNumber(scanner, "Choose a status: ");
                        String oldStatus = task.getStatus();
                        if (statusChoice == 1) {
                            task.setStatus(Task.STATUS_TO_DO);
                        } else if (statusChoice == 2) {
                            task.setStatus(Task.STATUS_DOING);
                        } else if (statusChoice == 3) {
                            task.setStatus(Task.STATUS_DONE);
                        } else {
                            System.out.println("Unknown status. Nothing was changed.");
                        }
                        if (task.getStatus().equals(oldStatus) == false) {
                            project.addLog(user.getName(), "Task '" + task.getTitle()
                                    + "' changed from " + oldStatus + " to " + task.getStatus() + ".");
                            System.out.println("Task is now: " + task.getStatus());
                        }
                    }
                }
            }
        }
    }

    private static void notificationSettingsFlow(Scanner scanner, Student user) {
        System.out.println();
        System.out.println("--- Notification settings ---");
        boolean answer = readYesNo(scanner, "Turn on deadline notifications? (yes/no): ");
        user.setNotifyEnabled(answer);

        if (answer) {
            int days = 0;
            while (days < 1 || days > 30) {
                days = readNumber(scanner, "Notify me this many days before a deadline (1-30): ");
                if (days < 1 || days > 30) {
                    System.out.println("Please choose a number from 1 to 30.");
                }
            }
            user.setNotifyDays(days);
        }

        System.out.println("Settings saved for this session. Use Save data to keep them.");
    }

    private static void portalSyncFlow(PortalSync portal) {
        System.out.println();
        System.out.println("--- SIT portal sync ---");
        boolean worked = portal.sync();
        if (worked) {
            System.out.println("Students imported: " + portal.getStudentsAdded());
            System.out.println("Milestones imported: " + portal.getMilestonesAdded());
        }
        System.out.println("The portal file used is " + PORTAL_FILE + ".");
    }

    private static void printMyDashboard(Student user, SystemManager manager) {
        System.out.println();
        System.out.println("===== MY DASHBOARD: " + user.getName() + " =====");
        ArrayList<Project> myProjects = manager.findProjectsForStudent(user.getId());

        if (myProjects.size() == 0) {
            System.out.println("You are not on any project teams yet.");
        } else {
            int i = 0;
            while (i < myProjects.size()) {
                Project project = myProjects.get(i);
                System.out.println();
                System.out.println(project.toString());
                System.out.println("My tasks:");
                int j = 0;
                int myTaskCount = 0;
                while (j < project.getTasks().size()) {
                    Task task = project.getTasks().get(j);
                    if (task.getAssignedStudentId().equals(user.getId())) {
                        String line = "  " + task.toString();
                        if (isOverdue(task.getDeadline()) && task.isDone() == false) {
                            line = line + " (OVERDUE)";
                        }
                        System.out.println(line);
                        myTaskCount = myTaskCount + 1;
                    }
                    j = j + 1;
                }
                if (myTaskCount == 0) {
                    System.out.println("  No tasks assigned to you.");
                }
                printNextMilestone(project);
                printRecentLog(project, 2);
                i = i + 1;
            }
        }
    }

    private static void projectDashboardFlow(Scanner scanner, SystemManager manager, Student user) {
        System.out.println();
        System.out.println("--- Project dashboard ---");
        printProjects(manager);
        int projectId = readNumber(scanner, "Project ID: ");
        Project project = manager.findProjectById(projectId);

        if (project == null) {
            System.out.println("No project with that ID.");
        } else {
            printProjectDashboard(project, user);
        }
    }

    private static void printProjectDashboard(Project project, Student user) {
        System.out.println();
        System.out.println("===== " + project.getName().toUpperCase() + " =====");
        System.out.println("ID: " + project.getId());
        System.out.println("Description: " + project.getDescription());
        System.out.println("Deadline: " + project.getDeadline());
        System.out.println("Progress: " + project.getProgressPercent() + "% ("
                + project.getDoneTaskCount() + " of " + project.getTasks().size() + " tasks done)");
        if (project.isStudentOnTeam(user.getId())) {
            System.out.println("You are on this team.");
        }

        printTeam(project);
        printTasks(project);
        System.out.println();
        System.out.println("Calendar for this project:");
        int i = 0;
        while (i < project.getMilestones().size()) {
            System.out.println("  " + project.getMilestones().get(i).toString());
            i = i + 1;
        }
        if (project.getMilestones().size() == 0) {
            System.out.println("  No check-ins or milestones yet.");
        }
        printRecentLog(project, project.getLog().size());
    }

    private static void printNextMilestone(Project project) {
        Milestone next = null;
        int i = 0;
        while (i < project.getMilestones().size()) {
            Milestone current = project.getMilestones().get(i);
            if (next == null || current.getDate().compareTo(next.getDate()) < 0) {
                next = current;
            }
            i = i + 1;
        }
        if (next == null) {
            System.out.println("Next check-in: none planned.");
        } else {
            System.out.println("Next check-in: " + next.toString());
        }
    }

    private static void printRecentLog(Project project, int howMany) {
        System.out.println();
        System.out.println("Project log:");
        int start = project.getLog().size() - howMany;
        if (start < 0) {
            start = 0;
        }
        int i = start;
        while (i < project.getLog().size()) {
            System.out.println("  " + project.getLog().get(i).toString());
            i = i + 1;
        }
        if (project.getLog().size() == 0) {
            System.out.println("  No updates yet.");
        }
    }

    private static void printCalendar(SystemManager manager) {
        System.out.println();
        System.out.println("===== CALENDAR: ALL MILESTONES AND DEADLINES =====");
        ArrayList<String> entries = new ArrayList<String>();
        ArrayList<Project> projects = manager.getProjects();

        int i = 0;
        while (i < projects.size()) {
            Project project = projects.get(i);
            entries.add(project.getDeadline() + "|Project deadline: " + project.getName());

            int j = 0;
            while (j < project.getMilestones().size()) {
                Milestone milestone = project.getMilestones().get(j);
                entries.add(milestone.getDate() + "|" + milestone.getKind() + ": "
                        + milestone.getTitle() + " (" + project.getName() + ")");
                j = j + 1;
            }

            int k = 0;
            while (k < project.getTasks().size()) {
                Task task = project.getTasks().get(k);
                entries.add(task.getDeadline() + "|Task: " + task.getTitle()
                        + " (" + project.getName() + ") - " + task.getStatus());
                k = k + 1;
            }
            i = i + 1;
        }

        int a = 0;
        while (a < entries.size() - 1) {
            int smallest = a;
            int b = a + 1;
            while (b < entries.size()) {
                if (getEntryDate(entries.get(b)).compareTo(getEntryDate(entries.get(smallest))) < 0) {
                    smallest = b;
                }
                b = b + 1;
            }
            String temporary = entries.get(a);
            entries.set(a, entries.get(smallest));
            entries.set(smallest, temporary);
            a = a + 1;
        }

        int c = 0;
        while (c < entries.size()) {
            String entry = entries.get(c);
            String line = entry.substring(0, 10) + " | " + entry.substring(11);
            if (isOverdue(entry.substring(0, 10))) {
                line = line + " (OVERDUE)";
            }
            System.out.println(line);
            c = c + 1;
        }

        if (entries.size() == 0) {
            System.out.println("There are no deadlines yet.");
        }
    }

    private static String getEntryDate(String entry) {
        return entry.substring(0, 10);
    }

    private static void printNotifications(Student user, SystemManager manager) {
        System.out.println();
        System.out.println("===== NOTIFICATIONS FOR " + user.getName().toUpperCase() + " =====");
        Notifications notifications = new Notifications(manager);
        ArrayList<String> messages = notifications.buildFor(user);
        int i = 0;
        while (i < messages.size()) {
            System.out.println("  " + messages.get(i));
            i = i + 1;
        }
    }

    private static void printStudents(SystemManager manager) {
        System.out.println();
        System.out.println("--- Students ---");
        ArrayList<Student> allStudents = manager.getStudents();

        if (allStudents.size() == 0) {
            System.out.println("There are no students yet.");
        } else {
            int i = 0;
            while (i < allStudents.size()) {
                System.out.println(allStudents.get(i).toString());
                i = i + 1;
            }
        }
    }

    private static void printProjects(SystemManager manager) {
        System.out.println();
        System.out.println("--- Projects ---");
        ArrayList<Project> allProjects = manager.getProjects();

        if (allProjects.size() == 0) {
            System.out.println("There are no projects yet.");
        } else {
            int i = 0;
            while (i < allProjects.size()) {
                System.out.println(allProjects.get(i).toString());
                i = i + 1;
            }
        }
    }

    private static void printTeam(Project project) {
        System.out.println();
        System.out.println("Team for " + project.getName() + ":");
        ArrayList<Student> team = project.getTeam();

        if (team.size() == 0) {
            System.out.println("No members yet.");
        } else {
            int i = 0;
            while (i < team.size()) {
                System.out.println("  " + team.get(i).toString());
                i = i + 1;
            }
        }
    }

    private static void printTasks(Project project) {
        System.out.println();
        System.out.println("Tasks for " + project.getName() + ":");
        ArrayList<Task> tasks = project.getTasks();

        if (tasks.size() == 0) {
            System.out.println("No tasks yet.");
        } else {
            int i = 0;
            while (i < tasks.size()) {
                Task task = tasks.get(i);
                String line = "  " + task.toString();
                if (isOverdue(task.getDeadline()) && task.isDone() == false) {
                    line = line + " (OVERDUE)";
                }
                System.out.println(line);
                i = i + 1;
            }
        }
    }

    private static void printAllTasks(SystemManager manager) {
        System.out.println();
        System.out.println("--- All tasks in every project ---");
        ArrayList<Project> allProjects = manager.getProjects();
        int totalTasks = 0;

        int i = 0;
        while (i < allProjects.size()) {
            Project project = allProjects.get(i);
            ArrayList<Task> tasks = project.getTasks();
            int j = 0;
            while (j < tasks.size()) {
                Task task = tasks.get(j);
                String line = "Project: " + project.getName() + " -> " + task.toString();
                if (isOverdue(task.getDeadline()) && task.isDone() == false) {
                    line = line + " (OVERDUE)";
                }
                System.out.println(line);
                totalTasks = totalTasks + 1;
                j = j + 1;
            }
            i = i + 1;
        }

        if (totalTasks == 0) {
            System.out.println("There are no tasks yet.");
        }
    }

    private static boolean isOverdue(String deadline) {
        String today = LocalDate.now().toString();
        boolean overdue = false;
        if (deadline.compareTo(today) < 0) {
            overdue = true;
        }
        return overdue;
    }

    private static String readText(Scanner scanner, String prompt) {
        String text = "";
        boolean valid = false;
        while (valid == false) {
            printPrompt(prompt);
            text = scanner.nextLine().trim();
            if (text.length() > 0) {
                valid = true;
            } else {
                System.out.println("Please type something.");
            }
        }
        return text;
    }

    private static int readNumber(Scanner scanner, String prompt) {
        int number = -1;
        boolean valid = false;
        while (valid == false) {
            printPrompt(prompt);
            String text = scanner.nextLine().trim();
            try {
                number = Integer.parseInt(text);
                valid = true;
            } catch (Exception error) {
                System.out.println("Please type a whole number.");
            }
        }
        return number;
    }

    private static boolean readYesNo(Scanner scanner, String prompt) {
        boolean answer = false;
        boolean valid = false;
        while (valid == false) {
            String text = readText(scanner, prompt).toLowerCase();
            if (text.equals("yes") || text.equals("y")) {
                answer = true;
                valid = true;
            } else if (text.equals("no") || text.equals("n")) {
                answer = false;
                valid = true;
            } else {
                System.out.println("Please type yes or no.");
            }
        }
        return answer;
    }

    private static String readDate(Scanner scanner, String prompt) {
        String date = "";
        boolean valid = false;
        while (valid == false) {
            date = readText(scanner, prompt);
            if (isDateValid(date)) {
                valid = true;
            } else {
                System.out.println("Use the format yyyy-mm-dd, for example 2026-12-15.");
            }
        }
        return date;
    }

    private static boolean isDateValid(String text) {
        boolean valid = false;

        if (text.length() == 10) {
            boolean shapeIsGood = true;
            int i = 0;
            while (i < text.length()) {
                if (i == 4 || i == 7) {
                    if (text.charAt(i) != '-') {
                        shapeIsGood = false;
                    }
                } else {
                    if (Character.isDigit(text.charAt(i)) == false) {
                        shapeIsGood = false;
                    }
                }
                i = i + 1;
            }

            if (shapeIsGood) {
                int year = Integer.parseInt(text.substring(0, 4));
                int month = Integer.parseInt(text.substring(5, 7));
                int day = Integer.parseInt(text.substring(8, 10));
                if (year >= 2000 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                    valid = true;
                }
            }
        }

        return valid;
    }
}
