package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    List<ClientHandler> clients;

    private AuthService authService;

    private int PORT = 8189;
    ServerSocket server = null;
    Socket socket = null;

    public Server(){
        clients = new Vector<>();
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");

                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void broadcastMsg(ClientHandler sender, String msg) {

        if (msg.startsWith("/w")) {
            String[] personal = msg.split("\\s", 3);
            String nick = personal[1];
            String msg2 = personal[2];
            String message = String.format("%s : %s", sender.getNickname(), msg2);
            for (ClientHandler client : clients) {
                if (client.getNickname().equals(nick) || client.getNickname() == sender.getNickname()) {
                    client.sendMsg(message);
                }
            }
        } else {
            String message = String.format("%s : %s", sender.getNickname(), msg);
            for (ClientHandler client : clients) {
                client.sendMsg(message);
            }
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

}
