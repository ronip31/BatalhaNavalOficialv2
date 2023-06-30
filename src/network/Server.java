package network;

import battlefield.BattleField;
import exception.ShotException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {
    private BattleField battleField;
    private final int PORT = 34001;
    private ServerSocket serverSocket;
    private List<Socket> clients;
    private List<PrintWriter> outputStreams;

    private Socket client;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            battleField = new BattleField();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        clients = new ArrayList<>();
        outputStreams = new CopyOnWriteArrayList<>();
        int count = 0;

        try {
            while (true) {
                client = serverSocket.accept();

                clients.add(client);

                PrintWriter writer = new PrintWriter(client.getOutputStream());
                outputStreams.add(writer);

                Thread clientThread = new Thread(new ClientHandler(client, count));
                clientThread.start();
                count++;
                System.out.println("got a connection, player: " + count);
            }
        } catch (Exception e) {
            System.out.println("Failed to make connection");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Failed to close server socket: " + e.getMessage());
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private int clientIndex;

        private ClientHandler(Socket clientSocket, int clientIndex) {
            this.clientSocket = clientSocket;
            this.clientIndex = clientIndex;
            try {
                InputStreamReader isReader = new InputStreamReader(this.clientSocket.getInputStream());
                reader = new BufferedReader(isReader);
                System.out.println("BufferedReader: " + isReader);
            } catch (IOException e) {
                System.out.println("Error while setting up client connection: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    sendBothUsers(message);
                }
            } catch (IOException e) {
                System.out.println("Error while reading message from client: " + e.getMessage());
            } finally {
                try {
                    reader.close();
                    clientSocket.close();
                    removeClient(clientIndex);
                } catch (IOException e) {
                    System.out.println("Failed to close client connection: " + e.getMessage());
                }
            }
        }

        private void sendBothUsers(String message) {
            for (int i = 0; i < outputStreams.size(); i++) {
                if (!clients.get(i).equals(clientSocket)) {
                    PrintWriter stream = outputStreams.get(i);
                    try {
                        stream.println(message);
                        System.out.println("Sending message: " + message);
                        stream.flush();
                    } catch (Exception e) {
                        System.out.println("Cannot send move from server");
                    }
                }
            }
        }
    }

    private void removeClient(int clientIndex) {
        clients.remove(clientIndex);
    }

    public static void main(String[] args) {
        Thread server = new Thread(new Server());
        server.start();
    }

    private void sendToUser(Socket user, String message) {
        try {
            PrintWriter writer = new PrintWriter(user.getOutputStream());
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error while sending message to user: " + e.getMessage());
        }
    }
}
