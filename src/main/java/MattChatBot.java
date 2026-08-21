import java.util.Scanner;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * At this stage (Level-3) the bot stores tasks, lists them with their
 * done/not-done status, and lets the user mark and unmark them.
 */
public class MattChatBot {

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "MattChatBot";

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
     * enters the bye command.
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
            // Split off the first word so that "mark 2" can be told apart from
            // a task that merely happens to start with the word "mark".
            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";

            switch (command) {
            case "bye" -> {
                return;
            }
            case "list" -> listTasks();
            case "mark" -> setDone(argument, true);
            case "unmark" -> setDone(argument, false);
            default -> addTask(input);
            }
        }
    }

    /**
     * Stores one task and confirms it to the user.
     *
     * @param description the text to store
     */
    private static void addTask(String description) {
        if (taskCount == MAX_TASKS) {
            say("Sorry, I can only remember " + MAX_TASKS + " things at a time.");
            return;
        }
        tasks[taskCount] = new Task(description);
        taskCount++;
        say("added: " + description);
    }

    /** Prints every stored task, numbered from 1, with its status. */
    private static void listTasks() {
        if (taskCount == 0) {
            say("You haven't told me anything to remember yet.");
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
     */
    private static void setDone(String argument, boolean isDone) {
        int index = parseTaskNumber(argument);
        if (index < 0) {
            return;
        }
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
     * Converts what the user typed into a valid index into {@link #tasks},
     * explaining the problem to the user if it is not usable.
     *
     * @param argument the task number as the user typed it, 1-based
     * @return the matching 0-based index, or -1 if the input was not usable
     */
    private static int parseTaskNumber(String argument) {
        int number;
        try {
            number = Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            say("Please give me a task number, e.g. mark 2");
            return -1;
        }
        if (number < 1 || number > taskCount) {
            say("You don't have a task " + number + ". Type list to see what you have.");
            return -1;
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
