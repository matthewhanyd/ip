import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * At this stage (Level-6) the bot tracks todos, deadlines and events, lets the
 * user mark, unmark and delete them, and reports bad input as a clear message
 * instead of crashing or silently doing the wrong thing.
 */
public class MattChatBot {

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "MattChatBot";

    /** Keyword separating a deadline's description from its due date. */
    private static final String KEYWORD_BY = "/by";

    /** Keyword separating an event's description from its start time. */
    private static final String KEYWORD_FROM = "/from";

    /** Keyword separating an event's start time from its end time. */
    private static final String KEYWORD_TO = "/to";

    /** Horizontal rule printed around each block of the bot's output. */
    private static final String DIVIDER =
            "____________________________________________________________";

    /** ASCII-art banner shown once when the program starts. */
    private static final String BANNER = """
            M   M   AAA   TTTTT  TTTTT
            MM MM  A   A    T      T
            M M M  AAAAA    T      T
            M   M  A   A    T      T
            M   M  A   A    T      T

             CCC   H   H   AAA   TTTTT  BBBB    OOO   TTTTT
            C   C  H   H  A   A    T    B   B  O   O    T
            C      HHHHH  AAAAA    T    BBBB   O   O    T
            C   C  H   H  A   A    T    B   B  O   O    T
             CCC   H   H  A   A    T    BBBB    OOO     T
            """;

