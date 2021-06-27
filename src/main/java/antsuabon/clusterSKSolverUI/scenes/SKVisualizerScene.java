package antsuabon.clusterSKSolverUI.scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import antsuabon.clusterSKSolverUI.utils.LogLoader;
import antsuabon.clusterSKSolverUI.utils.SudokuLoader;
import antsuabon.clusterSKSolverUI.utils.SudokuProblem;
import antsuabon.clusterSKSolverUI.utils.SudokuView;
import antsuabon.clusterSKSolverUI.utils.LogLoader.LogEntry;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class SKVisualizerScene {

	public static void start(Stage primaryStage, String sudokuProblemPath, String logPath) {
		SudokuProblem sudoku = SudokuLoader.loadSudoku(sudokuProblemPath);
		List<LogEntry> logEntries = LogLoader.loadLogFile(logPath);

		List<Color> colorsByBlock = SudokuView.generateNColors(sudoku.getBlocks().keySet().size());
		SudokuView sudokuView = SudokuView.getSudokuGrid(sudoku, colorsByBlock);

		Text stepLabelText = new Text("Iteración: ");
		stepLabelText.setStyle("-fx-font-weight: bold;");
		Text stepText = new Text(sudokuView.getStep().toString());

		Text depthLabelText = new Text("Profundidad: ");
		depthLabelText.setStyle("-fx-font-weight: bold;");
		Text depthText = new Text(logEntries.get(sudokuView.getStep()).getDepth().toString());

		Text timeLabelText = new Text("Tiempo trancurrido: ");
		timeLabelText.setStyle("-fx-font-weight: bold;");
		Text timeText = new Text(sudokuView.getStep().toString() + " s");
		
		Button backwardButton = new Button("Retroceder");
		backwardButton.setOnAction((event) -> {
			if (sudokuView.getStep() - 1 >= 0) {
				sudokuView.setStep(sudokuView.getStep() - 1);
				SudokuView.updateSudokuGrid(sudokuView, logEntries.get(sudokuView.getStep()).getState());
				updateInfoGrid(sudokuView, logEntries, stepText, depthText, timeText);
			}
		});
		GridPane.setHalignment(backwardButton, HPos.CENTER);

		Button forwardButton = new Button("Avanzar");
		forwardButton.setOnAction((event) -> {
			if (sudokuView.getStep() + 1 < logEntries.size()) {
				sudokuView.setStep(sudokuView.getStep() + 1);
				SudokuView.moveForwardSudokuGrid(sudokuView, logEntries.get(sudokuView.getStep()).getState());
				updateInfoGrid(sudokuView, logEntries, stepText, depthText, timeText);
			}
		});
		GridPane.setHalignment(forwardButton, HPos.CENTER);

		Button initialStateButton = new Button("Estado inicial");
		initialStateButton.setOnAction((event) -> {
			sudokuView.setStep(0);
			SudokuView.updateSudokuGrid(sudokuView, logEntries.get(sudokuView.getStep()).getState());
			updateInfoGrid(sudokuView, logEntries, stepText, depthText, timeText);
		});
		GridPane.setHalignment(initialStateButton, HPos.CENTER);

		Button finalStateButton = new Button("Estado final");
		finalStateButton.setOnAction((event) -> {
			Optional<LogEntry> logOptional = logEntries.stream().filter(LogEntry::getSolved).findFirst();

			sudokuView.setStep(logEntries.size() - 1);
			SudokuView.updateSudokuGrid(sudokuView, logEntries.get(sudokuView.getStep()).getState());
			updateInfoGrid(sudokuView, logEntries, stepText, depthText, timeText);
		});
		GridPane.setHalignment(finalStateButton, HPos.CENTER);

		TextField speedTextField = new TextField("4");

		Button playButton = new Button("Empezar");
		Button stopButton = new Button("Parar");
		stopButton.setDisable(true);

		playButton.setOnAction((event) -> {

			Integer speed = Integer.valueOf(speedTextField.getText());
			Float periodInSeconds = 1.0f / speed;

			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(new TimerTask() {
				
				@Override
				public void run() {
					if (!sudokuView.getStep().equals(logEntries.size() - 1)) {
						sudokuView.setStep(sudokuView.getStep() + 1);
						SudokuView.moveForwardSudokuGrid(sudokuView, logEntries.get(sudokuView.getStep()).getState());
						updateInfoGrid(sudokuView, logEntries, stepText, depthText, timeText);
					} else {
						scheduler.shutdown();
						playButton.setDisable(false);
						stopButton.setDisable(true);
					}
				}

			}, 0, Float.valueOf(periodInSeconds * 1000).longValue(), TimeUnit.MILLISECONDS);
			
			stopButton.setOnAction((eventStop) -> {
				scheduler.execute(() -> {
					handle.cancel(true);
					playButton.setDisable(false);
					stopButton.setDisable(true);
				});

			});
			playButton.setDisable(true);
			stopButton.setDisable(false);

		});

		Text titleText = new Text();
		titleText.setText("Visualizador secuencial del algoritmo SKSolver");
		titleText.setStyle("-fx-border-color: black; -fx-font-size: 20; -fx-font-weight: bold;");
		BorderPane.setAlignment(titleText, Pos.TOP_CENTER);
		
		Text autoControlLabel = new Text("Avance automático (iteraciones/s): ");

		GridPane stateMenu = new GridPane();
		stateMenu.setPadding(new Insets(20));
		stateMenu.setHgap(5);
		stateMenu.setVgap(5);

		stateMenu.add(backwardButton, 1, 1);
		stateMenu.add(forwardButton, 2, 1);
		stateMenu.add(initialStateButton, 1, 0);
		stateMenu.add(finalStateButton, 2, 0);

		GridPane infoGrid = new GridPane();
		infoGrid.setPadding(new Insets(20));
		infoGrid.setHgap(5);
		infoGrid.setVgap(5);
		
		infoGrid.add(stepLabelText, 0, 0);
		infoGrid.add(depthLabelText, 0, 1);
		infoGrid.add(timeLabelText, 0, 2);

		infoGrid.add(stepText, 1, 0);
		infoGrid.add(depthText, 1, 1);
		infoGrid.add(timeText, 1, 2);

		HBox autoControl = new HBox();
		ObservableList autoControlList = autoControl.getChildren();
		autoControlList.addAll(speedTextField, playButton, stopButton);

		VBox controlMenu = new VBox();
		ObservableList controlMenuList = controlMenu.getChildren();
		controlMenuList.addAll(stateMenu, autoControlLabel, autoControl, infoGrid);

		BorderPane mainGrid = new BorderPane();
		mainGrid.setPadding(new Insets(20));

		mainGrid.setTop(titleText);
		mainGrid.setLeft(sudokuView.getGridPane());
		mainGrid.setRight(controlMenu);

		Scene scene = new Scene(mainGrid);
		primaryStage.setTitle("Visualizador secuencial (SKSolver)");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private static void updateInfoGrid(SudokuView sudokuView, List<LogEntry> logEntries, Text stepText, Text depthText,
			Text timeText) {
		stepText.setText(sudokuView.getStep().toString());
		depthText.setText(logEntries.get(sudokuView.getStep()).getDepth().toString());
		timeText.setText(logEntries.get(sudokuView.getStep()).getTime().toString() + " s");
	}
	
	
	
}
