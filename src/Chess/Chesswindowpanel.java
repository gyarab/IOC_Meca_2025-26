/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

import Chess.Pieces.piece.Bishop;
import Chess.Pieces.piece.King;
import Chess.Pieces.piece.Knight;
import Chess.Pieces.piece.Pawn;
import Chess.Pieces.piece.Pieces;
import Chess.Pieces.piece.Queen;
import Chess.Pieces.piece.Rook;
import Chess.Pieces.piece.Types;
import static Chess.Pieces.piece.Types.BISHOP;
import static Chess.Pieces.piece.Types.KING;
import static Chess.Pieces.piece.Types.KNIGHT;
import static Chess.Pieces.piece.Types.PAWN;
import static Chess.Pieces.piece.Types.QUEEN;
import static Chess.Pieces.piece.Types.ROOK;
import java.util.HashMap;
import java.util.Map;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.MenuBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Admin
 */
public class Chesswindowpanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 810;
    final int FPS = 60;
    Thread gameThread;
    Chessboard board = new Chessboard();
    Mouse mouse = new Mouse();
    Chessboard.Move bestMove;

    //Pieces
    public static ArrayList<Pieces> pieces = new ArrayList<>();
    public static ArrayList<Pieces> simPieces = new ArrayList<>();
    ArrayList<Pieces> promotedP = new ArrayList<>();
    Pieces activeP, checkingP; //I use this to handle the piece that the player is currently holding
    public static Pieces castlingP;
    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE; //game starts with white pieces

    //Number of moves with the same piece
    private int count;

    private Chessboard chessboard;

    //BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;
    boolean draw;
    boolean isVsComputer = false; //Whether the mode against the PC is enabled

    //Check for castling
    private boolean whiteCastleK;
    private boolean whiteCastleQ;
    private boolean blackCastleK;
    private boolean blackCastleQ;

    // Vrátí referenci na šachovnici
    public Chessboard getBoard() {
        return board;
    }

    // Vrátí figuru, kterou hráč právě drží (aktivníP)
    public Pieces getSelectedPiece() {
        return activeP;
    }

    public ArrayList<Pieces> getPieces() {
        return pieces;
    }

    public ArrayList<Pieces> getSimPieces() {
        return simPieces;
    }

    // Vrátí aktuální barvu hráče, který je na tahu
    public int getCurrentColor() {
        return currentColor;
    }

    // Vrátí, zda je právě povolena promocí
    public boolean isPromotion() {
        return promotion;
    }

    // Vrátí figuru, která aktuálně ohrožuje krále (pro šachové hlášení)
    public Pieces getCheckingPiece() {
        return checkingP;
    }

    // Vrátí boolean, zda je hra ukončena
    public boolean isGameOver() {
        return gameover;
    }

    // Vrátí boolean, zda je pat/stalemate
    public boolean isStalemate() {
        return stalemate;
    }

    // Vrátí boolean, zda je remíza
    public boolean isDraw() {
        return draw;
    }

    private int getPieceTableValue(Types type, int sq) {
        switch (type) {
            case KNIGHT:
                return PSQT.KNIGHT_TABLE[sq];
            case KING:
                return PSQT.KING_TABLE[sq];
            case QUEEN:
                return PSQT.QUEEN_TABLE[sq];
            case ROOK:
                return PSQT.ROOK_TABLE[sq];
            case BISHOP:
                return PSQT.BISHOP_TABLE[sq];
            case PAWN:
                return PSQT.PAWN_TABLE[sq];
            default:
                return 0;
        }
    }

    //INTEGERS
    int computerColor = BLACK; //The computer will play for black

    //HashMaps
    private final Map<String, Integer> positionHistory = new HashMap<>();

    public Chesswindowpanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        setUpInitialPosition();
        //zavolat initializeEvaluation
        copyPieces(pieces, simPieces);
        button();
        button2();
        button3();
        button4();
        button5();
        initKeyBindings(this);
        board = new Chessboard();
        currentColor = WHITE; // začíná bílý

        // Inicializace bestMove až po inicializaci board a currentColor
