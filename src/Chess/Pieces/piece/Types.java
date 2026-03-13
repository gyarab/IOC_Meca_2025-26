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
      
   PAWN(100),
   KNIGHT(300),
   BISHOP(330),
   ROOK(500),
   QUEEN(900),
   KING(10000);
   
   public Types types;
   private final int value;
   
   
   public int getPieceValue() {
       return this.value;
   }
   
   public Types getPieceType() {
       return this.types;
   }
   
   private Types(final int value){
       this.value = value;
   }   
}

