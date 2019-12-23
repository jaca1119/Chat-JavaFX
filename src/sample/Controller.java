package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Objects;


public class Controller
{
    @FXML
    private Label labelText;
    @FXML
    private TextField textField;


    public void addText()
    {
        System.out.println("Button clicked");
        labelText.setText("Button clicked " + Math.random());
    }

    public void setText()
    {
        String textFieldText = textField.getText();

        if (Objects.equals(textFieldText, ""))
        {
            labelText.setText("Text field is empty!");
        }
        else
        {
            labelText.setText(textFieldText);
            textField.setText("");
        }
    }
}