//    bestMove = (Chessboard.Move) ai.execute(board, getCurrentColor());
    }

    // Spustí AI tah pro aktuálního hráče
    public void performAIMove() {
        MiniMax ai = new MiniMax(new BoardEvaluator() {
            @Override
            public int evaluate(Chessboard board, int depth) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        }, 3); // hloubka 3
        Chessboard.Move bestMove = (Chessboard.Move) ai.execute(board, 3);

        if (bestMove != null) {
            board.movePiece(bestMove);
            System.out.println("AI provedl tah: " + bestMove.piece + " na ["
                    + bestMove.targetRow + "," + bestMove.targetCol + "]");
        }

        // Přepnutí hráče
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;
    }

    // Tento kód můžeš zavolat třeba při stisku tlačítka "AI tah"
    public void onAITurnButtonPressed() {
        performAIMove();
        repaint(); // překreslí šachovnici
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setWhiteCastleK(boolean value) {
        this.whiteCastleK = value;
    }

    public void setWhiteCastleQ(boolean value) {
        this.whiteCastleQ = value;
    }

    public void setBlackCastleK(boolean value) {
        this.blackCastleK = value;
    }

    public void setBlackCastleQ(boolean value) {
        this.blackCastleQ = value;
    }

    public void testIllegalmove() {
        pieces.add(new Pawn(WHITE, 7, 6, false));
        addPieceCorrectly(new King(WHITE, 3, 7, false));
        addPieceCorrectly(new King(BLACK, 0, 3, false));
        pieces.add(new Bishop(BLACK, 1, 4, false));
        pieces.add(new Queen(BLACK, 4, 5, false));
    }

    public void testPromoting() {
        pieces.add(new Pawn(WHITE, 0, 3, false));
        pieces.add(new Pawn(BLACK, 7, 5, false));
        addPieceCorrectly(new King(WHITE, 1, 0, false));
        addPieceCorrectly(new King(BLACK, 1, 6, false));
    }

    public void copyPieces(ArrayList<Pieces> src, ArrayList<Pieces> dst) {
        dst.clear();
        for (int i = 0; i < src.size(); i++) {
            dst.add(src.get(i));
        }
    }

    private void addPieceCorrectly(Pieces newPiece) {
        //If a king is added, it is checked whether a king of the same suit already exists
        if (newPiece.type == Types.KING) {
            for (Pieces p : pieces) {
                if (p.type == Types.KING && p.color == newPiece.color) {
                    System.err.println("⚠ You cannot add a second king of the same suit!");
                    return; //Stop adding
                }
            }
        }
        //If it is all alrigth, add the piece
        pieces.add(newPiece);
    }

    public void setUpInitialPosition() {

        //White pieces
        addPieceCorrectly(new Rook(WHITE, 0, 7, false));
        addPieceCorrectly(new Rook(WHITE, 7, 7, false));
        addPieceCorrectly(new Knight(WHITE, 1, 7, false));
        addPieceCorrectly(new Knight(WHITE, 6, 7, false));
        addPieceCorrectly(new Bishop(WHITE, 2, 7, false));
        addPieceCorrectly(new Bishop(WHITE, 5, 7, false));
        addPieceCorrectly(new Queen(WHITE, 3, 7, false));
        addPieceCorrectly(new King(WHITE, 4, 7, false));

        for (int i = 0; i < 8; i++) {
            addPieceCorrectly(new Pawn(WHITE, i, 6, false));
        }

        //Black pieces
        addPieceCorrectly(new Rook(BLACK, 0, 0, false));
        addPieceCorrectly(new Rook(BLACK, 7, 0, false));
        addPieceCorrectly(new Knight(BLACK, 1, 0, false));
        addPieceCorrectly(new Knight(BLACK, 6, 0, false));
        addPieceCorrectly(new Bishop(BLACK, 2, 0, false));
        addPieceCorrectly(new Bishop(BLACK, 5, 0, false));
        addPieceCorrectly(new Queen(BLACK, 3, 0, false));
        addPieceCorrectly(new King(BLACK, 4, 0, false));

        for (int i = 0; i < 8; i++) {
            addPieceCorrectly(new Pawn(BLACK, i, 1, false));
        }

    }

    @Override
    public void run() {

        // Game  LOOP
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

        }

    }

    private void update() {

        if (promotion) { //I basically stop the game during the promotion, so I cannot pick up other pieces, until I finish this promotion process
            promoting();
        } else if (gameover == false && stalemate == false) {
            ///MOUSE BUTTON PRESSED///
        if (mouse.pressed) {
                //If the activeP (active piece) is null, check if you can pick up a piece
                if (activeP == null) {
                    //scanning this simPieces Arraylist<>()
                    for (Pieces pieces : simPieces) {
                        //If the mouse is on ally piece, pick it up as the activeP
                        if (pieces.color == currentColor && pieces.col == mouse.x / Chessboard.SQUARE_SIZE && pieces.row == mouse.y / Chessboard.SQUARE_SIZE) {
                            //setting this piece as active piece
                            activeP = pieces;
                        }
                    }
                } //if the activeP is not null -> the player is already holding a piece -> so he can move it
                else {
                    //if the player is holding a piece, simulate the move
                    simulate();
                }
            }
            ///MOUSE BUTTON RELEASED///
        //So if the player released the mouse button when he is holding a piece
        if (mouse.pressed == false) {
                if (activeP != null) {
                    if (validSquare) {
                        //MOVE CONFIRMED
                        //Update the piece list in case a piece has been captured and removed -> during the simulation
                        copyPieces(simPieces, pieces); //simPieces as the source and this pieces as the target
                        activeP.updatePosition(); //when the player released the mouse button I call this update method, if this validSquare is true
                        //the we update the postion
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }
                        if (isKingInCheck() && Checkmate()) { //I remove this else bracket cause
                            gameover = true;             //otherwise the program would have ended after finding out that the king was in check

                        } else if (Stalemate() && isKingInCheck() == false) {
                            stalemate = true;
                        } else { //The game is still going on
                            if (canBePromoted()) {
                                if (!isVsComputer || currentColor != computerColor) {
                                    promotion = true;
                                }
                            } else {
                                changePlayer();
                            }
                        }

                    } else { //but if not that means the piece cannot move so we should reset the position
                        //The move is not valid so reset anything
                        copyPieces(pieces, simPieces); //restoring the original list
                        activeP.resetPosition();
                        activeP = null; //player has moved with this piece so I set activeP null
                    }
                }
            }
        }
    }

    private void simulate() {

        canMove = false;
        validSquare = false;

        //Reset the piece list in every loop
        //This is basically for restoring the removed piece during the simulation
        copyPieces(pieces, simPieces);
        //Reset the castling piece's position
        if (castlingP != null) {
            castlingP.col = castlingP.pceCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        //If a piece is being held, update its position
        activeP.x = mouse.x - Chessboard.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Chessboard.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        //Check if the piece is hoveing over a reachable square
        if (activeP.canMove(activeP.col, activeP.row)) { //I pass this piece's current col and row as the target square
            canMove = true;

            //If hitting a piece, remove it from the board
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            if (isIlegalmove(activeP) == false && opponentCanCaptureKing() == false) {
                validSquare = true;
            }
        }

    }

    private boolean isIlegalmove(Pieces king) {

        if (king.type == Types.KING) {
            for (Pieces pieces : simPieces) { //If it's king, scan the simPieces and check
                //if there is a piece that is not the king itself and has a different color and can move to the square where king is trying to move now
                if (pieces != king && pieces.color != king.color && pieces.canMove(king.col, king.row)) { //If these conditions are matched, this move is illegal
                    return true;
                }
            }
        }

        return false;
    }

    boolean opponentCanCaptureKing() {

        Pieces king = getKing(false); //I want to get the current color's king
        if (king != null) {
            for (Pieces pieces : simPieces) { //I scan the simPieces and cheek if there is a piece that can move to the king's square
                if (pieces.color != king.color && pieces.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isKingInCheck() {

        Pieces king = getKing(true);//I get the opponent's king
        if (king != null) {
            if (activeP.canMove(king.col, king.row)) { //I check if the activeP can move to the square where the opponent's king is
                checkingP = activeP; //and if can, this piece is checking the king, so I set it as checking piece
                return true; //because king is in check
            } else {
                checkingP = null; //king is not in check
            }
        }
        return false;
    }

    private Pieces getKing(boolean opponent) {

        Pieces king = null;
        for (Pieces pieces : simPieces) { //This method finds the king  in the simPieces and return it
            if (opponent) { //If this boolean opponent true, return the opponent's king and if this boolean is false, return you own king
                if (pieces.type == Types.KING && pieces.color != currentColor) {
                    king = pieces;
                }
            } else {
                if (pieces.type == Types.KING && pieces.color == currentColor) {
                    king = pieces;
                }
            }
        }
        return king;
    }

    private boolean Checkmate() {
        Pieces king = getKing(true); // First we get the opponent's king

        // If the king can move, is not checkmate.
        if (kingCanMove(king)) {
            return false;
        }
        //If the checkingP is knight
        if (checkingP instanceof Knight) {
            //I scan the simPieces (defensive¨options)
            for (Pieces piece : simPieces) {
                //The defensive piece must have the same color as the king
                if (piece.color == king.color && piece.canMove(checkingP.col, checkingP.row)) {
                    return false; // The knight can be taken, so it is still not checkmate.
                }
            }
        } else {
            //For other pieces I check the defensive options
            int colDif = Math.abs(checkingP.col - king.col);
            int rowDif = Math.abs(checkingP.row - king.row);

            if (colDif == 0) {
                //Attack is carried out vetically
                if (checkingP.row < king.row) {
                    //The checkingP is above the king
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Pieces piece : simPieces) {
                            if (piece != king && piece.color == king.color && piece.canMove(checkingP.col, row)) {
                                return false; // The attack can be blocked
                            }
                        }
                    }
                } else {
                    //The checkingP is below the king
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Pieces piece : simPieces) {
                            if (piece != king && piece.color == king.color && piece.canMove(checkingP.col, row)) {
                                return false; //The attack can be blocked
                            }
                        }
                    }
                }
            } else if (rowDif == 0) {
                //Attack is carried out horizontally
                if (checkingP.col < king.col) {
                    //The checkingP is to the left of the king
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Pieces piece : simPieces) {
                            if (piece != king && piece.color == king.color && piece.canMove(col, checkingP.row)) {
                                return false; //The attack can be blocked
                            }
                        }
                    }
                } else {
                    //The checkingP is to the right of the king
                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Pieces piece : simPieces) {
                            if (piece != king && piece.color == king.color && piece.canMove(col, checkingP.row)) {
                                return false; //The attack can be blocked
                            }
                        }
                    }
                }
            } else if (colDif == rowDif) {
                //Attack is carried out diagonally
                if (checkingP.row < king.row) {
                    if (checkingP.col < king.col) {
                        //The checkingP is in the upper left direction towards the king
                        for (int col = checkingP.col, row = checkingP.row; col < king.col && row < king.row; col++, row++) {
                            for (Pieces piece : simPieces) {
                                if (piece != king && piece.color == king.color && piece.canMove(col, row)) {
                                    return false; //The attack can be blocked
                                }
                            }
                        }
                    } else {
                        //The checking is in the upper right direction towards the king
                        for (int col = checkingP.col, row = checkingP.row; col > king.col && row < king.row; col--, row++) {
                            for (Pieces piece : simPieces) {
                                if (piece != king && piece.color == king.color && piece.canMove(col, row)) {
                                    return false; //The attack can be blocked
                                }
                            }
                        }
                    }
                } else {
                    if (checkingP.col < king.col) {
                        //The checkingP is in the lower left direction towards the king
                        for (int col = checkingP.col, row = checkingP.row; col < king.col && row > king.row; col++, row--) {
                            for (Pieces piece : simPieces) {
                                if (piece != king && piece.color == king.color && piece.canMove(col, row)) {
                                    return false; //The attack can be blocked
                                }
                            }
                        }
                    } else {
                        //The checkingP is in the lower right direction towards the king
                        for (int col = checkingP.col, row = checkingP.row; col > king.col && row > king.row; col--, row--) {
                            for (Pieces piece : simPieces) {
                                if (piece != king && piece.color == king.color && piece.canMove(col, row)) {
                                    return false; //The attack can be blocked
                                }
                            }
                        }
                    }
                }
            }
        }

        // If the check cannot be blocked and the king cannot escape, it is checkmate.
        return true;
    }

    // Vrátí všechny možné tahy pro danou barvu
