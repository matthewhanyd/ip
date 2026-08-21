/**
 * A single item in the user's list, together with whether it has been done.
 * <p>
 * Introduced by the A-Classes extension: before this, items were plain
 * Strings, which left no place to keep the done/not-done state alongside the
 * description.
 */
public class Task {

    /** What the user asked to be reminded of. */
    protected String description;

    /** Whether the user has marked this task as done. */
    protected boolean isDone;

    /**
     * Creates a task that starts off as not done.
     *
     * @param description what the user typed
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the single character shown inside the status box.
     *
     * @return "X" if the task is done, a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the task as the user sees it, e.g. {@code [X] read book}.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
