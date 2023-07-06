package game;

import battlefield.*;
import exception.*;
import network.*;

import java.awt.event.KeyEvent;

public class GameRules {
    private String playerName;
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

    public GameRules() throws ShipExistException, ShipBoundException {
        this.battlefield = new BattleField();
        // this.view = new View(this, view.askUserName());
    }

    // Construtor com parâmetros
    public GameRules(BattleField board, Client client, View view, String playerName) throws ShipExistException, ShipBoundException {
        this.battlefield = new BattleField();
        this.playerName = playerName;
        initGame(board, client, playerName);
    }
    // Inicializa o jogo
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
    // Inicia o jogo
    public void startGame() {
        view.setSize(WIDTH, HEIGHT);
        view.setVisible(true);
        instructionMessage();
        allShipsPlaced(); // Chama o método allShipsPlaced() em vez de instructionMessage()
    }
    // Lida com o clique nas teclas
    public void keyClicked(int key) throws ShipExistException, ShipBoundException {
        if (key == KeyEvent.VK_Q) {
            quitGameMessage();
        } else if (key == KeyEvent.VK_R) {
            restartGameMessage();
        }
    }
    // Lida com o clique no tabuleiro do jogador
    public void userBoardClicked(int row, int column, int direction) {
        try {
            if (shipCount >= 4) throw new AllPlacedException();
            Ship ship = ships[shipCount++];
            userBoard.put(row - 1, column - 1, direction, ship);
            view.placeShipUserView(row, column, direction, ship);
            //userBoard.printField(); // For debugging
            if (shipCount == 4) allShipsPlaced();
        } catch (ShipExistException exception) {
            existMessage(); // Exibe a mensagem de erro de embarcação já existente
        } catch (ShipBoundException exception) {
            boundMessage(); // Exibe a mensagem de erro de embarcação fora dos limites
        } catch (AllPlacedException exception) {
            placedMessage(); // Exibe a mensagem de todas as embarcações colocadas
        }
    }
    // Lida com o clique no tabuleiro do oponente
    public void opponentBoardClicked(int row, int column) {
        try {
            client.sendBoard(boardToString(opponentBoard.getBoard()) + "Movee");
            opponentBoard.shoot(--row, --column);

            view.updateOpponentView(opponentBoard.getBoard());
            //  view.updateUserView(opponentBoard.getBoard());
        } catch (ShotException exception) {
            shotMessage();
        }
    }

    // Lida com todas as embarcações colocadas
    private void allShipsPlaced() {
        createShips(); // Exibe a mensagem da próxima embarcação antes de iniciar o jogo
        Thread incomingMove = new Thread(new IncomingMove());
        incomingMove.start();
        //view.disableUserView();
        client.sendBoard(boardToString(userBoard.getBoard()) + "First");
    }

    // Converte o tabuleiro em uma representação de string
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
    // Converte uma mensagem de string em um tabuleiro
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
    // Verifica se a mensagem é a primeira mensagem
    private boolean checkFirstMessage(String message) {
        return message.endsWith("First");
    }
    // Verifica se a mensagem é uma mensagem de movimento
    private boolean checkMoveMessage(String message) {
        return message.endsWith("Movee");
    }

    // Traduz a mensagem
    private String translateMessage(String message) {
        return message.substring(0, message.length() - 5);
    }
    // Classe interna para lidar com o movimento recebido
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
    // Exibe a mensagem de confirmação de saída do jogo
    private void quitGameMessage() {
        String message = "Are you sure to quit game?";
        String title = "Quit";
        int selected = view.sendQuestionMessage(message, title);
        if (selected == 0)
            System.exit(0);
    }
    // Exibe a mensagem de confirmação de reinício do jogo
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
    // Exibe a mensagem de erro de embarcação já existente
    private void existMessage() {
        shipCount--;
        String message = "There is a ship already. \nPlease choose another place!";
        String title = "Warning";
        view.sendMessage(message, title);
    }
    // Exibe a mensagem de erro de embarcação fora dos limites
    private void boundMessage() {
        shipCount--;
        String message = "Ship don't fit bounds. \nPlease choose another place!";
        String title = "Warning";
        view.sendMessage(message, title);
    }
    // Exibe a mensagem de todas as embarcações colocadas
    private void placedMessage() {
        String message = "You placed all your ships. \nWait for your opponent!";
        String title = "All Placed";
        view.sendMessage(message, title);
    }
    // Exibe a mensagem de instrução
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
    // Exibe a mensagem de tiro incorreto
    private void shotMessage() {
        String message = "Você já atirou nessa posição antes. \nPor favor, escolha outro lugar!";
        String title = "Tiro Incorreto";
        view.sendMessage(message, title);
    }
    // Obtém a largura da tela
    public int getWidth() {
        return WIDTH;
    }
    // Obtém a altura da tela
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
    // Verifica se há uma embarcação no tabuleiro do oponente na posição dada
    private boolean checkOpponentBoard(int row, int column) {
        int cell = opponentBoard.getCell(row, column);
        return cell == BattleField.SHIP;
    }
    // Cria as embarcações
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