/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * At this stage (Level-0) the bot has no interactive behaviour yet: it simply
 * greets the user and immediately says goodbye. Later increments will add a
 * command loop between the greeting and the farewell.
 */
public class MattChatBot {

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "MattChatBot";

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
        exit();
    }

    /** Prints the banner and the welcome message. */
    private static void greet() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
    }

    /** Prints the farewell message. */
    private static void exit() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
