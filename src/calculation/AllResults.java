package calculation;

import java.io.Serializable;
import java.util.ArrayList;

public class AllResults implements Serializable{


	private static final long serialVersionUID = 2L;
	
	public ArrayList<OneResult> results;
	public int N;

	public AllResults(ArrayList<OneResult> results, int N) {
		this.results = results;
		this.N = N;
	}

}
