/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

/**
 *
 * @author mecova
 */
import Chess.Pieces.piece.Pieces;

import java.util.List;
import java.util.Map;

public final class SimpleBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_MATE_BONUS = 10000;
    private static final int CHECK_BONUS = 45;
    private static final int CASTLE_BONUS = 35;
    private static final int CASTLING_RIGHTS_BONUS = 15;
    private static final int MOBILITY_MULTIPLIER = 2; //bonus for mobility of the piece
    private static final int ATTACK_MULTIPLIER = 2;
    private static final int TWO_BISHOP_BONUS = 25;
    private static final int CRAMPING_MULTIPLIER = 1; //negative specific bonus for lack of space  
    private static final int NO_BONUS = 0;

    private static final Map<GamePhase, List<ScalarFeature>> PHASE_FACTORS
            = Map.of(
                    GamePhase.OPENING,
                    List.of(
                            Feature.MATERIAL,
                            Feature.CHECK_AND_MATE,
                            Feature.MOBILITY,
                            Feature.PAWN_STRUCTURE,
                            Feature.CASTLE
                    ),
                    GamePhase.MIDDLEGAME, List.of(
                            Feature.MATERIAL,
                            Feature.CHECK_AND_MATE,
                            Feature.MOBILITY,
                            Feature.ATTACKS,
                            Feature.KING_SAFETY
                    ),
                    GamePhase.ENDGAME, List.of(
                            Feature.MATERIAL,
                            Feature.CHECK_AND_MATE,
                            Feature.KING_SAFETY,
                            Feature.CRAMPING,
                            Feature.PIECE_SAFETY
                    ),
                    GamePhase.DEBUG, List.of(
                            Feature.MOBILITY,
                            Feature.CRAMPING,
                            Feature.CHECK_AND_MATE,
                            Feature.ATTACKS,
                            Feature.CASTLE,
                            Feature.MATERIAL,
                            Feature.PAWN_STRUCTURE,
                            Feature.KING_SAFETY
                    )
            );

    private static final SimpleBoardEvaluator INSTANCE = new SimpleBoardEvaluator();

    private SimpleBoardEvaluator() {

    }

    public static SimpleBoardEvaluator get() {
        return INSTANCE;
    }

    @Override
    public int evaluate(final Chessboard board, final int depth) {
        return score(board.whitePlayer(), depth) - score(board.blackPlayer(), depth);
    }

    private static int score(final Player player, final int depth) {

        final GamePhase phase = detectPhase(player.getBoard(), true);
        final List<ScalarFeature> features = PHASE_FACTORS.get(phase);
        int total = 0;
        for (final ScalarFeature feature : features) {
            total += feature.apply(player, depth);
        }
        return total;
    }

    private static GamePhase detectPhase(final Chessboard board, final boolean isDebug) {

        final int totalPieces = board.getAllPieces().size();
        if (isDebug) {
            return GamePhase.DEBUG;
        }
        if (totalPieces > 24) {
            return GamePhase.OPENING;
        } else if (totalPieces > 12) {
            return GamePhase.MIDDLEGAME;
        } else {
            return GamePhase.ENDGAME;
        }
    }

    private static int mobility(final Player player) {
        return MOBILITY_MULTIPLIER * (player.getLegalMoves().size() - player.getOpponent().getLegalMoves().size());
    }

    private static int cramping(final Player player) {
        final int own = player.getLegalMoves().size();
        final int opp = player.getOpponent().getLegalMoves().size();
        if (opp == 0) { //oponent cannot make any good move
            return 100 * CRAMPING_MULTIPLIER;
        }
        final float ratio = (float) own / opp;
        return ratio > 2.0f ? (int) ((ratio - 2.0f) * 5) * CRAMPING_MULTIPLIER : NO_BONUS;
    }

    private static int check_or_checkmate(final Player player, final int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : check(player);
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : NO_BONUS;
    }

    private static int depthBonus(final int depth) {
        return depth == 0 ? 1 : 100 * depth;
    }

    private static int castle(final Player player) {
        if (player.isCastled()) {
            return CASTLE_BONUS;
        } else if (player.hasCastlingRights()) {
            return CASTLING_RIGHTS_BONUS;
        }
        return NO_BONUS;
    }

    private static int attacks(final Player player) {
        int score = 0;
        for (final Move move : player.getLegalMoves()) {
            if (move.isAttack()) { //I need to create a method isAttack() in class Move (package Chess)
                final Pieces mover = move.getMovedPiece(); //I need to create this method in class Chess.Move
                final Pieces victim = move.getAttackedPiece(); //I need to create this method in class Chess.Move
                if (move.getPieceValue() <= victim.getPieceValue()) { //I need to create this method in two classes: Move and Pieces
                    score += 2;
                }
            }
        }
        return score * ATTACK_MULTIPLIER;
    }

    private static int material(final Player player) {
        int score = 0;
        int bishopCount = 0;
        int knightCount = 0;
        int pawnCount = 0;
        for (final int index : player.getActivePieces()) {
            final Pieces p = player.getBoard().getPiece(index, index);
            score += p.getPieceValue() + p.locationBonus(); //I need to create this methods
            switch (p.getPieceType()) {
                case BISHOP:
                    bishopCount++;
                    break;
                case KNIGHT:
                    knightCount++;
                    break;
                case PAWN:
                    pawnCount++;
                    break;
            }
        }
        final int bishopBonus = (bishopCount == 2 ? TWO_BISHOP_BONUS : NO_BONUS);
        final int knightPairPenalty = knightCount >= 2 && pawnCount < 5 ? -10 : 0;
        return score + bishopBonus + knightPairPenalty;
    }

    private static int pawnStructure(final Player player) {
        return PawnStructureAnalyzer.get().pawnStructureScore(player);
    }

    private static int kingSafety(final Player player) {
        final int tropism = KingSafetyAnalyzer.get().calculateKingTropism(player); //I need to create this class in package Chess
        final int safety = KingSafetyAnalyzer.get().gptKingSafety(player);
        return tropism + safety;
    }

    private static int pieceSafety(final Player player) {
        int penalty = 0;
        final Chessboard board = player.getBoard();
        for (final int pos : player.getActivePieces()) {
            final Pieces piece = board.getPiece(pos, pos);
            //Find cheapest attacker
            int cheapestAttacker = Integer.MAX_VALUE;
            boolean isAttacked = false;
            for (final Move move : player.getOpponent().getLegalMoves()) {
                if (move.getDestinationCoordinate() == pos && move.isAttack()) { //I need to create this two methods in class Move in package Chess
                    isAttacked = true;
                    cheapestAttacker = Math.min(cheapestAttacker, move.getMovedPiece().getPieceValue()); //I need to create this method in class Move in package Chess
                }
            }
            if (!isAttacked) {
                continue; // Piece is safe 
            }
            //Count defenders and find cheapest defender
            int defenders = 0;
            int cheapestDefender = Integer.MAX_VALUE;
            for (Move move : player.getLegalMoves()) {
                if (move.getDestinationCoordinate() == pos
                        && move.getMovedPiece().getPiecePosition() != pos) {
                    defenders++;
                    cheapestDefender = Math.min(cheapestDefender, move.getMovedPiece().getPieceValue());
                }
            }

            //Apply penalty based on exchange outcome
            if (defenders == 0) {
                // Hanging piece - full penalty
                penalty += piece.getPieceValue(); // I need to create this method in class Pieces
            } else if (cheapestAttacker < piece.getPieceValue()) {
                // Unfavorable exchange likely
                final int exchangeValue = piece.getPieceValue() - cheapestDefender;
                if (exchangeValue > 0) {
                    penalty += exchangeValue;
                }

            }
        }
        return -penalty;
    }

    public String evaluationDetails(final Chessboard board, final int depth) {
        final StringBuilder sb = new StringBuilder();
        final Feature[] allFeatures = Feature.values();
        // White player details
        for (final Feature feature : allFeatures) {
            sb.append("White ").append(formatFeatureName(feature))
                    .append(" : ").append(feature.apply(board.whitePlayer(), depth))
                    .append("\n");
        }
        sb.append("---------------------\n");
        // Black player details
        for (final Feature feature : allFeatures) {
            sb.append("Black ").append(formatFeatureName(feature))
                    .append(" : ").append(feature.apply(board.blackPlayer(), depth))
                    .append("\n");
        }
        sb.append("\nFinal Score = ").append(evaluate(board, depth));
        return sb.toString();
    }

    private static String formatFeatureName(final Feature feature) {
        return feature.name()
                .replace("_", " ")
                .toLowerCase()
                .replaceFirst("^.", String.valueOf(feature.name().charAt(0)));
    }

    private enum GamePhase {
        OPENING,
        MIDDLEGAME,
        ENDGAME,
        DEBUG //game analysis
    }

    @FunctionalInterface
    interface ScalarFeature {

        int apply(final Player player, int depth);
    }

    enum Feature implements ScalarFeature {
        MOBILITY {
            @Override
            public int apply(final Player p,
                    final int d) {
                return mobility(p);
            }
        },
        CRAMPING {
            @Override
            public int apply(final Player p,
                    final int d) {
                return cramping(p);
            }
        }, CHECK_AND_MATE {
            @Override
            public int apply(final Player p,
                    final int d) {
                return check_or_checkmate(p, d);
            }
        }, ATTACKS {
            @Override
            public int apply(final Player p,
                    final int d) {
                return attacks(p);
            }
        }, CASTLE {
            @Override
            public int apply(final Player p,
                    final int d) {
                return castle(p);
            }
        }, MATERIAL {
            @Override
            public int apply(final Player p,
                    final int d) {
                return material(p);
            }
        }, PAWN_STRUCTURE {
            @Override
            public int apply(final Player p,
                    final int d) {
                return pawnStructure(p);
            }
        }, KING_SAFETY {
            @Override
            public int apply(final Player p,
                    final int d) {
                return kingSafety(p);
            }
        }, PIECE_SAFETY {
            @Override
            public int apply(final Player p,
                    final int d) {
                return pieceSafety(p);
            }
        };
    }    
}
