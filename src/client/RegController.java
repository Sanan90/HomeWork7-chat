package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    private Controller controller;
    @FXML
    private TextArea regTextArea;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nickField;

    public void tryToReg(ActionEvent actionEvent) {

        controller.tryToReg(loginField.getText().trim(),
                passwordField.getText().trim(),
                nickField.getText().trim());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void addMsgToTextArea (String msg) {
        if (msg.startsWith(" =) ")) {
            regTextArea.appendText("Поздравляем!!! " + msg + "\n" +
                    "Ваш логин для входа " + loginField.getText() + "\n" +
                    "Ваш пароль для входа " + passwordField.getText() + "\n");
        }   else {   regTextArea.appendText(msg + "\n");

        }
    }
}
