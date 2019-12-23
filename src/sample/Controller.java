package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class Controller
{
    @FXML
    private Label labelText;

    public void addText()
    {
        System.out.println("Button clicked");
        labelText.setText("Button clicked " + Math.random());
    }

}
