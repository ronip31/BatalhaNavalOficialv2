package battlefield;

/**
 * Submarinos S 2
 */
public class Submarine extends Ship {
    private static final int WIDTH = 2;
    private static final int HEIGHT = 1;

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getType() {
        return 2;
    }

    @Override
    public String getLetter() {
        return "S";
    }

    @Override
    public String getNextShipName() {
        return "Submarine";
    }
}
