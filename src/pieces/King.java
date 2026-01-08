package pieces;

import logic.Colors;
import logic.Position;

public class King extends Piece {
    public King(Colors color, Position pos){
        super(color,pos);
        this.moveStrategy = new KingStrategy();
    }
    @Override
    public int getValue(){
        return 0;
    }
    @Override
    public char type() {
        return 'K';
    }

}

