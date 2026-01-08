package pieces;

import logic.Colors;
import logic.Position;

public class Bishop extends Piece {
    public Bishop(Colors color, Position pos){
        super(color,pos);
        this.moveStrategy = new BishopStrategy();
    }
    @Override
    public int getValue(){
        return 30;
    }
    @Override
    public char type() {
        return 'B';
    }
}
