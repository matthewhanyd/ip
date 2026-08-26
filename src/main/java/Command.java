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
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    ON("on"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye");

    /** The word the user types to invoke this command. */
    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
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
     * Generating this from the enum keeps it in step with the commands that
     * actually exist.
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
