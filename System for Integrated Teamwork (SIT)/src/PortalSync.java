import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;

/** Moves data between the app and a portal file. */
public class PortalSync {
    private String fileName;
    private SystemManager manager;
    private int studentsAdded;
    private int milestonesAdded;

    /**
     * Creates a new PortalSync.
     * @param fileName the portal file name
     * @param manager the system manager
     */
    public PortalSync(String fileName, SystemManager manager) {
        this.fileName = fileName;
        this.manager = manager;
        this.studentsAdded = 0;
        this.milestonesAdded = 0;
    }

    /**
     * Creates a sample portal file if it does not exist.
     * @return the result
     */
    public boolean createSampleIfMissing() {
        boolean created = false;
        java.io.File file = new java.io.File(this.fileName);

        if (file.exists() == false) {
            try {
                FileWriter writer = new FileWriter(this.fileName, false);
                String checkIn = LocalDate.now().plusDays(7).toString();
                String finalDate = LocalDate.now().plusDays(21).toString();

                writer.write("PORTAL_STUDENT|S10|Lidya|S200|lidya@sit.edu|Team Lead\n");
                writer.write("PORTAL_STUDENT|S11|Nahom|S201|nahom@sit.edu|Coordinator\n");
                writer.write("PORTAL_STUDENT|S12|Mekdes|S202|mekdes@sit.edu|Member\n");
                writer.write("PORTAL_MILESTONE|Website|First progress check-in|" + checkIn
                        + "|Progress Check-in\n");
                writer.write("PORTAL_MILESTONE|Website|Final submission|" + finalDate
                        + "|Milestone\n");
                writer.close();
                created = true;
            } catch (Exception error) {
                System.out.println("Could not create the portal sample file: " + error.getMessage());
            }
        }

        return created;
    }

    /**
     * Reads and updates data from the portal file.
     * @return the result
     */
    public boolean sync() {
        boolean worked = false;
        this.studentsAdded = 0;
        this.milestonesAdded = 0;
        java.io.File portalFile = new java.io.File(this.fileName);
        if (portalFile.exists() == false) {
            System.out.println("Portal file not found: " + this.fileName);
            return false;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.fileName));
            String line = reader.readLine();

            while (line != null) {
                String[] parts = this.splitLine(line);

                if (parts.length >= 5 && parts[0].equals("PORTAL_STUDENT")) {
                    String id = "";
                    String name = "";
                    String number = "";
                    String email = "";
                    String role = "";

                    if (parts.length >= 6) {
                        id = parts[1];
                        name = parts[2];
                        number = parts[3];
                        email = parts[4];
                        role = parts[5];
                    } else {
                        id = parts[2];
                        name = parts[1];
                        number = parts[2];
                        email = parts[3];
                        role = parts[4];
                    }

                    Student existing = this.manager.findStudentByNumber(number);
                    Student byId = this.manager.findStudentById(id);
                    if (existing == null && byId == null) {
                        Student added = this.manager.addStudent(id, name, number, email, role);
                        if (added != null) {
                            this.studentsAdded = this.studentsAdded + 1;
                        }
                    } else {
                        Student toUpdate = existing;
                        if (toUpdate == null) {
                            toUpdate = byId;
                        }
                        toUpdate.setName(name);
                        toUpdate.setEmail(email);
                        toUpdate.setRole(role);
                    }
                } else if (parts.length >= 5 && parts[0].equals("PORTAL_MILESTONE")) {
                    Project project = this.manager.findProjectByName(parts[1]);
                    if (project != null) {
                        boolean alreadyThere = false;
                        int i = 0;
                        while (i < project.getMilestones().size()) {
                            Milestone current = project.getMilestones().get(i);
                            if (current.getTitle().equals(parts[2]) && current.getDate().equals(parts[3])) {
                                alreadyThere = true;
                            }
                            i = i + 1;
                        }
                        if (alreadyThere == false) {
                            this.manager.addMilestone(project.getId(), parts[2], parts[3], parts[4]);
                            this.milestonesAdded = this.milestonesAdded + 1;
                        }
                    }
                }

                line = reader.readLine();
            }

            reader.close();
            worked = true;
        } catch (Exception error) {
            System.out.println("Portal sync failed: " + error.getMessage());
        }

        return worked;
    }

    /**
     * Gets students added.
     * @return the result
     */
    public int getStudentsAdded() {
        return this.studentsAdded;
    }

    /**
     * Gets portal file name.
     * @return the result
     */
    public String getPortalFileName() {
        return this.fileName;
    }

    /**
     * Gets milestones added.
     * @return the result
     */
    public int getMilestonesAdded() {
        return this.milestonesAdded;
    }

    /**
     * Checks if the portal file is available.
     * @return the result
     */
    public boolean isPortalAvailable() {
        java.io.File portalFile = new java.io.File(this.fileName);
        return portalFile.exists();
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
