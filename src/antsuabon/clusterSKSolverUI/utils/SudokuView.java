package antsuabon.clusterSKSolverUI.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import antsuabon.clusterSKSolverUI.utils.SudokuProblem.Position;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class SudokuView {

	private Integer node;
	private Integer step;
	private GridPane gridPane;
	private Label[][] labelGrid;
	private SudokuProblem sudokuProblem;

	public SudokuView() {
		this.step = 0;
		this.node = 0;
	}

	public Integer getNode() {
		return node;
	}

	public void setNode(Integer node) {
		this.node = node;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void setGridPane(GridPane gridPane) {
		this.gridPane = gridPane;
	}

	public Label[][] getLabelGrid() {
		return labelGrid;
	}

	public void setLabelGrid(Label[][] labelGrid) {
		this.labelGrid = labelGrid;
	}

	public SudokuProblem getSudokuProblem() {
		return sudokuProblem;
	}

	public void setSudokuProblem(SudokuProblem sudokuProblem) {
		this.sudokuProblem = sudokuProblem;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public static List<Color> generateNColors(Integer n) {
		List<Color> result = new ArrayList<>();

		if (n == 0) {
			return result;
		}

		Double goldenRatioConjugate = 0.618033988749895;

		for (float i = 0; i < 360; i += 360.0 / n) {
			Color c = Color.hsb(i, 0.4, 1);
			result.add(c);
		}

		Collections.shuffle(result);

		return result;
	}

	public static SudokuView getSudokuGrid(SudokuProblem sudokuProblem, List<Color> colorsByBlock) {

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20));

		SudokuView sudokuView = new SudokuView();

		sudokuView.setSudokuProblem(sudokuProblem);
		sudokuView.setGridPane(grid);
		sudokuView.setLabelGrid(new Label[sudokuProblem.getRows()][sudokuProblem.getCols()]);

		List<List<Position>> blockKeyList = new ArrayList(sudokuProblem.getBlocks().keySet());

		for (int i0 = 0; i0 < sudokuProblem.getRows() / sudokuProblem.getRegionY(); i0++) {
			for (int j0 = 0; j0 < sudokuProblem.getCols() / sudokuProblem.getRegionX(); j0++) {
				GridPane region = new GridPane();
				
				for (int k = 0; k < sudokuProblem.getRegionY(); k++) {
					for (int k2 = 0; k2 < sudokuProblem.getRegionX(); k2++) {
						Integer i = sudokuProblem.getRegionY() * i0 + k;
						Integer j = sudokuProblem.getRegionX() * j0 + k2;

						String text = String.valueOf(sudokuProblem.getInitialState()[i][j] == 0 ? ""
								: sudokuProblem.getInitialState()[i][j].toString());
						Label label = new Label(text);

						String style = "-fx-border-color: black; -fx-font-size: 30; -fx-pref-height: 50; -fx-pref-width: 50; -fx-font-weight: bold; -fx-alignment: center;";

						if (!text.isBlank()) {
							style += "-fx-background-color: gainsboro;";
						}

						List<Position> blockEntry = blockKeyList.stream()
								.filter(list -> list.contains(new Position(i, j))).findFirst().orElse(null);
						Color backgroundColor = Color.WHITE;

						if (blockEntry != null) {
							backgroundColor = colorsByBlock.get(blockKeyList.indexOf(blockEntry));
						}

						label.setStyle(style);

						label.setBackground(
								new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));

						region.add(label, k2, k);

						sudokuView.getLabelGrid()[i][j] = label;

						for (Entry<List<SudokuProblem.Position>, Integer> entry : sudokuProblem.getBlocks()
								.entrySet()) {
							Position pos = entry.getKey().get(0);
							if (i == pos.getI() && j == pos.getJ()) {
								Text blockText = new Text(entry.getValue().toString());
								region.add(blockText, k2, k);

								GridPane.setMargin(blockText, new Insets(2));
								GridPane.setValignment(blockText, VPos.TOP);
							}

						}
					}
				}
				region.setStyle("-fx-border-color: black; -fx-border-width: 2; ");
				grid.add(region, j0, i0);
			}
		}

		return sudokuView;
	}

	public static void moveForwardSudokuGrid(SudokuView sudokuView, List<Integer> newState) {

		for (int i = 0; i < newState.size(); i++) {

			String oldValue = sudokuView.getLabelGrid()[i / sudokuView.getSudokuProblem().getCols()][i
					% sudokuView.getSudokuProblem().getCols()].getText();
			String newValue = String.valueOf(newState.get(i) == 0 ? "" : newState.get(i).toString());

			sudokuView.getLabelGrid()[i / sudokuView.getSudokuProblem().getCols()][i
					% sudokuView.getSudokuProblem().getCols()].setText(newValue);

			String style = "-fx-border-color: black; -fx-font-size: 30; -fx-pref-height: 50; -fx-pref-width: 50; -fx-font-weight: bold; -fx-alignment: center;";
			if (newValue.isBlank() && !oldValue.isBlank()) {
				style += "-fx-background-color: orangered;";
			} else if (oldValue.isBlank() && !newValue.isBlank()) {
				style += "-fx-background-color: mediumspringgreen;";
			} else if (!newValue.equals(oldValue)) {
				style += "-fx-background-color: goldenrod;";
			} else if (newValue.equals(
					sudokuView.getSudokuProblem().getInitialState()[i / sudokuView.getSudokuProblem().getCols()][i
							% sudokuView.getSudokuProblem().getCols()].toString())) {
				style += "-fx-background-color: gainsboro;";
			}

			sudokuView.getLabelGrid()[i / sudokuView.getSudokuProblem().getCols()][i
					% sudokuView.getSudokuProblem().getCols()].setStyle(style);
		}

	}

	public static void updateSudokuGrid(SudokuView sudokuView, List<Integer> newState) {

		for (int i = 0; i < newState.size(); i++) {

			String oldValue = sudokuView.getLabelGrid()[i / sudokuView.getSudokuProblem().getCols()][i
					% sudokuView.getSudokuProblem().getCols()].getText();
			String newValue = String.valueOf(newState.get(i) == 0 ? "" : newState.get(i).toString());

			sudokuView.getLabelGrid()[i / sudokuView.getSudokuProblem().getCols()][i
					% sudokuView.getSudokuProblem().getCols()].setText(newValue);

			String style = "-fx-border-color: black; -fx-font-size: 30; -fx-pref-height: 50; -fx-pref-width: 50; -fx-font-weight: bold; -fx-alignment: center;";

			if (sudokuView.getSudokuProblem().getInitialState()[i / sudokuView.getSudokuProblem().getCols()][i
					% sudokuView.getSudokuProblem().getCols()] != 0) {
				style += "-fx-background-color: gainsboro;";
			}

			sudokuView.getLabelGrid()[i / sudokuView.getSudokuProblem().getCols()][i
					% sudokuView.getSudokuProblem().getCols()].setStyle(style);
		}

	}

}
