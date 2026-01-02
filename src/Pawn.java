import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
    public Pawn(Colors color,Position pos){
        super(color,pos);
    }
    @Override
    public int getValue(){
        return 10;
    }
    @Override
    public char type() {
        return 'P';
    }
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int dir = (this.getColor() == Colors.WHITE) ? 1 : -1;//white=+1 moves up,opposite for black
        int currentRow = getPosition().getRow();;
        int currentCol = getPosition().getColumn();
        //simple move
        try{
            Position target = new Position((char)currentCol,currentRow+dir);
            if(board.getPieceAt(target) == null){
                moves.add(target);
                boolean isFirstMove =   (this.getColor() == Colors.WHITE && currentRow == 2) ||
                                        (this.getColor() == Colors.BLACK && currentRow == 7);
                if(isFirstMove){
                    Position moveby2 = new Position((char)currentCol,currentRow+2*dir);
                    if(board.getPieceAt(moveby2) == null){
                        moves.add(moveby2);
                    }
                }
            }
        } catch (InvalidMoveException _) {}
        //move capture diagonally
        int[] dirCapture = {-1,1};
        for(int dir_capture :  dirCapture){
            try{
                Position diagPos = new Position((char)(currentCol+dir_capture),currentRow + dir);
                Piece captured = board.getPieceAt(diagPos);
                if(captured != null && captured.getColor() != this.getColor()){
                    moves.add(diagPos);
                }
            }catch(InvalidMoveException _){}
        }
        return moves;
    }
}

