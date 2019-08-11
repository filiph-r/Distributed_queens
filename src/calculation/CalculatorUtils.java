package calculation;

import java.util.ArrayList;

public class CalculatorUtils {

	public static ArrayList<int[][]> filterDuplicates(ArrayList<int[][]> list, int N) {

		ArrayList<int[][]> resultList = new ArrayList<>();
		boolean contains = false;

		for (int[][] m : list) {
			contains = false;
			for (int[][] k : resultList) {
				if (compare(m, k, N)) {
					contains = true;
					break;
				}
			}

			if (!contains) {
				resultList.add(m);
			}
		}

		return resultList;
	}

	private static boolean compare(int[][] b1, int[][] b2, int N) {

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (b1[i][j] != b2[i][j]) {
					return false;
				}
			}
		}

		return true;
	}
}
