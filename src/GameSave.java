import java.io.Serializable;

public class GameSave implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private int score;
    private int step;
    private int[][] board;
    private int time;//已进行时间
    private String mode;

    public GameSave(String username, int score, int step, int[][] board,int time,String mode) {
        this.username = username;
        this.score = score;
        this.step=step;
        this.board = board;
        this.time=time;
        this.mode=mode;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStep() {
        return step;
    }
    public void setStep(int step) {
        this.step=step;
    }

    public int getBoard(int i,int j) {
        return board[i][j];
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
