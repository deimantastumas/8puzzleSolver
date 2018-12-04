package Game;

import Solution.Solver;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Stack;

public class guiInterface extends Application {
    private final Font normalFont = new Font("Arial", 15);
    private final Font textFont = new Font("Arial", 15);
    private final int size = Board.boardSize;
    private TextField[] puzzleBlocks = new TextField[size * size];
    private final int windowSize = 800;
    private int step = 0;
    private int allSteps = 0;
    private ArrayList<int[][]> stepsArray;
    private GridPane grid = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CreateFields(puzzleBlocks);

        BorderPane root = new BorderPane();
        root.setPrefSize(windowSize, windowSize);
        grid.setAlignment(Pos.CENTER);
        StackPane test = new StackPane();

        test.getChildren().add(grid);
        AddFields(puzzleBlocks);

        root.setCenter(grid);
        HBox buttons = new HBox();

        Button solve = createButton("Solve");
        Button left = createButton("<-");
        Button right = createButton("->");
        Button reset = createButton("Reset");

        SetButtonActions(solve, left, right, reset);

        buttons.setSpacing(30);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().add(left);
        buttons.getChildren().add(solve);
        buttons.getChildren().add(right);
        root.setBottom(buttons);
        root.setTop(reset);
        BorderPane.setAlignment(reset, Pos.TOP_CENTER);

        Scene scene = new Scene(root, windowSize, windowSize);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void SetButtonActions(Button solve, Button left, Button right, Button reset) {
        SolveAction(solve);
        ResetAction(reset);
        MoveLeftAction(left);
        MoveRightAction(right);
    }

    private void MoveLeftAction(Button left) {
        left.setOnAction(event -> {
            if (step != 0) {
                step--;
                Display();
            }
        });
    }

    private void MoveRightAction(Button right) {
        right.setOnAction(event -> {
            if (step != allSteps - 1) {
                step++;
                Display();
            }
        });
    }

    private void ResetAction(Button reset) {
        reset.setOnAction(event -> {
            Stage primaryStage1 = new Stage();
            step = 0;
            stepsArray.clear();
            start(primaryStage1);
        });
    }

    private void SolveAction(Button solve) {
        solve.setOnAction(event -> {
            int[][] start = getValues();
            boolean solvable = CheckIfSolvable(start);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (solvable) {
                Solver solution = new Solver(start);
                Stack<int[][]> steps = solution.states;
                stepsArray = new ArrayList<>();
                while (!steps.empty()) {
                    stepsArray.add(steps.pop());
                }
                allSteps = stepsArray.size();
                alert.setTitle("Least amount of moves");
                alert.setHeaderText(String.valueOf(allSteps - 1));
                alert.showAndWait();
                Display();
            }
            else {
                alert.setTitle("Error");
                alert.setHeaderText("This puzzle is unsolvable!");
                alert.showAndWait();
            }
        });
    }

    private Button createButton(String name) {
        Button button = new Button();

        button.setPrefSize(150, 50);
        button.setText(name);
        button.setAlignment(Pos.CENTER);
        button.setFont(normalFont);

        return button;
    }

    private void AddFields(TextField[] puzzleBlocks) {
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid.addRow(i, puzzleBlocks[index++]);
            }
        }
    }

    private void CreateFields(TextField[] puzzleBlocks) {
        int index = 0;
        for (int i = 0; i < size * size; i++) {
            TextField temp = new TextField();
            temp.setPrefSize(500 / size, 500 / size);
            temp.setAlignment(Pos.CENTER);
            temp.setFont(textFont);
            puzzleBlocks[index++] = temp;
        }
    }

    private boolean CheckIfSolvable(int[][] start) {
        int[] linearArray = CreateArray(start);
        int sum = CalculateOffsets(linearArray);
        System.out.println(sum);

        if (sum % 2 == 0)
            return true;
        return false;
    }

    private int CalculateOffsets(int[] linearArray) {
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

    private int[] CreateArray(int[][] start) {
        int index = 0;
        int[] linearArray = new int[8];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int number = start[i][j];
                if (number != 0)
                    linearArray[index++] = number;
            }
        }

        return linearArray;
    }

    private void Display() {
        int[][] currentStep = stepsArray.get(step);
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                puzzleBlocks[index++].setText(
                        String.valueOf(currentStep[i][j])
                );
            }
        }
    }

    private int[][] getValues() {
        int[][] start = new int[3][3];
        int index = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                start[i][j] = Integer.parseInt(puzzleBlocks[index++].getText());
            }
        }

        return start;
    }
}
