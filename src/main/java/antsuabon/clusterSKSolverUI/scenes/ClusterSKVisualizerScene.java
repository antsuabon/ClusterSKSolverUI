package antsuabon.clusterSKSolverUI.scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClusterSKVisualizerScene {

	private static Integer ROW_ELEMENTS = 2;

	public static void start(Stage primaryStage, String sudokuProblemPath, List<String> logPaths) {
		SudokuProblem sudoku = SudokuLoader.loadSudoku(sudokuProblemPath);
		List<Color> colorsByBlock = SudokuView.generateNColors(sudoku.getBlocks().keySet().size());

		Map<Integer, List<LogEntry>> logMap = getLogMaps(logPaths);

		if (logMap.get(0).isEmpty()) {
			LogEntry emptyEntry = new LogEntry();
			emptyEntry.setStep(0);
			emptyEntry.setDepth(0);
			emptyEntry.setSolved(false);
			emptyEntry.setTime(0f);
			emptyEntry.setState(
					Stream.of(sudoku.getInitialState()).flatMap(row -> Stream.of(row)).collect(Collectors.toList()));

			logMap.get(0).add(emptyEntry);
		}

		GridPane multiSudokuGrid = new GridPane();
		ScrollPane scrollPane = new ScrollPane();
		// multiSudokuGrid.setAlignment(Pos.CENTER);
		multiSudokuGrid.setHgap(10.);
		multiSudokuGrid.setVgap(10.);

		List<SudokuView> sudokuList = new ArrayList<>();
		List<Text> stepTexts = new ArrayList<>();
		List<Text> depthTexts = new ArrayList<>();
		List<Text> timeTexts = new ArrayList<>();

		for (Entry<Integer, List<LogEntry>> sudokuViewEntry : logMap.entrySet()) {

			SudokuView newSudoku = SudokuView.getSudokuGrid(sudoku, colorsByBlock);
			newSudoku.setNode(sudokuList.size());
			System.out.println(logMap.get(newSudoku.getNode()).isEmpty());

			Text nodeLabelText = new Text("Nodo: ");
			nodeLabelText.setStyle("-fx-font-weight: bold;");
			Text nodeText = new Text(newSudoku.getNode().toString());

			Text stepLabelText = new Text("Iteración: ");
			stepLabelText.setStyle("-fx-font-weight: bold;");
			Text stepText = new Text(newSudoku.getStep().toString());
			stepTexts.add(stepText);

			Text depthLabelText = new Text("Profundidad: ");
			depthLabelText.setStyle("-fx-font-weight: bold;");
			Text depthText = new Text(logMap.get(newSudoku.getNode()).isEmpty() ? "0"
					: logMap.get(newSudoku.getNode()).get(newSudoku.getStep()).getDepth().toString());
			depthTexts.add(depthText);

			Text timeLabelText = new Text("Tiempo trancurrido: ");
			timeLabelText.setStyle("-fx-font-weight: bold;");
			Text timeText = new Text(newSudoku.getStep().toString() + " s");
			timeTexts.add(timeText);

			GridPane infoGrid = new GridPane();
			infoGrid.setPadding(new Insets(20));
			infoGrid.setHgap(5);
			infoGrid.setVgap(5);

			infoGrid.add(nodeLabelText, 0, 0);
			infoGrid.add(stepLabelText, 0, 1);
			infoGrid.add(depthLabelText, 0, 2);
			infoGrid.add(timeLabelText, 0, 3);

			infoGrid.add(nodeText, 1, 0);
			infoGrid.add(stepText, 1, 1);
			infoGrid.add(depthText, 1, 2);
			infoGrid.add(timeText, 1, 3);

			HBox sudokuWithInfo = new HBox();
			ObservableList sudokuWithInfoList = sudokuWithInfo.getChildren();
			sudokuWithInfoList.addAll(newSudoku.getGridPane(), infoGrid);

			multiSudokuGrid.add(sudokuWithInfo, sudokuList.size() % ROW_ELEMENTS, sudokuList.size() / ROW_ELEMENTS);
			scrollPane.setContent(multiSudokuGrid);
			sudokuList.add(newSudoku);
		}

		Button backwardButton = new Button("Anterior");
		backwardButton.setOnAction((event) -> {

			if (sudokuList.get(0).getStep() - 1 >= 0
					&& sudokuList.stream().skip(1).allMatch(s -> s.getStep().equals(0))) {
				sudokuList.get(0).setStep(sudokuList.get(0).getStep() - 1);
				SudokuView.updateSudokuGrid(sudokuList.get(0),
						logMap.get(sudokuList.get(0).getNode()).get(sudokuList.get(0).getStep()).getState());
				updateInfoGrid(sudokuList.get(0), logMap.get(sudokuList.get(0).getNode()), stepTexts.get(0),
						depthTexts.get(0), timeTexts.get(0));
			}

			for (SudokuView sudokuView : sudokuList.subList(1, sudokuList.size())) {
				if (sudokuView.getStep() - 1 >= 0 && sudokuList.get(0).getStep().equals(logMap.get(0).size() - 1)) {
					sudokuView.setStep(sudokuView.getStep() - 1);
					SudokuView.updateSudokuGrid(sudokuView,
							logMap.get(sudokuView.getNode()).get(sudokuView.getStep()).getState());
					updateInfoGrid(sudokuView, logMap.get(sudokuView.getNode()), stepTexts.get(sudokuView.getNode()),
							depthTexts.get(sudokuView.getNode()), timeTexts.get(sudokuView.getNode()));
				}
			}

		});
		GridPane.setHalignment(backwardButton, HPos.CENTER);

		Button forwardButton = new Button("Siguiente");
		forwardButton.setOnAction((event) -> {

			if (sudokuList.get(0).getStep() + 1 < logMap.get(0).size()
					&& sudokuList.stream().skip(1).allMatch(s -> s.getStep().equals(0))) {
				sudokuList.get(0).setStep(sudokuList.get(0).getStep() + 1);
				SudokuView.moveForwardSudokuGrid(sudokuList.get(0),
						logMap.get(sudokuList.get(0).getNode()).get(sudokuList.get(0).getStep()).getState());
				updateInfoGrid(sudokuList.get(0), logMap.get(sudokuList.get(0).getNode()), stepTexts.get(0),
						depthTexts.get(0), timeTexts.get(0));
			}

			for (SudokuView sudokuView : sudokuList.subList(1, sudokuList.size())) {
				if (sudokuView.getStep() + 1 < logMap.get(sudokuView.getNode()).size()
						&& sudokuList.get(0).getStep().equals(logMap.get(0).size() - 1)) {
					sudokuView.setStep(sudokuView.getStep() + 1);
					SudokuView.moveForwardSudokuGrid(sudokuView,
							logMap.get(sudokuView.getNode()).get(sudokuView.getStep()).getState());
					updateInfoGrid(sudokuView, logMap.get(sudokuView.getNode()), stepTexts.get(sudokuView.getNode()),
							depthTexts.get(sudokuView.getNode()), timeTexts.get(sudokuView.getNode()));
				}
			}

		});
		GridPane.setHalignment(forwardButton, HPos.CENTER);

		Button initialStateButton = new Button("Estado inicial");
		initialStateButton.setOnAction((event) -> {

			for (SudokuView sudokuView : sudokuList) {
				sudokuView.setStep(0);
				SudokuView.updateSudokuGrid(sudokuView,
						logMap.get(sudokuView.getNode()).get(sudokuView.getStep()).getState());
				updateInfoGrid(sudokuView, logMap.get(sudokuView.getNode()), stepTexts.get(sudokuView.getNode()),
						depthTexts.get(sudokuView.getNode()), timeTexts.get(sudokuView.getNode()));
			}
		});
		GridPane.setHalignment(initialStateButton, HPos.CENTER);

		Button finalStateButton = new Button("Estado final");
		finalStateButton.setOnAction((event) -> {

			for (SudokuView sudokuView : sudokuList) {
				if (logMap.get(sudokuView.getNode()).size() > 0) {
					sudokuView.setStep(logMap.get(sudokuView.getNode()).size() - 1);
					SudokuView.updateSudokuGrid(sudokuView,
							logMap.get(sudokuView.getNode()).get(sudokuView.getStep()).getState());
					updateInfoGrid(sudokuView, logMap.get(sudokuView.getNode()), stepTexts.get(sudokuView.getNode()),
							depthTexts.get(sudokuView.getNode()), timeTexts.get(sudokuView.getNode()));
				}

			}

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
					if (sudokuList.stream().anyMatch(s -> !s.getStep().equals(logMap.get(s.getNode()).size() - 1))) {
						if (sudokuList.get(0).getStep() + 1 < logMap.get(0).size()
								&& sudokuList.stream().skip(1).allMatch(s -> s.getStep().equals(0))) {
							sudokuList.get(0).setStep(sudokuList.get(0).getStep() + 1);
							SudokuView.moveForwardSudokuGrid(sudokuList.get(0), logMap.get(sudokuList.get(0).getNode())
									.get(sudokuList.get(0).getStep()).getState());
							updateInfoGrid(sudokuList.get(0), logMap.get(sudokuList.get(0).getNode()), stepTexts.get(0),
									depthTexts.get(0), timeTexts.get(0));
						}

						for (SudokuView sudokuView : sudokuList.subList(1, sudokuList.size())) {
							if (sudokuView.getStep() + 1 < logMap.get(sudokuView.getNode()).size()
									&& sudokuList.get(0).getStep().equals(logMap.get(0).size() - 1)) {
								sudokuView.setStep(sudokuView.getStep() + 1);
								SudokuView.moveForwardSudokuGrid(sudokuView,
										logMap.get(sudokuView.getNode()).get(sudokuView.getStep()).getState());
								updateInfoGrid(sudokuView, logMap.get(sudokuView.getNode()),
										stepTexts.get(sudokuView.getNode()), depthTexts.get(sudokuView.getNode()),
										timeTexts.get(sudokuView.getNode()));
							}
						}
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
		titleText.setText("Visualizador paralelo del algoritmo SKSolver");
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

		HBox autoControl = new HBox();
		ObservableList autoControlList = autoControl.getChildren();
		autoControlList.addAll(speedTextField, playButton, stopButton);

		VBox controlMenu = new VBox();
		ObservableList controlMenuList = controlMenu.getChildren();
		controlMenuList.addAll(stateMenu, autoControlLabel, autoControl);

		BorderPane mainGrid = new BorderPane();
		mainGrid.setPadding(new Insets(20));

		mainGrid.setTop(titleText);
		mainGrid.setLeft(scrollPane);
		mainGrid.setRight(controlMenu);

		Scene scene = new Scene(mainGrid);
		primaryStage.setTitle("Visualizador paralelo (SKSolver)");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private static Map<Integer, List<LogEntry>> getLogMaps(List<String> logPaths) {
		Map<Integer, List<LogEntry>> logMap = new HashMap<>();

		for (String logPath : logPaths) {
			List<LogEntry> logEntries = LogLoader.loadLogFile(logPath);

			Pattern pattern = Pattern.compile("(.*)log-(\\d+)\\.log", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(logPath);

			while (matcher.find()) {
				Integer node = Integer.parseInt(matcher.group(2));

				if (node != 0 && !logMap.get(0).isEmpty()) {
					logEntries.add(0, logMap.get(0).get(0));
				}

				logMap.put(node, logEntries);
			}

		}

//		for (Entry<Integer, List<LogEntry>> logEntry : logMap.entrySet()) {
//			if (logEntry.getValue().isEmpty()) {
//				log
//			}
//		}

		return logMap;
	}

	private static void updateInfoGrid(SudokuView sudokuView, List<LogEntry> logEntries, Text stepText, Text depthText,
			Text timeText) {
		stepText.setText(sudokuView.getStep().toString());
		depthText.setText(logEntries.get(sudokuView.getStep()).getDepth().toString());
		timeText.setText(logEntries.get(sudokuView.getStep()).getTime().toString() + " s");
	}

}
