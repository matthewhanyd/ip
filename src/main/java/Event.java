/**
 * A task that runs from one point in time to another,
 * e.g. {@code project meeting (from: Mon 2pm to: 4pm)}.
 */
public class Event extends Task {

    /** When the event starts, as free text. */
    protected String from;

    /** When the event ends, as free text. */
    protected String to;

    /**
     * Creates an event.
     *
     * @param description what the event is
     * @param from        when it starts, as the user typed it
     * @param to          when it ends, as the user typed it
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
