package Solution;

import Game.Board;
import Game.State;

import java.util.*;

public class Solver {
    public Stack<int[][]> states;
    private int[][] start;

    private int[][] goal = {
            {1, 2, 3, 4, 5},
            {6, 7, 8, 9, 10},
            {11, 12, 13, 14, 15},
            {16, 17, 18, 19, 20},
            {21, 22, 23, 24, 0}
    };

    private Board startBoard;
    private Board endBoard = new Board(goal);

    public Solver(int[][] start) {
        this.start = start;
        startBoard = new Board(this.start);
        A_Star(startBoard, endBoard);
    }

    public boolean isSolvable() {
        return false;
    }

    public int moves() {
        return 0;
    }

    public String toString() {
        return " ";
    }

    public static void main(String [ ] args) {
//        int[][] start = {
//                {15, 2, 1, 12},
//                {8, 5, 6, 11},
//                {4, 9, 10, 7},
//                {3, 14, 13, 0}
//        };

//        int[][] start = {
//                {14, 0, 8, 12},
//                {10, 11, 9, 13},
//                {2, 6, 5, 1},
//                {3, 7, 4, 15}
//        };

//        int[][] start = {
//                {1, 2, 3, 4, 5},
//                {6, 7, 8, 9, 10},
//                {11, 12, 13, 14, 15},
//                {16, 17, 18, 19, 0},
//                {21, 22, 23, 24, 20}
//        };

        //new Solver(start);
    }

    public void A_Star(Board start, Board goal) {
        PriorityQueue<State> openSet = new PriorityQueue<>();

        State startStage = new State(start, null, 0);
        State goalStage = new State(goal, null, 0);
        openSet.add(startStage);

        Map<int[][], State> score = new HashMap<>();
        score.put(start.boardNumbers, startStage);

        while (!openSet.isEmpty()) {
            State current  = openSet.poll();
            System.out.println(current.getBoardPosition().Manhattan());

            System.out.println();
            if (current.equals(goalStage)) {
                Print(current);
                break;
            }

            List<Board> neighbors = current.getBoardPosition().neighbors(null);

            for (Board item : neighbors) {
                State neighbor = new State(item, current, current.getMovesCount() + 1);

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }

                if (score.containsKey(neighbor.getBoardPosition().boardNumbers)) {
                    UpdateScore(score, neighbor);
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
                else {
                    score.put(neighbor.getBoardPosition().boardNumbers, neighbor);
                }
            }
        }
    }

    private void Print(State current) {
        State letsGo = current;
        states = new Stack<>();
        while (letsGo != null) {
            letsGo.getBoardPosition().PrintBoard();
            System.out.println();
            states.add(letsGo.getBoardPosition().boardNumbers);
            letsGo = letsGo.getParent();
        }
    }

    private void UpdateScore(Map<int[][], State> score, State neighbor) {
        int[][] neighborNumbers = neighbor.getBoardPosition().boardNumbers;
        int neighborScore = neighbor.getPriorityNumber();

        if (score.get(neighborNumbers).getPriorityNumber() > neighborScore) {
            score.put(neighborNumbers, neighbor);
        }
    }
}
