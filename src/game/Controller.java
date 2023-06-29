package game;

import battlefield.*;
import exception.*;
import network.*;

import java.awt.event.KeyEvent;

import static battlefield.BattleField.*;

public class Controller {
    private BattleField userBoard;
    private BattleField opponentBoard;
    private View view;
    private Client client;
    private final int WIDTH = 1100;
    private final int HEIGHT = 650;
    private Ship[] ships = {new Destroyer(), new Submarine(), new BattleShip(), new Carrier()};
    private int currentShipIndex = 0;
    private int shipCount;
    private BattleField battlefield;

    public Controller() throws ShipExistException, ShipBoundException {
        this.battlefield = new BattleField();
       // this.view = new View(this, view.askUserName());
    }


    public Controller(BattleField board, Client client, View view, String playerName) throws ShipExistException, ShipBoundException {
        initGame(board, client, playerName);
    }

    private void initGame(BattleField board, Client client, String playerName) throws ShipExistException, ShipBoundException {
        this.userBoard = board;
        this.opponentBoard = new BattleField();
        this.view = new View(this, playerName);
        this.client = client;
       // this.view = view;
        //this.view.askUserName(); // Set the player name in the view instance
        this.shipCount = 0;
        Thread tt = new Thread(String.valueOf(client));
        tt.start();
    }

    public void startGame() {
        view.setSize(WIDTH, HEIGHT);
        view.setVisible(true);
        instructionMessage();
        allShipsPlaced(); // Chama o método allShipsPlaced() em vez de instructionMessage()
    }

    public void keyClicked(int key) throws ShipExistException, ShipBoundException {
        if (key == KeyEvent.VK_Q) {
            quitGameMessage();
        } else if (key == KeyEvent.VK_R) {
            restartGameMessage();
        }
    }

    public void userBoardClicked(int row, int column, int direction) {
        try {
            if (shipCount >= 4) throw new AllPlacedException();
            Ship ship = ships[shipCount++];
            userBoard.put(row - 1, column - 1, direction, ship);
            view.placeShipUserView(row, column, direction, ship);
            //userBoard.printField(); // For debugging
            if (shipCount == 4) allShipsPlaced();
        } catch (ShipExistException exception) {
            existMessage();
        } catch (ShipBoundException exception) {
            boundMessage();
        } catch (AllPlacedException exception) {
            placedMessage();
        }
    }

    public void opponentBoardClicked(int row, int column) {
        try {
            opponentBoard.shoot(--row, --column);
            client.sendBoard(boardToString(opponentBoard.getBoard()) + "Movee");
            view.updateOpponentView(opponentBoard.getBoard());
            opponentBoard.printField(); // Para depuração

            // Verifique se a posição escolhida tem uma embarcação do oponente
            boolean hasShip = checkOpponentShip(row, column);

            // Apresente o resultado em tela (pode ser personalizado de acordo com sua interface)
            if (hasShip) {
                view.showMessage("HIT!", "Result");
            } else {
                view.showMessage("MISS!", "Result");
            }
        } catch (ShotException exception) {
            shotMessage();
        }
    }
    private boolean checkOpponentShip(int row, int column) {
        // Implemente a lógica para verificar se a posição (row, column) tem uma embarcação do oponente
        // Use a estrutura de dados opponentBoard para fazer a verificação
        return hasShip(row, column);
    }
    public boolean hasShip(int x, int y) {
        int cell = battlefield.getCell(x, y);
        return cell != EMPTY && cell != MISS && cell != HIT;
    }

    private void allShipsPlaced() {
        createShips(); // Exibe a mensagem da próxima embarcação antes de iniciar o jogo
        Thread incomingMove = new Thread(new IncomingMove());
        incomingMove.start();
        //view.disableUserView();
        client.sendBoard(boardToString(userBoard.getBoard()) + "First");
    }


    private String boardToString(int[][] field) {
        StringBuilder builder = new StringBuilder();
        for (int[] row : field) {
            for (int cell : row) {
                builder.append(cell).append(":");
            }
            builder.append("/");
        }
        return builder.toString();
    }

