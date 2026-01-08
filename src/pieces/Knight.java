package pieces;

import logic.Colors;
import logic.Position;

public class Knight extends Piece {
    public Knight(Colors color, Position pos) {
        super(color, pos);
        this.moveStrategy = new KnightStrategy();
    }

    @Override
    public char type() {
        return 'N';
    }

    @Override
    public int getValue() {
        return 30;
    }

}