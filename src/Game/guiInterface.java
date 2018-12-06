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
import java.util.*;

public class guiInterface extends Application {
    private final Font normalFont = new Font("Arial", 20);
    private final Font textFont = new Font("Arial", 55);
    private final int size = Board.boardSize;
    private TextField[] puzzleBlocks = new TextField[size * size];
    private final int windowSize = 800;
    private int step = 0;
    private int allSteps = 0;
    private int[] colorPos;
    private ArrayList<int[][]> stepsArray = new ArrayList<>();
    private Button solve = createButton("Solve");
    private Button left = createButton("<-");
    private Button right = createButton("->");
    private Button reset = createButton("Reset");
    private Button generate = createButton("Random");
    private GridPane grid = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CreateFields(puzzleBlocks);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("everything");
        root.setPrefSize(windowSize, windowSize);
        grid.setAlignment(Pos.CENTER);
        StackPane test = new StackPane();

        test.getChildren().add(grid);
        AddFields(puzzleBlocks);

        root.setCenter(grid);
        HBox bottomButtons = new HBox();
        HBox topButtons = new HBox();

        SetButtonActions(solve, left, right, reset, generate);

        bottomButtons.setSpacing(30);
        topButtons.setSpacing(30);
        bottomButtons.setAlignment(Pos.CENTER);
        topButtons.setAlignment(Pos.CENTER);

        bottomButtons.getChildren().add(left);
        bottomButtons.getChildren().add(solve);
        bottomButtons.getChildren().add(right);

        topButtons.getChildren().add(reset);
        topButtons.getChildren().add(generate);

        root.setBottom(bottomButtons);
        root.setTop(topButtons);
        BorderPane.setAlignment(reset, Pos.TOP_CENTER);

        Scene scene = new Scene(root, windowSize, windowSize);
        scene.getStylesheets().add("Styles/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        Reset();
        primaryStage.show();
    }

    private void SetButtonActions(Button solve, Button left, Button right, Button reset, Button generate) {
        SolveAction(solve);
        ResetAction(reset);
        MoveLeftAction(left);
        MoveRightAction(right);
        GenerateAction(generate);
    }

    private void GenerateAction(Button generate) {
        generate.setOnAction(event -> {
            Reset();
            String[] numbers = new String[size * size];
            String[] temp = new String[size * size];
            Random rnd = new Random(System.currentTimeMillis());
            int index = 0;

            while (index != size * size) {
                String rndNumber = rnd.nextInt(size * size) + "";
                System.out.println(rndNumber);
                if (!Arrays.asList(temp).contains(rndNumber)) {
                    if (rndNumber.equals("0")) {
                        numbers[index] = "";
                        temp[index++] = String.valueOf(rndNumber);
                    }
                    else {
                        numbers[index] = String.valueOf(rndNumber);
                        temp[index++] = String.valueOf(rndNumber);
                    }
                }
            }

            SetValues(numbers);
        });
    }

    private void SetValues(String[] numbers) {
        int index = 0;
        for (int i = 0; i < size * size; i++) {
            puzzleBlocks[index].setText(numbers[index++]);
        }
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
            Reset();
        });
    }

    private void Reset() {
        step = 0;
        if (!stepsArray.isEmpty())
            stepsArray.clear();
        ClearGrid();
        DisableElements(false);
    }

    private void SolveAction(Button solve) {
        solve.setOnAction(event -> {
            step = 0;
            stepsArray.clear();
            int[][] start = getValues();
            boolean solvable = CheckIfSolvable(start);
            boolean correctFormat = CheckFormat(start);

            System.out.println(correctFormat);

            if (correctFormat) {
                if (solvable) {
                    DisableElements(true);
                    Solver solution = new Solver(start);
                    Stack<int[][]> steps = solution.states;
                    stepsArray = new ArrayList<>();
                    while (!steps.empty()) {
                        stepsArray.add(steps.pop());
                    }
                    allSteps = stepsArray.size();
                    DisplayAlert("Least amount of moves", String.valueOf(allSteps - 1));
                    colorPos = new int[allSteps];
                    GetColorPos(stepsArray);
                    Display();
                }
                else {
                    DisplayAlert("Error", "This puzzle is unsolvable!");
                }
            }
            else {
                DisplayAlert("Error", "Your grid contains duplicating numbers!");
                ClearGrid();
            }
        });
    }

    private void DisableElements(boolean b) {
        if (b) {
            left.setDisable(false);
            right.setDisable(false);
            solve.setDisable(true);
            generate.setDisable(true);
            DisableFields(true);
        }
        else {
            left.setDisable(true);
            right.setDisable(true);
            solve.setDisable(false);
            generate.setDisable(false);
            DisableFields(false);
        }

    }

    private void DisableFields(boolean b) {
        for (int i = 0; i < puzzleBlocks.length; i++) {
            puzzleBlocks[i].setDisable(b);
            puzzleBlocks[i].setStyle("-fx-color: blue;");
        }
    }

    private void DisplayAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
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

            temp.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue) {
                    if (temp.getText().matches("\\d+")) {
                        int number = Integer.parseInt(temp.getText());
                        if (number < 1 || number >= size * size) {
                            temp.setText("");
                        }
                    }
                    else {
                        temp.setText("");
                    }

                }
            });

            puzzleBlocks[index++] = temp;
        }
    }

    private void ClearGrid() {
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                puzzleBlocks[index++].setText("");
            }
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

    private boolean CheckFormat(int[][] start) {
        int[] array = CreateArray(start);
        int index = 0;

        String[] tempArray = new String[size * size];
        for (int item : array) {
            String sItem = String.valueOf(item);
            if (!Arrays.asList(tempArray).contains(sItem))
                tempArray[index++] = sItem;
            else
                return false;
        }
        return true;
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

    private void Display() {
        int[][] currentStep = stepsArray.get(step);

        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                puzzleBlocks[index].setStyle("-fx-text-inner-color: black;");
                if (currentStep[i][j] == 0) {
                    puzzleBlocks[index++].setText("");
                }
                else {
                    puzzleBlocks[index++].setText(
                            String.valueOf(currentStep[i][j])
                    );
                }
            }
        }

        if (step != allSteps - 1) {
            int colorIndex = colorPos[step];
            puzzleBlocks[colorIndex].setStyle("-fx-text-inner-color: red;");
        }

    }

    private void GetColorPos(ArrayList<int[][]> stepsArray) {
        int index = 0;
        int[][] currentStep;

        while (step != allSteps - 1) {
            currentStep = stepsArray.get(step + 1);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (currentStep[i][j] == 0) {
                        colorPos[step] = index;
                    }
                    index++;
                }
            }
            index = 0;
            step++;
        }

        System.out.println(colorPos);
        step = 0;
    }


    private int[][] getValues() {
        int[][] start = new int[size][size];
        int index = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!puzzleBlocks[index].getText().isEmpty()) {
                    System.out.println(index + ": " + puzzleBlocks[index].getText().isEmpty());
                    start[i][j] = Integer.parseInt(puzzleBlocks[index++].getText());
                }
                else {
                    start[i][j] = 0;
                    index++;
                }

            }
        }

        return start;
    }
}
