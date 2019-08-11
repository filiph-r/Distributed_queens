package calculation;

import java.io.Serializable;
import java.util.ArrayList;

public class OneResult implements Serializable{

	private static final long serialVersionUID = 148849849L;
	
	
	public int collumn;
	public int row;
	public ArrayList<int[][]> results;

	public OneResult(int collumn, int row, ArrayList<int[][]> results) {
		this.collumn = collumn;
		this.row = row;
		this.results = results;
	}

	public boolean equals(OneResult rez) {
		if (rez.collumn == this.collumn && rez.row == this.row) {
			return true;
		} else {
			return false;
		}
	}

}
