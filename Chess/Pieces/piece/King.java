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
public class King extends Pieces {
    
    public King(int color, int col, int row) {
        super(color, col, row);
        
        type = Types.KING;
    
    if(color == Chesswindowpanel.WHITE) {
         image = getImage("/Chess/Pieces/piece/w-king");
    } else {
        image = getImage("/Chess/Pieces/piece/b-king");
    }    
}
    //I pass the col and the row of a square and check if this King can move to this square
    @Override
    //this targetCol and targetRow is the square that the player is selecting with their mouse
    public boolean canMove(int targetCol, int targetRow){
        //So if the targeted square is within the board maybe this King can move
        if(isWithinBoard(targetCol, targetRow)){
            //I check 8 directions(left,right,up and down)and pceCol and pceRow are basically in this logic the King's previous position
            if(Math.abs(targetCol-pceCol)+Math.abs(targetRow-pceRow) == 1 || //Math.abs()is basically in this situation a function 
                 //   that returns the difference between two values and one of these answers must be 1 and the other must be 1
                Math.abs(targetCol-pceCol)*Math.abs(targetRow-pceRow)== 1){
                if(isValidSquare(targetCol, targetRow)) {
                    return true;  //the two answers muss be one and which covers these four diagonal squares
            }
        }
            //Castling
               if(hasmoved == false){
                //Right castling
                if(targetCol == pceCol+2 && targetRow == pceRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                     if (isSquareAttacked(pceCol + 1, pceRow) == false && isSquareAttacked(pceCol + 2, pceRow) == false) {
                    for(Pieces pieces:Chesswindowpanel.simPieces){
                        if(pieces.col == pceCol+3 && pieces.row == pceRow && pieces.hasmoved == false){
                            Chesswindowpanel.castlingP = pieces; //castlingP = Rook
                            return true;        
                        }
                    }
                     }
                }
                //Left castling
                if(targetCol == pceCol-2&& targetRow == pceRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                   if (!isSquareAttacked(pceCol - 1, pceRow) && !isSquareAttacked(pceCol - 2, pceRow)) {
                    Pieces p[] = new Pieces[3];
                    for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the simPieces
                        if(pieces.col == pceCol-3 && pieces.row == targetRow){
                           p[0] = pieces; //If there is a piece with col-3, then put the piece in the slot 0
                       }   
                       if(pieces.col == pceCol-4 && pieces.row == targetRow){
                           p[1] = pieces;
                       }
                       if(pieces.col == pceCol-2 && pieces.row == targetRow){
                           p[2]= pieces;
                       }
                       if(p[0] == null && p[2] == null && p[1] != null && p[1].hasmoved == false){
                           Chesswindowpanel.castlingP = p[1];                   
                                return true;
                       }
                    
                   }
                }
        else if(hittingP != null){
        }
    }
               }
        }
            return false; //the King cannnot move to this square
    }
    
       
   public boolean isSquareAttacked(int col, int row) {
    for (Pieces piece : Chesswindowpanel.simPieces) {
        // If it is a opponent's piece
        if (piece.color != this.color) {
            //If a piece can legally move to a given square, it means that it is endangered
            if (piece.canMove(col, row)) {
                return true;
            }
        }
    }
    return false; //The pole is not under attack
}
}   
   
   








    