// Vrátí všechny možné tahy pro danou barvu
//public ArrayList<Chessboard.Move> getAllAvailableMoves(int color) {
//    ArrayList<Chessboard.Move> moves = new ArrayList<>();
//
//    for (Pieces p : pieces) {
//        if (p.color != color) continue; // přeskočíme soupeřovy figury
//
//        for (int r = 0; r < 8; r++) {
//            for (int c = 0; c < 8; c++) {
//                // Reset simulovaných figur
//                copyPieces(pieces, simPieces);
//
//                int originalCol = p.col;
//                int originalRow = p.row;
//
//                if (p.canMove(c, r)) {
//                    Pieces captured = null;
//                    for (Pieces target : simPieces) {
//                        if (target != p && target.col == c && target.row == r) {
//                            captured = target;
//                            simPieces.remove(target);
//                            break;
//                        }
//                    }
//
//                    // Pokud tah není ilegální a král není ohrožen
//                    if (!isIlegalmove(p) && !opponentCanCaptureKing()) {
//
//                    Pieces capturedPieces = board[row][col];
//
//                    moves.add(new MoveImpl(
//                    p,
//                    p.row, p.col,   // START
//                    r, c,           // CÍL
//                    captured,
//                    evaluateMove(p, c, r)
//    ));
//}
//
//                    // Obnovíme původní pozici
//                    p.col = originalCol;
//                    p.row = originalRow;
//                    copyPieces(pieces, simPieces);
//                    if (captured != null) simPieces.add(captured);
//                }
//            }
//        }
//    }
//
//    return moves;
//}
    private boolean kingCanMove(Pieces king) { //I check if the king in check can move to a square where it is not under attack
        //here is some problem
        //Simulate if there is any square where the king can move to
        if (isValidMove(king, -1, -1)) {
            return true;
        } //It means to move to left diagonally(up) and if this return true, that means the king can move
        //to this direction and so on
        if (isValidMove(king, 0, -1)) {
            return true;
        }  //It means to move to up  
        if (isValidMove(king, 1, -1)) {
            return true;
        }  //It means to move to right diagonally(up)
        if (isValidMove(king, -1, 0)) {
            return true;
        }  //It means to move to right
        if (isValidMove(king, 1, 0)) {
            return true;
        }   //It means to move to left
        if (isValidMove(king, -1, 1)) {
            return true;
        } //It means to move to left diagonally(down)
        if (isValidMove(king, 0, 1)) {
            return true;
        }  //It means to move to down
        if (isValidMove(king, 1, 1)) {
            return true;
        }  //It means to move to right diagonally(down) and check if these all directions are valid moves                                            
        return false; //If none of them return ture, thatt means there is no square that the king can move
    }

    private boolean isValidMove(Pieces king, int colPlus, int rowPlus) {

        boolean isValidMove = false;
        //Update the king's position for a second
        //TODO: repair this code
        king.col += colPlus; //this square
        king.row += rowPlus;

        if (king.canMove(king.col, king.row)) { //I check if this king can move to tjis square
            if (king.hittingP != null) { //If he can, check if it's hitting any piece
                simPieces.remove(king.hittingP.getIndex()); //If it is, remove it ffom the list
            }
            if (isIlegalmove(king) == false) { //Then I check if the move is illegal or not
                isValidMove = true; //It is a safe spot for the king, if he can remove the piece
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces); //I reset the king's position and also I reset the piece list, so before checking other directions, I need to reset
        //the list as well because of simulating moves

        return isValidMove;
    }

    private boolean Stalemate() {

        int count = 0;
        //Count the number of pieces of the opponent
        for (Pieces pieces : simPieces) {
            if (pieces.color != currentColor) {
                count++;
            }
        }
        //If only one piece(the king) is left
        if (count == 1) {
            if (kingCanMove(getKing(true)) == false) {
                return true;  //The king cannot to move any square, so return true
            }
        }

        return false;
    }

    private void checkCastling() {

        if (castlingP != null) {
            if (castlingP.col == 0) { //It is the rook on the left
                castlingP.col += 3; //The rook moves by 3 squares
            } else if (castlingP.col == 7) { //It is the rook on the right
                castlingP.col -= 2; //The rook moves by 2 squares
            }
            castlingP.x = castlingP.getX(castlingP.col); //I update this castlinP.x based on updated col
        }
    }

    private String generatePositionHash() {
        StringBuilder sb = new StringBuilder();

        // Přidej informace o každé figuře
        for (Pieces p : pieces) {
            sb.append(p.type)
                    .append(p.color)
                    .append(p.col)
                    .append(p.row)
                    .append(";");
        }

        // Přidej informace o tahu
        sb.append("TURN:").append(currentColor).append(";");

        // Rošády a en passant ignorujeme, pokud je nesleduješ
        // (to řeší už tvoje metoda getBoardState)
        return sb.toString();
    }

    private void changePlayer() {
        // Přepnutí barvy
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;

        // Reset TwoStepped status
        for (Pieces p : pieces) {
            if (p.color == currentColor) {
                p.twoStepped = false;

            }
        }

        // Uložení pozice pro tři opakování
        String boardState = getBoardState();
        int count = positionHistory.getOrDefault(boardState, 0) + 1;
        positionHistory.put(boardState, count);

        //Checking the position repeated three times
        //TODO: more work here, because it won
        if (count == 3 && !stalemate && !gameover) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "The same position occurred three times.\nThe game ends in a draw.",
                    "Draw - Threefold Repetition",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            draw = true;
            //gameover = true;
            repaint();
            return;
        }

        // Kontrola nedostatečného materiálu
        if (!stalemate && !gameover) {
            int kingCount = 0, bishopCount = 0, knightCount = 0, totalPieces = 0;
            for (Pieces p : simPieces) {
                totalPieces++;
                switch (p.type) {
                    case KING:
                        kingCount++;
                    case BISHOP:
                        bishopCount++;
                    case KNIGHT:
                        knightCount++;
                }
            }
            //TODO: more work here

            boolean insufficientMaterial
                    = (totalPieces == 2 && kingCount == 2)
                    || (totalPieces == 3 && kingCount == 2 && (bishopCount == 1 || knightCount == 1))
                    || (totalPieces == 4 && kingCount == 2 && ((knightCount == 2) || (knightCount == 1 && bishopCount == 1)));

            if (insufficientMaterial) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Draw – insufficient material for the checkmate.",
                        "Draw",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
                draw = true;
                //gameover = true;
                repaint();
                return;
            }
        }

        activeP = null;
        repaint();

        // Pokud je na tahu počítač, spustíme jeho tah
        if (isVsComputer && currentColor == computerColor && !gameover && !stalemate && !draw) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } //repair catch
                    catch (InterruptedException e) {
                    }
                    makeComputerMove();  // AI udělá svůj tah

                    // Po tahu AI přepneme zpět na hráče
                    if (isDisplayable()) {
                        SwingUtilities.invokeLater(() -> changePlayer());
                    }
                }
            }).start();
        }
    }

    private boolean canBePromoted() {

        if (activeP.type == Types.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
                promotedP.clear();
                promotedP.add(new Rook(currentColor, 9, 2, true));
                promotedP.add(new Knight(currentColor, 9, 3, true));
                promotedP.add(new Bishop(currentColor, 9, 4, true));
                promotedP.add(new Queen(currentColor, 9, 5, true));
                return true;
            }
        }

        return false;
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Pieces piece : promotedP) {
                //If there is a piece that has the same col and row as the mouse col and row that means the mouse is one of these pieces
                if (piece.col == mouse.x / Chessboard.SQUARE_SIZE && piece.row == mouse.y / Chessboard.SQUARE_SIZE) {
                    switch (piece.type) {
                        case ROOK:
                            simPieces.add(new Rook(currentColor, activeP.col, activeP.row, true));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(currentColor, activeP.col, activeP.row, true));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(currentColor, activeP.col, activeP.row, true));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(currentColor, activeP.col, activeP.row, true));
                            break;
                        default:
                            break; //If no type matches, nothing happens
                    }
                    simPieces.remove(activeP.getIndex());//after that I remove the pawn from this list
                    copyPieces(simPieces, pieces); //I update this backup list
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    private void button() {
        javax.swing.JButton remizButton = new javax.swing.JButton("Offer a draw");
        remizButton.setBounds(850, 130, 200, 50); //Position and size of the button
        remizButton.setFont(new Font("Book Antiqua", Font.BOLD, 20));
        remizButton.setFocusPainted(false);
        remizButton.setBackground(new Color(200, 200, 200));

        //After clicking the button
        remizButton.addActionListener(e -> {
            if (!gameover && !stalemate) {
                int option = javax.swing.JOptionPane.showConfirmDialog(this, "Opponent offer a draw. Accept?", "Offer a draw", javax.swing.JOptionPane.YES_NO_OPTION);

                if (option == javax.swing.JOptionPane.YES_NO_OPTION) {
                    draw = true;
                    repaint();
                }
            }
        });

        this.setLayout(null); //I need to disable the layout manager for manual placement
        this.add(remizButton);
    }

    private void button2() {
        javax.swing.JButton giveupButton = new javax.swing.JButton("Give up");
        giveupButton.setBounds(850, 700, 200, 50); //Position and size of the button
        giveupButton.setFont(new Font("Book Antiqua", Font.BOLD, 20));
        giveupButton.setFocusPainted(false);
        giveupButton.setBackground(new Color(200, 200, 200));

        //After clicking the button
        giveupButton.addActionListener(e -> {
            if (!gameover && !stalemate) {
                int option = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to give up?", "Give up", javax.swing.JOptionPane.YES_NO_OPTION);

                if (option == javax.swing.JOptionPane.YES_NO_OPTION) {
                    gameover = true;

                    //Find out who gave up
                    String winner = (currentColor == WHITE) ? "Black wins by resignation" : "White wins by resignation";

                    javax.swing.JOptionPane.showMessageDialog(this, winner, "Game Over", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    repaint();
                }
            }
        });
        //Adding a button only once
        this.setLayout(null); //I need to disable the layout manager for manual placement
        this.add(giveupButton);
    }

    private void button3() {
        javax.swing.JButton newGameButton = new javax.swing.JButton("New game");
        newGameButton.setBounds(850, 750, 200, 50);
        newGameButton.setFont(new Font("Book Antiqua", Font.BOLD, 20));
        newGameButton.setFocusPainted(false);
        newGameButton.setBackground(new Color(200, 200, 200));

        newGameButton.addActionListener(e -> {
            int option = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Do you really want to start a new game?",
                    "New game",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (option == javax.swing.JOptionPane.YES_OPTION) {
                restartGame();
            }
        });

        this.setLayout(null);
        this.add(newGameButton);
    }

    private void button4() {
        javax.swing.JButton compButton = new javax.swing.JButton("Play vs PC");
        compButton.setBounds(850, 650, 200, 50);
        compButton.setFont(new Font("Book Antiqua", Font.BOLD, 20));
        compButton.setFocusPainted(false);
        compButton.setBackground(new Color(200, 200, 200));

        compButton.addActionListener(e -> {
            isVsComputer = true; //zapneme režim proti PC
            promotion = false;
            gameover = false;
            currentColor = WHITE;
            restartGame(); //Resets the game and starts a new one with the vs PC settings

            if (currentColor == computerColor && !gameover && !stalemate && !draw) {
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }
                    makeComputerMove();
                    repaint();
                }).start();
            }
            javax.swing.JOptionPane.showConfirmDialog(this, "You play against computer");
        });
        this.add(compButton);
    }

    private void button5() {
        javax.swing.JButton setUpButton = new javax.swing.JButton("Set position");
        setUpButton.setBounds(850, 300, 200, 55);
        setUpButton.setFont(new Font("Book Antiqua", Font.BOLD, 18));
        setUpButton.setFocusPainted(false);
        setUpButton.setBackground(new Color(200, 200, 200));
        this.setLayout(null);
        this.add(setUpButton);

        setUpButton.addActionListener(e -> {
            ChessPositionSetter setter = new ChessPositionSetter(this);
            setter.setVisible(true);
        });
    }

    private void initKeyBindings(JComponent component) {

        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('s'), "setPosition");

        component.getActionMap().put("setPosition", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button5();
            }
        });
    }

    public void makeComputerMove() {
        if (isGameOver() || isStalemate() || isDraw()) {
            return;
        }

        // Použijeme aktuální šachovnici a hloubku AI
        MiniMax ai = new MiniMax(new BoardEvaluator() {
            @Override
            public int evaluate(Chessboard board, int depth) {
                // jednoduché hodnocení - můžeš upravit podle vlastních pravidel
                int score = 0;
                for (Pieces p : board.getPieces()) {
                    switch (p.type) {
                        case PAWN:
                            score += 10;
                            break;
                        case KNIGHT:
                            score += 30;
                            break;
                        case BISHOP:
                            score += 33;
                            break;
                        case ROOK:
                            score += 50;
                            break;
                        case QUEEN:
                            score += 90;
                            break;
                        case KING:
                            score += 900;
                            break;
                    }
                }
                return score;
            }
        }, 3); // hloubka 3

        // Spustíme MiniMax s aktuální šachovnicí a hloubkou
        Chessboard.Move bestMove = (Chessboard.Move) ai.execute(board, 3);

        if (bestMove != null) {
            // Provedeme tah
            performMove(bestMove.piece, bestMove.targetCol, bestMove.targetRow);
        }
    }

