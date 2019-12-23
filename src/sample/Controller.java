package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.client.ClientGuiModel;

import java.util.Objects;


public class Controller
{
    private ClientGuiModel model = new ClientGuiModel();

    @FXML
    private TextArea messages;
    @FXML
    private TextArea users;
    @FXML
    private TextArea messageFromUser;

    public void sendMessage(KeyEvent keyEvent)
    {

        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            String textFromUser = messageFromUser.getText();
            if (Objects.equals(textFromUser.trim(), ""))
            {
                //nothing to send
            } else
            {
                model.setNewMessage(textFromUser);
                messages.appendText(textFromUser.trim() + '\n');
                messageFromUser.setText("");
            }
        }
    }

    public ClientGuiModel getModel()
    {
        return model;
    }

}
