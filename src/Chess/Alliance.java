/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

/**
 *
 * @author mecova
 */
public enum Alliance {
    WHITE {

        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return false; // default value
        }

        @Override
        public Player choosePlayerByAlliance(final WhitePlayer whitePlayer,
                final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public String toString() {
            return "White";
        }

         
        public int rookBonus(final int position) {
            return BoardUtils.WHITE_ROOK_PREFERRED_COORDINATES[position];
        }

    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return false; // default value
        }

        @Override
        public Player choosePlayerByAlliance(final WhitePlayer whitePlayer,
                final BlackPlayer blackPlayer) {
            return blackPlayer;

        }

          @Override
        public String toString() {
            return "Black";
        }
        
        public int rookBonus(final int position) {
            return BoardUtils.BLACK_ROOK_PREFERRED_COORDINATES[position];
        }
    };

    public abstract int getDirection();

    public abstract int getOppositeDirection();

     public abstract int rookBonus(int position);

    public abstract boolean isWhite();

    public abstract boolean isBlack();

    public abstract boolean isPawnPromotionSquare(int position);

    public abstract Player choosePlayerByAlliance(final WhitePlayer whitePlayer,
            final BlackPlayer blackPlayer);

}
