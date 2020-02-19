package sample.Controller;

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
import sample.ServerConnection;

import java.io.IOException;
import java.util.Set;

public class LoginController
{

    @FXML
    private TextField serverIP;
    @FXML
    private TextField tfUserName;
    @FXML
    private Button connectToServer;

    private ServerConnection serverConnection = ServerConnection.getInstance();

    public void connect()
    {
        String ip = serverIP.getText();

        serverConnection.setIp(ip);
        tfUserName.requestFocus();
    }

    public void btnConnect(ActionEvent event) throws IOException
    {
        String ip = serverIP.getText();
        String userName = tfUserName.getText();

        if (!ip.equals("") && !userName.equals(""))
        {
            serverConnection.setIp(serverIP.getText());
            serverConnection.setUserName(tfUserName.getText());

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            //scene2
            Parent rootChat = FXMLLoader.load(getClass().getResource("../View/sample.fxml"));
            Scene chatScene = new Scene(rootChat, 720, 720);
            chatScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            primaryStage.setScene(chatScene);
        }
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
