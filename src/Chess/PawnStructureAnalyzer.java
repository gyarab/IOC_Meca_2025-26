/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

import Chess.Pieces.piece.Pieces;
import Chess.Pieces.piece.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author mecova
 */
public class PawnStructureAnalyzer {

    private static final PawnStructureAnalyzer INSTANCE = new PawnStructureAnalyzer();
    public static final int ISOLATED_PAWN_PENALTY = -10;
    public static final int DOUBLED_PAWN_PENALTY = -10;

    private PawnStructureAnalyzer() {
    }

    public static PawnStructureAnalyzer get() {
        return INSTANCE;
    }

    public int isolatedPawnPenalty(final Player player) {
        return calculateIsolatedPawnPenalty(createPawnColumnTable(player));
    }

    public int doubledPawnPenalty(final Player player) {
        return calculatePawnColumnStack(createPawnColumnTable(player));
    }

    public int pawnStructureScore(final Player player) {
        final int[] playerPawns = createPawnColumnTable(player);
        return calculatePawnColumnStack(playerPawns)
                + calculateIsolatedPawnPenalty(playerPawns);
    }

    private static Collection<Pieces> calculatePlayerPawns(final Player player) {
        final Chessboard board = player.getBoard();
        final int[] activeIndexes = player.getActivePieces();
        final List<Pieces> pawns = new ArrayList();
        for (final int index : activeIndexes) {
            final Pieces piece = board.getPiece(index, index);
            if (piece.getPieceType() == Types.PAWN) {
                pawns.add(piece);
            }
        }
        return pawns;
    }

    private static int calculatePawnColumnStack(final int[] pawnsOnColumnTable) {
        int pawnStackPenalty = 0;
        for (final int pawnStack : pawnsOnColumnTable) {
            if (pawnStack > 1) {
                pawnStackPenalty += pawnStack;
            } //if there are two pawns on the column, add them to the pawnStack
        }
        return pawnStackPenalty * DOUBLED_PAWN_PENALTY;
    }

    private static int calculateIsolatedPawnPenalty(final int[] pawnsOnColumnTable) {
        int numIsolatedPawns = 0;
        if (pawnsOnColumnTable[0] > 0 && pawnsOnColumnTable[1] == 0) {
            numIsolatedPawns += pawnsOnColumnTable[0];
        }
        if (pawnsOnColumnTable[7] > 0 && pawnsOnColumnTable[6] == 0) {
            numIsolatedPawns += pawnsOnColumnTable[7];
        }
        for (int i = 1; i < pawnsOnColumnTable.length - 1; i++) {
            if ((pawnsOnColumnTable[i - 1] == 0 && pawnsOnColumnTable[i + 1] == 0)) {
                numIsolatedPawns += pawnsOnColumnTable[i];
            }
        }
        return numIsolatedPawns * ISOLATED_PAWN_PENALTY;
    }

    private int[] createPawnColumnTable(final Player player) {
        final int[] table = new int[8];
        for (final Pieces playerPawn : calculatePlayerPawns(player)) {
            table[playerPawn.getPiecePosition() % 8]++;
        }
        return table;
    }
}
