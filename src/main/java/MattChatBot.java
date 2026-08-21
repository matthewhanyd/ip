import java.util.Scanner;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * At this stage (Level-5) the bot tracks todos, deadlines and events, lets
 * the user mark and unmark them, and reports bad input as a clear message
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

    /** Maximum number of tasks that can be stored, as allowed by the requirements. */
    private static final int MAX_TASKS = 100;

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

    /** Tasks the user has stored, filled from index 0 upwards. */
    private static final Task[] tasks = new Task[MAX_TASKS];

    /** How many entries of {@link #tasks} are actually in use. */
    private static int taskCount = 0;

    public static void main(String[] args) {
        greet();
        runCommandLoop();
        exit();
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
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
        case "bye" -> {
            return true;
        }
        case "list" -> listTasks();
        case "mark" -> setDone(argument, true);
        case "unmark" -> setDone(argument, false);
        case "todo" -> addTodo(argument);
        case "deadline" -> addDeadline(argument);
        case "event" -> addEvent(argument);
        default -> throw new MattChatBotException(
                "I don't know what \"" + command + "\" means. "
                        + "I understand: todo, deadline, event, list, mark, unmark, bye");
        }
        return false;
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
                    "A todo needs a description. Try: todo borrow book");
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
        String example = "Try: deadline return book /by Sunday";
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
        String example = "Try: event project meeting /from Mon 2pm /to 4pm";
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
     * @throws MattChatBotException if the list is already full
     */
    private static void addTask(Task task) throws MattChatBotException {
        if (taskCount == MAX_TASKS) {
            throw new MattChatBotException("My list is full at " + MAX_TASKS
                    + " tasks, so I can't add another one.");
        }
        tasks[taskCount] = task;
        taskCount++;
        say("Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + (taskCount == 1 ? " task" : " tasks")
                        + " in the list.");
    }

    /** Prints every stored task, numbered from 1, with its type and status. */
    private static void listTasks() {
        if (taskCount == 0) {
            say("Your list is empty. Add something with todo, deadline or event.");
            return;
        }
        String[] lines = new String[taskCount + 1];
        lines[0] = "Here are the tasks in your list:";
        for (int i = 0; i < taskCount; i++) {
            lines[i + 1] = (i + 1) + "." + tasks[i];
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
        int index = parseTaskNumber(argument, isDone ? "mark" : "unmark");
        Task task = tasks[index];
        if (isDone) {
            task.markAsDone();
            say("Nice! I've marked this task as done:", "  " + task);
        } else {
            task.markAsNotDone();
            say("OK, I've marked this task as not done yet:", "  " + task);
        }
    }

    /**
     * Converts what the user typed into a valid index into {@link #tasks}.
     *
     * @param argument the task number as the user typed it, 1-based
     * @param command  the command being run, used to make the message specific
     * @return the matching 0-based index
     * @throws MattChatBotException if the number is missing, not a number, or
     *                              outside the list
     */
    private static int parseTaskNumber(String argument, String command)
            throws MattChatBotException {
        if (argument.isEmpty()) {
            throw new MattChatBotException("Which task should I " + command
                    + "? Try: " + command + " 2");
        }
        int number;
        try {
            number = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new MattChatBotException("\"" + argument
                    + "\" is not a task number. Try: " + command + " 2");
        }
        if (taskCount == 0) {
            throw new MattChatBotException(
                    "Your list is empty, so there is no task " + number + " yet.");
        }
        if (number < 1 || number > taskCount) {
            throw new MattChatBotException("You have " + taskCount
                    + (taskCount == 1 ? " task" : " tasks")
                    + ", so there is no task " + number + ". Type list to see them.");
        }
        return number - 1;
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
