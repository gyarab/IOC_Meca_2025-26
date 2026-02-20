/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

/**
 *
 * @author mecova
 */
import Chess.Pieces.piece.*;

public class SimpleBoardEvaluator implements BoardEvaluator {

    @Override
    public int evaluate(Chessboard board, int depth) {
        // Hodnocení: +10 za pěšce, +30 za jezdce, +30 za střelce...
        int score = 0;
        for (Pieces p : board.getPieces()) {
            if (p.color == Chesswindowpanel.WHITE) {
                switch (p.type) {
                    case PAWN: score += 10; break;
                    case KNIGHT: score += 30; break;
                    case BISHOP: score += 30; break;
                    case ROOK: score += 50; break;
                    case QUEEN: score += 90; break;
                    case KING: score += 900; break;
                }
            } else {
                switch (p.type) {
                    case PAWN: score -= 10; break;
                    case KNIGHT: score -= 30;break; 
                    case BISHOP: score -= 30; break;
                    case ROOK: score -= 50; break;
                    case QUEEN: score -= 90; break;
                    case KING: score -= 900; break;
                }
            }
        }
        return score;
    }
}

    

    

