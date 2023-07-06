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
            // Inicializa o servidor de socket na porta especificada
            this.serverSocket = new ServerSocket(PORT);
            // Inicializa o campo de batalha
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
                // Aceita uma conexão de cliente
                client = serverSocket.accept();
                // Adiciona o cliente à lista de clientes conectados
                clients.add(client);
                // Cria um PrintWriter para enviar dados ao cliente
                PrintWriter writer = new PrintWriter(client.getOutputStream());
                // Adiciona o PrintWriter à lista de fluxos de saída
                outputStreams.add(writer);
                // Cria uma thread para lidar com as mensagens do cliente
                Thread clientThread = new Thread(new ClientHandler(client, count));
                clientThread.start();
                count++;
                System.out.println("got a connection, player: " + count);
            }
        } catch (Exception e) {
            System.out.println("Failed to make connection");
        } finally {
            try {
                // Fecha o socket do servidor
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
                // Cria um InputStreamReader para ler os dados recebidos do cliente
                InputStreamReader isReader = new InputStreamReader(this.clientSocket.getInputStream());
                // Cria um BufferedReader para facilitar a leitura dos dados
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
                // Lê as mensagens enviadas pelo cliente
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    // Envia a mensagem para todos os usuários conectados, exceto o próprio remetente
                    sendBothUsers(message);
                }
            } catch (IOException e) {
                System.out.println("Error while reading message from client: " + e.getMessage());
            } finally {
                try {
                    // Fecha o BufferedReader, o socket do cliente e remove o cliente da lista
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
                        // Envia a mensagem para o fluxo de saída correspondente ao outro cliente
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
        // Remove o cliente da lista de clientes conectados
        clients.remove(clientIndex);
    }

    public static void main(String[] args) {
        // Cria uma nova instância de Server e inicia a execução em uma thread separada
        Thread server = new Thread(new Server());
        server.start();
    }

    private void sendToUser(Socket user, String message) {
        try {
            // Envia uma mensagem para um usuário específico através do seu socket
            PrintWriter writer = new PrintWriter(user.getOutputStream());
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error while sending message to user: " + e.getMessage());
        }
    }
}
