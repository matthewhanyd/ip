package seedu.mattchatbot.task;

import java.time.LocalDate;
import java.util.ArrayList;
import seedu.mattchatbot.Command;
import seedu.mattchatbot.MattChatBotException;

/**
 * The list of tasks, with the operations that change it.
 * <p>
 * Extracted so that the rules about the list -- what counts as a valid task
 * number, what it means for a task to fall on a date -- live with the list
 * itself, rather than being repeated by whoever happens to be using it.
 */
public class TaskList {

    /** The tasks, in the order they were added. */
    private final ArrayList<Task> tasks;

    /** Creates an empty list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding the given tasks, e.g. those just loaded from disk.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the given position.
     *
     * @param index the task's position, 0-based
     * @return the task that was removed
     * @throws MattChatBotException if there is no task at that position
     */
    public Task remove(int index) throws MattChatBotException {
        checkInRange(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index the task's position, 0-based
     * @return the task there
     * @throws MattChatBotException if there is no task at that position
     */
    public Task get(int index) throws MattChatBotException {
        checkInRange(index);
        return tasks.get(index);
    }

    /**
     * Returns the tasks that fall on the given date.
     *
     * @param date the date being asked about
     * @return the matching tasks, in list order
     */
    public ArrayList<Task> occurringOn(LocalDate date) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns the tasks whose description contains the given keyword,
     * ignoring case.
     *
     * @param keyword the text to look for
     * @return the matching tasks, in list order
     */
    public ArrayList<Task> matching(String keyword) {
        ArrayList<Task> matches = new ArrayList<>();
        String wanted = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(wanted)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns every task, for saving or displaying.
     *
     * @return the tasks, in list order
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    /**
     * Returns how many tasks the list holds.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Rejects a position that no task occupies.
     * <p>
     * Checked here rather than by each caller, so that every command that
     * refers to a task by number reports the same thing for the same mistake.
     *
     * @param index the position to check, 0-based
     * @throws MattChatBotException if there is no task at that position
     */
    private void checkInRange(int index) throws MattChatBotException {
        if (tasks.isEmpty()) {
            throw new MattChatBotException(
                    "Your list is empty, so there is no task " + (index + 1) + " yet.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new MattChatBotException("You have " + describeSize()
                    + ", so there is no task " + (index + 1) + ". Type "
                    + Command.LIST.getKeyword() + " to see them.");
        }
    }

    /**
     * Returns the task count with the right singular or plural noun, e.g.
     * {@code 1 task} or {@code 4 tasks}.
     *
     * @return the count and its noun
     */
    public String describeSize() {
        return tasks.size() + (tasks.size() == 1 ? " task" : " tasks");
    }
}
