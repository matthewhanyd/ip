package seedu.mattchatbot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import seedu.mattchatbot.DateTimes;

/**
 * A task that runs from one point in time to another,
 * e.g. {@code project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)}.
 */
public class Event extends Task {

    /** When the event starts. */
    protected LocalDateTime from;

    /** When the event ends. */
    protected LocalDateTime to;

    /**
     * Creates an event.
     *
     * @param description what the event is
     * @param from        when it starts
     * @param to          when it ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * {@inheritDoc}
     * <p>
     * An event covers every date from its start to its end, so a multi-day
     * event occurs on each of those dates, not only the day it begins.
     */
    @Override
    public boolean isOn(LocalDate date) {
        LocalDate start = from.toLocalDate();
        LocalDate end = to.toLocalDate();
        return !date.isBefore(start) && !date.isAfter(end);
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + DateTimes.toFileString(from)
                + " | " + DateTimes.toFileString(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimes.format(from)
                + " to: " + DateTimes.format(to) + ")";
    }
}
