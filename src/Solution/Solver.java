package Solution;

import Game.Board;
import Game.State;

import java.util.*;

public class Solver {
    public Stack<int[][]> states;
    private int[][] start;
    private static int size = 0;
    public static int nodeCount;

    private Board startBoard;
    private Board endBoard;

    public Solver(int[][] start, String dataStructure, int[][] goal) {
        this.start = start;
        startBoard = new Board(this.start, size);
        endBoard = new Board(goal, size);
        Solver.nodeCount = 1;
        ChooseStructure(dataStructure, startBoard, endBoard);
    }

    public static void setSize(int size) {
        Solver.size = size;
    }

    private void ChooseStructure(String dataStructure, Board startBoard, Board endBoard) {
        switch (dataStructure) {
            case "priority_queue":
                A_Star_priorityQueue(startBoard, endBoard);
                break;
            case "array_list":
                A_Star_arrayList(startBoard, endBoard);
                break;
            case "tree_set":
                A_Star_sortedSet(startBoard, endBoard);
                break;
        }
    }

    public String toString() {
        return " ";
    }

    private void A_Star_priorityQueue(Board start, Board goal) {
        PriorityQueue<State> openSet = new PriorityQueue<>();

        State startStage = new State(start, null, 0);
        State goalStage = new State(goal, null, 0);
        openSet.add(startStage);

        State previous;

        while (!openSet.isEmpty()) {
            State current  = openSet.poll();
            previous = current.getParent();

            if (current.equals(goalStage)) {
                Print(current);
                break;
            }

            List<Board> neighbors = current.getBoardPosition().neighbors(null);

            for (Board item : neighbors) {
                State neighbor = new State(item, current, current.getMovesCount() + 1);
                nodeCount++;

                if (previous != null) {
                    if (!previous.equals(neighbor))
                        openSet.add(neighbor);
                }
                else
                    openSet.add(neighbor);
            }
        }
    }
    private void A_Star_arrayList(Board start, Board goal) {
        ArrayList<State> openSet = new ArrayList<>();

        State startStage = new State(start, null, 0);
        State goalStage = new State(goal, null, 0);
        openSet.add(startStage);

        State previous;

        while (!openSet.isEmpty()) {
            State current  = openSet.get(0);
            openSet.remove(0);
            previous = current.getParent();
            System.out.println(openSet.size());

            if (current.equals(goalStage)) {
                Print(current);
                break;
            }

            List<Board> neighbors = current.getBoardPosition().neighbors(null);

            for (Board item : neighbors) {
                State neighbor = new State(item, current, current.getMovesCount() + 1);
                nodeCount++;

                if (previous != null) {
                    if (!previous.equals(neighbor))
                        openSet.add(neighbor);
                }
                else
                    openSet.add(neighbor);
            }
            Collections.sort(openSet);
        }
    }
    private void A_Star_sortedSet(Board start, Board goal) {
        TreeSet<State> openSet = new TreeSet<>();

        State startStage = new State(start, null, 0);
        State goalStage = new State(goal, null, 0);
        openSet.add(startStage);

        State previous;

        while (!openSet.isEmpty()) {
            State current  = openSet.pollFirst();
            previous = current.getParent();

            if (current.equals(goalStage)) {
                Print(current);
                break;
            }

            List<Board> neighbors = current.getBoardPosition().neighbors(null);

            for (Board item : neighbors) {
                State neighbor = new State(item, current, current.getMovesCount() + 1);
                nodeCount++;

                if (previous != null) {
                    if (!previous.equals(neighbor))
                        openSet.add(neighbor);
                }
                else
                    openSet.add(neighbor);
            }
        }
    }

    public static boolean CheckIfSolvable(int[][] start) {
        int[] linearArray = CreateArray(start);
        int sum = CalculateOffsets(linearArray);

        System.out.println(sum);
        boolean inversionOddity = (sum % 2 == 0);

        if (size % 2 == 0) {
            return CheckIfSolvableEven(start, inversionOddity);
        }
        else {
            return inversionOddity;
        }
    }

    public static int[] CreateArray(int[][] start) {
        int index = 0;
        int[] linearArray = new int[size * size - 1];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int number = start[i][j];
                if (number != 0)
                    linearArray[index++] = number;
            }
        }
        return linearArray;
    }

    private static boolean CheckIfSolvableEven(int[][] start, boolean inversionOddity) {
        boolean blankPosOddity = findBlankOddity(start);

        if (inversionOddity) {
            return !blankPosOddity;
        }
        return blankPosOddity;
    }

    private static boolean findBlankOddity(int[][] start) {
        int row = 1;
        for (int i = size - 1; i >= 0; i--) {
            for (int j = 0; j < size; j++) {
                if (start[i][j] == 0) { return row % 2 == 0; }
            }
            row++;
        }
        return false;
    }

    private static int CalculateOffsets(int[] linearArray) {
        int sum = 0;
        for (int i = 0; i < linearArray.length; i++) {
            int current = linearArray[i];
            for (int j = i; j < linearArray.length; j++) {
                int followingNumber = linearArray[j];
                if (followingNumber < current)
                    sum++;
            }
        }
        return sum;
    }

    private void Print(State current) {
        State letsGo = current;
        states = new Stack<>();
        while (letsGo != null) {
            states.add(letsGo.getBoardPosition().boardNumbers);
            letsGo = letsGo.getParent();
        }
    }
}
