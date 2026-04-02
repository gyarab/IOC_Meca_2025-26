package Chess;

/**
 * Reprezentuje stav pokusu o provedení tahu na šachovnici.
 * Používá se k ověření, zda byl tah legální a zda nezanechal krále v šachu.
 */
public enum MoveStatus {
       
    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    },
    LEAVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    /**
     * @return true, pokud je tah platný a byl úspěšně proveden.
     */
    public abstract boolean isDone();
}   