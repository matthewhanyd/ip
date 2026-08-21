/**
 * A task that must be done before a given point in time,
 * e.g. {@code submit report (by: Sunday)}.
 */
public class Deadline extends Task {

    /**
     * When the task is due. Kept as free text, since this increment does not
     * require parsing real dates.
     */
    protected String by;

    /**
     * Creates a deadline.
     *
     * @param description what the user needs to do
     * @param by          when it is due, as the user typed it
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
