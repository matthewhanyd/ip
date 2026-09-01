package seedu.mattchatbot.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * One message in the conversation: a bubble of text next to a round avatar.
 * <p>
 * Loads its own FXML with itself as both root and controller, so that a dialog
 * box can be created with {@code new} like any other control, rather than the
 * window having to know how it is put together.
 */
public class DialogBox extends HBox {

    /** The text of the message. */
    @FXML
    private Label dialog;

    /** The initials standing in for a speaker's picture. */
    @FXML
    private Label avatar;

    /**
     * Builds one dialog box.
     *
     * @param text     the message to show
     * @param initials the short label shown in the avatar circle
     */
    private DialogBox(String text, String initials) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load a dialog box.", e);
        }
        dialog.setText(text);
        avatar.setText(initials);
    }

    /**
     * Returns a box for something the user said, avatar on the right.
     *
     * @param text what the user typed
     * @return the dialog box to add to the conversation
     */
    public static DialogBox forUser(String text) {
        DialogBox box = new DialogBox(text, "You");
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /**
     * Returns a box for something the chatbot said, avatar on the left.
     *
     * @param text the chatbot's reply
     * @return the dialog box to add to the conversation
     */
    public static DialogBox forChatBot(String text) {
        DialogBox box = new DialogBox(text, "MB");
        box.getStyleClass().add("bot-dialog");
        box.flip();
        return box;
    }

    /**
     * Mirrors the box so the avatar sits on the left instead of the right,
     * which is what separates the chatbot's side of the conversation from the
     * user's at a glance.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
