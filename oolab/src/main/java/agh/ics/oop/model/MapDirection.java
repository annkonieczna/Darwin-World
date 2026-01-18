package agh.ics.oop.model;

public enum MapDirection {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    public MapDirection rotate(int gene) {
        int newOrdinal = (this.ordinal() + gene) % 8;
        return MapDirection.values()[newOrdinal];

    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case N -> new Vector2d(0, 1);
            case NE -> new Vector2d(1, 1);
            case E -> new Vector2d(1, 0);
            case SE -> new Vector2d(1, -1);
            case S -> new Vector2d(0, -1);
            case SW -> new Vector2d(-1, -1);
            case W -> new Vector2d(-1, 0);
            case NW -> new Vector2d(-1, 1);
        };
    }

    public int toAngle() {
        return switch (this) {
            case N -> 0;
            case NE -> 45;
            case E -> 90;
            case SE -> 135;
            case S -> 180;
            case SW -> 225;
            case W -> 270;
            case NW -> 315;
        };
    }
}
