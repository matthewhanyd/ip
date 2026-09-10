package seedu.mattchatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.mattchatbot.task.Deadline;
import seedu.mattchatbot.task.Event;
import seedu.mattchatbot.task.TaskUpdate;
import seedu.mattchatbot.task.Todo;

/**
 * Tests that Parser reads well-formed input correctly, and rejects malformed
 * input with a message rather than by throwing something unexpected.
 */
public class ParserTest {

    @Test
    public void parseCommand_knownKeyword_commandReturned() throws Exception {
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_differentCase_commandReturned() throws Exception {
        assertEquals(Command.TODO, Parser.parseCommand("ToDo read book"));
        assertEquals(Command.LIST, Parser.parseCommand("LIST"));
    }

    @Test
    public void parseCommand_unknownKeyword_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseCommand("blah blah"));
        assertEquals("I don't know what \"blah\" means. I understand: "
                + "todo, deadline, event, list, on, find, mark, unmark, delete, update, bye",
                e.getMessage());
    }

    @Test
    public void parseArgument_variousInputs_remainderReturned() {
        assertEquals("read book", Parser.parseArgument("todo read book"));
        assertEquals("", Parser.parseArgument("list"));
        // Only the first space separates the command from its argument, so the
        // rest of the line is kept intact.
        assertEquals("read  a  book", Parser.parseArgument("todo read  a  book"));
    }

    @Test
    public void parseTodo_hasDescription_todoReturned() throws Exception {
        Todo todo = Parser.parseTodo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseTodo(""));
        assertEquals("A todo needs a description. Try: todo borrow book", e.getMessage());
    }

    @Test
    public void parseDeadline_descriptionAndDate_deadlineReturned() throws Exception {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_dateWithTime_timeKept() throws Exception {
        Deadline deadline = Parser.parseDeadline("submit report /by 2019-10-15 1800");
        assertEquals("[D][ ] submit report (by: Oct 15 2019, 6:00pm)", deadline.toString());
    }

    @Test
    public void parseDeadline_missingBy_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseDeadline("return book"));
        assertEquals("A deadline needs a /by, so I know when it is due. "
                + "Try: deadline return book /by 2019-10-15", e.getMessage());
    }

    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseDeadline("/by 2019-10-15"));
        assertEquals("A deadline needs a description before the /by. "
                + "Try: deadline return book /by 2019-10-15", e.getMessage());
    }

    @Test
    public void parseDeadline_nothingAfterBy_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseDeadline("return book /by"));
        assertEquals("A deadline needs a date or time after the /by. "
                + "Try: deadline return book /by 2019-10-15", e.getMessage());
    }

    @Test
    public void parseEvent_descriptionAndRange_eventReturned() throws Exception {
        Event event = Parser.parseEvent("meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)",
                event.toString());
    }

    @Test
    public void parseEvent_missingTo_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseEvent("meeting /from 2019-10-15"));
        assertEquals("An event needs a /to, so I know when it ends. Try: event "
                + "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600", e.getMessage());
    }

    @Test
    public void parseTaskNumber_validNumber_zeroBasedIndexReturned() throws Exception {
        assertEquals(0, Parser.parseTaskNumber("1", Command.MARK));
        assertEquals(41, Parser.parseTaskNumber("42", Command.DELETE));
    }

    @Test
    public void parseTaskNumber_missingNumber_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseTaskNumber("", Command.MARK));
        assertEquals("Which task should I mark? Try: mark 2", e.getMessage());
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseTaskNumber("abc", Command.UNMARK));
        assertEquals("\"abc\" is not a task number. Try: unmark 2", e.getMessage());
    }

    @Test
    public void parseKeyword_hasKeyword_keywordReturned() throws Exception {
        assertEquals("book", Parser.parseKeyword("book"));
        assertEquals("read book", Parser.parseKeyword("read book"));
    }

    @Test
    public void parseKeyword_missingKeyword_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseKeyword(""));
        assertEquals("What should I look for? Try: find book", e.getMessage());
    }

    @Test
    public void parseOnDate_validDate_dateReturned() throws Exception {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0).toLocalDate(),
                Parser.parseOnDate("2019-10-15"));
    }

    @Test
    public void parseOnDate_missingDate_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseOnDate(""));
        assertEquals("Which date? Try: on 2019-10-15", e.getMessage());
    }

    @Test
    public void parseUpdateTarget_validNumber_indexReturned() throws Exception {
        assertEquals(1, Parser.parseUpdateTarget("2 new description"));
        assertEquals(0, Parser.parseUpdateTarget("1 /by 2019-12-01"));
    }

    @Test
    public void parseUpdateTarget_missingNumber_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseUpdateTarget(""));
        assertEquals("Which task should I update? Try: update 2 new description",
                e.getMessage());
    }

    @Test
    public void parseUpdateTarget_notANumber_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseUpdateTarget("abc something"));
        assertEquals("\"abc\" is not a task number. Try: update 2", e.getMessage());
    }

    @Test
    public void parseUpdateChanges_descriptionContainingTo_markerNotMistaken() throws Exception {
        // "cover to cover" holds the word "to" but not the marker "/to", so
        // the whole phrase must stay part of the description.
        TaskUpdate update = Parser.parseUpdateChanges("2 read book cover to cover");
        assertEquals("read book cover to cover", update.description());
        assertFalse(update.hasBy());
        assertFalse(update.hasFrom());
        assertFalse(update.hasTo());
    }

    @Test
    public void parseUpdateChanges_endTimeOnly_onlyEndTimeSet() throws Exception {
        TaskUpdate update = Parser.parseUpdateChanges("3 /to 2019-10-15 1800");
        assertFalse(update.hasDescription());
        assertFalse(update.hasFrom());
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), update.to());
    }

    @Test
    public void parseUpdateChanges_bothEventTimes_bothSet() throws Exception {
        TaskUpdate update =
                Parser.parseUpdateChanges("3 /from 2019-10-15 1000 /to 2019-10-15 1200");
        assertEquals(LocalDateTime.of(2019, 10, 15, 10, 0), update.from());
        assertEquals(LocalDateTime.of(2019, 10, 15, 12, 0), update.to());
        assertFalse(update.hasDescription());
    }

    @Test
    public void parseUpdateChanges_descriptionAndDueDate_bothSet() throws Exception {
        TaskUpdate update = Parser.parseUpdateChanges("1 buy bread /by 2019-12-01");
        assertEquals("buy bread", update.description());
        assertEquals(LocalDateTime.of(2019, 12, 1, 0, 0), update.by());
    }

    @Test
    public void parseUpdateChanges_nothingAsked_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseUpdateChanges("2"));
        assertEquals("Tell me what to change. Try: update 2 new description, "
                + "or update 2 /by 2019-12-01", e.getMessage());
    }

    @Test
    public void parseUpdateChanges_markerWithoutValue_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                Parser.parseUpdateChanges("2 /by"));
        assertEquals("The /by needs a date after it. Try: update 2 /by 2019-12-01",
                e.getMessage());
    }

    @Test
    public void parseUpdateChanges_unreadableDate_exceptionThrown() {
        assertThrows(MattChatBotException.class, () ->
                Parser.parseUpdateChanges("2 /by notadate"));
    }
}
