package antsuabon.clusterSKSolverUI.scenes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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
import javafx.stage.Stage;

public class SKAnalyzerScene {

	public static void start(Stage primaryStage, String sudokuProblemPath, String logPath) {
		SudokuProblem sudoku = SudokuLoader.loadSudoku(sudokuProblemPath);
		List<LogEntry> logEntries = LogLoader.loadLogFile(logPath);

		Map<String, Integer> depthStats = new HashMap<>();
		measureDepthStats(logEntries, depthStats);

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setCategories(FXCollections.observableArrayList(depthStats.keySet().stream()
				.sorted(Comparator.comparing((key) -> Integer.valueOf(key))).collect(Collectors.toList())));
		xAxis.setLabel("Profundidad");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Número de hojas");

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Estadística de profundidad de las hojas encontradas");
		barChart.setLegendVisible(false);
		
		XYChart.Series<String, Number> series1 = new XYChart.Series<>();

		depthStats.entrySet().stream().sorted(Comparator.comparing((entry) -> Integer.valueOf(entry.getKey())))
				.forEach((entry) -> {
					System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
					series1.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
				});
		barChart.getData().add(series1);

		GridPane mainGrid = new GridPane();
		mainGrid.setAlignment(Pos.CENTER);

		mainGrid.add(barChart, 0, 0);

		Scene scene = new Scene(mainGrid);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private static void measureDepthStats(List<LogEntry> logEntries, Map<String, Integer> results) {
		
		Integer previousDepth = -1;
		for (LogEntry logEntry : logEntries) {
			if (logEntry.getDepth() <= previousDepth) {
				String depthString = logEntry.getDepth().toString();
				Integer currentCount = results.get(depthString);

				if (currentCount != null) {
					results.put(depthString, currentCount + 1);
				} else {
					results.put(depthString, 1);
				}
			}
			
			previousDepth = logEntry.getDepth();
		}
		
		if (logEntries.size() > 2 && logEntries.get(logEntries.size() - 2).getDepth() < logEntries.get(logEntries.size() - 1).getDepth()) {
			String depthString = logEntries.get(logEntries.size() - 1).getDepth().toString();
			Integer currentCount = results.get(depthString);
			
			if (currentCount != null) {
				results.put(depthString, currentCount + 1);
			} else {
				results.put(depthString, 1);
			}
		}
		
	}

}
