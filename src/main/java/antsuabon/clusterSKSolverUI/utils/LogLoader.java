package antsuabon.clusterSKSolverUI.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogLoader {

	public static class LogEntry {
		
		private Integer step;
		private Integer depth;
		private List<Integer> state;
		private Float time;
		private Boolean solved;
		private Integer globalStep;
		
		public LogEntry() {
		}

		public Integer getStep() {
			return step;
		}

		public void setStep(Integer step) {
			this.step = step;
		}
		
		public Integer getGlobalStep() {
			return globalStep;
		}

		public void setGlobalStep(Integer globalStep) {
			this.globalStep = globalStep;
		}

		public Integer getDepth() {
			return depth;
		}

		public void setDepth(Integer depth) {
			this.depth = depth;
		}

		public List<Integer> getState() {
			return state;
		}

		public void setState(List<Integer> state) {
			this.state = state;
		}

		public Float getTime() {
			return time;
		}

		public void setTime(Float time) {
			this.time = time;
		}

		public Boolean getSolved() {
			return solved;
		}

		public void setSolved(Boolean solved) {
			this.solved = solved;
		}
		
		
	}
	
	public static List<LogEntry> loadLogFile(String file) {
		List<LogEntry> entries = new ArrayList<>();
		
		try {
			List<String> lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
			
			for (String line : lines) {
				String[] aux = line.split("\t");
				
				LogEntry logEntry = new LogEntry();
				logEntry.setStep(Integer.valueOf(aux[0]));
				logEntry.setDepth(Integer.valueOf(aux[1]));
				logEntry.setTime(Float.valueOf(aux[2]));
				logEntry.setSolved(Boolean.valueOf(aux[3]));
				logEntry.setState(Stream.of(aux[4].split(",")).map(Integer::valueOf).collect(Collectors.toList()));
				
				logEntry.setGlobalStep(entries.size() + 1);
				
				entries.add(logEntry);
			}
		} catch (Exception e) {
			System.err.println("Error al leer el fichero: " + file);
			e.printStackTrace();
			System.exit(1);
		}
		
		
		
		return entries;
	}
	
}
