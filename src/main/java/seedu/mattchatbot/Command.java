package seedu.mattchatbot;

/**
 * The commands the chatbot understands, each paired with the keyword the user
 * types to invoke it.
 * <p>
 * Introduced by the A-Enums extension. Previously the keywords existed twice:
 * as string literals in the dispatch switch, and again inside the "I don't
 * know what that means" message that lists them. The two could drift apart
 * without the compiler noticing. Here each keyword is written once, and the
 * help text is generated from the same values that drive the dispatch.
 */
public enum Command {
    /** Adds a task with no date attached. */
    TODO("todo"),

    /** Adds a task due by a given date. */
    DEADLINE("deadline"),

    /** Adds a task running between two given dates. */
    EVENT("event"),

    /** Shows every task, numbered. */
    LIST("list"),

    /** Shows the tasks falling on a given date. */
    ON("on"),

    /** Marks a task as done. */
    MARK("mark"),

    /** Marks a task as not done yet. */
    UNMARK("unmark"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Ends the session. */
    BYE("bye");

    /** The word the user types to invoke this command. */
    private final String keyword;

    /**
     * Creates a command bound to the word that invokes it.
     *
     * @param keyword the word the user types
     */
    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, e.g. {@code deadline}
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Finds the command matching a word the user typed, ignoring case.
     *
     * @param word the first word of the user's input
     * @return the matching command
     * @throws MattChatBotException if no command uses that keyword
     */
    public static Command fromKeyword(String word) throws MattChatBotException {
        for (Command command : values()) {
            if (command.keyword.equalsIgnoreCase(word)) {
                return command;
            }
        }
        throw new MattChatBotException("I don't know what \"" + word.toLowerCase()
                + "\" means. I understand: " + listKeywords());
    }

    /**
     * Returns every keyword, comma separated, for use in messages to the user.
     * <p>
     * Generated from the enum, so the list stays in step with the commands
     * that actually exist rather than being maintained by hand.
     *
     * @return the keywords in declaration order, separated by ", "
     */
    public static String listKeywords() {
        StringBuilder keywords = new StringBuilder();
        for (Command command : values()) {
            if (!keywords.isEmpty()) {
                keywords.append(", ");
            }
            keywords.append(command.keyword);
        }
        return keywords.toString();
    }
}
