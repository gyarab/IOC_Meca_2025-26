/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Chess;

import Chess.Pieces.piece.*;
import java.util.*;

/**
 *
 * @author mecova
 */
public class MoveGenerator {



    private final Chessboard board;
    private final PSQT psqt;

    public MoveGenerator(Chessboard board) {
        this.board = board;
        this.psqt = new PSQT();
    }

    public List<String> generateLegalMoves(Piece piece, int row, int col) {
        List<String> moves = new ArrayList<>();
        
        // Jednoduchý příklad pro pěšce, později rozšířit pro všechny typy
        if(piece instanceof Pawn) {
            int direction = piece.isWhite() ? -1 : 1; // bílé dolů, černé nahoru
            int nextRow = row + direction;

            if(board.isEmpty(nextRow, col)) {
                moves.add(toAlgebraic(nextRow, col));
                // první tah z výchozí pozice 2 polí
                if((piece.isWhite() && row == 6) || (!piece.isWhite() && row == 1)) {
                    if(board.isEmpty(nextRow + direction, col)) {
                        moves.add(toAlgebraic(nextRow + direction, col));
                    }
                }
            }
            // diagonální bijí
            if(col > 0 && board.isEnemy(nextRow, col - 1, piece.isWhite())) {
                moves.add(toAlgebraic(nextRow, col - 1));
            }
            if(col < 7 && board.isEnemy(nextRow, col + 1, piece.isWhite())) {
                moves.add(toAlgebraic(nextRow, col + 1));
            }
        }
        
        // Zobrazit PSQT hodnotu pro tuto pozici
        int psqtValue = psqt.getPieceTableValue(piece.getPieceType(), col, row, piece.isWhite());
        System.out.println(piece.getClass().getSimpleName() + " at " + toAlgebraic(row, col) + " PSQT=" + psqtValue);

        return moves;
    }

    private String toAlgebraic(int row, int col) {
        char file = (char)('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
}

