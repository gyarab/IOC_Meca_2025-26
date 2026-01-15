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
import java.util.HashMap;
import java.util.Map;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Admin
 */
public class Chesswindowpanel extends JPanel implements Runnable{
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 810;
    final int FPS = 60;
    private Pieces activeP;
    private boolean running = true;
    private PromotionPanel promotionPanel;
    Chessboard board = new Chessboard();
    Mouse mouse = new Mouse();

    private boolean isPawnPromotion(Pieces p) {
       return p != null && p.type == Types.PAWN &&
            ((p.color == WHITE && p.row == 0) ||
             (p.color == BLACK && p.row == 7));
    }

    private void showPromotionPanel() {
      // "this" je Chesswindowpanel, ale PromotionPanel chce JFrame -> použij hlavní JFrame
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this); 

    // barva nebo číslo tahy
    int someValue = 1; // podle potřeby

    // Consumer: co se stane po výběru promované figury
       Consumer<Types> promotionAction = type -> {
        // nastav nový typ na aktuální pěšce
        activeP.type = type;
        activeP = null; // odznačení
        };

        // správný konstruktor
        PromotionPanel panel = new PromotionPanel(frame, someValue, promotionAction);
        panel.setVisible(true);
    }

    private void finishPromotion(Types chosenType) {
        
        activeP.type = promotionPawn.type;
        remove(promotionPanel);
        promotionPanel = null;
        gameState = GameState.NORMAL;
        activeP = null;
        repaint();
    }
    
    //Enums
    enum GameState {
        NORMAL, PROMOTION
    }
    
    //Threads
    Thread gameThread;
    private Thread thread;
    
    //State of the game
    private GameState gameState = GameState.NORMAL;
    
    //Promotion
    private Pieces promotionPawn; //a pawn waiting for promote
   
    //Pieces
    public static ArrayList<Pieces>pieces = new ArrayList<>();
    public static ArrayList<Pieces>simPieces = new ArrayList<>();
    ArrayList<Pieces>promotedP = new ArrayList<>();
    Pieces checkingP; //I use this to handle the piece that the player is currently holding
    private Pieces lastMovedPiece;
    public static Pieces castlingP;
    
    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE; //game starts with white piecesi
    int computerColor = BLACK; //the computer will play as black
   
    //Number of moves with the same piece
    private int count;
    
    //BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;
    boolean draw;
    boolean isVsComputer = false; //Whether the mode against the PC is enabled
    
    //Castling rights
    private boolean isBoardRotated = false;
    private boolean whiteCastleK = true;
    private boolean whiteCastleQ = true;
    private boolean blackCastleK = true;
    private boolean blackCastleQ = true;
    
    //HashMaps
    private final Map<String, Integer> positionHistory = new HashMap<>();

    //Getter/Setter for castle
    public void setWhiteCastleK(boolean val) { 
        whiteCastleK = val; 
    }
    public void setWhiteCastleQ(boolean val) { 
        whiteCastleQ = val; 
    }
    public void setBlackCastleK(boolean val) { 
        blackCastleK = val; 
    }
    
    public void setBlackCastleQ(boolean val) {
        blackCastleQ = val; 
    }

    public boolean isWhiteCastleK() { return whiteCastleK; }
    public boolean isWhiteCastleQ() { return whiteCastleQ; }
    public boolean isBlackCastleK() { return blackCastleK; }
    public boolean isBlackCastleQ() { return blackCastleQ; }
    
    
    public void setCurrentColor(int color) {
        currentColor = color; 
    }
    
    public int getCurrentColor() {
        return currentColor; 
    }

    public Chesswindowpanel(){
     
     setLayout(null); //absolut position
     thread = new Thread(this);
     thread.start();
     setPreferredSize(new Dimension(WIDTH,HEIGHT));
     setBackground(Color.black);
     addMouseMotionListener(mouse);
     addMouseListener(mouse);
     setUpInitialPosition();
     copyPieces(pieces,simPieces);
     initButtons();
    }
    public void launchGame() {
    gameThread = new Thread(this);
    gameThread.start();
    }
   
    public void testIllegalmove(){
    pieces.add(new Pawn(WHITE,7,6));
    addPieceCorrectly(new King(WHITE,3,7));
    addPieceCorrectly(new King(BLACK,0,3));
    pieces.add(new Bishop(BLACK,1,4));
    pieces.add(new Queen(BLACK,4,5));
    }
    public void testPromoting(){
    pieces.add(new Pawn(WHITE,0,3));
    pieces.add(new Pawn(BLACK,7,5));
    addPieceCorrectly(new King(WHITE,1,0));
    addPieceCorrectly(new King(BLACK,1,6));
    }
    
       public void copyPieces(ArrayList<Pieces> source, ArrayList<Pieces> dest){
        dest.clear();
        dest.addAll(source);
    }
    
 private void setUpInitialPosition(){
         
      //White pieces
      addPieceCorrectly(new Rook(WHITE,0,7));
      addPieceCorrectly(new Rook(WHITE,7,7));
      addPieceCorrectly(new Knight(WHITE,1,7));
      addPieceCorrectly(new Knight(WHITE,6,7));
      addPieceCorrectly(new Bishop(WHITE,2,7));
      addPieceCorrectly(new Bishop(WHITE,5,7));
      addPieceCorrectly(new Queen(WHITE,3,7));
      addPieceCorrectly(new King(WHITE,4,7));
      for(int i=0;i<8;i++) 
          addPieceCorrectly(new Pawn(WHITE,i,6));
       
      //Black pieces
      addPieceCorrectly(new Rook(BLACK,0,0));
      addPieceCorrectly(new Rook(BLACK,7,0));
      addPieceCorrectly(new Knight(BLACK,1,0));
      addPieceCorrectly(new Knight(BLACK,6,0));
      addPieceCorrectly(new Bishop(BLACK,2,0));
      addPieceCorrectly(new Bishop(BLACK,5,0));
      addPieceCorrectly(new Queen(BLACK,3,0));
      addPieceCorrectly(new King(BLACK,4,0)); 
      for(int i=0;i<8;i++) 
         addPieceCorrectly(new Pawn(BLACK,i,1)); 
      
      }
   
     private void addPieceCorrectly(Pieces newPiece) {
        if(newPiece.type == Types.KING) {
            for(Pieces p : pieces)
                if(p.type == Types.KING && p.color == newPiece.color)
                    return; // už je král stejné barvy
        }
        pieces.add(newPiece);
    }
   
   
    @Override
    public void run(){
   
    // Game  LOOP
    double drawInterval = 1000000000/FPS;
    double delta = 0;
    long lastTime = System.nanoTime();
    long currentTime;
   
    while(gameThread != null){
      currentTime = System.nanoTime();
     
      delta += (currentTime - lastTime)/drawInterval;
      lastTime = currentTime;
     
      if(delta >=1){
          update();
          repaint();
          delta--;
      }
       
    }
   
    }
    private void update(){
       
        if(gameState == GameState.PROMOTION){
            return; //the game is stop
        }

        else if(!gameover  && !stalemate){
            ///MOUSE BUTTON PRESSED///
        if(mouse.pressed){
    //If the activeP (active piece) is null, check if you can pick up a piece
            if(activeP == null) {
                //scanning this simPieces Arraylist<>()
                for(Pieces p: simPieces){
                  //If the mouse is on ally piece, pick it up as the activeP
                    if(p.color == currentColor && p.col == mouse.x/Chessboard.SQUARE_SIZE && p.row == mouse.y/Chessboard.SQUARE_SIZE){
                       //setting this piece as active piece
                       activeP = p;
                   }
                }
            }
            //if the activeP is not null -> the player is already holding a piece -> so he can move it
            else {
        //if the player is holding a piece, simulate the move
                simulate();
            }
        }
        ///MOUSE BUTTON RELEASED///
        //So if the player released the mouse button when he is holding a piece
        if(!mouse.pressed){
            if(activeP != null && isPawnPromotion(activeP)){
                showPromotionPanel();
                return;
            }
            if(activeP != null){
                if(validSquare){
                   //MOVE CONFIRMED
                   //Update the piece list in case a piece has been captured and removed -> during the simulation
                    copyPieces(simPieces, pieces); //simPieces as the source and this pieces as the target
                    activeP.updatePosition(); //when the player released the mouse button I call this update method, if this validSquare is true
                    lastMovedPiece = activeP; //important piece of code
                    //the we check, if the player don't move three same moves
                    updateDrawState();
                    
                    if(isPawnPromotion(activeP)){
                       promotionPawn = activeP;
                       gameState = GameState.PROMOTION;
                       showPromotionPanel();
                       return;
                    }
                    
                    if(castlingP != null){
                        castlingP.updatePosition();
                    }
                     if(isKingInCheck(currentColor == WHITE ? BLACK : WHITE)&& Checkmate()){ //I remove this else bracket cause
                            gameover = true;             //otherwise the program would have ended after finding out that the king was in check
                       
                    }
                    else if(Stalemate() && !isKingInCheck()){
                        stalemate = true;
                    }
                    else{ //The game is still going on
                         if(canBePromoted()){
                             if(!isVsComputer || currentColor != computerColor){
                       promotion = true;
                    }
                         }  
                    else{
                      changePlayer();      
                    }
                   }
             
                }
                else { //but if not that means the piece cannot move so we should reset the position
                    //The move is not valid so reset anything
                    copyPieces(pieces, simPieces); //restoring the original list
                    activeP.resetPosition();
                    activeP = null; //player has moved with this piece so I set activeP null
                }
            }
        }  
        }
    }
    private void simulate(){
       
        canMove = false;
        validSquare = false;
       
        //Reset the piece list in every loop
        //This is basically for restoring the removed piece during the simulation
        copyPieces(pieces,simPieces);
        //Reset the castling piece's position
        if(castlingP != null){
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
        if(activeP.canMove(activeP.col,activeP.row)){ //I pass this piece's current col and row as the target square
            canMove = true;
           
            //If hitting a piece, remove it from the board
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            if(isIlegalmove(activeP)== false && opponentCanCaptureKing() == false){
                      validSquare = true;
            }
        }
       
    }
    private boolean isIlegalmove(Pieces king){

        if(king.type != Types.KING){
            return false;
            }        
            for(Pieces p:simPieces){ //If it's king, scan the simPieces and check
                //if there is a piece that is not the king itself and has a different color and can move to the square where king is trying to move now
                if(p.color != king.color && p.canMove(king.col,king.row)){ //If these conditions are matched, this move is illegal
                    return true;
                }
            }
            return false;
    }
    
    private boolean opponentCanCaptureKing(){
       
        Pieces king = getKingByColor(currentColor); //I want to get the current color's king
        for(Pieces p:simPieces){ //I scan the simPieces and cheek if there is a piece that can move to the king's square
            if(p.color != king.color && p.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }
    
    boolean isKingInCheck(){
         
        Pieces king = getKing(true);//I get the opponent's king
       
        if(activeP.canMove(king.col,king.row)){ //I check if the activeP can move to the square where the opponent's king is
            checkingP = activeP; //and if can, this piece is checking the king, so I set it as checking piece
            return true; //because king is in check
        } else {
            checkingP = null; //king is not in check
        }
        return false;
    }
    public boolean isKingInCheck(int kingColor) {

    Pieces king = getKingByColor(kingColor);
    if (king == null) return false;

    for (Pieces p : pieces) {
        if (p.color != kingColor && p.canMove(king.col, king.row)) {
                checkingP = p;
                return true;
            }
        }
    checkingP = null;
    return false;
}

    
    private Pieces getKing(boolean opponent){
        return getKingByColor(opponent?(currentColor==WHITE?BLACK:WHITE):currentColor);
    }
    
    private void copySimPieces(){
        copyPieces(pieces, simPieces);
    }
    
    private void initButtons(){
        button(); button2(); button3(); button4(); button5();
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

    private boolean kingCanMove(Pieces king){ //I check if the king in check can move to a square where it is not under attack
       
         int[] dx = {-1,0,1,-1,1,-1,0,1};
         int[] dy = {-1,-1,-1,0,0,1,1,1};
         
         for(int i=0;i<8;i++)
            if(isValidKingMove(king, dx[i], dy[i])) {
                return true;
            }
         return false;
         }
    private boolean isValidKingMove(Pieces king, int colPlus, int rowPlus){
       
        boolean valid = false;
       //Update the king's position for a second
       king.col += colPlus; //this square
       king.row += rowPlus;
       
       if(king.canMove(king.col,king.row)){ //I check if this king can move to tjis square
           if(king.hittingP != null){ //If he can, check if it's hitting any piece
               simPieces.remove(king.hittingP.getIndex()); //If it is, remove it ffom the list
           }
           if(!isIlegalmove(king)){ //Then I check if the move is illegal or not
               valid = true; //It is a safe spot for the king, if he can remove the piece
           }
       }
       king.resetPosition();
       copyPieces(pieces, simPieces); //I reset the king's position and also I reset the piece list, so before checking other directions, I need to reset
       //the list as well because of simulating moves
       return valid;
    }
    private boolean Stalemate(){
       
        int count = 0;
        //Count the number of pieces of the opponent
        for(Pieces pieces:simPieces){
            if(pieces.color != currentColor){
                count++;
            }
        }
        //If only one piece(the king) is left
        if(count==1){
            if(kingCanMove(getKing(true)) == false){
                return true;  //The king cannot to move any square, so return true
            }
        }
       
       
        return false;
    }
    private void checkCastling(){
       
        if(castlingP != null){
            if(castlingP.col == 0){ //It is the rook on the left
                castlingP.col +=3; //The rook moves by 3 squares
            }
            else if(castlingP.col == 7){ //It is the rook on the right
                castlingP.col -=2; //The rook moves by 2 squares
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
    
    private void changePlayer(){
       
        currentColor = (currentColor == WHITE)? BLACK : WHITE;       
        //Reset black's two stepped status
        for(Pieces p:pieces) //If I am switching to black, then we scan the list and find black pieces and disable all the black pieces's
            //TwoStepped boolean to false
            if(p.color == currentColor)
                p.twoStepped = false;
        
            savePosition();
            activeP = null;
            
            if(isVsComputer && currentColor==computerColor && !gameover && !stalemate && !draw) {
            new Thread(() -> { try{ Thread.sleep(1000); } catch(Exception e){};
                makeComputerMove(); }).start(); 
           }
        }
        
        private void savePosition(){
            String boardState = getBoardState();
            int count = positionHistory.getOrDefault(boardState,0)+ 1;  
            positionHistory.put(boardState,count);
            if(count >= 3 && !stalemate && !gameover){
                draw = true;
                repaint();
            }
        }


        //Saving current position for repeated detection
        public void updateDrawState() {
        //Repeated position
        if (count >= 3 && !stalemate && !gameover) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "The same position occurred three times.\nThe game ends in a draw.",
                "Draw - Threefold Repetition",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
        draw = true;
        repaint();
        return;
        }
        
        // Insufficient material
        if (!stalemate && !gameover) {
            int kingCount = 0;
            int bishopCount = 0;
            int knightCount = 0;
            int pawnCount = 0;
            int rookCount = 0;
            int queenCount = 0;
            int totalPieces = 0;
            
        for (Pieces p : simPieces) {
            totalPieces++;
        switch (p.type) {
            case KING:  
                   kingCount++;
                   break;
            case PAWN:
                   pawnCount++;
                   break;
            case KNIGHT:
                   knightCount++;
                   break;
            case BISHOP:
                   bishopCount++;
                   break;
            case ROOK:
                    rookCount++;
                    break;
            case QUEEN:
                    queenCount++;
                    break;
        }
    }
        
    boolean insufficientMaterial = false;
       
    // First: Only two kings
    if (totalPieces == 2 && kingCount == 2) {
        insufficientMaterial = true;
    }

    // Second: King + bishop vs. king
    if (totalPieces == 3 && kingCount == 2 && bishopCount == 1) {
        insufficientMaterial = true;
    }

    // Third: King + knight vs. king
    if (totalPieces == 3 && kingCount == 2 && knightCount == 1) {
        insufficientMaterial = true;
    }

    // Fourth: King + knight vs. king + knight || King + two knights vs king
    if (totalPieces == 4 && kingCount == 2 && knightCount == 2) {
        insufficientMaterial = true;
    }
    // Fifth: King + knight vs. king + bishop
    if(totalPieces == 4 && kingCount == 2 && knightCount == 1 && bishopCount == 1){
        insufficientMaterial = true;
    }

    if (insufficientMaterial) {
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "Draw – insufficient material for the checkmate.",
            "Draw",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
        draw = true;
        repaint();
    }
   }
        activeP = null;        
        if (isVsComputer && currentColor == computerColor && !gameover && !stalemate && !draw) {
        // Spustíme tah v novém vlákně, aby nezamrzlo okno aplikace
        new Thread(() -> {
            try {
                Thread.sleep(1000); //The small pause to seem that computer is thinking
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            makeComputerMove();
        }).start();
       }
    }
    

    private boolean canBePromoted(){
        
        if(lastMovedPiece == null){
            return false;
        }
        
        if(lastMovedPiece.type != Types.PAWN){
             return false;
        }
        
        if((lastMovedPiece.color == WHITE && lastMovedPiece.row == 0) || 
           (lastMovedPiece.color == BLACK && lastMovedPiece.row == 7)){
           
            
            promotedP.clear();
            promotedP.add(new Rook(currentColor,9,2));
            promotedP.add(new Knight(currentColor,9,3));
            promotedP.add(new Bishop(currentColor,9,4));
            promotedP.add(new Queen(currentColor,9,5));
            return true;
        }
        return false;
    }
    
    private void promoting(){
        if(mouse.pressed){
            for(Pieces p:promotedP){
               //If there is a piece that has the same col and row as the mouse col and row that means the mouse is one of these pieces
               if(lastMovedPiece == null)
                   return;
               
               int col = lastMovedPiece.col;
               int row = lastMovedPiece.row; 
               pieces.remove(lastMovedPiece);
               Pieces newPiece = null;
               
//               if(piece.col == mouse.x/Chessboard.SQUARE_SIZE && piece.row == mouse.y/Chessboard.SQUARE_SIZE){
                    switch(promotionPawn.type){
                        case ROOK: newPiece = new Rook(currentColor, col, row); break;
                        case QUEEN: newPiece = new Queen(currentColor, col, row); break;
                        case BISHOP: newPiece = new Bishop(currentColor, col, row); break;
                        case KNIGHT: newPiece = new Knight(currentColor, col, row); break;
                    }
                    if(newPiece != null){
                        pieces.add(newPiece);
                    }
                    
                    promotion = false;
                    lastMovedPiece = null;
                    changePlayer();
                    }
            }
    }

    private void button(){
      javax.swing.JButton remizButton = new javax.swing.JButton("Offer a draw");
      remizButton.setBounds(850, 130, 200, 50); //Position and size of the button
      remizButton.setFont(new Font("Book Antiqua", Font.BOLD, 20));
      remizButton.setFocusPainted(false);
      remizButton.setBackground(new Color(200, 200, 200));
      
      //After clicking the button
      remizButton.addActionListener(e -> {
        if (!gameover && !stalemate) {
            int option = javax.swing.JOptionPane.showConfirmDialog(this,"Opponent offer a draw. Accept?","Offer a draw",javax.swing.JOptionPane.YES_NO_OPTION);
            
            if(option == javax.swing.JOptionPane.YES_NO_OPTION){
                draw = true;
                repaint();
            }
        }
      });
    
    this.setLayout(null); //I need to disable the layout manager for manual placement
    this.add(remizButton);
    }
    private void button2(){
      javax.swing.JButton giveupButton = new javax.swing.JButton("Give up");
      giveupButton.setBounds(850, 700, 200, 50); //Position and size of the button
      giveupButton.setFont(new Font("Book Antiqua", Font.BOLD, 20));
      giveupButton.setFocusPainted(false);
      giveupButton.setBackground(new Color(200, 200, 200));
      
      //After clicking the button
      giveupButton.addActionListener(e -> {
          if(!gameover && !stalemate){
              int option = javax.swing.JOptionPane.showConfirmDialog(this,"Are you sure you want to give up?","Give up",javax.swing.JOptionPane.YES_NO_OPTION);
              
              if(option == javax.swing.JOptionPane.YES_NO_OPTION){
                  gameover = true;
                  
                  //Find out who gave up
                  String winner = (currentColor == WHITE)? "White wins by resignation" : "Black wins by resignation";
                  
                  javax.swing.JOptionPane.showMessageDialog(this,winner,"Game Over",javax.swing.JOptionPane.INFORMATION_MESSAGE);
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

    // =============================
    // PLAY VS PC – WHITE
    // =============================
    javax.swing.JButton whiteButton = new javax.swing.JButton("Play vs PC (White)");
    whiteButton.setBounds(850, 600, 200, 45);
    whiteButton.setFont(new Font("Book Antiqua", Font.BOLD, 18));
    whiteButton.setFocusPainted(false);
    whiteButton.setBackground(new Color(220, 220, 220));

    whiteButton.addActionListener(e -> {
        startVsComputer(WHITE);
        javax.swing.JOptionPane.showMessageDialog(this, "You play as WHITE");
    });

    // =============================
    // PLAY VS PC – BLACK
    // =============================
    javax.swing.JButton blackButton = new javax.swing.JButton("Play vs PC (Black)");
    blackButton.setBounds(850, 650, 200, 45);
    blackButton.setFont(new Font("Book Antiqua", Font.BOLD, 18));
    blackButton.setFocusPainted(false);
    blackButton.setBackground(new Color(220, 220, 220));

    blackButton.addActionListener(e -> {
        startVsComputer(BLACK);
        javax.swing.JOptionPane.showMessageDialog(this, "You play as BLACK");
    });

    this.setLayout(null);
    this.add(whiteButton);
    this.add(blackButton);
    whiteButton.setEnabled(!isVsComputer);
    blackButton.setEnabled(!isVsComputer);

}
    
    private void startVsComputer(int playerColor){
        isVsComputer = true;
        
        if(playerColor == WHITE){
          computerColor = BLACK;
          currentColor = WHITE;
          isBoardRotated = false; // normal orientation
        } else {
            computerColor = WHITE;
            currentColor = WHITE; // White always starts in chess
            isBoardRotated = true; // rotate board
        }
        
        restartGame();
        
        // If the player chooses BLACK, the computer must move first
        if(playerColor == BLACK){
           new Thread(() -> {
               try {
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               makeComputerMove();
           }).start();
        }
    }

    private void button5() {
     javax.swing.JButton setUpButton = new javax.swing.JButton("Set position");
     setUpButton.setBounds(850,300,200,55);
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

    private Pieces getKingByColor(int color) {
    for (Pieces p : pieces) {
        if (p.type == Types.KING && p.color == color) {
            return p;
        }
    }
    return null;
    }
    
    public boolean isValidPosition(ArrayList<Pieces> newPieces, int sideToMove,
                                   boolean wck, boolean wcq, boolean bck, boolean bcq){ 

    // dočasně uložíme starý stav
    ArrayList<Pieces> backup = new ArrayList<>(pieces);
    int oldTurn = currentColor;

    // nastavíme testovanou pozici
    pieces.clear();
    pieces.addAll(newPieces);
    currentColor = sideToMove;

    // 1. přesně jeden král
    long wk = pieces.stream().filter(p -> p.type == Types.KING && p.color == WHITE).count();
    long bk = pieces.stream().filter(p -> p.type == Types.KING && p.color == BLACK).count();

    if (wk != 1 || bk != 1) {
        restore(backup, oldTurn);
        return false;
    }

    // 2️. král mimo tah NESMÍ být v šachu
    int sideNotToMove = (sideToMove == WHITE) ? BLACK : WHITE;
    if (isKingInCheck(sideNotToMove)) {
        restore(backup, oldTurn);
        return false;
    }

    // (volitelně) králové vedle sebe
    if (isKingsAdjacent()) {
        restore(backup, oldTurn);
        return false;
    }

    restore(backup, oldTurn);
    return true;
}

private void restore(ArrayList<Pieces> backup, int oldTurn) {
    pieces.clear();
    pieces.addAll(backup);
    currentColor = oldTurn;
}

    private boolean isKingsAdjacent() {
              
    Pieces whiteKing = null;
    Pieces blackKing = null;

    for (Pieces p : pieces) {
        if (p.type == Types.KING) {
            if (p.color == WHITE) 
                 whiteKing = p;
            else blackKing = p;
        }
    }

    if (whiteKing == null || blackKing == null) 
        return false;

    int dc = Math.abs(whiteKing.col - blackKing.col);
    int dr = Math.abs(whiteKing.row - blackKing.row);

    return dc <= 1 && dr <= 1;
    } 
    
    // Třída pro uložení nejlepšího tahu
    class Move {
    Pieces piece;
    int targetCol;
    int targetRow;
    int score;

    public Move(Pieces piece, int col, int row, int score) {
        this.piece = piece;
        this.targetCol = col;
        this.targetRow = row;
        this.score = score;
    }
}

public void makeComputerMove() {
    ArrayList<Move> validMoves = new ArrayList<>();

    // 1. Najít všechny možné tahy pro barvu počítače
    for (Pieces p : pieces) {
        if (p.color == computerColor) {
            // Projdeme celou šachovnici (všechny čtverce)
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    // Resetujeme simPieces pro testování
                    copyPieces(pieces, simPieces); 
                    
                    // Zkontrolujeme, zda figura může na dané pole
                    if (p.canMove(c, r)){
                        boolean moveIsSafe = true;
                        
                        // Simulace tahu pro kontrolu legality (nesmí vystavit krále šachu)
                        // Musíme dočasně nastavit souřadnice
                        int pceCol = p.col;
                        int pceRow = p.row;
                        p.col = c;
                        p.row = r;

                        if (isIlegalmove(p) || opponentCanCaptureKing()) {
                            moveIsSafe = false;
                        }

                        // Vrátíme figuru zpět
                        p.col = pceCol;
                        p.row = pceRow;

                        if (moveIsSafe) {
                            int score = 0;
                            // Pokud něco vyhazujeme, zvýšíme skóre
                            for(Pieces enemy : pieces) {
                                if(enemy.color != computerColor && enemy.col == c && enemy.row == r) {
                                    score = 10; // Bere figuru
                                    break;
                                }
                            }
                            validMoves.add(new Move(p, c, r, score));
                        }
                    }
                }
            }
        }
    }

    if (validMoves.isEmpty()) return; // Žádný tah (mat nebo pat)

    // 2. Vybrat nejlepší tah
    // Seřadíme tahy podle skóre (od nejvyššího)
    validMoves.sort((m1, m2) -> Integer.compare(m2.score, m1.score));

    // Vybereme nejlepší tah. Pokud je jich víc se stejným skóre, vybereme náhodný z nich.
    Move bestMove = validMoves.get(0);
    
    // Malá logika pro náhodnost mezi stejně dobrými tahy
    ArrayList<Move> bestMoves = new ArrayList<>();
    for(Move m : validMoves) {
        if(m.score == bestMove.score) 
            bestMoves.add(m);
    }
    Move finalMove = bestMoves.get((int)(Math.random() * bestMoves.size()));

    // 3. Provést tah
    final Move moveToDo = finalMove; // Proměnná pro lambda výraz
    javax.swing.SwingUtilities.invokeLater(() -> {
        performMove(moveToDo.piece, moveToDo.targetCol, moveToDo.targetRow);
    });
}

// Jednoduchá ohodnocovací funkce
private int evaluateMove(Pieces p, int targetCol, int targetRow) {
    int score = 0;
    
    // Zjistíme, jestli na cílovém políčku je soupeřova figura
    for (Pieces target : pieces) {
        if (target.col == targetCol && target.row == targetRow && target.color != computerColor) {
            // Hodnoty figur:
            switch(target.type) {
                case QUEEN: score += 90; break;
                case ROOK: score += 50; break;
                case BISHOP: score += 30; break;
                case KNIGHT: score += 30; break;
                case PAWN: score += 10; break;
                case KING: score += 900; break;
            }
        }
    }
    
    // Bonus za pozici (např. pěšci jdou dopředu, jezdci do středu)
    if(p.type == Types.PAWN) score += 1;
    if(p.type == Types.KNIGHT && targetCol > 2 && targetCol < 6 && targetRow > 2 && targetRow < 6) score += 2;

    return score;
}

// Metoda pro fyzické provedení tahu (bez myši)
private void performMove(Pieces piece, int col, int row) {
    // 1. Najdeme figuru v reálném listu 'pieces'
    Pieces realPiece = null;
    for(Pieces p : pieces) {
        // Porovnáváme ideálně přes referenci, nebo přes ID, pokud máš. 
        // Zde spoléháme na to, že objekt z 'makeComputerMove' (p) je z listu 'pieces'.
        // Pokud p pochází z kopie, musíme najít originál podle souřadnic/typu.
        if(p.type == piece.type && p.color == piece.color && p.col == piece.col && p.row == piece.row) {
            realPiece = p;
            break;
        }
    }
    
    // Pokud se nepodařilo najít (pojistka)
    if(realPiece == null) {
        if (pieces.contains(piece)) realPiece = piece;
        else return;
    }

    activeP = realPiece;
    
    // 2. Simulujeme "uchopení" a přesun
    activeP.col = col;
    activeP.row = row;
    activeP.x = activeP.getX(col);
    activeP.y = activeP.getY(row);
    
    // 3. Zkontrolujeme braní figury
    activeP.hittingP = null;
    for(Pieces p : pieces) {
        if(p != activeP && p.col == activeP.col && p.row == activeP.row) {
            activeP.hittingP = p;
            break;
        }
    }
    
    // Odstranění vyhozené figury
    if(activeP.hittingP != null) {
        pieces.remove(activeP.hittingP);
    }

    // =========================================================
    // 4. OPRAVENÁ LOGIKA PROMĚNY (PROMOTION)
    // =========================================================
    if (isPawnPromotion(activeP)) {
    finishPromotion(Types.QUEEN); // PC always queen
    return;
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
    public void paintComponent(Graphics g){
       super.paintComponent(g);
       Graphics2D g2d = (Graphics2D)g.create();
       
       if(isBoardRotated){
//          g2d.rotate(Math.PI, getWidth() / 2, getHeight() / 2);
       }
       //BOARD
       board.draw(g2d);

       //PIECES
       for(Pieces p: pieces){
          p.draw(g2d);
       }    
       if(activeP != null){
           if(canMove){
               if(isIlegalmove(activeP) || opponentCanCaptureKing()){//So if the king's move is illegal
                  g2d.setColor(Color.red);
                  g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
                  g2d.fillRect(activeP.col*Chessboard.SQUARE_SIZE,activeP.row*Chessboard.SQUARE_SIZE,Chessboard.SQUARE_SIZE,Chessboard.SQUARE_SIZE);
                  g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));        
               }
               else{
                   g2d.setColor(Color.white);
              g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
              g2d.fillRect(activeP.col*Chessboard.SQUARE_SIZE,activeP.row*Chessboard.SQUARE_SIZE,Chessboard.SQUARE_SIZE,Chessboard.SQUARE_SIZE);
              g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
           }
           }
           //Draw the active piece in the end so it won´t be hidden by the board of the colored square
           activeP.draw(g2d);
       }
       //STATUS MESSAGE
       g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
       g2d.setFont(new Font("Book Antiqua",Font.PLAIN, 40));
       g2d.setColor(Color.white);
       
       if(promotion){
           g2d.drawString("Promote to:",840,150);
           for(Pieces pieces:promotedP){ //I scan the promoted pieces list and draw the image one by one
               g2d.drawImage(pieces.image,pieces.getX(pieces.col),pieces.getY(pieces.row),Chessboard.SQUARE_SIZE,Chessboard.SQUARE_SIZE, null);
           } //I get X and Y coordinate and the image size
       }
       else{
          if(currentColor == WHITE){
           g2d.drawString("White's Move",840,550);
           if(checkingP != null && checkingP.color == BLACK ){
               g2d.setColor(Color.red);
               g2d.drawString("The King",840,100);
               g2d.drawString("is in check!",840,150);
           }
       }
          else{
           g2d.drawString("Black's move",840,250);
           if(checkingP != null && checkingP.color == WHITE){
           g2d.setColor(Color.red);
           g2d.drawString("The King",840,600);
           g2d.drawString("is in check!",840,650);
           }
           }  
       }
       if(gameover){
           String s ="";
           if(currentColor == WHITE){
               s = "White wins";
           }
           else{
            s = "Black wins";  
           }
           g2d.setFont(new Font("Times new Roman",Font.PLAIN, 90));
           g2d.setColor(Color.green);
           g2d.drawString(s,240,420);
       }
       if(stalemate){
           g2d.setFont(new Font("Times new Roman",Font.PLAIN, 90));
           g2d.setColor(Color.blue);
           g2d.drawString("Stalemate",240,420);
       }
       if(draw){
           g2d.setFont(new Font("Times new Roman",Font.PLAIN, 90));
           g2d.setColor(Color.blue);
           g2d.drawString("Draw",350,420);   
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
            if (p.color == WHITE && p.hasmoved) whiteKingMoved = true;
            if (p.color == BLACK && p.hasmoved) blackKingMoved = true;
        }
        if (p.type == Types.ROOK) {
            if (p.color == WHITE) {
                if (p.col == 0) whiteRookLeftMoved = p.hasmoved;
                if (p.col == 7) whiteRookRightMoved = p.hasmoved;
            } else if (p.color == BLACK) {
                if (p.col == 0) blackRookLeftMoved = p.hasmoved;
                if (p.col == 7) blackRookRightMoved = p.hasmoved;
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
    if(positionHistory != null){
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
}
    