    /**
     * Tasks the user has stored, in the order they were added.
     * <p>
     * An ArrayList rather than a fixed array (the A-Collections extension): it
     * grows as needed, so there is no 100-task ceiling, and it closes the gap
     * itself when a task is deleted.
     */
    private static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        greet();
        loadSavedTasks();
        runCommandLoop();
        exit();
    }

    /**
     * Loads any previously saved tasks, so a session continues where the last
     * one left off.
     */
    private static void loadSavedTasks() {
        try {
            tasks = Storage.load();
            int skipped = Storage.getSkippedLineCount();
            if (skipped > 0) {
                say("I couldn't understand " + skipped + (skipped == 1
                        ? " line in your saved file, so I skipped it."
                        : " lines in your saved file, so I skipped them."),
                        "The tasks I could read are still here.");
            }
        } catch (MattChatBotException e) {
            say(e.getMessage(), "Starting with an empty list instead.");
        }
    }

    /**
     * Reads user input line by line and acts on it, stopping when the user
     * says bye.
     * <p>
     * This is the single place where a {@link MattChatBotException} is turned
     * into a reply: the command methods below describe what went wrong and
     * throw, and this loop reports it and carries on with the next command, so
     * one bad input never ends the session.
     * <p>
     * The loop also stops if the input stream ends (e.g. the user presses
     * Ctrl-D, or input is piped in from a file), so the bot exits cleanly
     * instead of crashing when there is no more input to read.
     */
    private static void runCommandLoop() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            try {
                boolean isExit = handleCommand(input);
                if (isExit) {
                    return;
                }
            } catch (MattChatBotException e) {
                say(e.getMessage());
            }
        }
    }

    /**
     * Carries out one command.
     * <p>
     * Written as a switch expression over {@link Command} rather than a switch
     * statement over strings: because every case yields the exit flag, the
     * compiler requires the switch to cover every command, so adding a command
     * to the enum will not compile until it is handled here.
     *
     * @param input one non-empty line as the user typed it
     * @return true if the user asked to exit
     * @throws MattChatBotException if the command is unknown, or is missing a
     *                              part that it needs
     */
    private static boolean handleCommand(String input) throws MattChatBotException {
        // Split off the first word, so that a command word can be told apart
        // from a description that merely starts with the same word.
        String[] parts = input.split("\\s+", 2);
        Command command = Command.fromKeyword(parts[0]);
        String argument = parts.length > 1 ? parts[1].trim() : "";

        return switch (command) {
        case BYE -> true;
        case LIST -> {
            listTasks();
            yield false;
        }
        case MARK -> {
            setDone(argument, true);
            yield false;
        }
        case UNMARK -> {
            setDone(argument, false);
            yield false;
        }
        case DELETE -> {
            deleteTask(argument);
            yield false;
        }
        case TODO -> {
            addTodo(argument);
            yield false;
        }
        case DEADLINE -> {
            addDeadline(argument);
            yield false;
        }
        case EVENT -> {
            addEvent(argument);
            yield false;
        }
        };
    }

    /**
     * Adds a todo.
     *
     * @param argument the description, as the user typed it
     * @throws MattChatBotException if the description is missing
     */
    private static void addTodo(String argument) throws MattChatBotException {
        if (argument.isEmpty()) {
            throw new MattChatBotException(
                    "A todo needs a description. Try: "
                            + Command.TODO.getKeyword() + " borrow book");
        }
        addTask(new Todo(argument));
    }

    /**
     * Adds a deadline, whose argument has the form
     * {@code <description> /by <when>}.
     *
     * @param argument the description and due time, as the user typed them
     * @throws MattChatBotException if the description or the /by part is missing
     */
    private static void addDeadline(String argument) throws MattChatBotException {
        String example = "Try: " + Command.DEADLINE.getKeyword()
                + " return book /by Sunday";
        int byAt = argument.indexOf(KEYWORD_BY);
        if (byAt < 0) {
            throw new MattChatBotException(
                    "A deadline needs a /by, so I know when it is due. " + example);
        }
        String description = argument.substring(0, byAt).trim();
        String by = argument.substring(byAt + KEYWORD_BY.length()).trim();
        if (description.isEmpty()) {
            throw new MattChatBotException(
                    "A deadline needs a description before the /by. " + example);
        }
        if (by.isEmpty()) {
            throw new MattChatBotException(
                    "A deadline needs a date or time after the /by. " + example);
        }
        addTask(new Deadline(description, by));
    }

    /**
     * Adds an event, whose argument has the form
     * {@code <description> /from <start> /to <end>}.
     *
     * @param argument the description and time range, as the user typed them
     * @throws MattChatBotException if the description, the /from or the /to is missing
     */
    private static void addEvent(String argument) throws MattChatBotException {
        String example = "Try: " + Command.EVENT.getKeyword()
                + " project meeting /from Mon 2pm /to 4pm";
        int fromAt = argument.indexOf(KEYWORD_FROM);
        if (fromAt < 0) {
            throw new MattChatBotException(
                    "An event needs a /from, so I know when it starts. " + example);
        }
        // Look for /to only after /from, so that a description mentioning "/to"
        // does not get mistaken for the end time.
        int toAt = argument.indexOf(KEYWORD_TO, fromAt + KEYWORD_FROM.length());
        if (toAt < 0) {
            throw new MattChatBotException(
                    "An event needs a /to, so I know when it ends. " + example);
        }
        String description = argument.substring(0, fromAt).trim();
        String from = argument.substring(fromAt + KEYWORD_FROM.length(), toAt).trim();
        String to = argument.substring(toAt + KEYWORD_TO.length()).trim();
        if (description.isEmpty()) {
            throw new MattChatBotException(
                    "An event needs a description before the /from. " + example);
        }
        if (from.isEmpty()) {
            throw new MattChatBotException(
                    "An event needs a start time after the /from. " + example);
        }
        if (to.isEmpty()) {
            throw new MattChatBotException(
                    "An event needs an end time after the /to. " + example);
        }
        addTask(new Event(description, from, to));
    }

    /**
     * Stores one task and confirms it to the user.
     *
     * @param task the task to store
     * @throws MattChatBotException if the updated list cannot be saved
     */
    private static void addTask(Task task) throws MattChatBotException {
        tasks.add(task);
        Storage.save(tasks);
        say("Got it. I've added this task:", "  " + task, countSummary());
    }

    /**
     * Removes the task at the given position and confirms it to the user.
     *
     * @param argument the task number as the user typed it, 1-based
     * @throws MattChatBotException if the number is missing, not a number, or
     *                              outside the list
     */
    private static void deleteTask(String argument) throws MattChatBotException {
        int index = parseTaskNumber(argument, Command.DELETE);
        Task removed = tasks.remove(index);
        Storage.save(tasks);
        say("Noted. I've removed this task:", "  " + removed, countSummary());
    }

    /** Prints every stored task, numbered from 1, with its type and status. */
    private static void listTasks() {
        if (tasks.isEmpty()) {
            say("Your list is empty. Add something with "
                    + Command.TODO.getKeyword() + ", " + Command.DEADLINE.getKeyword()
                    + " or " + Command.EVENT.getKeyword() + ".");
            return;
        }
        String[] lines = new String[tasks.size() + 1];
        lines[0] = "Here are the tasks in your list:";
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
        }
        say(lines);
    }

    /**
     * Marks the task at the given position as done or not done.
     *
     * @param argument the task number as the user typed it, 1-based
     * @param isDone   true to mark the task done, false to reverse it
     * @throws MattChatBotException if the number is missing, not a number, or
     *                              outside the list
     */
    private static void setDone(String argument, boolean isDone)
            throws MattChatBotException {
        int index = parseTaskNumber(argument, isDone ? Command.MARK : Command.UNMARK);
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        Storage.save(tasks);
        if (isDone) {
            say("Nice! I've marked this task as done:", "  " + task);
        } else {
            say("OK, I've marked this task as not done yet:", "  " + task);
        }
    }

    /**
     * Converts what the user typed into a valid index into {@link #tasks}.
     *
     * @param argument the task number as the user typed it, 1-based
     * @param command  the command being run, so the message can name it
     * @return the matching 0-based index
     * @throws MattChatBotException if the number is missing, not a number, or
     *                              outside the list
     */
    private static int parseTaskNumber(String argument, Command command)
            throws MattChatBotException {
        String keyword = command.getKeyword();
        if (argument.isEmpty()) {
            throw new MattChatBotException("Which task should I " + keyword
                    + "? Try: " + keyword + " 2");
        }
        int number;
        try {
            number = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new MattChatBotException("\"" + argument
                    + "\" is not a task number. Try: " + keyword + " 2");
        }
        if (tasks.isEmpty()) {
            throw new MattChatBotException(
                    "Your list is empty, so there is no task " + number + " yet.");
        }
        if (number < 1 || number > tasks.size()) {
            throw new MattChatBotException("You have " + describeCount()
                    + ", so there is no task " + number + ". Type "
                    + Command.LIST.getKeyword() + " to see them.");
        }
        return number - 1;
    }

    /**
     * Returns the sentence reporting how many tasks remain, e.g.
     * {@code Now you have 4 tasks in the list.}
     */
    private static String countSummary() {
        return "Now you have " + describeCount() + " in the list.";
    }

    /**
     * Returns the task count with the right singular or plural noun, e.g.
     * {@code 1 task} or {@code 4 tasks}.
     */
    private static String describeCount() {
        return tasks.size() + (tasks.size() == 1 ? " task" : " tasks");
    }

    /** Prints the banner and the welcome message. */
    private static void greet() {
        say(BANNER, "Hello! I'm " + NAME + ".", "What can I do for you?");
    }

    /** Prints the farewell message. */
    private static void exit() {
        say("Bye. Hope to see you again soon!");
    }

    /**
     * Prints the given lines as one block, wrapped in horizontal dividers, so
     * that every reply the bot makes is formatted consistently.
     *
     * @param lines the lines to print between the dividers
     */
    private static void say(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
    }
}
