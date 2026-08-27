package seedu.mattchatbot.task;

import java.time.LocalDate;

/**
 * A single item in the user's list, together with whether it has been done.
 * <p>
 * This is the parent of the three concrete task types introduced by the
 * A-Inheritance extension. It is abstract because every real task is a todo,
 * a deadline or an event: a task with no type would have nothing to show in
 * its type box.
 */
public abstract class Task {

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
     * Returns the single character shown in the type box, e.g. "T" for a todo.
     * <p>
     * Each subclass supplies its own, so adding a new task type does not mean
     * editing a type-checking branch elsewhere in the code.
     *
     * @return the one-character type marker
     */
    public abstract String getTypeIcon();

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
     * Returns whether this task happens on the given date.
     * <p>
     * Todos have no date, so the default is false; the task types that do
     * carry dates override this. Asking the task itself avoids having to test
     * its type with instanceof from outside.
     *
     * @param date the date being asked about
     * @return true if this task falls on that date
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the task encoded for the save file, e.g. {@code T | 1 | read book}.
     * <p>
     * Subclasses append their own fields, so the encoding of each task type
     * lives with that type rather than in one big switch inside Storage.
     *
     * @return the line to write to the save file
     */
    public String toFileFormat() {
        return getTypeIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns the task as the user sees it, e.g. {@code [T][X] read book}.
     * Subclasses that carry dates append them to this.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
