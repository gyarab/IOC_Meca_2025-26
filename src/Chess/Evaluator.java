/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

import Chess.Pieces.piece.Pieces;
import Chess.Pieces.piece.Types;

/**
 *
 * @author mecova
 */
public class Evaluator {

    public static int evaluateBoard(Pieces[][] board) {
        int score = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Pieces piece = board[row][col];
                if (piece != null) {
                    int materialValue = piece.getType().getValue();
                    int positionalValue = getPositionalValue(piece, row, col);

                    int pieceScore = materialValue + positionalValue;

                    //Penalizace ohrožení figurky
                    if (isPieceAttacked(piece, board)) {
                        pieceScore -= 10; // každá ohrožená figurka = -10
                    }

                    // Bílé +, černé -
                    score += piece.getAlliance() == Alliance.WHITE ? pieceScore : -pieceScore;

                    // Mobilita
                    int mobility = piece.getLegalMoves(board).size(); // počet tahů
                    score += piece.getAlliance() == Alliance.WHITE ? mobility : -mobility;

                }
            }

        }

        // Bezpečnost krále
        score += evaluateKingSafety(board, Alliance.WHITE);
        score -= evaluateKingSafety(board, Alliance.BLACK);

        return score; // kladné = výhoda bílé, záporné = výhoda černé
    }

    private static int getPositionalValue(Pieces piece, int row, int col) {

        switch (piece.getType()) {
            case PAWN:
                return piece.getAlliance() == Alliance.WHITE
                        ? PSQT.PAWN_TABLE[row][col]
                        : PSQT.PAWN_TABLE[7 - row][col];

            case KNIGHT:
                return piece.getAlliance() == Alliance.WHITE
                        ? PSQT.KNIGHT_TABLE[row][col]
                        : PSQT.KNIGHT_TABLE[7 - row][col];

            case BISHOP:
                return piece.getAlliance() == Alliance.WHITE
                        ? PSQT.BISHOP_TABLE[row][col]
                        : PSQT.BISHOP_TABLE[7 - row][col];

            case ROOK:
                return piece.getAlliance() == Alliance.WHITE
                        ? PSQT.ROOK_TABLE[row][col]
                        : PSQT.ROOK_TABLE[7 - row][col];

            case QUEEN:
                return piece.getAlliance() == Alliance.WHITE
                        ? PSQT.QUEEN_TABLE[row][col]
                        : PSQT.QUEEN_TABLE[7 - row][col];

            case KING:
                return piece.getAlliance() == Alliance.WHITE
                        ? PSQT.KING_TABLE[row][col]
                        : PSQT.KING_TABLE[7 - row][col];

            default:
                return 0;
        }
    }

    private static boolean isPieceAttacked(Pieces piece, Pieces[][] board) {
    // aliance nepřítele
    Alliance enemy = piece.getAlliance() == Alliance.WHITE ? Alliance.BLACK : Alliance.WHITE;

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Pieces attacker = board[row][col];
            if (attacker != null && attacker.getAlliance() == enemy) {
                for (Move move : attacker.getLegalMoves(board)) {
                    // pokud tah protivníka cílí na tuto figurku
                    if (move.getDestinationRow() == piece.getRow() &&
                        move.getDestinationCol() == piece.getCol()) {
                        return true;
                    }
                }
            }
        }
    }

    return false; // nikdo tuto figurku neohrožuje
}

    private static int evaluateKingSafety(Pieces[][] board, Alliance alliance) {
        // Najdi krále
        Pieces king = null;
        int kingRow = -1, kingCol = -1;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Pieces piece = board[row][col];
                if (piece != null && piece.getType() == Types.KING && piece.getAlliance() == alliance) {
                    king = piece;
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        if (king == null) {
            return 0;
        }

        int danger = 0;
        // Jednoduchá penalizace: pokud kolem krále není dost vlastních figur
        int friendlyAround = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = kingRow + dr;
                int c = kingCol + dc;
                if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                    Pieces p = board[r][c];
                    if (p != null && p.getAlliance() == alliance) {
                        friendlyAround++;
                    }
                }
            }
        }

        danger = (3 - friendlyAround) * 20; // méně figur kolem = větší penalizace
        return -danger; // záporné znamená ohrožení
    }
}
