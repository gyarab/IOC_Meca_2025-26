/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;
import Chess.Alliance;
import Chess.Chessboard;
import Chess.Chesswindowpanel;
import Chess.Move;
import Chess.MoveUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class Rook extends Pieces {
   
    private final static int[] CANDIDATE_MOVE_COORDINATES = { -8, -1, 1, 8 };
    private final static Map<Integer, MoveUtils.Line[]> PRECOMPUTED_CANDIDATES = computeCandidates();
    private final Alliance pieceAlliance = Alliance.WHITE;
   
    public Rook(int color, int col, int row, final boolean isFirstMove) {
        super(color, col, row, true);
       
        type = Types.ROOK;
       
        if(color == Chesswindowpanel.WHITE){
         image = getImage("/Chess/Pieces/piece/w-rook");
    } else {
            image = getImage("/Chess/Pieces/piece/b-rook");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow){
   
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow)== false){
            //Rook can move as long as either its col or row is the same
            if(targetCol == pceCol || targetRow == pceRow){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        return false;
}
   
 @Override
    public Collection<Move> calculateLegalMoves(Chessboard board) {

        List<Move> legalMoves = new ArrayList<>();

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] d : directions) {

            int columns = this.pceCol;
            int rows = this.pceRow;

            while (true) {

                columns += d[0];
                rows += d[1];

                if (columns < 0 || columns > 7 || rows < 0 || rows > 7) {
                    break;
                }

                Piece target = board.getPiece(rows, columns);

                if (target == null) {
                    legalMoves.add(new Move(board, this, columns, rows));
                } else {

                    if (target.color != this.color) {
                        legalMoves.add(new Move(board, this, columns, rows));
                    }

                    break;
                }
            }
        }

        return legalMoves;
    }

    public int locationBonus() {
      return this.pieceType.rookBonus(this.x,y);
    }

    public Pieces getMovedPiece(Move move) {
      return activeP;   
    }   


}
