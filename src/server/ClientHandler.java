package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String  nickname;
    private String login;

    public ClientHandler(Server server, Socket socket) throws SocketTimeoutException {
        try{
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());


            new Thread(()-> {
                try {

                    socket.setSoTimeout(120000);

                    while (true) {
                        String str = in.readUTF();

                        if (str.equals("/end")) {
                            out.writeUTF("/end");
                            break;
                        }

                        if (str.startsWith("/auth")) {
                            String[] token = str.split("\\s");
                            if (token.length < 3) {
                                continue;
                            }
                            String newNick = server.getAuthService()
                                    .getNickNameByLoginAndPassword(token[1], token[2]);
                            login = token[1];
                            if (newNick != null) {
                                if (!server.isLoginAuthenticated(login)) {
                                    nickname = newNick;
                                    sendMsg("/authok " + nickname);
                                    server.subscribe(this);
                                    System.out.println("Клиент " + nickname + " подключен");
                                    socket.setSoTimeout(0);

                                    break;
                                } else {
                                    sendMsg("Данной учеткой уже пользуются");
                                }
                            } else {
                                sendMsg("Неверный логин или пароль");
                            }
                        }

                        if (str.startsWith("/reg ")) {
                            String[] token = str.split("\\s");
                            if (token.length < 4) {
                                continue;
                            }
                            boolean b = server.getAuthService()
                                    .registration(token[1], token[2], token[3]);
                            if (b) {
                                socket.setSoTimeout(0);
                                sendMsg("/regok");
                            } else {
                                sendMsg("/regno");
                            }
                        }
                    }

                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                out.writeUTF("/end");
                                break;
                        }
                            if (str.startsWith("/w")) {
                                String[] token = str.split("\\s+", 3);
                                if (token.length<3){
                                    continue;
                                }
                                server.privateMsg(this, token[1], token[2]);
                            }
                        }   else {
                            server.broadcastMsg(this, str);
                        }
                    }
                }   catch (SocketTimeoutException e) {
                    socket.isClosed();
                    sendMsg("Время вышло");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                    finally {
                    System.out.println("Клиент отключился");
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
