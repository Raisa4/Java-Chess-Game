package pieces;
import logic.Colors;
import logic.Position;

public class Rook extends Piece {
    public Rook(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new RookStrategy();
    }
    @Override
    public char type() {
        return 'R';
    }
    @Override
    public int getValue(){
        return 50;
    }

}
