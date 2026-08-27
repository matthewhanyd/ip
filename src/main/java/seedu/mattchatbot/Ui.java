package seedu.mattchatbot;

import java.util.Scanner;

/**
 * Handles everything the user sees and types.
 * <p>
 * Extracted so that the wording and layout of the chatbot's replies live in
 * one place: the rest of the code decides what happened, and asks Ui to say
 * it. Changing how replies look, or moving them to a different interface
 * later, then touches only this class.
 */
public class Ui {

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

    /** Where the user's input is read from. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns whether there is another line of input to read.
     * <p>
     * False once the input stream ends, e.g. the user presses Ctrl-D or piped
     * input runs out, which lets the chatbot exit cleanly rather than crash.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one line of input, with surrounding spaces removed.
     *
     * @return what the user typed
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the banner and the welcome message. */
    public void showWelcome() {
        show(BANNER, "Hello! I'm MattChatBot.", "What can I do for you?");
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        show("Bye. Hope to see you again soon!");
    }

    /**
     * Prints an error back to the user.
     *
     * @param message what went wrong, written for the user
     */
    public void showError(String message) {
        show(message);
    }

    /**
     * Prints the problem met while loading the save file, and says what the
     * chatbot is doing about it.
     *
     * @param message what went wrong, written for the user
     */
    public void showLoadingError(String message) {
        show(message, "Starting with an empty list instead.");
    }

    /**
     * Prints the given lines as one block, wrapped in horizontal dividers, so
     * that every reply the bot makes is formatted consistently.
     *
     * @param lines the lines to print between the dividers
     */
    public void show(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
    }
}
