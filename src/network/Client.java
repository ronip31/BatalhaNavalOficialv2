package network;

import battlefield.BattleField;
import exception.ShipBoundException;
import exception.ShipExistException;
import game.Controller;
import game.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private static PrintWriter writer;
    private String newField;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 34001;
    private BattleField field;
    private Controller controller;
    private boolean connected;
    private View view;
    private Object server;
    private String playerName;

    public static void main(String[] args) throws ShipExistException, ShipBoundException {
        Client client = new Client();
        View view = new View(new Controller(), "");
        String playerName = view.askUserName();

        client.connectToServer();

        // Wait for the connection to be established
        while (!client.isConnected()) {
            try {
                Thread.sleep(1000); // Wait for 1 second before checking again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BattleField field = new BattleField();
        Controller controller = new Controller(field, client, view, playerName);
        controller.startGame();
    }

    // Connect to the server
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isReader);
            writer = new PrintWriter(socket.getOutputStream());
            writer.println("Connected");
            writer.flush();

            connected = true; // Mark as connected

            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            connected = false; // Mark as not connected
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendBoard(String message) {
        try {
            writer.println(message);
            writer.flush();
        } catch (Exception exception) {
            System.out.println("The message was not sent");
        }
    }

    public String getMessage() {
        return newField;
    }

    public void sendMessageToServer(int row, int column) {
        String message = row + "," + column; // Format the position as a string
        writer.println(message);
        writer.flush();
        System.out.println("sendMessageToServer: " + playerName + " " + message);

        // Chamar o método no Controller para processar a resposta do servidor
        sendBoard(message);
    }

    public void sendPositionToServer(int row, int column) {
        sendMessageToServer(row, column);
        System.out.println("sendPositionToServer : " + row + column);

    }

    public class IncomingReader implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    newField = message;
                    //System.out.println("Jogador " + newField + " conectado!");
                    System.out.println("Received message: " + message);

                }
            } catch (IOException ex) {
                System.out.println("No new field");
            } finally {
                try {
                    reader.close();
                    writer.close();
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }
}