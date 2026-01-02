import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
    public King(Colors color,Position pos){
        super(color,pos);
    }
    @Override
    public int getValue(){
        return 0;
    }
    @Override
    public char type() {
        return 'K';
    }
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions ={
                {-1, -1}, {-1, 0}, {-1, 1}, // Top
                {0, -1},           {0, 1},  // Left and Right
                {1, -1},  {1, 0},  {1, 1}   // Bottom
        };
        for(int[] dir : directions){
            int dRow = dir[0];
            int dCol = dir[1];

            int currentRow = getPosition().getRow();
            int currentCol = getPosition().getColumn();

            currentRow += dRow;
            currentCol += dCol;
            try{
                Position target = new Position((char)currentCol,currentRow);
                Piece pieceAtTarget = board.getPieceAt(target);
                //empty square so I can keep going
                if(pieceAtTarget == null){
                    moves.add(target);
                }
                //square not empty so take/stop
                else{
                    if(pieceAtTarget.getColor()!=this.getColor()){
                        moves.add(target);
                    }
                }
            }catch(InvalidMoveException _){}
        }
        return moves;
    }
}

