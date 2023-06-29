package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private int clientIndex;
    private final int PORT = 34001;
    private ServerSocket serverSocket;
    private List<Socket> clients;
    private List<PrintWriter> outputStreams;
    private static final String PLAYER1_MESSAGE = "Você é o jogador 1.";
    private static final String PLAYER2_MESSAGE = "Você é o jogador 2.";

    public Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        clients = new ArrayList<>();
        outputStreams = new CopyOnWriteArrayList<>();
        int connectedClients = 0;

        try {
            while (true) {
                Socket client = serverSocket.accept();
                clients.add(client);

                PrintWriter writer = new PrintWriter(client.getOutputStream());
                outputStreams.add(writer);

                Thread clientThread = new Thread(new ClientHandler(client, connectedClients));
                clientThread.start();
                connectedClients++;
                System.out.println("Got a connection");

                if (connectedClients == 1) {
                    sendToUser(client, PLAYER1_MESSAGE);
                    System.out.println("Player 1 connected");
                } else if (connectedClients == 2) {
                    sendToUser(client, PLAYER2_MESSAGE);
                    System.out.println("Player 2 connected");
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to make connection: " + e.getMessage());
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
                    sendToOtherClients(message, clientIndex);
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

        private void sendToOtherClients(String message, int senderIndex) {
            for (int i = 0; i < clients.size(); i++) {
                if (i != senderIndex) {
                    try {
                        PrintWriter writer = new PrintWriter(clients.get(i).getOutputStream());
                        writer.println(message);
                        writer.flush();
                    } catch (IOException e) {
                         System.out.println("Error while sending message to client: " + e.getMessage());
                    }
                }
            }
        }

        private void removeClient(int clientIndex) {
            clients.remove(clientIndex);
        }
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

