/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Chess.Alliance;
import Chess.Chessboard;
import Chess.Chesswindowpanel;
import Chess.Move;
import Chess.Move.MajorAttackMove;

/**
 *
 * @author Admin
 */
public class Bishop extends Piece {
    
    // GUI konstruktor
    public Bishop(int color, int col, int row, boolean isGui) {
        super(color, col, row, isGui);
        this.type = Types.BISHOP;
        this.piecePosition = row * 8 + col;
        this.pceCol = col;
        this.pceRow = row;

        if (color == Chesswindowpanel.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-bishop");
        } else {
            image = getImage("/Chess/Pieces/piece/b-bishop");
        }
    }
    
     // ENGINE delegátor, pokud chceš convenience overload
    public Bishop(final Alliance alliance, final int piecePosition) {
        this(alliance, piecePosition, false);
    }

    // ENGINE konstruktor
    public Bishop(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
        super(alliance, piecePosition, isFirstMove);

        type = Types.BISHOP;

        if (alliance == Alliance.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-bishop");
        } else {
            image = getImage("/Chess/Pieces/piece/b-bishop");
        }
    }


    @Override
  public boolean canMove(int targetCol, int targetRow){
     if(isWithinBoard(targetCol, targetRow)&& isSameSquare(targetCol, targetRow) == false){
         if(Math.abs(targetCol-pceCol) == Math.abs(targetRow-pceRow)){ //the col difference and the row difference always need to be equal -> diagonal move
             if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                 return true;
             }
         }
     } 
    return false;
  }  


  @Override
    public int locationBonus() {
         return this.pieceAlliance.bishopBonus(this.piecePosition);   
     }

    @Override
    public Piece getMovedPiece(Move move) {
        return new Bishop(this.pieceAlliance,move.getDestinationCoordinate(),false);
    }

     @Override
    public Collection<Move> calculateLegalMoves(Chessboard board) {

        List<Move> legalMoves = new ArrayList<>();

        int[][] directions = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : directions) {

            int columns = pceCol;
            int rows = pceRow;

            while (true) {

                columns += d[0];
                rows += d[1];

                if (columns < 0 || columns > 7 || rows < 0 || rows > 7) {
                    break;
                }

//                Piece target = board.getPiece(columns, rows);
                int targetIndex = rows * 8 + columns;
                Piece target = board.getPiece(targetIndex);

                if (target == null) {
                    legalMoves.add(new Move(board, this, columns, rows));
                } else {

                    if (target.color != this.color) {
                        legalMoves.add(new MajorAttackMove(board, this, rows * 8 + columns, target));
                    }

                    break;
                }
            }
        }

        return legalMoves;
    }
  }