// Jednoduchá ohodnocovací funkce
    int evaluateMove(Pieces p, int targetCol, int targetRow) {
        int score = 0;

        // Zjistíme, jestli na cílovém políčku je soupeřova figura
        for (Pieces target : pieces) {
            if (target.col == targetCol && target.row == targetRow && target.color != currentColor) {
                // Hodnoty figur:
                switch (target.type) {
                    case QUEEN:
                        score += 90;
                        break;
                    case ROOK:
                        score += 50;
                        break;
                    case BISHOP:
                        score += 30;
                        break;
                    case KNIGHT:
                        score += 30;
                        break;
                    case PAWN:
                        score += 10;
                        break;
                    case KING:
                        score += 900;
                        break;
                }
            }
        }

        // Bonus za pozici (např. pěšci jdou dopředu, jezdci do středu)
        if (p.type == Types.PAWN) {
            score += 1;
        }
        if (p.type == Types.KNIGHT && targetCol > 2 && targetCol < 6 && targetRow > 2 && targetRow < 6) {
            score += 2;
        }

        // Pokud hraje černý a je na tahu, invertujeme skóre
        if (currentColor == BLACK) {
            score = -score;
        }

        return score;
    }

// Metoda pro fyzické provedení tahu (bez myši)
    private void performMove(Pieces piece, int col, int row) {
        // 1. Najdeme figuru v reálném listu 'pieces'
        Pieces realPiece = null;
        for (Pieces p : pieces) {
            // Porovnáváme ideálně přes referenci, nebo přes ID, pokud máš. 
            // Zde spoléháme na to, že objekt z 'makeComputerMove' (p) je z listu 'pieces'.
            // Pokud p pochází z kopie, musíme najít originál podle souřadnic/typu.
            if (p.type == piece.type && p.color == piece.color && p.col == piece.col && p.row == piece.row) {
                realPiece = p;
                break;
            }
        }

        // Pokud se nepodařilo najít (pojistka)
        if (realPiece == null) {
            if (pieces.contains(piece)) {
                realPiece = piece;
            } else {
                return;
            }
        }

        activeP = realPiece;

        // 2. Simulujeme "uchopení" a přesun
        activeP.col = col;
        activeP.row = row;
        activeP.x = activeP.getX(col);
        activeP.y = activeP.getY(row);

        // 3. Zkontrolujeme braní figury
        activeP.hittingP = null;
        for (Pieces p : pieces) {
            if (p != activeP && p.col == activeP.col && p.row == activeP.row) {
                activeP.hittingP = p;
                break;
            }
        }

        // Odstranění vyhozené figury
        if (activeP.hittingP != null) {
            pieces.remove(activeP.hittingP);
        }

        // =========================================================
        // 4. OPRAVENÁ LOGIKA PROMĚNY (PROMOTION)
        // =========================================================
        if (activeP.type == Types.PAWN) {
            // Pokud pěšec došel na konec
            if ((activeP.color == BLACK && activeP.row == 7) || (activeP.color == WHITE && activeP.row == 0)) {

                // DŮLEŽITÉ: Ujistíme se, že hra nečeká na menu
                promotion = false;

                // Odstraníme pěšce
                pieces.remove(activeP);

                // Vytvoříme dámu (počítač si bere vždy dámu)
                Pieces queen = new Queen(activeP.color, activeP.col, activeP.row, true);
                pieces.add(queen);

                // Přepneme activeP na novou dámu, aby updatePosition níže fungoval
                activeP = queen;
            }
        }
        // =========================================================

        // 5. Aktualizace seznamů a grafiky
        copyPieces(pieces, simPieces);

        activeP.updatePosition(); // Aktualizuje grafiku
        activeP = null; // Uvolníme výběr

        // Změna hráče a překreslení
        changePlayer();
        repaint();
    }
