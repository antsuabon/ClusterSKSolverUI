package antsuabon.clusterSKSolverUI.scenes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import antsuabon.clusterSKSolverUI.utils.LogLoader;
import antsuabon.clusterSKSolverUI.utils.SudokuLoader;
import antsuabon.clusterSKSolverUI.utils.SudokuProblem;
import antsuabon.clusterSKSolverUI.utils.SudokuView;
import antsuabon.clusterSKSolverUI.utils.LogLoader.LogEntry;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClusterSKAnalyzerScene {

	private static Integer ROW_ELEMENTS = 3;

	public static void start(Stage primaryStage, String sudokuProblemPath, List<String> logPaths) {
		SudokuProblem sudoku = SudokuLoader.loadSudoku(sudokuProblemPath);

		Map<Integer, List<LogEntry>> logMap = getLogMaps(logPaths);

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setCategories(FXCollections.observableArrayList(
				logMap.keySet().stream().sorted(Comparator.comparing((key) -> Integer.valueOf(key)))
						.map(String::valueOf).collect(Collectors.toList())));
		xAxis.setLabel("Nodo");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Número de iteraciones");

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Estadística del número de iteraciones por nodo");
		barChart.setLegendVisible(false);

		XYChart.Series<String, Number> series1 = new XYChart.Series<>();

		logMap.entrySet().stream().sorted(Comparator.comparing((entry) -> Integer.valueOf(entry.getKey())))
				.forEach((entry) -> {
					System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue().size());
					series1.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue().size()));
				});
		barChart.getData().add(series1);

		GridPane mainGrid = new GridPane();
		mainGrid.setAlignment(Pos.CENTER);

		mainGrid.add(barChart, 0, 0);

		Scene scene = new Scene(mainGrid);
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

				logMap.put(node, logEntries);
			}

		}

		return logMap;
	}
}
