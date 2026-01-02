import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(Colors color,Position pos){
        super(color,pos);
    }
    @Override
    public char type() {
        return 'R';
    }
    @Override
    public int getValue(){
        return 50;
    }
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions ={
                //{row,column}
                {1,0},//up
                {-1,0},//down
                {0,1},//right
                {0,-1}//left
        };
        for(int[] dir : directions){
            int dRow = dir[0];
            int dCol = dir[1];

            int currentRow = getPosition().getRow();
            int currentCol = getPosition().getColumn();

            while(true){
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
                            break;
                    }
                }catch(InvalidMoveException e){
                    break;
                }
            }
        }
        return moves;
    }

}
