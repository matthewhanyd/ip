import java.util.Scanner;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * At this stage (Level-1) the bot echoes back whatever the user types, until
 * the user enters the {@code bye} command. Later increments will replace the
 * echoing with real command handling.
 */
public class MattChatBot {

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "MattChatBot";

    /** Command that ends the conversation. */
    private static final String COMMAND_BYE = "bye";

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

    public static void main(String[] args) {
        greet();
        echoUntilBye();
        exit();
    }

    /**
     * Reads user input line by line and echoes each line back, stopping when
     * the user enters {@link #COMMAND_BYE}.
     * <p>
     * The loop also stops if the input stream ends (e.g. the user presses
     * Ctrl-D, or input is piped in from a file), so the bot exits cleanly
     * instead of crashing when there is no more input to read.
     */
    private static void echoUntilBye() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase(COMMAND_BYE)) {
                return;
            }
            say(input);
        }
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
