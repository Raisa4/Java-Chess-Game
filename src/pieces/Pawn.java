package pieces;

import logic.Colors;
import logic.Position;

public class Pawn extends Piece {
    public Pawn(Colors color, Position pos){
        super(color,pos);
        this.moveStrategy = new PawnStrategy();
    }
    @Override
    public int getValue(){
        return 10;
    }
    @Override
    public char type() {
        return 'P';
    }

}

