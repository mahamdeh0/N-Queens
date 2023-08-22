package com.ai;
import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Random;

public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    private int boardSize;
    private int[] queens;
    private boolean isSolved = false;
    private boolean flag = true;
    private String selectedAlgorithm = "Hill Climbing";

    private JFXTextField sizeTextField;
    private JFXComboBox<String> algorithmComboBox;
    private JFXButton solveButton;
    private JFXButton generateProblemButton;
    private Label iterationLabel;
    private Label temperatureLabel;
    private Label sizeLabel;
    private Label algorithmLabel;
    private Label initialTemperatureLabel;
    private Label coolingRateLabel;
    private Label maxIterationsLabel;
    private Label initialScore;
    private Label finalScore;

    private JFXTextField initialTemperatureTextField;
    private JFXTextField coolingRateTextField;
    private JFXTextField maxIterationsTextField;

    private HBox titleBar;
    private VBox resultBox;

    private GridPane chessboardGrid;
    private StackPane bottomBar;

    private StackPane spacing;
    private StackPane main;

    @Override
    public void start(Stage primaryStage) {
        titleBar = new HBox();
        titleBar.setStyle("-fx-background-color: transparent; -fx-background-radius: 18px 18px 0 0; ; -fx-padding: 5px;");
        titleBar.setAlignment(Pos.CENTER_RIGHT);

        Button closeButton = new Button("\u274C");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;");
        
        closeButton.setOnMouseEntered(event -> closeButton.setStyle("-fx-background-color: #e81123; -fx-text-fill: white; -fx-font-size: 14;"));
        closeButton.setOnMouseExited(event -> closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;"));

        closeButton.setOnAction(event -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.8), new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 0.0))
            );
            timeline.play();
            timeline.setOnFinished(finishedEvent -> {
                primaryStage.close();
                System.exit(0);
            });
        });

        Button minimizeButton = new Button("\u2014");
        minimizeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;");
        minimizeButton.setOnAction(event -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(0.8), new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 0.0))
            );
            timeline.play();
            timeline.setOnFinished(finishedEvent -> {
                primaryStage.setIconified(true);
            });
        });

        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 0.0)),
                        new KeyFrame(Duration.seconds(0.8), new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 1.0))
                );
                timeline.play();
            }

            else {
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 1.0)),
                        new KeyFrame(Duration.seconds(0.8), new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 0.0))
                );
                timeline.play();
            }
        });

        minimizeButton.setOnMouseEntered(event -> minimizeButton.setStyle("-fx-background-color: #353840; -fx-text-fill: white; -fx-font-size: 14;"));
        minimizeButton.setOnMouseExited(event -> minimizeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;"));
        
       

        titleBar.getChildren().addAll(minimizeButton, closeButton);

        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        sizeLabel = new Label("Board Size:");
        sizeTextField = new JFXTextField();
        sizeTextField.setText("4");

        algorithmLabel = new Label("Search Algorithm:");
        algorithmComboBox = new JFXComboBox<>();
        algorithmComboBox.getItems().addAll("Hill Climbing", "Simulated Annealing");
        algorithmComboBox.setValue("Hill Climbing");

        initialTemperatureLabel = new Label("Initial Temperature:");
        initialTemperatureTextField = new JFXTextField();
        initialTemperatureTextField.setText("2000");

        coolingRateLabel = new Label("Cooling Rate:");
        coolingRateTextField = new JFXTextField();
        coolingRateTextField.setText("0.04");

        maxIterationsLabel = new Label("Max Iterations:");
        maxIterationsTextField = new JFXTextField();
        maxIterationsTextField.setText("100000");

        generateProblemButton = new JFXButton("Generate Problem");
        generateProblemButton.getStyleClass().add("button-raised");
        generateProblemButton.setOnAction(e -> {
            generateProblem();
            Timeline fadeInTime = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(resultBox.opacityProperty(), 0.0)),
                new KeyFrame(Duration.seconds(0.8), new KeyValue(resultBox.opacityProperty(), 1.0))
            );
            fadeInTime.play();
        });

        solveButton = new JFXButton("Solve");
        solveButton.getStyleClass().add("button-raised");
        solveButton.setOnAction(e -> {
            solveNQueens();
            Timeline fadeInTime = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(resultBox.opacityProperty(), 0.0)),
                new KeyFrame(Duration.seconds(0.8), new KeyValue(resultBox.opacityProperty(), 1.0))
            );
            fadeInTime.play();
        } );
        
        solveButton.setDisable(true);

        initialScore = new Label("Initial Score: 0");
        initialScore.setVisible(false);
        finalScore = new Label("Final Score: 0");
        finalScore.setVisible(false);

        iterationLabel = new Label("Iteration: 0");
        iterationLabel.setAlignment(Pos.CENTER);
        iterationLabel.setVisible(false);
        GridPane.setColumnSpan(iterationLabel, Integer.MAX_VALUE);
        temperatureLabel = new Label("Temperature: 0");
        temperatureLabel.setAlignment(Pos.CENTER);
        temperatureLabel.setVisible(false);
        GridPane.setColumnSpan(temperatureLabel, Integer.MAX_VALUE);

        chessboardGrid = new GridPane();
        chessboardGrid.setAlignment(Pos.CENTER);

        resultBox = new VBox();
        resultBox.setAlignment(Pos.TOP_CENTER);
        resultBox.setSpacing(10);
        Label gap = new Label();
        gap.setStyle("-fx-font-size: 50;");
        resultBox.getChildren().addAll(gap, chessboardGrid, iterationLabel, temperatureLabel);

        GridPane mainContent = new GridPane();
        mainContent.setHgap(10);
        mainContent.setVgap(10);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(10));
        mainContent.getStyleClass().add("root");

        Label title = new Label("N-Queens Problem");
        title.setStyle("-fx-font-size: 24; -fx-text-fill: white; -fx-font-weight: bold;");

        mainContent.add(title, 0, 0, 2, 1);
        mainContent.add(sizeLabel, 0, 2);
        mainContent.add(sizeTextField, 1, 2);
        mainContent.add(algorithmLabel, 0, 3);
        mainContent.add(algorithmComboBox, 1, 3);
        mainContent.add(generateProblemButton, 0, 4);
        mainContent.add(solveButton, 1, 4);
        mainContent.add(initialScore, 0, 6);
        mainContent.add(finalScore, 0, 7);

        algorithmComboBox.setOnAction(e -> {
            if (algorithmComboBox.getValue() != null && flag) {
                flag = false;
                selectedAlgorithm = algorithmComboBox.getValue();
                algorithmComboBox.getItems().clear();
                algorithmComboBox.setItems(FXCollections.observableArrayList("Hill Climbing", "Simulated Annealing"));
                return;
            }
            
            mainContent.getChildren().clear();
            if (selectedAlgorithm.equalsIgnoreCase("simulated annealing")) {
                mainContent.add(title, 0, 0, 2, 1);
                mainContent.add(sizeLabel, 0, 2);
                mainContent.add(sizeTextField, 1, 2);
                mainContent.add(algorithmLabel, 0, 3);
                mainContent.add(algorithmComboBox, 1, 3);
                mainContent.add(initialTemperatureLabel, 0, 4);
                mainContent.add(initialTemperatureTextField, 1, 4);
                mainContent.add(coolingRateLabel, 0, 5);
                mainContent.add(coolingRateTextField, 1, 5);
                mainContent.add(maxIterationsLabel, 0, 6);
                mainContent.add(maxIterationsTextField, 1, 6);
                mainContent.add(generateProblemButton, 0, 7);
                mainContent.add(solveButton, 1, 7);
                mainContent.add(initialScore, 0, 9);
                mainContent.add(finalScore, 0, 10);
            } else if (selectedAlgorithm.equalsIgnoreCase("hill climbing")) {
                mainContent.add(title, 0, 0, 2, 1);
                mainContent.add(sizeLabel, 0, 2);
                mainContent.add(sizeTextField, 1, 2);
                mainContent.add(algorithmLabel, 0, 3);
                mainContent.add(algorithmComboBox, 1, 3);
                mainContent.add(generateProblemButton, 0, 4);
                mainContent.add(solveButton, 1, 4);
                mainContent.add(initialScore, 0, 6);
                mainContent.add(finalScore, 0, 7);
            }

            algorithmComboBox.setValue(selectedAlgorithm);
            flag = true;            
        });

        spacing = new StackPane();
        spacing.setPrefSize(50, 50);
        spacing.setAlignment(Pos.CENTER);

        HBox box = new HBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(30);
        box.getChildren().addAll(mainContent, spacing, resultBox);

        main = new StackPane();
        main.getChildren().addAll(box);

        bottomBar = new StackPane();
        bottomBar.setAlignment(Pos.BOTTOM_CENTER);

        BorderPane root = new BorderPane();
        root.setTop(titleBar);
        root.setCenter(main);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 1000, 600);
        
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.setTitle("N-Queens Problem");
        primaryStage.show();

        primaryStage.getScene().getRoot().setOpacity(0.0);
        Timeline fadeInTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 0.0)),
                new KeyFrame(Duration.seconds(0.8), new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 1.0))
        );
        fadeInTimeline.play();

    }

    private void generateProblem() {
        finalScore.setVisible(false);
        iterationLabel.setVisible(false);
        temperatureLabel.setVisible(false);

        try {
            solveButton.setDisable(false);

            boardSize = Integer.parseInt(sizeTextField.getText());

            if (boardSize < 4 || boardSize > 8) {
                displayAlert("Invalid board size. Please enter a value between 4 and 8.");
                solveButton.setDisable(true);
                return;
            }

            queens = new int[boardSize];

            Random random = new Random();
            for (int i = 0; i < boardSize; i++) {
                queens[i] = random.nextInt(boardSize);
            }

            initialScore.setText("Initial Score: " + getScore(queens));
            initialScore.setVisible(true);
            displayBoard();

        } catch (NumberFormatException e) {
            displayAlert("Invalid board size. Please enter a valid integer.");
            solveButton.setDisable(true);
            iterationLabel.setVisible(false);
            temperatureLabel.setVisible(false);
        }

    }

    private void solveNQueens() {
        solveButton.setDisable(true);

        String searchAlgorithm = algorithmComboBox.getValue();

        if (searchAlgorithm.equalsIgnoreCase("hill climbing")) {
            hillClimbing();
        }
        else if (searchAlgorithm.equalsIgnoreCase("simulated annealing")) {
            simulatedAnnealing();
        }

        finalScore.setText("Final Score: " + getScore(queens));
        finalScore.setVisible(true);
        iterationLabel.setVisible(true);
        if (algorithmComboBox.getValue().equalsIgnoreCase("simulated annealing")) {
            temperatureLabel.setVisible(true);
        }
        
        displayBoard();

        JFXSnackbar snackbar = new JFXSnackbar(bottomBar);
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(isSolved ? "Solution found!" : "No solution found! Please try again."));
    }

    private void hillClimbing() {
        int[] currentSolution = queens;

        for (int i = 0;; i++) {
        	
            iterationLabel.setText("Iteration: " + (i + 1));

            int currentScore = getScore(currentSolution);

            if (currentScore == 0) {
                queens = currentSolution;
                isSolved = true;
                return;
            }

            int[] neighborSolution = getSuccessor(currentSolution, currentScore);
            int neighborScore = getScore(neighborSolution);

            if (neighborScore < currentScore) {
                currentSolution = neighborSolution;
            }

            if (neighborScore == currentScore) {
                queens = currentSolution;
                isSolved = false;
                return;
            }

        }
    }

    private int[] getSuccessor(int[] solution, int currentScore) {
        int[] bestNeighbor = Arrays.copyOf(solution, boardSize);
        int bestScore = currentScore;

        for (int col = 0; col < boardSize; col++) {
            int currentRow = solution[col];

            for (int row = 0; row < boardSize; row++) {
                if (row != currentRow) {
                    solution[col] = row;
                    int neighborScore = getScore(solution);

                    if (neighborScore < bestScore) {
                        bestNeighbor = Arrays.copyOf(solution, boardSize);
                        bestScore = neighborScore;
                    }
                }
            }

            solution[col] = currentRow;
        }

        return bestNeighbor;
    }

    private void simulatedAnnealing() {
        int maxIterations = Integer.parseInt(maxIterationsTextField.getText());
        double initialTemperature = Double.parseDouble(initialTemperatureTextField.getText());
        double coolingRate = Double.parseDouble(coolingRateTextField.getText());

        int[] currentSolution = queens;
        int[] bestSolution = Arrays.copyOf(currentSolution, boardSize);
        Double temperature = initialTemperature;

        for (int i = 0; (!(getScore(bestSolution) == 0)) && i < maxIterations; i++) {

            int[] neighborSolution = getRandomSuccessor(currentSolution);
            int currentScore = getScore(currentSolution);
            int neighborScore = getScore(neighborSolution);

            
            if (neighborScore < getScore(bestSolution)) {
                bestSolution = neighborSolution;
            }

            
            if (new Random().nextDouble() < Math.exp((currentScore - neighborScore) / temperature)) {
                currentSolution = neighborSolution;
            }

           
            temperature *= 1 - coolingRate;

            iterationLabel.setText("Iteration: " + (i + 1));
            temperatureLabel.setText("Temperature: " + String.format("%.9f", temperature.doubleValue()));
        }

        queens = bestSolution;
        isSolved = getScore(bestSolution) == 0;
    }

    private int[] getRandomSuccessor(int[] solution) {
        int[] neighbor = Arrays.copyOf(solution, boardSize);
        Random random = new Random();
        int randomRow = random.nextInt(boardSize);
        int randomCol = random.nextInt(boardSize);
        neighbor[randomRow] = randomCol;
        return neighbor;
    }

    private int getScore(int[] solution) {
        int score = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = i + 1; j < boardSize; j++) {
                if (solution[i] == solution[j] || Math.abs(i - j) == Math.abs(solution[i] - solution[j])) {
                    score++;
                }
            }
        }
        return score;
    }

    private void displayBoard() {
        chessboardGrid.getChildren().clear();
        
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                StackPane squarePane = new StackPane();
                squarePane.getStyleClass().add((i + j) % 2 == 0 ? "square-white" : "square-green");
                squarePane.setPrefSize(40, 40);
                chessboardGrid.add(squarePane, j, i);

                if (j == queens[i]) {
                    Label queenLabel = new Label("\u265B");
                    queenLabel.getStyleClass().add("queen-label");
                    squarePane.getChildren().add(queenLabel);
                }
            }
        }
    }

    private void displayAlert(String message) {
        titleBar.setStyle("-fx-background-color: #24282f; -fx-background-radius: 18px 18px 0 0; -fx-padding: 5px;");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setStyle("-fx-background-color: white;");
        layout.setPrefSize(350, 200);
        Label heading = new Label("Error");
        heading.getStyleClass().add("dialog-heading");
        layout.setHeading(heading);

        Label body = new Label(message);
        body.getStyleClass().add("dialog-body");
        layout.setBody(body);

        JFXDialog dialog = new JFXDialog();
        dialog.setContent(layout);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.setDialogContainer(main);

        JFXButton closeButton = new JFXButton("OK");
        closeButton.getStyleClass().add("dialog-button");
        closeButton.setOnAction(e -> dialog.close());

        layout.setActions(closeButton);

        dialog.setOnDialogClosed(e -> {
            titleBar.setStyle("-fx-background-color: transparent; -fx-background-radius: 18px 18px 0 0; -fx-padding: 5px;");
        });

        dialog.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}