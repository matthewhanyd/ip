import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Reads the task list from disk when the chatbot starts, and writes it back
 * whenever the list changes.
 */
public class Storage {

    /**
     * Where the task list is kept, relative to the project root.
     * <p>
     * Built with {@link Paths#get(String, String...)} from separate name parts
     * rather than written as "data/mattchatbot.txt", so that Java inserts the
     * separator the current operating system expects. The path is relative, so
     * the chatbot works wherever the project folder is copied to.
     */
    private static final Path FILE_PATH = Paths.get("data", "mattchatbot.txt");

    /** Separates the fields of one task within a line of the save file. */
    private static final String SEPARATOR = " | ";

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
    public static void save(ArrayList<Task> tasks) throws MattChatBotException {
        StringBuilder contents = new StringBuilder();
        for (Task task : tasks) {
            contents.append(task.toFileFormat()).append(System.lineSeparator());
        }
        try {
            // The folder may not exist yet, e.g. on a fresh copy of the project.
            Files.createDirectories(FILE_PATH.getParent());
            Files.writeString(FILE_PATH, contents.toString());
        } catch (IOException e) {
            throw new MattChatBotException(
                    "I couldn't save your tasks to " + FILE_PATH + ".");
        }
    }
}
