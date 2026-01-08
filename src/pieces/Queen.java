package pieces;

import logic.Colors;
import logic.Position;

public class Queen extends Piece {
    public Queen(Colors color, Position pos){
        super(color,pos);
        this.moveStrategy = new QueenStrategy();
    }
    @Override
    public char type() {
        return 'Q';
    }
    @Override
    public int getValue(){
        return 90;
    }

}
