/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

import java.util.*;

/**
 *
 * @author mecova
 */
public enum BoardUtils {
    
    INSTANCE, FIRST_RANK, EIGHT_RANK;
    
    public static final List<Boolean> FIRST_COLUMN = initColumn(0);
    public static final List<Boolean> SECOND_COLUMN = initColumn(1);
    public static final List<Boolean> THIRD_COLUMN = initColumn(2);
    public static final List<Boolean> FOURTH_COLUMN = initColumn(3);
    public static final List<Boolean> FIFTH_COLUMN = initColumn(4);
    public static final List<Boolean> SIXTH_COLUMN = initColumn(5);
    public static final List<Boolean> SEVENTH_COLUMN = initColumn(6);
    public static final List<Boolean> EIGHTH_COLUMN = initColumn(7);
    public static final List<Boolean> FIRST_ROW = initRow(0);
    public static final List<Boolean> SECOND_ROW = initRow(8);
    public static final List<Boolean> THIRD_ROW = initRow(16);
    public static final List<Boolean> FOURTH_ROW = initRow(24);
    public static final List<Boolean> FIFTH_ROW = initRow(32);
    public static final List<Boolean> SIXTH_ROW = initRow(40);
    public static final List<Boolean> SEVENTH_ROW = initRow(48);
    public static final List<Boolean> EIGHTH_ROW = initRow(56);
    
     private static List<Boolean> initColumn(int columnNumber) {
        final Boolean[] column = new Boolean[NUM_TILES];
        Arrays.fill(column, false);
        do {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        } while(columnNumber < NUM_TILES);
        return Collections.unmodifiableList(Arrays.asList((column)));
    }

    private static List<Boolean> initRow(int rowNumber) {
        final Boolean[] row = new Boolean[NUM_TILES];
        Arrays.fill(row, false);
        do {
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % NUM_TILES_PER_ROW != 0);
        return Collections.unmodifiableList(Arrays.asList(row));
    }
    
}

