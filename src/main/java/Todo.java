/**
 * A task with no date or time attached, e.g. {@code visit new theme park}.
 */
public class Todo extends Task {

    /**
     * Creates a todo.
     *
     * @param description what the user wants to do
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}
