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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Admin
 */
public class Knight extends Piece {
    
     // GUI konstruktor
    public Knight(int color, int col, int row, boolean isGui) {
        super(color, col, row, isGui);
        this.type = Types.KNIGHT;
        this.piecePosition = row * 8 + col;
        this.pceCol = col;
        this.pceRow = row;

        if (color == Chesswindowpanel.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-knight");
        } else {
            image = getImage("/Chess/Pieces/piece/b-knight");
        }
    }
    
     // ENGINE delegátor, pokud chceš convenience overload
    public Knight(final Alliance alliance, final int piecePosition) {
        this(alliance, piecePosition, false);
    }

    // ENGINE konstruktor
    public Knight(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
        super(alliance, piecePosition, isFirstMove);

        type = Types.KNIGHT;

        if (alliance == Alliance.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-knight");
        } else {
            image = getImage("/Chess/Pieces/piece/b-knight");
        }
    }


  @Override
  public boolean canMove(int targetCol,int targetRow){
      
      if(isWithinBoard(targetCol, targetRow)){
          //knight can move if its movement ratio of col and row is 1:2 or 2:1
          if(Math.abs(targetCol-pceCol)* Math.abs(targetRow-pceRow) == 2){
              //so one of these needs to be 1 and one of these needs to be 2
              if(isValidSquare(targetCol, targetRow)){
                  return true;
              }
          }
      }
    return false;
  }

    @Override
    public int locationBonus() {
       return this.pieceAlliance.knightBonus(this.piecePosition);   
    }    

    @Override
    public Piece getMovedPiece(Move move) {
        return new Knight(this.pieceAlliance,move.getDestinationCoordinate(),false);   
    }   

    @Override
    public Collection<Move> calculateLegalMoves(Chessboard board) {
        
         List<Move> legalMoves = new ArrayList<>();

        int[][] moves = {
            {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
            {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        for (int[] m : moves) {

            int columns = pceCol + m[0];
            int rows = pceRow + m[1];

            if (columns < 0 || columns > 7 || rows < 0 || rows > 7) {
                continue;
            }

            Piece target = board.getPiece(rows, columns);

            if (target == null || target.color != this.color) {
                legalMoves.add(new Move(board, this, columns, rows));
            }
        }

        return legalMoves;
    }
  }