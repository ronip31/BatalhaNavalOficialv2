package battlefield;

/**
 * Contra-torpedeiros C 3
 */
public class Destroyer extends Ship {
    private static final int WIDTH = 3;
    private static final int HEIGHT = 1;

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getType() {
        return 3;
    }

    @Override
    public String getLetter() {
        return "C";
    }

    @Override
    public String getNextShipName() {
        return "Destroyers";
    }
}
