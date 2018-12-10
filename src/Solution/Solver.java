package Solution;

import Game.Board;
import Game.State;

import java.util.*;

public class Solver {
    public Stack<int[][]> states;
    private int[][] start;

     //3x3 GOAL
//        private int[][] goal = {
//                {1, 2, 3},
//                {4, 5, 6},
//                {7, 8, 0},
//        };

//    // 4x4 GOAL
    private int[][] goal = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };

    private Board startBoard;
    private Board endBoard = new Board(goal);

    public Solver(int[][] start) {
        this.start = start;
        startBoard = new Board(this.start);
        A_Star(startBoard, endBoard);
    }

    public String toString() {
        return " ";
    }

    public void A_Star(Board start, Board goal) {
        PriorityQueue<State> openSet = new PriorityQueue<>();

        State startStage = new State(start, null, 0);
        State goalStage = new State(goal, null, 0);
        openSet.add(startStage);

        Map<int[][], State> score = new HashMap<>();
        score.put(start.boardNumbers, startStage);
        State previous;

        while (!openSet.isEmpty()) {
            State current  = openSet.poll();
            previous = current.getParent();

            if (current.equals(goalStage)) {
                System.out.println(score.size());
                Print(current);
                break;
            }

            List<Board> neighbors = current.getBoardPosition().neighbors(null);

            for (Board item : neighbors) {
                State neighbor = new State(item, current, current.getMovesCount() + 1);

                if (previous != null) {
                    if (!previous.equals(neighbor))
                        openSet.add(neighbor);
                }
                else
                    openSet.add(neighbor);

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
//            letsGo.getBoardPosition().PrintBoard();
//            System.out.println();
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
