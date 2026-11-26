/** Holds information about one student. */
public class Student {
    /** The role lead value. */
    public static final String ROLE_LEAD = "Team Lead";
    /** The role coordinator value. */
    public static final String ROLE_COORDINATOR = "Coordinator";
    /** The role member value. */
    public static final String ROLE_MEMBER = "Member";

    private String id;
    private String name;
    private String studentNumber;
    private String email;
    private String role;
    private int notifyDays;
    private boolean notifyEnabled;

    /**
     * Creates a new Student.
     * @param id the ID
     * @param name the name
     * @param studentNumber the student number
     * @param email the email
     * @param role the role
     */
    public Student(String id, String name, String studentNumber, String email, String role) {
        this.id = id;
        this.name = name;
        this.studentNumber = studentNumber;
        this.email = email;
        this.role = role;
        this.notifyDays = 3;
        this.notifyEnabled = true;
    }

    /**
     * Gets id.
     * @return the result
     */
    public String getId() {
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
     * Gets student number.
     * @return the result
     */
    public String getStudentNumber() {
        return this.studentNumber;
    }

    /**
     * Gets email.
     * @return the result
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Gets role.
     * @return the result
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Gets notify days.
     * @return the result
     */
    public int getNotifyDays() {
        return this.notifyDays;
    }

    /**
     * Gets notify enabled.
     * @return the result
     */
    public boolean getNotifyEnabled() {
        return this.notifyEnabled;
    }

    /**
     * Sets name.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets student number.
     * @param studentNumber the student number
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * Sets email.
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets role.
     * @param role the role
     */
    public void setRole(String role) {
        boolean allowed = false;
        if (role.equals(ROLE_LEAD) || role.equals(ROLE_COORDINATOR) || role.equals(ROLE_MEMBER)) {
            allowed = true;
        }
        if (allowed) {
            this.role = role;
        }
    }

    /**
     * Sets notify days.
     * @param notifyDays the number of notification days
     */
    public void setNotifyDays(int notifyDays) {
        int chosen = notifyDays;
        if (chosen < 1) {
            chosen = 1;
        }
        if (chosen > 30) {
            chosen = 30;
        }
        this.notifyDays = chosen;
    }

    /**
     * Sets notify enabled.
     * @param notifyEnabled if notifications are enabled
     */
    public void setNotifyEnabled(boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
    }

    /**
     * Checks if text matches the student ID or number.
     * @param text the text to match
     * @return the result
     */
    public boolean matchesIdOrNumber(String text) {
        boolean matched = false;
        if (this.id.equals(text)) {
            matched = true;
        }
        if (this.studentNumber.equals(text)) {
            matched = true;
        }
        return matched;
    }

    /**
     * Returns a short text for this object.
     * @return the result
     */
    public String toString() {
        String text = "Student ID: " + this.id;
        text = text + " | Name: " + this.name;
        text = text + " | Number: " + this.studentNumber;
        text = text + " | Role: " + this.role;
        text = text + " | Email: " + this.email;
        return text;
    }
}
