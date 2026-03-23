/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;

/**
 *
 * @author Admin
 */
public enum Types {
   
    PAWN(100, "P"),
    KNIGHT(300, "N"),
    BISHOP(330, "B"),
    ROOK(500, "R"),
    QUEEN(900, "Q"),
    KING(1000, "K");
   
   public Types types;
   private final int value;
   private final String pieceName;
   
   public int getPieceValue() {
       return this.value;
   }

   @Override
   public String toString() {
       return this.pieceName;
   }
   
   public Types getPieceType() {
       return this.types;
   }
   
   private Types(final int value, final String pieceName){
       this.value = value;
       this.pieceName = pieceName;
   }   
}

