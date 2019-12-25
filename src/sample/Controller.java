package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.client.Client;
import sample.client.ClientGuiModel;


import java.util.Objects;


public class Controller extends Client
{
    private static final int PORT = 1025;
    private ClientGuiModel model = new ClientGuiModel();

    @FXML
    private TextArea messages;
    @FXML
    private TextArea users;
    @FXML
    private TextArea messageFromUser;
    @FXML
    private Button sendMessage;

    public Controller()
    {
        run();
    }

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
                sendTextMessage(textFromUser.trim());

                messageFromUser.setText("");
            }
        }
    }

    public void buttonSendMessage()
    {
        String textFromUser = messageFromUser.getText().trim();
        if (Objects.equals(textFromUser, ""))
        {
            //nothing to send
        } else
        {
            sendTextMessage(textFromUser);

            messageFromUser.setText("");
        }
    }

    @Override
    protected SocketThread getSocketThread()
    {
        return new GuiSocketThread();
    }

    @Override
    public void run()
    {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
    }

    @Override
    protected String getServerAddress()
    {
        return ServerConnection.getInstance().getIp();
    }

    @Override
    protected int getServerPort()
    {
        return PORT;
    }

    @Override
    protected String getUserName()
    {
        return ServerConnection.getInstance().getUserName();
    }

    public class GuiSocketThread extends SocketThread
    {
        @Override
        protected void processIncomingMessage(String message)
        {
            model.setNewMessage(message);
            refreshMessages();
        }

        @Override
        protected void informAboutAddingNewUser(String userName)
        {
            model.addUser(userName);
            refreshUsers();
        }

        @Override
        protected void informAboutDeletingNewUser(String userName)
        {
            model.deleteUser(userName);
            refreshUsers();
        }

        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected)
        {
            connectionStatusChanged(clientConnected);
        }
    }

    private void refreshMessages()
    {
        messages.appendText(model.getNewMessage() + '\n');
    }

    private void refreshUsers()
    {
        StringBuilder builder = new StringBuilder();
        model.getAllUserNames().forEach(userName -> {
            builder.append(userName).append('\n');
        });

        users.setText(builder.toString());
    }

    private void connectionStatusChanged(boolean clientConnected)
    {
//        messageFromUser.setEditable(clientConnected);
    }
}
