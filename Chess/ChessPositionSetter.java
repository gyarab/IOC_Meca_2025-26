/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;

/**
 *
 * @author Admin
 */
import Chess.Pieces.piece.Bishop;
import Chess.Pieces.piece.King;
import Chess.Pieces.piece.Knight;
import Chess.Pieces.piece.Pawn;
import Chess.Pieces.piece.Pieces;
import Chess.Pieces.piece.Queen;
import Chess.Pieces.piece.Rook;
import Chess.Pieces.piece.Types;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ChessPositionSetter extends JDialog {

    private final JButton[][] board = new JButton[8][8];
    private final Chesswindowpanel parent;

    private Types selectedType = Types.PAWN;
    private int selectedColor = Chesswindowpanel.WHITE;

    private boolean whiteCastleK = true;
    private boolean whiteCastleQ = true;
    private boolean blackCastleK = true;
    private boolean blackCastleQ = true;

    private int turnColor = Chesswindowpanel.WHITE;

    private final Map<Types, Integer> maxCounts = new EnumMap<>(Types.class);
    private final Map<Types, Integer> currentCounts = new EnumMap<>(Types.class);
    private final Map<Integer, Integer> kingCount = new HashMap<>();
    private final Map<Integer, Integer> knightCount = new HashMap<>();
    private final Map<Integer, Integer> bishopCount = new HashMap<>();
    private final Map<Integer, Integer> queenCount = new HashMap<>();
    private final Map<Integer, Integer> pawnCount = new HashMap<>(); 
    private final Map<Integer, Integer> rookCount = new HashMap<>();
    
    
    

    private final ArrayList<Pieces> resultPieces = new ArrayList<>();

    public ChessPositionSetter(Chesswindowpanel parent) {
        super((Frame) null, "Set Chess Position", true);
        this.parent = parent;

        setSize(700, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        initLimits();
        initBoard();
        initControls();
    }

    // ================= INIT =================

    private void initLimits() {
        maxCounts.put(Types.KING, 2);
        maxCounts.put(Types.QUEEN, 18); //with promotion max
        maxCounts.put(Types.ROOK, 20);
        maxCounts.put(Types.BISHOP, 20);
        maxCounts.put(Types.KNIGHT, 20);
        maxCounts.put(Types.PAWN, 16);

        kingCount.put(Chesswindowpanel.WHITE, 0);
        kingCount.put(Chesswindowpanel.BLACK, 0);
        knightCount.put(Chesswindowpanel.WHITE, 0);
        knightCount.put(Chesswindowpanel.BLACK, 0);
        bishopCount.put(Chesswindowpanel.WHITE, 0);
        bishopCount.put(Chesswindowpanel.BLACK, 0);
        queenCount.put(Chesswindowpanel.WHITE, 0);
        queenCount.put(Chesswindowpanel.BLACK, 0);
        pawnCount.put(Chesswindowpanel.WHITE, 0);
        pawnCount.put(Chesswindowpanel.BLACK, 0);
        rookCount.put(Chesswindowpanel.WHITE, 0);
        rookCount.put(Chesswindowpanel.BLACK, 0);

        for (Types t : Types.values()) {
            currentCounts.put(t, 0);
        }
    }

    private void initBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton b = new JButton();
                b.setFont(new Font("Arial", Font.PLAIN, 26));
                b.setBackground((r + c) % 2 == 0 ? Color.WHITE : Color.GRAY);
                b.setFocusPainted(false);

                int row = r;
                int col = c;

                b.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            removePiece(row, col);
                        } else {
                            placePiece(row, col);
                        }
                    }
                });

                board[r][c] = b;
                boardPanel.add(b);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    private void initControls() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JComboBox<Types> pieceBox = new JComboBox<>(Types.values());
        pieceBox.addActionListener(e -> selectedType = (Types)pieceBox.getSelectedItem());

        JComboBox<String> colorBox = new JComboBox<>(new String[]{"White", "Black"});
        colorBox.addActionListener(e -> selectedColor = colorBox.getSelectedIndex());

        JCheckBox wk = new JCheckBox("White O-O", true);
        JCheckBox wq = new JCheckBox("White O-O-O", true);
        JCheckBox bk = new JCheckBox("Black O-O", true);
        JCheckBox bq = new JCheckBox("Black O-O-O", true);

        JComboBox<String> turnBox = new JComboBox<>(new String[]{"White to move", "Black to move"});
        turnBox.addActionListener(e -> turnColor = turnBox.getSelectedIndex());

        JButton confirm = new JButton("Confirm position");
        confirm.addActionListener(e -> applyPosition());

        right.add(new JLabel("Piece:"));
        right.add(pieceBox);
        right.add(new JLabel("Color:"));
        right.add(colorBox);
        right.add(new JLabel("Move:"));
        right.add(turnBox);
        right.add(wk);
        right.add(wq);
        right.add(bk);
        right.add(bq);
        right.add(confirm);

        add(right, BorderLayout.EAST);

        wk.addActionListener(e -> whiteCastleK = wk.isSelected());
        wq.addActionListener(e -> whiteCastleQ = wq.isSelected());
        bk.addActionListener(e -> blackCastleK = bk.isSelected());
        bq.addActionListener(e -> blackCastleQ = bq.isSelected());
    }

    // ================= LOGIC =================

    private void placePiece(int row, int col) {
        
    // ===== Pawn position validation =====
        if (selectedType == Types.PAWN) {
            if ((selectedColor == Chesswindowpanel.WHITE && row == 0) ||
                (selectedColor == Chesswindowpanel.BLACK && row == 7)) {
                JOptionPane.showMessageDialog(this,
                "This is not a valid position!");
                return;
            }
        }

        if (!board[row][col].getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This square is already occupied!");
            return;
        }

        if (selectedType == Types.KING && kingCount.get(selectedColor) >= 1) {
            JOptionPane.showMessageDialog(this, "Each side can have only one king!");
            return;
        }

        if (selectedType == Types.KNIGHT && knightCount.get(selectedColor) >= 10) {
            JOptionPane.showMessageDialog(this, "Each side can have at most 10 knights!");
            return;
        }
        
        if (selectedType == Types.BISHOP && bishopCount.get(selectedColor) >= 10) {
            JOptionPane.showMessageDialog(this, "Each side can have at most 10 bishops!");
            return;
        }
        
        if (selectedType == Types.QUEEN && queenCount.get(selectedColor) >= 9) {
            JOptionPane.showMessageDialog(this, "Each side can have at most 9 queens!");
            return;
        }
        
        if (selectedType == Types.PAWN && pawnCount.get(selectedColor) >= 8) {
            JOptionPane.showMessageDialog(this, "Each side can have at most 8 pawns!");
            return;
        }
        
          if (selectedType == Types.ROOK && rookCount.get(selectedColor) >= 10) {
            JOptionPane.showMessageDialog(this, "Each side can have at most 10 rooks!");
            return;
        }
         

        if (currentCounts.get(selectedType) >= maxCounts.get(selectedType)) {
            JOptionPane.showMessageDialog(this, "Max count reached for " + selectedType);
            return;
        }

        board[row][col].setText(getSymbol());
        currentCounts.put(selectedType, currentCounts.get(selectedType) + 1);

        Pieces p;
        switch (selectedType) {
            case KING:   p = new King(selectedColor, col, row); break;
            case QUEEN:  p = new Queen(selectedColor, col, row); break;
            case ROOK:   p = new Rook(selectedColor, col, row); break;
            case BISHOP: p = new Bishop(selectedColor, col, row); break;
            case KNIGHT: p = new Knight(selectedColor, col, row); break;
            default:     p = new Pawn(selectedColor, col, row);
        }

        if (selectedType == Types.KING)
            kingCount.put(selectedColor, kingCount.get(selectedColor) + 1);

        if (selectedType == Types.KNIGHT)
            knightCount.put(selectedColor, knightCount.get(selectedColor) + 1);
        
        if(selectedType == Types.BISHOP)
            bishopCount.put(selectedColor, bishopCount.get(selectedColor) + 1);
        
        if(selectedType == Types.QUEEN)
            queenCount.put(selectedColor, queenCount.get(selectedColor) + 1);
       
        if(selectedType == Types.PAWN)
            pawnCount.put(selectedColor, pawnCount.get(selectedColor) + 1);
        
         if(selectedType == Types.ROOK)
            rookCount.put(selectedColor, rookCount.get(selectedColor) + 1);
        

        resultPieces.add(p);
        board [row][col].putClientProperty("piece", p);
    }

    private void removePiece(int row, int col) {
        JButton b = board[row][col];
        Pieces p = (Pieces) b.getClientProperty("piece");

        if (p == null) 
            return;

        resultPieces.remove(p);
        currentCounts.put(p.type, currentCounts.get(p.type) - 1);

        if (p.type == Types.KING)
            kingCount.put(p.color, kingCount.get(p.color) - 1);

        if (p.type == Types.KNIGHT)
            knightCount.put(p.color, knightCount.get(p.color) - 1);      
        
        if(p.type == Types.BISHOP)
            bishopCount.put(p.color,bishopCount.get(p.color) - 1);
        
        if(p.type == Types.QUEEN)
            queenCount.put(p.color,queenCount.get(p.color) - 1);
        
        if(p.type == Types.PAWN)
            pawnCount.put(p.color,pawnCount.get(p.color) - 1);
        
         if(p.type == Types.ROOK)
            rookCount.put(p.color,rookCount.get(p.color) - 1);

        b.setText("");
        b.putClientProperty("piece", null);
    }

    private String getSymbol() {
        switch (selectedType) {
            case KING:   return selectedColor == 0 ? "♔" : "♚";
            case QUEEN:  return selectedColor == 0 ? "♕" : "♛";
            case ROOK:   return selectedColor == 0 ? "♖" : "♜";
            case BISHOP: return selectedColor == 0 ? "♗" : "♝";
            case KNIGHT: return selectedColor == 0 ? "♘" : "♞";
            case PAWN:
            default:     return selectedColor == 0 ? "♙" : "♟";
        }
    }

    private void applyPosition() {
        if (kingCount.get(Chesswindowpanel.WHITE) != 1 ||
            kingCount.get(Chesswindowpanel.BLACK) != 1) {

            JOptionPane.showMessageDialog(this, 
                    "Each side must have exactly one king!");
            return;
        }

        if (!parent.isValidPosition(
            resultPieces,
            turnColor,
            whiteCastleK, whiteCastleQ,
            blackCastleK, blackCastleQ)) {

            JOptionPane.showMessageDialog(this,
            "This is not a valid position!");
        return;
        }

        parent.pieces.clear();
        parent.simPieces.clear();
        parent.pieces.addAll(resultPieces);
        parent.copyPieces(parent.pieces, parent.simPieces);

        parent.currentColor = turnColor;
        parent.setWhiteCastleK(whiteCastleK);
        parent.setWhiteCastleQ(whiteCastleQ);
        parent.setBlackCastleK(blackCastleK);
        parent.setBlackCastleQ(blackCastleQ);

        // ===== Check validity: king in check but not to move =====
        int sideNotToMove = (turnColor == Chesswindowpanel.WHITE)
        ? Chesswindowpanel.BLACK
        : Chesswindowpanel.WHITE;

        if (parent.isKingInCheck(sideNotToMove)) {
        JOptionPane.showMessageDialog(this,
        "This is not a valid position!");
        return;
}
        
        dispose();
        parent.repaint();
    }
}
