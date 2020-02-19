package sample.Controller;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Message.Message;
import sample.Message.MessageType;
import sample.ServerConnection;
import sample.client.Client;
import sample.client.ClientGuiModel;


import java.io.*;
import java.util.Objects;


public class Controller extends Client
{
    private static final int PORT = 1025;
    private ClientGuiModel model = new ClientGuiModel();
    private SocketThread socketThread;

    @FXML
    private TextFlow messagesFlow;
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
    @FXML
    private Button btnSendImage;

    public Controller()
    {
        System.out.println("Controller constructor");
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

    public void sendImage()
    {
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(btnSendImage.getScene().getWindow());

        System.out.println(file);
        if (file != null)
        {
            byte[] imageInBytes = new byte[(int) file.length()];

            try (FileInputStream inputStream = new FileInputStream(file);
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream))
            {

                int length = bufferedInputStream.read(imageInBytes, 0, imageInBytes.length);
                System.out.println("Image size in buff " + length);
                System.out.println("Byte array size " + imageInBytes.length);
                System.out.println("File length " + file.length());

                connection.send(new Message(MessageType.IMAGE, imageInBytes));

                Message message = new Message(MessageType.IMAGE, imageInBytes);

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                objectOutputStream.close();

                System.out.println("Bos size: " + byteOutputStream.size());
                System.out.println("Bos to bytearray len: " + byteOutputStream.toByteArray().length);

            } catch (IOException e)
            {
                shouldStop(true);

                e.printStackTrace();
            }
        }
    }

    public void backToLogin(ActionEvent event) throws IOException
    {
        stop();
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Parent rootChat = FXMLLoader.load(getClass().getResource("../View/login.fxml"));
        Scene loginScene = new Scene(rootChat, 720, 720);


        primaryStage.setScene(loginScene);
    }

    public void stop()
    {
        shouldStop(true);
        try
        {
            if (connection != null)
            {
                connection.close();
            }
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
        protected void processIncomingMessage(byte[] image)
        {
            model.setNewImage(image);
            refreshImage();
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
            Platform.runLater(() -> messagesFlow.getChildren().add(new Text(model.getNewMessage() + "\n")));
        }

        private void refreshImage()
        {
            Image processedImage = processToOutputImage();
            ImageView imageView = new ImageView(processedImage);

            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            Platform.runLater(() -> messagesFlow.getChildren().addAll(new Text(getUserName() + ":\n"), imageView, new Text("\n")));
        }

        private Image processToOutputImage()
        {
            System.out.println("Refresh image: " + model.getNewImage().length);
            File file;
            FileOutputStream fileOutputStream = null;
            BufferedOutputStream outputStream = null;
            Image image = null;

            try
            {
                file = File.createTempFile("img", null);
                fileOutputStream = new FileOutputStream(file);
                outputStream = new BufferedOutputStream(fileOutputStream);

                byte[] img = model.getNewImage();

                outputStream.write(img, 0, img.length);

                outputStream.flush();
                fileOutputStream.close();
                outputStream.close();

                System.out.println(file.toString());
                image = new Image(new FileInputStream(file));

            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return image;
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
