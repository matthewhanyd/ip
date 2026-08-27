package seedu.mattchatbot;

import java.time.LocalDate;
import seedu.mattchatbot.task.Deadline;
import seedu.mattchatbot.task.Event;
import seedu.mattchatbot.task.Todo;

/**
 * Turns the raw text the user types into things the rest of the code can act
 * on: a command, a task, a task number, a date.
 * <p>
 * Extracted so that the input syntax -- which word is the command, where the
 * /by and /from markers go, what a valid date looks like -- is described in
 * one place, separate from what the chatbot then does about it.
 */
public class Parser {

    /** Not meant to be instantiated: every member of Parser is static. */
    private Parser() {
    }

    /** Keyword separating a deadline's description from its due date. */
    private static final String KEYWORD_BY = "/by";

    /** Keyword separating an event's description from its start time. */
    private static final String KEYWORD_FROM = "/from";

    /** Keyword separating an event's start time from its end time. */
    private static final String KEYWORD_TO = "/to";

    /**
     * Reads the command word, i.e. the first word of the input.
     *
     * @param input one non-empty line as the user typed it
     * @return the command it names
     * @throws MattChatBotException if no command uses that word
     */
    public static Command parseCommand(String input) throws MattChatBotException {
        return Command.fromKeyword(splitOffCommandWord(input)[0]);
    }

    /**
     * Reads everything after the command word.
     *
     * @param input one non-empty line as the user typed it
     * @return the rest of the line, or an empty string if there is none
     */
    public static String parseArgument(String input) {
        String[] parts = splitOffCommandWord(input);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Splits the input into its command word and the rest.
     * <p>
     * Done on the first space only, so that a description which happens to
     * begin with a command word is not mistaken for that command.
     *
     * @param input one non-empty line as the user typed it
     * @return one or two parts: the command word, then the remainder
     */
    private static String[] splitOffCommandWord(String input) {
        return input.split("\\s+", 2);
    }

    /**
     * Builds a todo from the text after the command word.
     *
     * @param argument the description, as the user typed it
     * @return the todo
     * @throws MattChatBotException if the description is missing
     */
    public static Todo parseTodo(String argument) throws MattChatBotException {
        if (argument.isEmpty()) {
            throw new MattChatBotException("A todo needs a description. Try: "
                    + Command.TODO.getKeyword() + " borrow book");
        }
        return new Todo(argument);
    }

    /**
     * Builds a deadline from text of the form {@code <description> /by <when>}.
     *
     * @param argument the description and due date, as the user typed them
     * @return the deadline
     * @throws MattChatBotException if a part is missing or the date is not understood
     */
    public static Deadline parseDeadline(String argument) throws MattChatBotException {
        String example = "Try: " + Command.DEADLINE.getKeyword()
                + " return book /by 2019-10-15";
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
        return new Deadline(description, DateTimes.parse(by));
    }

    /**
     * Builds an event from text of the form
     * {@code <description> /from <start> /to <end>}.
     *
     * @param argument the description and time range, as the user typed them
     * @return the event
     * @throws MattChatBotException if a part is missing or a date is not understood
     */
    public static Event parseEvent(String argument) throws MattChatBotException {
        String example = "Try: " + Command.EVENT.getKeyword()
                + " project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";
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
        return new Event(description, DateTimes.parse(from), DateTimes.parse(to));
    }

    /**
     * Converts a task number the user typed into a list position.
     * <p>
     * Only converts: whether the position exists is TaskList's business, so
     * this does not need to know how many tasks there are.
     *
     * @param argument the task number as the user typed it, 1-based
     * @param command  the command being run, so the message can name it
     * @return the matching 0-based index
     * @throws MattChatBotException if the number is missing or is not a number
     */
    public static int parseTaskNumber(String argument, Command command)
            throws MattChatBotException {
        String keyword = command.getKeyword();
        if (argument.isEmpty()) {
            throw new MattChatBotException("Which task should I " + keyword
                    + "? Try: " + keyword + " 2");
        }
        try {
            return Integer.parseInt(argument) - 1;
        } catch (NumberFormatException e) {
            throw new MattChatBotException("\"" + argument
                    + "\" is not a task number. Try: " + keyword + " 2");
        }
    }

    /**
     * Reads the date given to the on command.
     *
     * @param argument the date as the user typed it
     * @return the date
     * @throws MattChatBotException if the date is missing or not understood
     */
    public static LocalDate parseOnDate(String argument) throws MattChatBotException {
        if (argument.isEmpty()) {
            throw new MattChatBotException("Which date? Try: "
                    + Command.ON.getKeyword() + " 2019-10-15");
        }
        return DateTimes.parseDate(argument);
    }
}
