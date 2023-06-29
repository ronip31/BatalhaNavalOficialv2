package game;

import battlefield.*;
import exception.ShipBoundException;
import exception.ShipExistException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.SwingUtilities.*;

public class View extends JFrame {
    private final int WIDTH = 11;
    private final int HEIGHT = 11;
    private int FRAME_WIDTH;
    private int FRAME_HEIGHT;
    private JButton[][] userButtons;
    private JButton[][] opponentButtons;
    private Controller controller;

    public View(final Controller controller, String playerName) throws ShipExistException, ShipBoundException {
        this.userButtons = new JButton[WIDTH][HEIGHT];
        this.opponentButtons = new JButton[WIDTH][HEIGHT];
       // createView(askUserName());
        this.controller = controller;
        this.FRAME_WIDTH = controller.getWidth();
        this.FRAME_HEIGHT = controller.getHeight();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createView(playerName);
    }

    private void createView(String playerName) throws ShipExistException, ShipBoundException {
        setLayout(new GridLayout(1, 1));
        createUserView(playerName);;
        createOpponentView();
    }

    private void createUserView(String playerName) throws ShipExistException, ShipBoundException {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());
        add(userPanel);

        JPanel userGamePanel = new JPanel();
        userGamePanel.setSize(FRAME_WIDTH / 2, FRAME_WIDTH / 2);
        userPanel.add(userGamePanel);

        createPlayerNamePanel(userPanel, askUserName());
        System.out.println(askUserName());
        createUserGameField(userGamePanel);
    }

    private void createOpponentView() {
        JPanel opponentPanel = new JPanel();
        opponentPanel.setLayout(new BorderLayout());
        add(opponentPanel);

        JPanel opponentGamePanel = new JPanel();
        opponentGamePanel.setSize(FRAME_WIDTH / 2, FRAME_WIDTH / 2);
        opponentPanel.add(opponentGamePanel);

        createPlayerNamePanel(opponentPanel, "Opponent");
        createOpponentField(opponentGamePanel);
    }

    private void createPlayerNamePanel(JPanel panel, String player) {
        JPanel userNamePanel = new JPanel();
        userNamePanel.setSize(FRAME_WIDTH / 2, FRAME_HEIGHT / 13);
        userNamePanel.add(new JLabel(player));
        panel.add(userNamePanel, BorderLayout.NORTH);
    }

    private void createUserGameField(JPanel panel) {
        panel.setLayout(new GridLayout(WIDTH, HEIGHT));
        char ch = 'A';
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                JButton button = new JButton();
                userButtons[i][j] = button;
                panel.add(button);
                if (i == 0 && j == 0) button.setEnabled(false);
                else if (i == 0) {
                    button.setEnabled(false);
                    button.setText(Character.toString(ch++));
                } else if (j == 0) {
                    button.setEnabled(false);
                    button.setText(Integer.toString(i));
                } else {
                    listenUserBoard(button, i, j);
                    listenKeyBoard(button);
                }
            }
        }
    }


    private void createOpponentField(JPanel panel) {
        panel.setLayout(new GridLayout(WIDTH, HEIGHT));
        char ch = 'A';
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                JButton button = new JButton();
                opponentButtons[i][j] = button;
                panel.add(button);
                if (i == 0 && j == 0) button.setEnabled(false);
                else if (i == 0) {
                    button.setEnabled(false);
                    button.setText(Character.toString(ch++));
                } else if (j == 0) {
                    button.setEnabled(false);
                    button.setText(Integer.toString(i));
                } else {
                    listenOpponentBoard(button, i, j);
                    listenKeyBoard(button);
                }
            }
        }
    }

    private void listenKeyBoard(JButton button) {
        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                int key = event.getKeyCode();
                try {
                    controller.keyClicked(key);
                } catch (ShipExistException e) {
                    throw new RuntimeException(e);
                } catch (ShipBoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void placeShipUserView(int x, int y, int direction, Ship ship) {
        if (direction == HORIZONTAL) {
            for (int i = y; i < y + ship.getWidth(); i++) {
                userButtons[x][i].setText(ship.getLetter());
            }
        } else if (direction == VERTICAL) {
            for (int i = x; i < x + ship.getWidth(); i++) {
                userButtons[i][y].setText(ship.getLetter());
            }
        }

        controller.createShips();
    }

    public void updateUserView(int[][] field) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == -1) userButtons[i + 1][j + 1].setBackground(Color.GREEN);
                else if (field[i][j] == 1) userButtons[i + 1][j + 1].setBackground(Color.RED);
            }
        }
    }

    public void updateOpponentView(int[][] field) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == -1) opponentButtons[i + 1][j + 1].setBackground(Color.RED);
                else if (field[i][j] == 1) opponentButtons[i + 1][j + 1].setBackground(Color.GREEN);
            }
        }
    }

    public void disableUserView() {
        for (JButton[] row : userButtons) {
            for (JButton cell : row) {
                cell.setEnabled(false);
            }
        }
    } // TODO for opponent side of view make it disabled when user made shoot

    public int sendQuestionMessage(String message, String title) {
        return JOptionPane.showConfirmDialog(new JFrame(), message, title, JOptionPane.YES_NO_OPTION);
    }

    public void sendInfoMessage(String message, String title) {
        JOptionPane.showMessageDialog(new JFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void sendVessel(String title, String message, String message2) {
        JOptionPane.showMessageDialog(new JFrame(), message + message2, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void sendMessage(String message, String title) {
        JOptionPane.showMessageDialog(new JFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(new JFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    public String askUserName() {
        String message = "Enter Name:";
        String title = "Welcome BattleShip";
        JFrame frame = new JFrame();
        String playerName = JOptionPane.showInputDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
        return playerName;
    }

    private void listenUserBoard(JButton button, int row, int column) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (SwingUtilities.isLeftMouseButton(arg0)) {
                    controller.userBoardClicked(row, column, BattleField.HORIZONTAL);
                } else if (SwingUtilities.isRightMouseButton(arg0)) {
                    controller.userBoardClicked(row, column, BattleField.VERTICAL);
                }
            }
        });
    }

    private void listenOpponentBoard(JButton button, int row, int column) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (SwingUtilities.isLeftMouseButton(arg0)) {
                    // Captura do clique do jogador no campo do adversário
                    controller.opponentBoardClicked(row, column);
                    System.out.println("Jogador clicou na posição: (" + row + ", " + column + ")");
                }
            }
        });
    }

    private void listenShipPlacement(JButton button, int row, int column, Ship ship, String shipLetter) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (isLeftMouseButton(arg0)) {
                    controller.placeShip(row, column, HORIZONTAL, ship);
                    button.setText(shipLetter);
                    button.setEnabled(false);
                } else if (isRightMouseButton(arg0)) {
                    controller.placeShip(row, column, VERTICAL, ship);
                    button.setText(shipLetter);
                    button.setEnabled(false);
                }
            }
        });
    }

}
