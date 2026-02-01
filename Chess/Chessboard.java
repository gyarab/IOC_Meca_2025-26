package Chess;

import Chess.Pieces.piece.Pieces;
import Chess.Pieces.piece.Types;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Stack;

public class Chessboard {

    public static final int MAX_COL = 8;
    public static final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    private Pieces[][] board = new Pieces[MAX_ROW][MAX_COL];
    private ArrayList<Pieces> pieces = new ArrayList<>();
    private Stack<Move> moveHistory = new Stack<>();

    boolean isOpenFile(int col) {
        
        return true;
        
    }

    /* =========================
       MOVE
       ========================= */
    public static class Move {
        public Pieces piece;
        public int startRow, startCol;
        public int targetRow, targetCol;
        public Pieces capturedPiece;
        public int value;

        public Move(Pieces piece,
                    int startRow, int startCol,
                    int targetRow, int targetCol,
                    Pieces capturedPiece,
                    int value) {
            this.piece = piece;
            this.startRow = startRow;
            this.startCol = startCol;
            this.targetRow = targetRow;
            this.targetCol = targetCol;
            this.capturedPiece = capturedPiece;
            this.value = value;
        }
    }

    /* =========================
       GETTERS
       ========================= */
    public ArrayList<Pieces> getPieces() {
        return pieces;
    }

    public Pieces getPiece(int row, int col) {
        return board[row][col];
    }

    /* =========================
       BOARD SETUP
       ========================= */
    public void setPieces(ArrayList<Pieces> newPieces) {
        pieces = newPieces;
        rebuildBoard();
    }

    private void rebuildBoard() {
        board = new Pieces[MAX_ROW][MAX_COL];
        for (Pieces p : pieces) {
            board[p.row][p.col] = p;
        }
    }

    /* =========================
       MOVE / UNDO
       ========================= */
    public void movePiece(Move move) {
        board[move.startRow][move.startCol] = null;
        board[move.targetRow][move.targetCol] = move.piece;

        move.piece.row = move.targetRow;
        move.piece.col = move.targetCol;

        if (move.capturedPiece != null) {
            pieces.remove(move.capturedPiece);
        }

        moveHistory.push(move);
    }

    public void undoMove() {
        if (moveHistory.isEmpty()) return;

        Move move = moveHistory.pop();

        board[move.targetRow][move.targetCol] = move.capturedPiece;
        board[move.startRow][move.startCol] = move.piece;

        move.piece.row = move.startRow;
        move.piece.col = move.startCol;

        if (move.capturedPiece != null) {
            pieces.add(move.capturedPiece);
        }
    }

    /* =========================
       MOVE GENERATION
       ========================= */
    public ArrayList<Move> getAllAvailableMoves(int color) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Pieces p : pieces) {
            if (p.color != color) continue;

            for (int r = 0; r < MAX_ROW; r++) {
                for (int c = 0; c < MAX_COL; c++) {
                    if (!p.canMove(c, r)) continue;

                    Pieces captured = board[r][c];
                    moves.add(new Move(
                            p,
                            p.row, p.col,
                            r, c,
                            captured,
                            0
                    ));
                }
            }
        }
        return moves;
    }

    public ArrayList<Move> getLegalMoves(int color) {
        ArrayList<Move> legal = new ArrayList<>();

        for (Move m : getAllAvailableMoves(color)) {
            movePiece(m);
            if (!isKingInCheck(color)) {
                legal.add(m);
            }
            undoMove();
        }
        return legal;
    }

    /* =========================
       CHECK
       ========================= */
    public boolean isKingInCheck(int color) {
        Pieces king = null;

        for (Pieces p : pieces) {
            if (p.color == color && p.type == Types.KING) {
                king = p;
                break;
            }
        }
        if (king == null) return false;

        for (Pieces p : pieces) {
            if (p.color != color && p.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    /* =========================
       DRAW
       ========================= */
    public void draw(Graphics2D g2) {
        int c = 0;
        for (int r = 0; r < MAX_ROW; r++) {
            for (int col = 0; col < MAX_COL; col++) {
                g2.setColor(c == 0
                        ? new Color(210, 165, 125)
                        : new Color(175, 115, 70));
                g2.fillRect(col * SQUARE_SIZE, r * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                c = 1 - c;
            }
            c = 1 - c;
        }
    }
}
