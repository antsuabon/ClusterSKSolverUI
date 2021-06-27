package antsuabon.clusterSKSolverUI.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import antsuabon.clusterSKSolverUI.utils.SudokuProblem.Position;

public class SudokuLoader {
	
	public static SudokuProblem loadSudoku(String file) {
		SudokuProblem problem = new SudokuProblem();
		
		try {
			List<String> lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
			List<List<Integer>> grid = new ArrayList<>();
			Map<List<Position>, Integer> blocks = new HashMap();
			String state = null;
			
			for (String line : lines) {
				if (!line.isEmpty()) {
					if (line.contains("r")) {
						
						state = "r";
						
					} else if (line.contains("b")) {
						
						state = "b";
						
					} else if (state == "r") {
						
						String[] tokens = line.split("x");
						
						problem.setRegionX(Integer.valueOf(tokens[0].trim()));
						problem.setRegionY(Integer.valueOf(tokens[1].trim()));
						
					} else if (state == "b") {
						
						String[] block = line.split("->");
						
						List<Position> positions = Stream.of(block[0].split("\\|"))
								.map(pos -> {
									String[] posArr = pos.split(",");
									return new Position(Integer.valueOf(posArr[0].trim()), Integer.valueOf(posArr[1].trim()));
								})
								.collect(Collectors.toList());
						Integer sum = Integer.valueOf(block[1].trim());
						
						blocks.put(positions, sum);
						
					} else {
						
						List<Integer> row = Stream.of(line.split(",")).map(String::trim).map(Integer::valueOf).collect(Collectors.toList());
						grid.add(row);
						
					}
				}
			}
			
			problem.setRows(grid.size());
			problem.setCols(grid.get(0).size());
			
			Integer[][] aux = new Integer[problem.getRows()][problem.getCols()];
			for (int i = 0; i < grid.size(); i++) {
				for (int j = 0; j < grid.get(0).size(); j++) {
					aux[i][j] = grid.get(i).get(j);
				}
			}
			
			problem.setInitialState(aux);
			problem.setBlocks(blocks);
			
		} catch (IOException e) {
			System.err.println("Error al leer el fichero: " + file);
			System.exit(1);
		}
		
		return problem;
	}
	
}
