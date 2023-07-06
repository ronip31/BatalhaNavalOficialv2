package battlefield;

import exception.ShipBoundException;
import exception.ShipExistException;
import exception.ShotException;

public class BattleField {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    public static final int EMPTY = 0;
    public static final int MISS = -1;
    public static final int HIT = 1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static int SHIP;
    private int[][] field;

    public BattleField() {
        field = new int[WIDTH][HEIGHT];
        createField();
    }

    private void createField() {
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                field[i][j] = 0;
    }

    // Coloca um navio na posição especificada no campo de batalha
    public void put(int x, int y, int direction, Ship ship) throws ShipBoundException, ShipExistException {
        if (isShipFit(x, y, direction, ship)) throw new ShipBoundException(); // Verifica se o navio cabe nas coordenadas especificadas
        if (isAnyShip(x, y, direction, ship)) throw new ShipExistException(); // Verifica se há algum navio na posição especificada
        addShip(x, y, direction, ship); // Adiciona o navio ao campo de batalha
    }

    // Verifica se há algum navio presente na posição especificada
    public boolean isShipPresent(int x, int y) {
        System.out.println("isAnyShip: " + x + y);
        return field[x][y] != EMPTY && field[x][y] != MISS;
    }

    // Verifica se o navio cabe nas coordenadas especificadas
    private boolean isShipFit(int x, int y, int direction, Ship ship) {
        if (direction == HORIZONTAL) {
            boolean isHorizontalWidth = y + ship.getWidth() > WIDTH; // Verifica se o navio ultrapassa a largura do campo de batalha
            boolean isVerticalHeight = x + ship.getHeight() > HEIGHT; // Verifica se o navio ultrapassa a altura do campo de batalha
            return isHorizontalWidth || isVerticalHeight;
        } else {
            boolean isVerticalWidth = y + ship.getHeight() > WIDTH; // Verifica se o navio ultrapassa a largura do campo de batalha
            boolean isHorizontalHeight = x + ship.getWidth() > HEIGHT; // Verifica se o navio ultrapassa a altura do campo de batalha
            return isVerticalWidth || isHorizontalHeight;
        }
    }

    // Verifica se há algum navio presente na posição especificada
    private boolean isAnyShip(int x, int y, int direction, Ship ship) {
        if (direction == HORIZONTAL) {
            for (int i = y; i < y + ship.getWidth(); i++)
                if (field[x][i] != EMPTY) return true;
        } else if (direction == VERTICAL) {
            for (int i = x; i < x + ship.getWidth(); i++)
                if (field[i][y] != EMPTY) return true;
        }
        return false;
    }

    // Adiciona um navio ao campo de batalha
    private void addShip(int x, int y, int direction, Ship ship) {
        if (direction == HORIZONTAL) {
            for (int i = y; i < y + ship.getWidth(); i++)
                field[x][i] = ship.getType();
        } else if (direction == VERTICAL) {
            for (int i = x; i < x + ship.getWidth(); i++)
                field[i][y] = ship.getType();
        }
    }

    // Realiza um disparo nas coordenadas especificadas
    public void shoot(int x, int y) throws ShotException {
        if (isShot(x, y)) throw new ShotException(); // Verifica se já foi feito um disparo nas coordenadas especificadas
        if (isEmpty(x, y)) field[x][y] = MISS; // Se estiver vazio, marca como "tiro na água"
        else field[x][y] = HIT; // Se houver um navio, marca como "acerto"
    }

    // Verifica se já foi feito um disparo nas coordenadas especificadas
    private boolean isShot(int x, int y) {
        return field[x][y] == MISS || field[x][y] == HIT;
    }

    // Verifica se a posição especificada está vazia
    private boolean isEmpty(int x, int y) {
        return field[x][y] == EMPTY;
    }

    // Imprime o campo de batalha
    public void printField() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Retorna o campo de batalha como uma matriz de inteiros
    public int[][] getBoard() {
        return field;
    }

    // Define o campo de batalha com a matriz de inteiros especificada
    public void setBoard(int[][] field) {
        this.field = field;
    }

    // Retorna o valor da célula na posição especificada
    public int getCell(int x, int y) {
        return field[x][y];
    }
}
