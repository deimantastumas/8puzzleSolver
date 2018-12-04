package Game;

public class State implements Comparable<State> {
    private Board boardPosition;
    private int movesCount;
    private State parent;
    private int priorityNumber;

    public State(Board boardPosition, State parent, int movesCount) {
        this.boardPosition = boardPosition;
        this.parent = parent;
        this.movesCount = movesCount;
        setPriority();
    }

    private void setPriority() {
        priorityNumber = boardPosition.Manhattan() + movesCount;
    }

    public Board getBoardPosition() {
        return boardPosition;
    }

    public State getParent() {
        return parent;
    }

    public int getMovesCount() {
        return movesCount;
    }

//    public void setParent(State newParent) {
//        parent = newParent;
//    }

//    public void setMovesCount(int moves) {
//        movesCount = moves;
//    }

    public int getPriorityNumber() {
        return priorityNumber;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        State a = (State) o;
        return boardPosition.isEquals(a.boardPosition);
    }

    @Override
    public int compareTo(State o) {
        if (o == null)
            return 1;
        return priorityNumber >= o.priorityNumber ? 1 : -1;
    }
}
