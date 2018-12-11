package Game;

import Solution.Solver;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.*;

public class guiInterface extends Application {
    //Fonts
    private final Font normalFont = new Font("Arial", 20);
    private final Font textFont = new Font("Arial", 30);

    //Buttons
    private Button solve = createButton("Solve");
    private Button left = createButton("<-");
    private Button right = createButton("->");
    private Button reset = createButton("Reset");
    private Button options = createButton("Options");
    private Button generate = createButton("Random");

    //Integers
    private final int windowSize = 800;
    private int step = 0;
    private int allSteps = 0;
    private int[] colorPos;
    private int size = 3;
    private int[][] initialGrid = null;

    //Strings
    private String dataStructure = "priority_queue";

    //Other
    private TextField[] puzzleBlocks;
    private ArrayList<int[][]> stepsArray = new ArrayList<>();
    private GridPane grid = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        TextInputDialog dialog = new TextInputDialog("3");
        dialog.setTitle("Grid size");
        dialog.setContentText("Please enter the size of grid:");
        Optional<String> sizeResult = dialog.showAndWait();
        sizeResult.ifPresent(name -> size = Integer.parseInt(String.valueOf(name)));
        CreateFields();
        Reset(true);
        initialGrid = new int[size][size];
        BorderPane root = new BorderPane();
        root.getStyleClass().add("everything");
        root.setPrefSize(windowSize, windowSize);
        grid.setAlignment(Pos.CENTER);
        AddFields();

        root.setCenter(grid);
        HBox bottomButtons = new HBox();
        HBox topButtons = new HBox();

        SetButtonActions(solve, left, right, reset, generate, options);

        bottomButtons.setSpacing(30);
        topButtons.setSpacing(30);
        bottomButtons.setAlignment(Pos.CENTER);
        topButtons.setAlignment(Pos.CENTER);

        bottomButtons.getChildren().add(left);
        bottomButtons.getChildren().add(solve);
        bottomButtons.getChildren().add(right);

        topButtons.getChildren().add(reset);
        topButtons.getChildren().add(options);
        topButtons.getChildren().add(generate);

        root.setBottom(bottomButtons);
        root.setTop(topButtons);
        BorderPane.setAlignment(reset, Pos.TOP_CENTER);

        Scene scene = new Scene(root, windowSize, windowSize);
        scene.getStylesheets().add("Styles/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void SetButtonActions(Button solve, Button left, Button right, Button reset, Button generate, Button options) {
        SolveAction(solve);
        ResetAction(reset);
        MoveLeftAction(left);
        MoveRightAction(right);
        GenerateAction(generate);
        OptionsAction(options);
    }

    private void OptionsAction(Button options) {
        options.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Options");
            alert.setHeaderText("Programmed by: Deimantas Tumas");
            alert.setContentText("Select your options...");
            
            ButtonType selectDataStructure = new ButtonType("Select structure");
            ButtonType cancel = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(selectDataStructure, cancel);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == selectDataStructure) {
                List<String> choices = new ArrayList<>();
                choices.add("Array List");
                choices.add("Priority Queue");
                choices.add("Tree Set");

                ChoiceDialog<String> dialog = new ChoiceDialog<>("Priority Queue", choices);
                dialog.setTitle("Data structure selection");
                dialog.setContentText("Select a data structure:");

                Optional<String> structureResult = dialog.showAndWait();
                structureResult.ifPresent(structure -> SetStructure(structure));
            }
            else {
                alert.close();
            }
        });
    }

    private void SetStructure(String structure) {
        switch (structure) {
            case "Array List":
                dataStructure = "array_list";
                break;
            case "Priority Queue":
                dataStructure = "priority_queue";
                break;
            case "Tree Set":
                dataStructure = "tree_set";
                break;
        }
        FillInitialGrid();
        Reset(false);
    }

    private void GenerateAction(Button generate) {
        generate.setOnAction(event -> {
            Reset(true);
            String[] numbers = new String[size * size];
            String[] temp = new String[size * size];
            Random rnd = new Random(System.currentTimeMillis());
            int index = 0;

            while (index != size * size) {
                String rndNumber = rnd.nextInt(size * size) + "";
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
            Reset(true);
        });
    }

    private void Reset(boolean hardReset) {
        step = 0;
        if (!stepsArray.isEmpty())
            stepsArray.clear();
        initialGrid = hardReset ? new int[size][size] : initialGrid;
        ClearGrid();
        DisableElements(false);
    }

    private void SolveAction(Button solve) {
        solve.setOnAction(event -> {
            final long startTime = System.nanoTime();
            step = 0;
            stepsArray.clear();
            int[][] start = getValues();
            Solver.setSize(size);
            FillInitialGrid();
            boolean solvable = Solver.CheckIfSolvable(start);
            boolean correctFormat = CheckFormat(start);

            if (correctFormat) {
                if (solvable) {
                    DisableElements(true);
                    int[][] goal = setGoal();
                    Solver solution = new Solver(start, dataStructure, goal);
                    final long duration = System.nanoTime() - startTime;
                    Stack<int[][]> steps = solution.states;
                    stepsArray = new ArrayList<>();
                    while (!steps.empty()) {
                        stepsArray.add(steps.pop());
                    }
                    allSteps = stepsArray.size();
                    DisplayAlert("Efficiency", String.valueOf(duration / Math.pow(10,9)), allSteps - 1);
                    colorPos = new int[allSteps];
                    GetColorPos(stepsArray);
                    Display();
                }
                else {
                    DisplayAlert("Error", "This puzzle is unsolvable!", 0);
                }
            }
            else {
                DisplayAlert("Error", "Incorrect format of the grid!", 0);
                ClearGrid();
            }
        });
    }

    private int[][] setGoal() {
        int[][] array = new int[size][size];
        int number = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                array[i][j] = number++;
                if (number == size*size)
                    number = 0;
            }
        }
        return array;
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
        }
    }

    private void DisplayAlert(String title, String headerText, int moves) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        if (title.equals("Efficiency")) {
            alert.setHeaderText("Data structure: " + dataStructure);
            alert.setContentText(
                    "Time spent: " + headerText + " seconds." + "\n" + "Visited nodes: " + Solver.nodeCount +
                            "\n" + "Amount of moves: " + moves
            );
        }
        else alert.setHeaderText(headerText);
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

    private void AddFields() {
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid.addRow(i, puzzleBlocks[index++]);
            }
        }
    }

    private void FillInitialGrid() {
        int value;
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                value = puzzleBlocks[index].getText().equals("") ? 0 : Integer.parseInt(puzzleBlocks[index].getText());
                initialGrid[i][j] = value;
                index++;
            }
        }
    }

    private void CreateFields() {
        puzzleBlocks = new TextField[size * size];
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
        if (initialGrid == null) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    puzzleBlocks[index].setStyle("-fx-text-inner-color: black;");
                    puzzleBlocks[index++].setText("");
                }
            }
        }
        else {
            String value;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    puzzleBlocks[index].setStyle("-fx-text-inner-color: black;");
                    value = initialGrid[i][j] == 0 ? "" : String.valueOf(initialGrid[i][j]);
                    puzzleBlocks[index++].setText(value);
                }
            }
        }

    }

    private boolean CheckFormat(int[][] start) {
        int[] array = Solver.CreateArray(start);
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
        step = 0;
    }


    private int[][] getValues() {
        int[][] start = new int[size][size];
        int index = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!puzzleBlocks[index].getText().isEmpty()) {
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
