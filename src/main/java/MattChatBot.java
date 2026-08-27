import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * Decides what to do with each command, leaving the details to the classes
 * that own them: Ui for talking to the user, Parser for reading the input,
 * TaskList for the tasks, and Storage for the save file.
 */
public class MattChatBot {

    /** The tasks the user is keeping track of. */
    private static TaskList tasks = new TaskList();

    /** Everything the user sees and types goes through here. */
    private static final Ui ui = new Ui();

    public static void main(String[] args) {
        ui.showWelcome();
        loadSavedTasks();
        runCommandLoop();
        ui.showGoodbye();
    }

    /**
     * Loads any previously saved tasks, so a session continues where the last
     * one left off.
     */
    private static void loadSavedTasks() {
        try {
            tasks = new TaskList(Storage.load());
            int skipped = Storage.getSkippedLineCount();
            if (skipped > 0) {
                ui.show("I couldn't understand " + skipped + (skipped == 1
                        ? " line in your saved file, so I skipped it."
                        : " lines in your saved file, so I skipped them."),
                        "The tasks I could read are still here.");
            }
        } catch (MattChatBotException e) {
            ui.showLoadingError(e.getMessage());
        }
    }

    /**
     * Reads commands until the user says bye, reporting any problem and
     * carrying on rather than ending the session.
     */
    private static void runCommandLoop() {
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.isEmpty()) {
                continue;
            }
            try {
                boolean isExit = handleCommand(input);
                if (isExit) {
                    return;
                }
            } catch (MattChatBotException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Carries out one command.
     * <p>
     * Written as a switch expression over {@link Command}: because every case
     * yields the exit flag, the compiler requires the switch to cover every
     * command, so adding a command to the enum will not compile until it is
     * handled here.
     *
     * @param input one non-empty line as the user typed it
     * @return true if the user asked to exit
     * @throws MattChatBotException if the command cannot be carried out
     */
    private static boolean handleCommand(String input) throws MattChatBotException {
        Command command = Parser.parseCommand(input);
        String argument = Parser.parseArgument(input);

        return switch (command) {
        case BYE -> true;
        case LIST -> {
            listTasks();
            yield false;
        }
        case ON -> {
            listTasksOn(Parser.parseOnDate(argument));
            yield false;
        }
        case MARK -> {
            setDone(Parser.parseTaskNumber(argument, Command.MARK), true);
            yield false;
        }
        case UNMARK -> {
            setDone(Parser.parseTaskNumber(argument, Command.UNMARK), false);
            yield false;
        }
        case DELETE -> {
            deleteTask(Parser.parseTaskNumber(argument, Command.DELETE));
            yield false;
        }
        case TODO -> {
            addTask(Parser.parseTodo(argument));
            yield false;
        }
        case DEADLINE -> {
            addTask(Parser.parseDeadline(argument));
            yield false;
        }
        case EVENT -> {
            addTask(Parser.parseEvent(argument));
            yield false;
        }
        };
    }

    /**
     * Stores one task and confirms it to the user.
     *
     * @param task the task to store
     * @throws MattChatBotException if the updated list cannot be saved
     */
    private static void addTask(Task task) throws MattChatBotException {
        tasks.add(task);
        Storage.save(tasks.asList());
        ui.show("Got it. I've added this task:", "  " + task, countSummary());
    }

    /**
     * Removes the task at the given position and confirms it to the user.
     *
     * @param index the task's position, 0-based
     * @throws MattChatBotException if there is no such task, or saving fails
     */
    private static void deleteTask(int index) throws MattChatBotException {
        Task removed = tasks.remove(index);
        Storage.save(tasks.asList());
        ui.show("Noted. I've removed this task:", "  " + removed, countSummary());
    }

    /**
     * Marks the task at the given position as done or not done.
     *
     * @param index  the task's position, 0-based
     * @param isDone true to mark the task done, false to reverse it
     * @throws MattChatBotException if there is no such task, or saving fails
     */
    private static void setDone(int index, boolean isDone) throws MattChatBotException {
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        Storage.save(tasks.asList());
        if (isDone) {
            ui.show("Nice! I've marked this task as done:", "  " + task);
        } else {
            ui.show("OK, I've marked this task as not done yet:", "  " + task);
        }
    }

    /** Prints every stored task, numbered from 1, with its type and status. */
    private static void listTasks() {
        if (tasks.isEmpty()) {
            ui.show("Your list is empty. Add something with "
                    + Command.TODO.getKeyword() + ", " + Command.DEADLINE.getKeyword()
                    + " or " + Command.EVENT.getKeyword() + ".");
            return;
        }
        ui.show(numbered("Here are the tasks in your list:", tasks.asList()));
    }

    /**
     * Prints the tasks that fall on a given date.
     *
     * @param date the date to report on
     */
    private static void listTasksOn(LocalDate date) {
        String shownDate = date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        ArrayList<Task> matches = tasks.occurringOn(date);
        if (matches.isEmpty()) {
            ui.show("Nothing on " + shownDate + ".");
            return;
        }
        ui.show(numbered("Here is what you have on " + shownDate + ":", matches));
    }

    /**
     * Builds a block of output: a heading, then the tasks numbered from 1.
     *
     * @param heading the first line
     * @param shown   the tasks to number
     * @return the lines to print
     */
    private static String[] numbered(String heading, ArrayList<Task> shown) {
        String[] lines = new String[shown.size() + 1];
        lines[0] = heading;
        for (int i = 0; i < shown.size(); i++) {
            lines[i + 1] = (i + 1) + "." + shown.get(i);
        }
        return lines;
    }

    /**
     * Returns the sentence reporting how many tasks remain, e.g.
     * {@code Now you have 4 tasks in the list.}
     */
    private static String countSummary() {
        return "Now you have " + tasks.describeSize() + " in the list.";
    }
}
