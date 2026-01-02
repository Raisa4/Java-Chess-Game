import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece{
    public Bishop(Colors color,Position pos){
        super(color,pos);
    }
    @Override
    public int getValue(){
        return 30;
    }
    @Override
    public char type() {
        return 'B';
    }
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions ={
                //{row,column}
                {1,1},//up right
                {-1,1},//down right
                {1,-1},//up left
                {-1,-1}//down left
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
