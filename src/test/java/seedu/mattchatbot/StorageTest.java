package seedu.mattchatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.mattchatbot.task.Deadline;
import seedu.mattchatbot.task.Task;
import seedu.mattchatbot.task.TaskList;
import seedu.mattchatbot.task.Todo;

/**
 * Tests saving and loading, including the awkward cases the requirements call
 * out: a missing file, a missing folder, and a damaged file.
 * <p>
 * Each test writes inside a temporary folder supplied by JUnit, so no test
 * touches the real save file.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    private Storage storageAt(String name) {
        return new Storage(tempDir.resolve(name).toString());
    }

    @Test
    public void load_fileDoesNotExist_emptyListReturned() throws Exception {
        ArrayList<Task> loaded = storageAt("nothing-here.txt").load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void save_folderDoesNotExist_folderCreated() throws Exception {
        Storage storage = new Storage(tempDir.resolve("new/nested/tasks.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        storage.save(tasks);
        assertTrue(Files.exists(tempDir.resolve("new/nested/tasks.txt")));
    }

    @Test
    public void saveThenLoad_allTaskTypes_roundTripPreservesEverything() throws Exception {
        Storage storage = storageAt("round-trip.txt");
        TaskList original = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", DateTimes.parse("2019-10-15")));
        original.add(Parser.parseEvent("meeting /from 2019-10-15 1400 /to 2019-10-15 1600"));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)",
                loaded.get(2).toString());
    }

    @Test
    public void load_damagedLines_badLinesSkippedAndCounted() throws Exception {
        Path file = tempDir.resolve("damaged.txt");
        writeLines(file,
                "T | 1 | read book",
                "this is not a task",
                "D | 0 | missing its date",
                "X | 0 | unknown type | x",
                "T | 9 | bad status flag",
                "E | 0 | good event | 2019-10-15 1400 | 2019-10-15 1600");
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals(4, storage.getSkippedLineCount());
        assertEquals("[T][X] read book", loaded.get(0).toString());
    }

    @Test
    public void load_blankLines_ignoredWithoutCountingAsDamage() throws Exception {
        Path file = tempDir.resolve("blanks.txt");
        writeLines(file, "T | 0 | read book", "", "   ", "T | 0 | borrow book");
        Storage storage = new Storage(file.toString());

        assertEquals(2, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_calledTwice_skippedCountResets() throws Exception {
        Path file = tempDir.resolve("reset.txt");
        writeLines(file, "rubbish");
        Storage storage = new Storage(file.toString());
        storage.load();
        assertEquals(1, storage.getSkippedLineCount());

        writeLines(file, "T | 0 | fine now");
        storage.load();
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void save_calledTwice_fileReplacedNotAppended() throws Exception {
        Storage storage = storageAt("replace.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        storage.save(tasks);
        tasks.remove(0);
        tasks.add(new Todo("second"));
        storage.save(tasks);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[T][ ] second", loaded.get(0).toString());
    }

    private static void writeLines(Path file, String... lines) throws IOException {
        Files.createDirectories(file.getParent());
        Files.write(file, java.util.List.of(lines));
    }
}
