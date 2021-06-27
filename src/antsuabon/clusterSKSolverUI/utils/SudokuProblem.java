package antsuabon.clusterSKSolverUI.utils;

import java.util.List;
import java.util.Map;

public class SudokuProblem {

	public static class Position {
		
		private Integer i;
		private Integer j;
		
		public Position(Integer i, Integer j) {
			super();
			this.i = i;
			this.j = j;
		}
		public Integer getI() {
			return i;
		}
		public void setI(Integer i) {
			this.i = i;
		}
		public Integer getJ() {
			return j;
		}
		public void setJ(Integer j) {
			this.j = j;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((i == null) ? 0 : i.hashCode());
			result = prime * result + ((j == null) ? 0 : j.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Position other = (Position) obj;
			if (i == null) {
				if (other.i != null)
					return false;
			} else if (!i.equals(other.i))
				return false;
			if (j == null) {
				if (other.j != null)
					return false;
			} else if (!j.equals(other.j))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "Position [i=" + i + ", j=" + j + "]";
		}

		
	}
	
	private Integer rows;
	private Integer cols;
	private Integer regionX;
	private Integer regionY;
	
	private Integer[][] initialState;
	
	private Map<List<Position>, Integer> blocks;

	
	public SudokuProblem() {
		
	}
	
	public SudokuProblem(Integer rows, Integer cols, Integer regionX, Integer regionY, Integer[][] initialState,
			Map<List<Position>, Integer> blocks) {
		super();
		this.rows = rows;
		this.cols = cols;
		this.regionX = regionX;
		this.regionY = regionY;
		this.initialState = initialState;
		this.blocks = blocks;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getCols() {
		return cols;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public Integer getRegionX() {
		return regionX;
	}

	public void setRegionX(Integer regionX) {
		this.regionX = regionX;
	}

	public Integer getRegionY() {
		return regionY;
	}

	public void setRegionY(Integer regionY) {
		this.regionY = regionY;
	}

	public Integer[][] getInitialState() {
		return initialState;
	}

	public void setInitialState(Integer[][] initialState) {
		this.initialState = initialState;
	}

	public Map<List<Position>, Integer> getBlocks() {
		return blocks;
	}

	public void setBlocks(Map<List<Position>, Integer> blocks) {
		this.blocks = blocks;
	}
	
	
}
