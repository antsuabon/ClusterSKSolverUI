package antsuabon.clusterSKSolverUI.scenes;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainScene extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Selección del fichero que contiene las características del problema
		FileChooser fileChooserProblem = new FileChooser();

		fileChooserProblem.setInitialDirectory(new File("./res"));
		fileChooserProblem.getExtensionFilters().add(new ExtensionFilter("Archivo de texto", "*.txt"));

		fileChooserProblem.setTitle("Seleccionar el archivo de problema");

		// Selección del fichero o ficheros de log
		FileChooser fileChooserLog = new FileChooser();

		fileChooserLog.setInitialDirectory(new File("./logs"));
		fileChooserLog.getExtensionFilters().add(new ExtensionFilter("Archivo de log", "*.log"));

		File sudokuFile = fileChooserProblem.showOpenDialog(primaryStage);

		if (sudokuFile == null) {
			System.exit(1);
		}

		Button sequentialVisualizationButton = new Button("Visualizador secuencial");
		sequentialVisualizationButton.setOnAction((event) -> {

			fileChooserLog.setTitle("Seleccionar el archivo de log");

			File logFile = fileChooserLog.showOpenDialog(primaryStage);

			if (logFile != null) {
				SKVisualizerScene.start(primaryStage, sudokuFile.getAbsolutePath(), logFile.getAbsolutePath());
			}
		});


		Button parallelVisualizationButton = new Button("Visualizador paralelo");
		parallelVisualizationButton.setOnAction((event) -> {

			fileChooserLog.setTitle("Seleccionar varios archivos de log");

			List<File> logFiles = fileChooserLog.showOpenMultipleDialog(primaryStage);

			if (logFiles != null && logFiles.size() > 1) {
				ClusterSKVisualizerScene.start(primaryStage, sudokuFile.getAbsolutePath(), logFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()));
			}
		});
		

		Button sequentialAnalysisButton = new Button("Análisis secuencial");
		sequentialAnalysisButton.setOnAction((event) -> {

			fileChooserLog.setTitle("Seleccionar el archivo de log");

			File logFile = fileChooserLog.showOpenDialog(primaryStage);

			if (logFile != null) {
				SKAnalyzerScene.start(primaryStage, sudokuFile.getAbsolutePath(), logFile.getAbsolutePath());
			}
		});
		

		Button parallelAnalysisButton = new Button("Análisis paralelo");
		parallelAnalysisButton.setOnAction((event) -> {

			fileChooserLog.setTitle("Seleccionar varios archivos de log");

			List<File> logFiles = fileChooserLog.showOpenMultipleDialog(primaryStage);

			if (logFiles != null && logFiles.size() > 1) {
				ClusterSKAnalyzerScene.start(primaryStage, sudokuFile.getAbsolutePath(), logFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()));
			}
		});

		Text title = new Text();
		title.setText("Herramienta para la visualización y el análisis de ClusterSKSolver");
		title.setStyle("-fx-border-color: black; -fx-font-size: 20; -fx-font-weight: bold;");
		BorderPane.setAlignment(title, Pos.TOP_CENTER);
		BorderPane.setMargin(title, new Insets(10));
		
		GridPane buttonGrid = new GridPane();
		buttonGrid.setAlignment(Pos.CENTER);
		buttonGrid.setHgap(5);
		buttonGrid.setVgap(5);
		
		buttonGrid.add(sequentialVisualizationButton, 0, 0);
		buttonGrid.add(parallelVisualizationButton, 1, 0);
		buttonGrid.add(sequentialAnalysisButton, 0, 1);
		buttonGrid.add(parallelAnalysisButton, 1, 1);
		
		BorderPane mainGrid = new BorderPane();
		mainGrid.setPadding(new Insets(20));
		mainGrid.setTop(title);
		mainGrid.setCenter(buttonGrid);
		
		Scene scene = new Scene(mainGrid);
		primaryStage.setScene(scene);
		primaryStage.setTitle("ClusterSKSolverUI");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
