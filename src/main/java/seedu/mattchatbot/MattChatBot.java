package seedu.mattchatbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import seedu.mattchatbot.task.Task;
import seedu.mattchatbot.task.TaskList;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * Decides what to do with each command, leaving the details to the classes
 * that own them: Ui for talking to the user, Parser for reading the input,
 * TaskList for the tasks, and Storage for the save file.
 */
public class MattChatBot {

    /** Where the task list is kept between sessions. */
    private static final String SAVE_FILE = "data/mattchatbot.txt";

    /** Everything the user sees and types goes through here. */
    private final Ui ui;

    /** Reads and writes the save file. */
    private final Storage storage;

    /** The tasks the user is keeping track of. */
    private TaskList tasks;

    /**
     * A problem met while loading, to report once the welcome has been shown,
     * or null if loading went fine.
     */
    private String loadWarning;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * @param filePath where to load from and save to
     */
    public MattChatBot(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
            int skipped = storage.getSkippedLineCount();
            if (skipped > 0) {
                loadWarning = "I couldn't understand " + skipped + (skipped == 1
                        ? " line in your saved file, so I skipped it."
                        : " lines in your saved file, so I skipped them.");
            }
        } catch (MattChatBotException e) {
            tasks = new TaskList();
            loadWarning = e.getMessage();
        }
    }

    /** Greets the user, handles commands until they say bye, then signs off. */
    public void run() {
        ui.showWelcome();
        if (loadWarning != null) {
            ui.show(loadWarning, "The tasks I could read are still here.");
        }
        runCommandLoop();
        ui.showGoodbye();
    }

    /**
     * Starts the chatbot, reading and writing the default save file.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        new MattChatBot(SAVE_FILE).run();
    }

    /**
     * Reads commands until the user says bye, reporting any problem and
     * carrying on rather than ending the session.
     */
    private void runCommandLoop() {
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
    private boolean handleCommand(String input) throws MattChatBotException {
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
    private void addTask(Task task) throws MattChatBotException {
        tasks.add(task);
        storage.save(tasks);
        ui.show("Got it. I've added this task:", "  " + task, countSummary());
    }

    /**
     * Removes the task at the given position and confirms it to the user.
     *
     * @param index the task's position, 0-based
     * @throws MattChatBotException if there is no such task, or saving fails
     */
    private void deleteTask(int index) throws MattChatBotException {
        Task removed = tasks.remove(index);
        storage.save(tasks);
        ui.show("Noted. I've removed this task:", "  " + removed, countSummary());
    }

    /**
     * Marks the task at the given position as done or not done.
     *
     * @param index  the task's position, 0-based
     * @param isDone true to mark the task done, false to reverse it
     * @throws MattChatBotException if there is no such task, or saving fails
     */
    private void setDone(int index, boolean isDone) throws MattChatBotException {
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks);
        if (isDone) {
            ui.show("Nice! I've marked this task as done:", "  " + task);
        } else {
            ui.show("OK, I've marked this task as not done yet:", "  " + task);
        }
    }

    /** Prints every stored task, numbered from 1, with its type and status. */
    private void listTasks() {
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
    private void listTasksOn(LocalDate date) {
        String shownDate = date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        ArrayList<Task> matches = tasks.getTasksOn(date);
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
    private String countSummary() {
        return "Now you have " + tasks.describeSize() + " in the list.";
    }
}
