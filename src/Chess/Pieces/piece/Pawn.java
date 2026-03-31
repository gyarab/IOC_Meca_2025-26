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
public class Pawn extends Piece {
    
    public boolean hasMoved = hasmoved;
   
    
       // GUI konstruktor
    public Pawn(int color, int col, int row, boolean isGui) {
        super(color, col, row, isGui);
        this.type = Types.PAWN;
        this.piecePosition = row * 8 + col;
        this.pceCol = col;
        this.pceRow = row;
        this.hasMoved = hasmoved;

        if(color == Chesswindowpanel.WHITE){
            image = getImage("/Chess/Pieces/piece/w-pawn");
        } else {
            image = getImage("/Chess/Pieces/piece/b-pawn");
        }
    }
    
     // ENGINE delegátor, pokud chceš convenience overload
    public Pawn(final Alliance alliance, final int piecePosition) {
        this(alliance, piecePosition, false);
    }

    // ENGINE konstruktor
    public Pawn(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
        super(alliance, piecePosition,isFirstMove);

        type = Types.PAWN;

        if(alliance == Alliance.WHITE){
            image = getImage("/Chess/Pieces/piece/w-pawn");
        } else {
            image = getImage("/Chess/Pieces/piece/b-pawn");
        }
    }
   
    @Override
    public boolean canMove(int targetCol, int targetRow) {

        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            //Define the move value based on its color
            int moveValue;
            if (hasMoved == false) {
                if (color == Chesswindowpanel.WHITE) {
                    moveValue = -1; //for white pawn(up)
                } else {
                    moveValue = 1; //for black pawn(down)
                }
                //Check the hitting piece
                hittingP = getHittingP(targetCol, targetRow);

                //1 square movement
                if (targetCol == pceCol && targetRow == pceRow + moveValue && hittingP == null) {
                    return true;
                }
                //2 square movement
                if (!hasMoved && targetCol == pceCol && targetRow == pceRow + moveValue * 2 && hittingP == null) {
                    if (pieceIsOnStraightLine(targetCol, targetRow) == false) {
                        return true;
                    }
                }
                //1 square movement when the pawn has moved
                if (targetCol == pceCol && row == pceRow + moveValue && hittingP == null) {
                    return true;
                }

                //Diagonal movement & Capture(if a piece is on a square diagonally in front of it)
                if (Math.abs(targetCol - pceCol) == 1
                        && targetRow == pceRow + moveValue && hittingP != null && hittingP.color != color) {
                    return true;//col difference needs to be 1 and targetRow needs to be pceRow+1 or pceRow-1
                }
                //En Passant
                if (Math.abs(targetCol - pceCol) == 1 && targetRow == pceRow + moveValue) {
                    for (Piece pieces : Chesswindowpanel.simPieces) {
                        if (pieces.col == targetCol && pieces.row == pceRow && pieces.twoStepped == true) {
                            hittingP = pieces; //If there is a piece that it's col is equal to the targetCol
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }   

    @Override
    public int locationBonus() {
         return this.pieceAlliance.pawnBonus(this.piecePosition);
    }

    @Override
    public Piece getMovedPiece(Move move) {
        return new Pawn(this.pieceAlliance,move.getDestinationCoordinate(),false);
    }
    
    @Override
    public Collection<Move> calculateLegalMoves(Chessboard board) {

        List<Move> legalMoves = new ArrayList<>();

        // Směr pohybu: Bílý nahoru (-1), Černý dolů (+1)
        int direction = (color == Chesswindowpanel.WHITE) ? -1 : 1;

        int targetRow = pceRow + direction;
        int targetCol = pceCol;

        // 1. POHYB O 1 A 2 POLÍČKA DOPŘEDU
        if (targetRow >= 0 && targetRow <= 7) {
            int targetIndex = targetRow * 8 + targetCol; // Převod na 1D index

            // Pěšec se může posunout dopředu JEN POKUD je políčko prázdné
            if (board.getPiece(targetIndex) == null) {
                // Přidání běžného tahu (uprav na instanci, kterou engine reálně bere, např. Move.MajorMove)
                legalMoves.add(new Move(board, this, targetCol, targetRow));

                // 2. Pohyb o 2 políčka ze startovní čáry 
                // Zkontrolujeme, zda stojí na výchozí řadě (bílý na řadě 6, černý na řadě 1)
                boolean isStartingSquare = (color == Chesswindowpanel.WHITE && pceRow == 6)
                        || (color != Chesswindowpanel.WHITE && pceRow == 1);

                if (isStartingSquare) {
                    int doubleStepRow = pceRow + (direction * 2);
                    int doubleStepIndex = doubleStepRow * 8 + targetCol;

                    // Podmínkou je, že I DRUHÉ políčko musí být prázdné (pěšec nemůže přeskočit figuru)
                    if (board.getPiece(doubleStepIndex) == null) {
                        legalMoves.add(new Move(board, this, targetCol, doubleStepRow));
                    }
                }
            }
        }

        // 3. BRANÍ ŠIKMO (útoky vlevo a vpravo)
        int[] captureCols = {pceCol - 1, pceCol + 1}; // Oba možné sloupce pro útok

        for (int cCol : captureCols) {
            // Kontrola, že nevyjdeme ven z šachovnice
            if (cCol >= 0 && cCol <= 7 && targetRow >= 0 && targetRow <= 7) {
                int captureIndex = targetRow * 8 + cCol;
                Piece targetPiece = board.getPiece(captureIndex);

                // Pokud na políčku stojí nějaká figura a má OPAČNOU barvu
                if (targetPiece != null && targetPiece.color != this.color) {
                    legalMoves.add(new Move.MajorAttackMove(board, this, captureIndex, targetPiece));
                }

                // --- 4. EN PASSANT (Braní mimochodem) ---
                // Minimax potřebuje vědět, jestli soupeř v předchozím tahu pohnul pěšcem o 2 políčka.
                // K tomu potřebuješ mít v 'board' uložený poslední zahraný tah (např. board.getTransitionMove()).
                // Pokud to tvůj engine zatím neumí sledovat, doporučuji nechat tuto část zakomentovanou.
                /*
                else if (targetPiece == null) {
                    Move lastMove = board.getLastTransitionMove(); 
                    if (lastMove != null && lastMove.getMovedPiece() instanceof Pawn) {
                        Piece lastMovedPawn = lastMove.getMovedPiece();
                        // Pokud stojí vedle mě a pohnul se o 2 políčka (můžeš kontrolovat i přes lastMovedPawn.twoStepped)
                        if (lastMovedPawn.pceRow == this.pceRow && lastMovedPawn.pceCol == cCol) {
                            // Přidej speciální En Passant tah
                        }
                    }
                }
                 */
            }
        }

        return legalMoves;
    }
 }