package seedu.mattchatbot.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import seedu.mattchatbot.MattChatBot;

/**
 * Controller for the chatbot's window: takes what the user types, asks the
 * chatbot about it, and adds both to the conversation.
 */
public class MainWindow {

    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    /** Scrolls the conversation when it grows past the window. */
    @FXML
    private ScrollPane scrollPane;

    /** Holds the dialog boxes, oldest first. */
    @FXML
    private VBox dialogContainer;

    /** Where the user types a command. */
    @FXML
    private TextField userInput;

    /** Sends whatever is in the text field. */
    @FXML
    private Button sendButton;

    /** The chatbot answering the user. */
    private MattChatBot chatBot;

    /** Creates the controller. FXMLLoader calls this itself when loading the window. */
    public MainWindow() {
    }

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives the window the chatbot to talk to, and shows its greeting.
     *
     * @param chatBot the chatbot answering the user
     */
    public void setChatBot(MattChatBot chatBot) {
        this.chatBot = chatBot;
        dialogContainer.getChildren().add(DialogBox.forChatBot(chatBot.getWelcomeMessage()));
    }

    /**
     * Shows the user's command and the chatbot's reply, then clears the text
     * field ready for the next command.
     * <p>
     * On a bye command the window closes after a short pause, so the farewell
     * is readable rather than vanishing with the window.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = chatBot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.forUser(input),
                DialogBox.forChatBot(response));
        userInput.clear();

        if (chatBot.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