    private int[][] stringToBoard(String message) {
        String[] rows = message.split("/");
        String[][] cells = new String[rows.length][10];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = rows[i].split(":");
        }
        int[][] field = new int[10][10];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                field[i][j] = Integer.valueOf(cells[i][j]);
            }
        }
        return field;
    }

    private boolean checkFirstMessage(String message) {
        return message.endsWith("First");
    }

    private boolean checkMoveMessage(String message) {
        return message.endsWith("Movee");
    }


    private String translateMessage(String message) {
        return message.substring(0, message.length() - 5);
    }

    public class IncomingMove implements Runnable {
        private boolean isFirstTime = true;

        @Override
        public void run() {
            try {
                String message;
                while ((message = client.getMessage()) != null) {
                    if (checkFirstMessage(message) && isFirstTime) {
                        opponentBoard.setBoard(stringToBoard(translateMessage(message)));
//                        System.out.println("Opp Board"); // For debugging
//                        opponentBoard.printField(); // For debugging
                        view.updateOpponentView(opponentBoard.getBoard());
                        isFirstTime = false;
                    } else if (checkMoveMessage(message)) {
                        userBoard.setBoard(stringToBoard(translateMessage(message)));
                        view.updateUserView(userBoard.getBoard());
//                        System.out.println("User Board"); // For debugging
//                        userBoard.printField(); // For debugging
                    }
                }
            } catch (Exception e) {
                System.out.println("no new field");
            }
        }
    }

    private void quitGameMessage() {
        String message = "Are you sure to quit game?";
        String title = "Quit";
        int selected = view.sendQuestionMessage(message, title);
        if (selected == 0)
            System.exit(0);
    }

    private void restartGameMessage() throws ShipExistException, ShipBoundException {
        String message = "Are you sure to restart game?";
        String title = "Restart";
        int selected = view.sendQuestionMessage(message, title);
        if (selected == 0) {
            view.dispose();
       //     initGame(new BattleField(), new Client());
            startGame();
        }
    }

    private void existMessage() {
        shipCount--;
        String message = "There is a ship already. \nPlease choose another place!";
        String title = "Warning";
        view.sendMessage(message, title);
    }

    private void boundMessage() {
        shipCount--;
        String message = "Ship don't fit bounds. \nPlease choose another place!";
        String title = "Warning";
        view.sendMessage(message, title);
    }

    private void placedMessage() {
        String message = "You placed all your ships. \nWait for your opponent!";
        String title = "All Placed";
        view.sendMessage(message, title);
    }

    private void instructionMessage() {
        String message = " ***   During all game play   ***\n"
                + " * Q - Quit Game\n"
                + " * R - Restart Game\n\n"
                + " * Mouse Click:\n"
                + " * Left Click ----> Place Horizontally\n"
                + " * Right Click ---> Place Vertically\n"
                + " * Middle Click -> Shoot";
        String title = "Welcome BattleShip";
        view.sendInfoMessage(message, title);
    }

    private void shotMessage() {
        String message = "You shot there before. \nPlease try another place!";
        String title = "Wrong Shoot";
        view.sendMessage(message, title);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public void placeShip(int row, int column, int direction, Ship ship) {
        try {
            battlefield.put(row, column, direction, ship);
            view.placeShipUserView(row, column, direction, ship);
        } catch (ShipBoundException e) {
            view.sendMessage("The ship cannot be placed there. It goes beyond the boundaries of the battlefield.", "Placement Error");
        } catch (ShipExistException e) {
            view.sendMessage("There is already a ship in that position.", "Placement Error");
        }
    }


    public void createShips() {
        if (currentShipIndex < ships.length) {
            Ship nextShip = ships[currentShipIndex];
            Ship nextType = ships[currentShipIndex];
            currentShipIndex++;
            String nextShipName = nextShip.getNextShipName();
            int nextgetType = nextType.getType();
            String title = "Insert the vessel!";

            String message = nextShipName;
            String messages = " contains: " + nextgetType + " cells!";
            view.sendVessel(title, message, messages);
        } else {
            String title = "OPA!";
            String message = "All vessels have been created. \n";
            view.sendInfoMessage(message, title);
        }
    }
}