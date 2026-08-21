import java.util.Scanner;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * At this stage (Level-2) the bot stores whatever text the user enters and
 * lists it back on request. Anything that is not a recognised command is
 * treated as a new item to store.
 */
public class MattChatBot {

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "MattChatBot";

    /** Command that ends the conversation. */
    private static final String COMMAND_BYE = "bye";

    /** Command that lists everything stored so far. */
    private static final String COMMAND_LIST = "list";

    /** Maximum number of items that can be stored, as allowed by the requirements. */
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

    /** Items the user has stored, filled from index 0 upwards. */
    private static final String[] tasks = new String[MAX_TASKS];

    /** How many entries of {@link #tasks} are actually in use. */
    private static int taskCount = 0;

    public static void main(String[] args) {
        greet();
        runCommandLoop();
        exit();
    }

    /**
     * Reads user input line by line and acts on it, stopping when the user
     * enters {@link #COMMAND_BYE}.
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
            if (input.equalsIgnoreCase(COMMAND_BYE)) {
                return;
            }
            if (input.equalsIgnoreCase(COMMAND_LIST)) {
                listTasks();
            } else {
                addTask(input);
            }
        }
    }

    /**
     * Stores one item and confirms it to the user.
     *
     * @param task the text to store
     */
    private static void addTask(String task) {
        if (taskCount == MAX_TASKS) {
            say("Sorry, I can only remember " + MAX_TASKS + " things at a time.");
            return;
        }
        tasks[taskCount] = task;
        taskCount++;
        say("added: " + task);
    }

    /** Prints every stored item, numbered from 1. */
    private static void listTasks() {
        if (taskCount == 0) {
            say("You haven't told me anything to remember yet.");
            return;
        }
        String[] numbered = new String[taskCount];
        for (int i = 0; i < taskCount; i++) {
            numbered[i] = (i + 1) + ". " + tasks[i];
        }
        say(numbered);
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
