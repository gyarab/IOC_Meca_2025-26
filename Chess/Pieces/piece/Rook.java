/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece; 
import Chess.Chesswindowpanel;

/**
 *
 * @author Admin
 */
public class Rook extends Pieces {
    
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
}
