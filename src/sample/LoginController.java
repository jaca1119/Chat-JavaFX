package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController
{

    @FXML
    private TextField serverIP;
    @FXML
    private TextField tfUserName;
    @FXML
    private Button connectToServer;
    ServerConnection serverConnection = ServerConnection.getInstance();


    public void changeScene(ActionEvent event) throws IOException
    {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        //scene2
        Parent rootChat = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene chatScene = new Scene(rootChat, 720, 720);
        primaryStage.setScene(chatScene);
    }

    public void connect()
    {
        String ip = serverIP.getText();

        serverConnection.setIp(ip);
        tfUserName.requestFocus();
    }

    public void inputUserName()
    {
        String userName = tfUserName.getText();

        if (!userName.equals(""))
        {
            serverConnection.setIp(serverIP.getText());
            serverConnection.setUserName(userName);
            connectToServer.fire();
        }
    }
}
