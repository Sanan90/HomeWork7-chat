package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField logindField;
    @FXML
    public TextField passwordField;
    @FXML
    public HBox mshPanel;

    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8189;

    private Socket socket;
    DataInputStream in;
    DataOutputStream out;

    private boolean authenticated;
    private String  nickname;
    private final String TITLE = "";

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        mshPanel.setVisible(authenticated);
        mshPanel.setManaged(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);

        if (!authenticated) {
            nickname = "";
        }
        setTitle(nickname);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/authok")) {
                                nickname = str.split(" ", 2) [1];
                                setAuthenticated(true);
                                break;
                            }

                            LocalDateTime time = LocalDateTime.now();
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
                            String afterFormat = time.format(format);

                            textArea.appendText(afterFormat + ": " + str + "\n");
                        }



                        while (true) {
                            String str = in.readUTF();

                            if (str.equals("/end")) {
                                break;
                            }

                            if (str.startsWith("/w")){
                                String[] personal = str.split("\\s", 3);


                            }

                            LocalDateTime time = LocalDateTime.now();
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
                            String afterFormat = time.format(format);

                            textArea.appendText(afterFormat +  ": " + str + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        setAuthenticated(false);
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToLogin(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(String.format("/auth %s %s",
                    logindField.getText().trim().toLowerCase(),
                    passwordField.getText().trim()));
            passwordField.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nick) {
        Platform.runLater(()-> {
            ((Stage) textField.getScene().getWindow()).setTitle(TITLE + nick);
        });
    }
}
