import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task that must be done before a given point in time,
 * e.g. {@code return book (by: Oct 15 2019)}.
 */
public class Deadline extends Task {

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
        return "D";
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + DateTimes.toFileString(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimes.format(by) + ")";
    }
}
