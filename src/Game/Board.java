package Game;

import java.util.*;

public class Board {

    private Map<Integer, Vector> placement;
    public int[][] boardNumbers;
    private Vector<Integer> blankPos;
    static public int boardSize = 3;

    public Board(int[][] tiles) {
        placement = new HashMap<>();
        boardNumbers = new int[boardSize][boardSize];
        setValues(tiles);
        createDictionary();
        blankPos = getBlank();
    }

    private void setValues(int[][] tiles) {
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(tiles[i], 0, boardNumbers[i], 0, boardSize);
        }
    }

    public boolean isEquals(Board y) {
        if (y == null)
            return false;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardNumbers[i][j] != y.boardNumbers[i][j])
                    return false;
            }
        }

        return true;
    }

    public List<Board> neighbors(State previousState) {
        if (previousState == null)
            return GetNeighbors(null);
        return GetNeighbors(previousState.getBoardPosition());
    }

    private List<Board> GetNeighbors(Board previousBoard) {
        ArrayList<Board> neighbors = new ArrayList<>();

        //Check up
        if (moveTile("Up")) {
            Board up = new Board(boardNumbers);
            AddElement(previousBoard, neighbors, up); // Add a board to the list if it's not equal to previous board
            moveTile("Down"); // Revert to starting position for further actions
        }

        //Check down
        if (moveTile("Down")) {
            Board down = new Board(boardNumbers);
            AddElement(previousBoard, neighbors, down); // Add a board to the list if it's not equal to previous board
            moveTile("Up"); // Revert to starting position for further actions
        }

        //Check left
        if (moveTile("Left")) {
            Board left = new Board(boardNumbers);
            AddElement(previousBoard, neighbors, left); // Add a board to the list if it's not equal to previous board
            moveTile("Right"); // Revert to starting position for further actions
        }

        //Check right
        if (moveTile("Right")) {
            Board right = new Board(boardNumbers);
            AddElement(previousBoard, neighbors, right); // Add a board to the list if it's not equal to previous board
            moveTile("Left"); // Revert to starting position for further actions
        }

        return neighbors;
    }

    private void AddElement(Board previousBoard, ArrayList<Board> neighbors, Board newBoard) {
        if (previousBoard == null) {
            neighbors.add(newBoard);
            return;
        }

        if (!previousBoard.equals(newBoard))
            neighbors.add(newBoard);
    }

    private Vector<Integer> getBlank() {
        Vector<Integer> blankPos = new Vector<>();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardNumbers[i][j] == 0) {
                    blankPos.add(i);
                    blankPos.add(j);
                    return blankPos;
                }
            }
        }
        return blankPos;
    }

    public int Manhattan() {
        int manhattanNumber = 0;
        int number;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Vector<Integer> numPos = new Vector<>();
                numPos.add(i);
                numPos.add(j);
                number = boardNumbers[i][j];
                manhattanNumber += calculateOffset(numPos, number);
            }
        }
        return manhattanNumber;
    }

    private int calculateOffset(Vector<Integer> numPos, int number) {
        if (number != 0) {
            Vector num = placement.get(number);
            return Math.abs(numPos.get(0) - (int)num.get(0)) +
                    Math.abs(numPos.get(1) - (int)num.get(1));
        }
        return 0;
    }

    private void createDictionary() {
        int number = 1;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Vector<Integer> cord = new Vector<>();
                cord.add(i);
                cord.add(j);
                placement.put(number++, cord);
            }
        }
        placement.remove(boardSize * boardSize);
    }

    public void PrintBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(boardNumbers[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean moveTile(String direction) {
        int row = blankPos.get(0), column = blankPos.get(1);

        //Moving a tile
        switch(direction) {
            case "Up":
                if (row == boardSize - 1)
                    return false;
                boardNumbers[row][column] = boardNumbers[row + 1][column];
                boardNumbers[row + 1][column] = 0;
                blankPos = getBlank();
                break;

            case "Down":
                if (row == 0)
                    return false;
                boardNumbers[row][column] = boardNumbers[row - 1][column];
                boardNumbers[row - 1][column] = 0;
                blankPos = getBlank();
                break;

            case "Left":
                if (column == boardSize - 1)
                    return false;
                boardNumbers[row][column] = boardNumbers[row][column + 1];
                boardNumbers[row][column + 1] = 0;
                blankPos = getBlank();
                break;

            case "Right":
                if (column == 0)
                    return false;
                boardNumbers[row][column] = boardNumbers[row][column - 1];
                boardNumbers[row][column - 1] = 0;
                blankPos = getBlank();
                break;
        }

        return true;
    }
}
