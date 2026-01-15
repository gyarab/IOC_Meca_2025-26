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
public class Pawn extends Pieces{
    
    public Pawn(int color, int col, int row) {
        super(color, col, row);
        
        type = Types.PAWN;
        
        if(color == Chesswindowpanel.WHITE){
         image = getImage("/Chess/Pieces/piece/w-pawn");
    } else {
            image = getImage("/Chess/Pieces/piece/b-pawn");
        }
}
    @Override
    public boolean canMove(int targetCol,int targetRow){
    
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            //Define the move value based on its color
            int moveValue;
            if(color == Chesswindowpanel.WHITE){
                moveValue = -1; //for white pawn(up)
            }
            else{
                moveValue = 1; //for black pawn(down)
            }
        //Check the hitting piece
        hittingP = getHittingP(targetCol, targetRow);
        
        //1 square movement
        if(targetCol == pceCol && targetRow == pceRow + moveValue && hittingP == null){
            return true;
        }
        //2 square movement
        if(targetCol == pceCol && targetRow == pceRow + moveValue*2 && hittingP == null && hasmoved == false){
                if (pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    return true;
                }
        }
        //Diagonal movement & Capture(if a piece is on a square diagonally in front of it)
        if(Math.abs(targetCol-pceCol) == 1 
                && targetRow == pceRow + moveValue && hittingP != null && hittingP.color != color){
            return true;//col difference needs to be 1 and targetRow needs to be pceRow+1 or pceRow-1
        }
        //En Passant
        if(Math.abs(targetCol-pceCol) == 1 && targetRow == pceRow + moveValue){
            for(Pieces pieces:Chesswindowpanel.simPieces){
                if(pieces.col == targetCol && pieces.row == pceRow && pieces.twoStepped == true){
                    hittingP = pieces; //If there is a piece that it's col is equal to the targetCol
                    return true; 
                }
            }
        }
        }
        return false;
}    
 }
