package seedu.mattchatbot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import seedu.mattchatbot.DateTimes;

/**
 * A task that must be done before a given point in time,
 * e.g. {@code return book (by: Oct 15 2019)}.
 */
public class Deadline extends Task {

    /** The marker shown in a deadline's type box and written to the save file. */
    public static final String TYPE_ICON = "D";

    /** When the task is due. */
    protected LocalDateTime by;

    /**
     * Creates a deadline.
     *
     * @param description what the user needs to do
     * @param by          when it is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return TYPE_ICON;
    }

    @Override
    public boolean isOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + FIELD_SEPARATOR + DateTimes.toFileString(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimes.format(by) + ")";
    }
}
