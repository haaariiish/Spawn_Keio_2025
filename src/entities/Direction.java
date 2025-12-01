package entities;

public enum Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT;

    private static final double TWO_PI = Math.PI * 2;
    private static final double STEP = Math.PI / 4;

    public double toAngle() {
        switch (this) {
            case RIGHT:
                return 0;
            case DOWN_RIGHT:
                return STEP;
            case DOWN:
                return STEP * 2;
            case DOWN_LEFT:
                return STEP * 3;
            case LEFT:
                return Math.PI;
            case UP_LEFT:
                return STEP * 5;
            case UP:
                return -Math.PI / 2;
            case UP_RIGHT:
                return STEP * 7;
            default:
                return 0;
        }
    }

    public static Direction fromAngle(double angle) {
        double normalized = normalize(angle);
        int index = (int) Math.round(normalized / STEP) % 8;
        switch (index) {
            case 0:
                return RIGHT;
            case 1:
                return DOWN_RIGHT;
            case 2:
                return DOWN;
            case 3:
                return DOWN_LEFT;
            case 4:
                return LEFT;
            case 5:
                return UP_LEFT;
            case 6:
                return UP;
            case 7:
            default:
                return UP_RIGHT;
        }
    }

    public static double normalize(double angle) {
        double normalized = angle % TWO_PI;
        if (normalized < 0) {
            normalized += TWO_PI;
        }
        return normalized;
    }
}
