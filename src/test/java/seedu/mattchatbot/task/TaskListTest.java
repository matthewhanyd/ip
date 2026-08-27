package seedu.mattchatbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.mattchatbot.MattChatBotException;

/**
 * Tests that TaskList enforces its own bounds, and that the date search
 * matches the task types that carry dates.
 */
public class TaskListTest {

    private static LocalDateTime at(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    private static TaskList listOfThree() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", at(2019, 10, 15)));
        tasks.add(new Event("orientation", at(2019, 10, 14), at(2019, 10, 18)));
        return tasks;
    }

    @Test
    public void newList_noArguments_isEmpty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void newList_existingTasks_holdsThem() {
        ArrayList<Task> given = new ArrayList<>();
        given.add(new Todo("read book"));
        TaskList tasks = new TaskList(given);
        assertEquals(1, tasks.size());
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void get_validIndex_taskReturned() throws Exception {
        TaskList tasks = listOfThree();
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[E][ ] orientation (from: Oct 14 2019 to: Oct 18 2019)",
                tasks.get(2).toString());
    }

    @Test
    public void get_emptyList_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class,
                () -> new TaskList().get(0));
        assertEquals("Your list is empty, so there is no task 1 yet.", e.getMessage());
    }

    @Test
    public void get_indexPastEnd_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class,
                () -> listOfThree().get(3));
        assertEquals("You have 3 tasks, so there is no task 4. Type list to see them.",
                e.getMessage());
    }

    @Test
    public void get_negativeIndex_exceptionThrown() {
        assertThrows(MattChatBotException.class, () -> listOfThree().get(-1));
    }

    @Test
    public void remove_validIndex_taskRemovedAndReturned() throws Exception {
        TaskList tasks = listOfThree();
        Task removed = tasks.remove(1);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", removed.toString());
        assertEquals(2, tasks.size());
        // The tasks after it shift down, so position 1 is now the event.
        assertEquals("[E][ ] orientation (from: Oct 14 2019 to: Oct 18 2019)",
                tasks.get(1).toString());
    }

    @Test
    public void remove_indexPastEnd_exceptionThrownAndListUnchanged() {
        TaskList tasks = listOfThree();
        assertThrows(MattChatBotException.class, () -> tasks.remove(5));
        assertEquals(3, tasks.size());
    }

    @Test
    public void getTasksOn_deadlineOnThatDate_deadlineMatched() {
        ArrayList<Task> found = listOfThree().getTasksOn(LocalDate.of(2019, 10, 15));
        // The deadline falls on the date, and the event spans it.
        assertEquals(2, found.size());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", found.get(0).toString());
    }

    @Test
    public void getTasksOn_dateInsideEventRange_eventMatched() {
        ArrayList<Task> found = listOfThree().getTasksOn(LocalDate.of(2019, 10, 17));
        assertEquals(1, found.size());
        assertEquals("[E][ ] orientation (from: Oct 14 2019 to: Oct 18 2019)",
                found.get(0).toString());
    }

    @Test
    public void getTasksOn_eventBoundaryDates_bothEndsIncluded() {
        assertEquals(1, listOfThree().getTasksOn(LocalDate.of(2019, 10, 14)).size());
        assertEquals(1, listOfThree().getTasksOn(LocalDate.of(2019, 10, 18)).size());
    }

    @Test
    public void getTasksOn_dateOutsideEverything_nothingMatched() {
        assertTrue(listOfThree().getTasksOn(LocalDate.of(2020, 1, 1)).isEmpty());
        // A todo has no date, so it never matches.
        assertTrue(new TaskList(new ArrayList<>()).getTasksOn(LocalDate.now()).isEmpty());
    }

    @Test
    public void describeSize_variousCounts_correctPlural() {
        TaskList tasks = new TaskList();
        assertEquals("0 tasks", tasks.describeSize());
        tasks.add(new Todo("one"));
        assertEquals("1 task", tasks.describeSize());
        tasks.add(new Todo("two"));
        assertEquals("2 tasks", tasks.describeSize());
    }
}
