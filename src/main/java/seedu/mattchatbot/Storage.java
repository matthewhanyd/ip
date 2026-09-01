package seedu.mattchatbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import seedu.mattchatbot.task.Deadline;
import seedu.mattchatbot.task.Event;
import seedu.mattchatbot.task.Task;
import seedu.mattchatbot.task.TaskList;
import seedu.mattchatbot.task.Todo;

/**
 * Reads the task list from disk when the chatbot starts, and writes it back
 * whenever the list changes.
 */
public class Storage {

    /** Separates the fields of one task within a line of the save file. */
    private static final String SEPARATOR = " | ";

    /**
     * Where this Storage keeps the task list.
     * <p>
     * Passed in rather than fixed, so the caller decides where the data lives
     * and a test can point at a scratch file instead of the real one.
     */
    private final Path filePath;

    /** How many damaged lines the most recent {@link #load()} had to skip. */
    private int skippedLineCount = 0;

    /**
     * Creates a Storage reading and writing the given file.
     * <p>
     * Converted with {@link Paths#get(String, String...)}, which accepts "/"
     * as a separator on every platform and applies the one the current
     * operating system uses. Keep the path relative, so the chatbot works
     * wherever the project folder is copied to.
     *
     * @param filePath the save file's path, e.g. {@code data/mattchatbot.txt}
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Writes the whole task list to disk, replacing whatever was there before.
     * <p>
     * Rewriting the entire file on every change is more work than appending,
     * but it is the simplest thing that stays correct when a task is deleted
     * or its status changes, which appending alone cannot express.
     *
     * @param tasks the tasks to save
     * @throws MattChatBotException if the file cannot be written
     */
    public void save(TaskList tasks) throws MattChatBotException {
        StringBuilder contents = new StringBuilder();
        for (Task task : tasks.asList()) {
            contents.append(task.toFileFormat()).append(System.lineSeparator());
        }
        try {
            // The folder may not exist yet, e.g. on a fresh copy of the project.
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, contents.toString());
        } catch (IOException e) {
            throw new MattChatBotException(
                    "I couldn't save your tasks to " + filePath + ".");
        }
    }

    /**
     * Reads the saved task list back from disk.
     * <p>
     * A missing file is not an error: it simply means nothing has been saved
     * yet, which is what a first run on another computer looks like.
     *
     * @return the saved tasks, or an empty list if there is nothing saved
     * @throws MattChatBotException if the file exists but cannot be read
     */
    public ArrayList<Task> load() throws MattChatBotException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        skippedLineCount = 0;
        try {
            for (String line : Files.readAllLines(filePath)) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parse(line));
                } catch (MattChatBotException e) {
                    // One damaged line should not cost the user every other
                    // task, so skip it and carry on with the rest of the file.
                    skippedLineCount++;
                }
            }
        } catch (IOException e) {
            throw new MattChatBotException(
                    "I couldn't read your saved tasks from " + filePath + ".");
        }
        return tasks;
    }

    /**
     * Turns one line of the save file back into a task.
     *
     * @param line one line of the save file
     * @return the task it describes
     * @throws MattChatBotException if the line is not in the expected format
     */
    private static Task parse(String line) throws MattChatBotException {
        String[] fields = line.split("\\s*\\|\\s*");
        String type = fields.length > 0 ? fields[0] : "";
        // Each type has a fixed field count, so a line with the wrong number
        // of fields is damaged and is rejected before any field is read.
        int expectedFields = switch (type) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new MattChatBotException("Unknown task type: " + type);
        };
        if (fields.length != expectedFields) {
            throw new MattChatBotException("Expected " + expectedFields
                    + " fields but found " + fields.length);
        }
        if (fields[2].isBlank()) {
            throw new MattChatBotException("Task has no description");
        }
        Task task = switch (type) {
            case "T" -> new Todo(fields[2]);
            case "D" -> new Deadline(fields[2], DateTimes.parse(fields[3]));
            case "E" -> new Event(fields[2], DateTimes.parse(fields[3]),
                    DateTimes.parse(fields[4]));
            default -> throw new MattChatBotException("Unknown task type: " + type);
        };
        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new MattChatBotException("Status must be 0 or 1");
        }
        return task;
    }

    /**
     * Returns how many damaged lines the most recent {@link #load()} skipped,
     * so the chatbot can tell the user that some saved tasks were lost.
     *
     * @return the number of lines skipped, zero if the file was intact
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }
}
