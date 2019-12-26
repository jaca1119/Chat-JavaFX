package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sample.client.Client;
import sample.client.ClientGuiModel;


import java.io.IOException;
import java.util.Objects;
import java.util.Set;


public class Controller extends Client
{
    private static final int PORT = 1025;
    private ClientGuiModel model = new ClientGuiModel();
    private SocketThread socketThread;

    @FXML
    private TextArea messages;
    @FXML
    private TextArea users;
    @FXML
    private TextArea messageFromUser;
    @FXML
    private Button sendMessage;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Button btnArrow;

    public Controller()
    {
        System.out.println("Controller constructor");
        run();
    }

    @FXML
    public void initialize()
    {
        checkBox.setDisable(true);
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

    public void backToLogin(ActionEvent event) throws IOException
    {
        stop();
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Parent rootChat = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(rootChat, 720, 720);


        primaryStage.setScene(loginScene);
    }

    public void stop()
    {
        shouldStop(true);
        try
        {
            connection.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected SocketThread getSocketThread()
    {
        SocketThread thread = new GuiSocketThread();
        thread.setName("Chat thread");
        return thread;
    }

    @Override
    public void run()
    {
        socketThread = getSocketThread();
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

        private void refreshMessages()
        {
            messages.appendText(model.getNewMessage() + '\n');
        }

        private void refreshUsers()
        {
            StringBuilder builder = new StringBuilder();
            model.getAllUserNames().forEach(userName ->
            {
                builder.append(userName).append('\n');
            });

            Platform.runLater(() -> {
                users.setText(builder.toString());
            });
        }

        private void connectionStatusChanged(boolean clientConnected)
        {
//        messageFromUser.setEditable(clientConnected);
            if (clientConnected)
            {
                Platform.runLater(() ->{
                    checkBox.setSelected(clientConnected);
                    checkBox.setText("Connected to server");
                });

            } else
            {
                Platform.runLater(() ->{
                    checkBox.setIndeterminate(true);
                    checkBox.setText("Not connected to server");
                });

            }
        }
    }
}
