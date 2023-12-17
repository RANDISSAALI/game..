package connect4;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Connect4Game {

    private char[][] grid;
    private int[] topPieceIndex;

    private int width;
    private int height;
    private int numOfPiecesToWin;

    private int fills;
    private int lastColumnPlayed = -1;

    public Connect4Game(int width, int height) {
        fills = 0;
        this.width = width;
        this.height = height;
        System.out.println("enter pieces number to win :");
        this.numOfPiecesToWin = new Scanner(System.in).nextInt();
        ;
        grid = new char[height][width];
        topPieceIndex = new int[width];
        for (int i = 0; i < topPieceIndex.length; i++) {
            topPieceIndex[i] = height;
        }
        for (char[] grid1 : grid) {
            for (int j = 0; j < grid1.length; j++) {
                grid1[j] = ' ';
            }
        }
    }

    public Connect4Game(Connect4Game board) {
        grid = new char[board.height][board.width];
        topPieceIndex = new int[board.width];
        System.arraycopy(board.topPieceIndex, 0, topPieceIndex, 0, this.topPieceIndex.length);
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(board.grid[i], 0, this.grid[i], 0, board.width);
        }
        this.fills = board.fills;
        this.height = board.height;
        this.width = board.width;
        this.numOfPiecesToWin = board.numOfPiecesToWin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    Connect4Game makeNewBoard() {
        return new Connect4Game(this);
    }

    boolean isColFilled(int col) {
        return topPieceIndex[col] == 0;
    }

    boolean isColEmpty(int col) {
        return topPieceIndex[col] == height;
    }

    boolean isSamePlayer(int col, char player) {
        return grid[topPieceIndex[col]][col] == player;
    }


    public List<Connect4Game> allNextMoves(char nextPlayer) {
        List<Connect4Game> nextBoards = new LinkedList<>();
        for (int i = 0; i < width; i++) {
            if (!isColFilled(i)) {
                Connect4Game nextBoard = makeNewBoard();
                nextBoard.addPiece(nextPlayer, i);
                nextBoards.add(nextBoard);
            }
        }
        for (int i = 0; i < width; i++) {
            if (!isColEmpty(i)) {
                if (!isSamePlayer(i, nextPlayer)) {
                    Connect4Game nextBoard = makeNewBoard();
                    nextBoard.takePiece(i);
                    nextBoards.add(nextBoard);
                }
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = i + 1; j < width; j++) {
                if (!isColEmpty(i) && !isColEmpty(j)) {
                    Connect4Game nextBoard = makeNewBoard();
                    nextBoard.swapPieces(i, j);
                    nextBoards.add(nextBoard);
                }
            }
        }
        return nextBoards;
    }

    public boolean addPiece(char player, int col) {
        if (!isColFilled(col)) {
            topPieceIndex[col]--;
            grid[topPieceIndex[col]][col] = player;
            fills++;
            lastColumnPlayed = col;
            return true;
        }
        return false;
    }

    public boolean takePiece(int col) {
        if (!isColEmpty(col)) {
            grid[topPieceIndex[col]][col] = ' ';
            topPieceIndex[col]++;
            fills--;
            lastColumnPlayed = col;
            return true;
        }
        return false;
    }

    public boolean swapPieces(int firstCol, int secondCol) {
        if (!isColEmpty(firstCol) && !isColEmpty(secondCol)) {
            char temp = grid[topPieceIndex[firstCol]][firstCol];
            grid[topPieceIndex[firstCol]][firstCol] = grid[topPieceIndex[secondCol]][secondCol];
            grid[topPieceIndex[secondCol]][secondCol] = temp;
            lastColumnPlayed = secondCol;
            return true;
        }
        return false;
    }

    /**
     * how good is the board for this player?
     *
     * @param player
     * @return
     */
    public int evaluate(char player) {
        if (isFinished()) {
            if (isWin(player)) return Integer.MAX_VALUE;
            else if (isWithdraw()) return 0;
            else return Integer.MIN_VALUE;
        }
        return 0;

    }

    /**
     * checks if the game is withdraw
     *
     * @return
     */
    public boolean isWithdraw() {
        return (fills == width * height);
    }

    /**
     * checks if player putting last piece makes him win (connect four pieces)
     *
     * @param player
     * @return true if win
     */
    public boolean isWinWithLastPiece(char player) {
        int col = this.lastColumnPlayed;
        return (isWinInColumn(player, col) || isWinInRow(player, col) || isWinInDiagonal_1(player, col) || isWinInDiagonal_2(player, col));
    }

    /**
     * checks if player is a winner (searching all the board not just last
     * piece)
     *
     * @param player
     * @return true if win
     */
    public boolean isWin(char player) {
        for (int col = 0; col < width; col++) {
            if (isWinInColumn(player, col)) {
                return true;
            } else if (isWinInRow(player, col)) {
                return true;
            } else if (isWinInDiagonal_1(player, col)) {
                return true;
            } else if (isWinInDiagonal_2(player, col)) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if the game is win of withdraw for any player
     *
     * @return
     */
    public boolean isFinished() {
        return (isWin('x') || isWin('o') || isWithdraw());
    }

    /**
     * checks if player putting piece in col makes him win (connect four pieces)
     * in this col
     *
     * @param player
     * @param col    the column the player played in
     * @return true if win
     */
    private boolean isWinInColumn(char player, int col) {
        int row = topPieceIndex[col];
        // row is empty
        if (row == height) {
            return false;
        }
        // the cell itself
        if (grid[row][col] != player) {
            return false;
        }
        int count = 1;
        // check cells below
        try {
            for (int i = row + 1; i < height; i++) {
                if (grid[i][col] == player) {
                    count++;
                } else {
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return (count >= numOfPiecesToWin);
    }

    /**
     * checks if player putting piece in col makes him win (connect four pieces)
     * in the row containing piece
     *
     * @param player
     * @param col    the column the player played in
     * @return true if win
     */
    private boolean isWinInRow(char player, int col) {
        // collect row
        int row = topPieceIndex[col];
        // row is empty
        if (row == height) {
            return false;
        }
        // the cell itself
        if (grid[row][col] != player) {
            return false;
        }
        int count = 1;
        // cells befor
        try {
            for (int i = col - 1; i >= 0; i--) {
                if (grid[row][i] == player) {
                    count++;
                } else {
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // cells after
        try {
            for (int i = col + 1; i < width; i++) {
                if (grid[row][i] == player) {
                    count++;
                } else {
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return (count >= numOfPiecesToWin);

    }

    /**
     * checks if player putting piece in col makes him win (connect four pieces)
     * in the first diagonal containing this piece
     *
     * @param player
     * @param col    the column the player played in
     * @return true if win
     */
    private boolean isWinInDiagonal_1(char player, int col) {
        // collect diagonal
        int row = topPieceIndex[col];
        // row is empty
        if (row == height) {
            return false;
        }
        // the cell itself
        if (grid[row][col] != player) {
            return false;
        }
        int count = 1;
        // cells befor
        try {
            for (int i = 1; ; i++) {
                if (grid[row - i][col - i] == player) {
                    count++;
                } else {
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // cells after
        try {
            for (int i = 1; ; i++) {
                if (grid[row + i][col + i] == player) {
                    count++;
                } else {
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return (count >= numOfPiecesToWin);

    }

    /**
     * checks if player putting piece in col makes him win (connect four pieces)
     * in the second diagonal containing this piece
     *
     * @param player
     * @param col    the column the player played in
     * @return true if win
     */
    private boolean isWinInDiagonal_2(char player, int col) {
        // collect diagonal
        int row = topPieceIndex[col];
        // row is empty
        if (row == height) {
            return false;
        }
        // the cell itself
        if (grid[row][col] != player) {
            return false;
        }
        int count = 1;
        // cells befor
        try {
            for (int i = 1; ; i++) {
                if (grid[row - i][col + i] == player) {
                    count++;
                } else {
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        // cells after
        try {
            for (int i = 1; ; i++) {
                if (grid[row + i][col - i] == player) {
                    count++;
                } else {
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return (count >= numOfPiecesToWin);

    }

    private char otherPlayer(char player) {
        if (player == 'x') {
            return 'o';
        }
        return 'x';
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            sb.append(" | ");
            for (int j = 0; j < width; j++) {
                sb.append(grid[i][j]);
                sb.append(" | ");
            }
            // sb.delete(sb.length() - 2, sb.length() - 1);
            sb.append('\n');
        }
        sb.append(" ");
        for (int i = 1; i < height; i++) {
            sb.append("-----");
        }
        sb.append("\n | ");
        for (int i = 1; i <= height; i++) {
            sb.append(i);
            sb.append(" | ");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Connect4Game board = new Connect4Game(4, 4);

        board.addPiece('o', 1);
        board.addPiece('x', 1);
        board.addPiece('o', 2);
        board.addPiece('x', 2);
        board.addPiece('x', 3);
        board.addPiece('x', 3);

        System.out.println("board:");
        System.out.println(board);
        System.out.println("****************");
        System.out.println("is win for x? " + board.isWin('x'));
        System.out.println("****************");

        List<Connect4Game> next = board.allNextMoves('x');
        int i = 1;
        for (Connect4Game b : next) {
            System.out.println(i + ": (" + b.evaluate('x') + ")");
            System.out.println(b);
            System.out.println("is win for x? " + b.isWinWithLastPiece('x'));
            i++;
        }

    }
}