// Pomocná metoda pro rošádu počítače (pokud ji chcete podporovat)

    private boolean checkCastlingForComputer(Pieces p) {
        // Vaše metoda checkCastling spoléhá na logiku pohybu myší, 
        // pro bota by se musela upravit. Pro základní verzi stačí vrátit false.
        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //BOARD
        board.draw(g2);
        //PIECES
        for (Pieces p : simPieces) {
            p.draw(g2);
        }
        if (activeP != null) {
            if (canMove) {
                if (isIlegalmove(activeP) || opponentCanCaptureKing()) {//So if the king's move is illegal
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Chessboard.SQUARE_SIZE, activeP.row * Chessboard.SQUARE_SIZE, Chessboard.SQUARE_SIZE, Chessboard.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                } else {
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Chessboard.SQUARE_SIZE, activeP.row * Chessboard.SQUARE_SIZE, Chessboard.SQUARE_SIZE, Chessboard.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }
            //Draw the active piece in the end so it won´t be hidden by the board of the colored square
            activeP.draw(g2);
        }
        //STATUS MESSAGE
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if (promotion) {
            g2.drawString("Promote to:", 840, 150);
            for (Pieces pieces : promotedP) { //I scan the promoted pieces list and draw the image one by one
                g2.drawImage(pieces.image, pieces.getX(pieces.col), pieces.getY(pieces.row), Chessboard.SQUARE_SIZE, Chessboard.SQUARE_SIZE, null);
            } //I get X and Y coordinate and the image size
        } else {
            if (currentColor == WHITE) {
                g2.drawString("White's Move", 840, 550);
                if (checkingP != null && checkingP.color == BLACK) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            } else {
                g2.drawString("Black's move", 840, 250);
                if (checkingP != null && checkingP.color == WHITE) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 100);
                    g2.drawString("is in check!", 840, 150);
                }
            }
        }
        if (gameover) {
            String s = "";
            if (currentColor == WHITE) {
                s = "White wins";
            } else {
                s = "Black wins";
            }
            g2.setFont(new Font("Times new Roman", Font.PLAIN, 90));
            g2.setColor(Color.green);
            g2.drawString(s, 240, 420);
        }
        if (stalemate) {
            g2.setFont(new Font("Times new Roman", Font.PLAIN, 90));
            g2.setColor(Color.blue);
            g2.drawString("Stalemate", 240, 420);
        }
        if (draw) {
            g2.setFont(new Font("Times new Roman", Font.PLAIN, 90));
            g2.setColor(Color.blue);
            g2.drawString("Draw", 350, 420);
        }
    }

    private String getBoardState() {
        StringBuilder sb = new StringBuilder();

        //Position all pieces
        for (Pieces p : pieces) {
            sb.append(p.type)
                    .append("_").append(p.color)
                    .append("_").append(p.col)
                    .append("_").append(p.row)
                    .append(";");
        }

        //The player has a turn
        sb.append("turn_").append(currentColor).append(";");

        //Information about castlings   
        boolean whiteKingMoved = false, blackKingMoved = false;
        boolean whiteRookLeftMoved = false, whiteRookRightMoved = false;
        boolean blackRookLeftMoved = false, blackRookRightMoved = false;

        for (Pieces p : pieces) {
            if (p.type == Types.KING) {
                if (p.color == WHITE && p.hasmoved) {
                    whiteKingMoved = true;
                }
                if (p.color == BLACK && p.hasmoved) {
                    blackKingMoved = true;
                }
            }
            if (p.type == Types.ROOK) {
                if (p.color == WHITE) {
                    if (p.col == 0) {
                        whiteRookLeftMoved = p.hasmoved;
                    }
                    if (p.col == 7) {
                        whiteRookRightMoved = p.hasmoved;
                    }
                } else if (p.color == BLACK) {
                    if (p.col == 0) {
                        blackRookLeftMoved = p.hasmoved;
                    }
                    if (p.col == 7) {
                        blackRookRightMoved = p.hasmoved;
                    }
                }
            }
        }

        sb.append("castle:")
                .append(whiteKingMoved).append("_")
                .append(whiteRookLeftMoved).append("_")
                .append(whiteRookRightMoved).append("_")
                .append(blackKingMoved).append("_")
                .append(blackRookLeftMoved).append("_")
                .append(blackRookRightMoved)
                .append(";");

        // --- En passant (dvoukrokový pěšec) ---
        for (Pieces p : pieces) {
            if (p.type == Types.PAWN && p.twoStepped) {
                sb.append("enpassant:")
                        .append(p.col).append("_").append(p.row)
                        .append(";");
                break;
            }
        }

        // --- Výsledek ---
        return sb.toString();
    }

    private void restartGame() {
        // Reset všech herních proměnných
        pieces.clear();
        simPieces.clear();
        promotedP.clear();
        activeP = null;
        checkingP = null;
        castlingP = null;
        gameover = false;
        stalemate = false;
        draw = false;
        promotion = false;
        currentColor = WHITE;
        //Important: you need t 
        if (positionHistory != null) {
            positionHistory.clear();
        }

        // Nastavení výchozích figur
        setUpInitialPosition();
        copyPieces(pieces, simPieces);

        repaint();

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "The game has ended. You started a new game",
                "New game",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static class MoveImpl extends Chessboard.Move {

        public MoveImpl(Pieces p,
                int startRow, int startCol,
                int targetRow, int targetCol,
                Pieces capturedPiece,
                int value) {

            super(
                    p,
                    startRow, startCol,
                    targetRow, targetCol,
                    capturedPiece,
                    value
            );
        }
    }
}
