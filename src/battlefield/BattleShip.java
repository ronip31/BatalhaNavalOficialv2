package battlefield;

/**
 * Navios-tanque N 4
 */
public class BattleShip extends battlefield.Ship {
    private static final int WIDTH = 4;
    private static final int HEIGHT = 1;

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getType() {
        return 4;
    }
    @Override
    public String getLetter() {
        return "N";
    }
    @Override
    public String getNextShipName() {
        return "tankers";
    }
}
