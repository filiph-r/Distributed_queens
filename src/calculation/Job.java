package calculation;

import java.io.Serializable;
import java.util.ArrayList;

import app.AppConfig;

public class Job implements Serializable {

	private static final long serialVersionUID = 164894L;

	public int N;
	public int collumn;
	public int row;

	public volatile boolean aborted = false;
	public volatile boolean stoped = false;

	private ArrayList<int[][]> results = new ArrayList<>();

	public Job(int N, int collumn, int row) {
		super();
		this.N = N;
		this.collumn = collumn;
		this.row = row;
	}

	public void calculate() {
		if (AppConfig.resultMap.get(N) == null) {
			AppConfig.resultMap.put(N, new ArrayList<OneResult>());
		}

		int board[][] = new int[N][N];
		board = resetBoard(board);

		prepareAndSolve(board, collumn, row);
		if (aborted == false) {
			AppConfig.resultMap.get(N).add(new OneResult(collumn, row, results));
		}
	}

	private void prepareAndSolve(int board[][], int i, int j) {
		board[i][j] = 1;
		solve(board, N - 1);
		board[i][j] = 0;
	}

	private void solve(int board[][], int Q) {

		if (aborted || stoped) {
			return;
		}

		try {
			if (AppConfig.stoping) {
				stoped = true;
				return;
			}
			if (AppConfig.finalResults.getStatus(N).equals("Paused")) {
				aborted = true;
				return;
			}
		} catch (Exception e) {
		}

		if (Q == 0) {
			if (checkBoard(board)) {
				compareAndSet(board);
			}
			return;
		}

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (board[i][j] == 0) {
					board[i][j] = 1;
					solve(board, Q - 1);
					if (aborted) {
						return;
					}
					board[i][j] = 0;
				}
			}
		}

		return;
	}

	private boolean compare(int[][] b1, int[][] b2) {

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (b1[i][j] != b2[i][j]) {
					return false;
				}
			}
		}

		return true;
	}

	private void compareAndSet(int[][] board) {
		for (int i = 0; i < AppConfig.resultMap.get(N).size(); i++) {
			for (int j = 0; j < AppConfig.resultMap.get(N).get(i).results.size(); j++) {
				if (compare(AppConfig.resultMap.get(N).get(i).results.get(j), board)) {
					return;
				}
			}
		}
		int newBoard[][] = new int[N][N];
		newBoard = resetBoard(newBoard);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (board[i][j] == 1) {
					newBoard[i][j] = 1;
				}
			}
		}

		results.add(newBoard);
	}

	private int[][] resetBoard(int[][] board) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				board[i][j] = 0;
			}
		}

		return board;
	}

	private boolean checkBoard(int board[][]) {
		int counter = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (board[i][j] == 1) {
					counter++;
				}
			}
		}
		if (counter != N) {
			// System.out.println(1);
			return false;
		}

		for (int i = 0; i < N; i++) {
			counter = 0;
			for (int j = 0; j < N; j++) {
				if (board[i][j] == 1) {
					counter++;
				}
			}
			if (counter > 1) {
				// System.out.println(2);
				return false;
			}
		}

		for (int i = 0; i < N; i++) {
			counter = 0;
			for (int j = 0; j < N; j++) {
				if (board[j][i] == 1) {
					counter++;
				}
			}
			if (counter > 1) {
				// System.out.println(3);
				return false;
			}
		}

		// diagonal
		counter = 0;
		for (int row = 0; row < N; row++) {
			for (int i = 0, j = row; i < N && j < N; i++, j++) {
				if (board[i][j] == 1) {
					counter++;
					if (counter > 1) {
						// System.out.println(4);
						return false;
					}
				}
			}
			counter = 0;
		}

		counter = 0;
		for (int col = 0; col < N; col++) {
			for (int i = col, j = 0; i < N && j < N; i++, j++) {
				if (board[i][j] == 1) {
					counter++;
					if (counter > 1) {
						// System.out.println(5);
						return false;
					}
				}
			}
			counter = 0;
		}

		// diagonal
		counter = 0;
		for (int row = 0; row < N; row++) {
			for (int i = N - 1, j = row; i >= 0 && j < N; i--, j++) {
				if (board[i][j] == 1) {
					counter++;
					if (counter > 1) {
						// System.out.println(6);
						return false;
					}
				}
			}
			counter = 0;
		}

		counter = 0;
		for (int col = N - 1; col >= 0; col--) {
			for (int i = col, j = 0; i >= 0 && j < N; i--, j++) {
				if (board[i][j] == 1) {
					counter++;
					if (counter > 1) {
						// System.out.println(7);
						return false;
					}
				}
			}
			counter = 0;
		}

		return true;
	}

}
