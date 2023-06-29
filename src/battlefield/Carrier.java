package battlefield;

/**
 * Porta-aviões P 5
 */
public class Carrier extends Ship {
    private static final int WIDTH = 5;
    private static final int HEIGHT = 1;

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getType() {
        return 5;
    }

    @Override
    public String getLetter() {
        return "P";
    }

    @Override
    public String getNextShipName() {
        return "Aircraft carrier";
    }
}
