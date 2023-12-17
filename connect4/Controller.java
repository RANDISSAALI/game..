 package connect4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {

    char computer = 'o';
    char human = 'x';
    Connect4Game board;

    public Controller() {
        this.board = new Connect4Game(5, 5);
    }

    public void play() {
        System.out.println(board);
        while (true) {
            humanPlay();
            System.out.println(board);

            if (board.isWin(human)) {
                System.out.println("Human wins");
                break;
            }
            if (board.isWithdraw()) {
                System.out.println("Draw");
                break;
            }
            computerPlay();
            System.out.println("_____Computer Turn______");
            System.out.println(board);
            if (board.isWin(computer)) {
                System.out.println("Computer wins!");
                break;
            }
            if (board.isWithdraw()) {
                System.out.println("Draw");
                break;
            }
        }

    }

    // ************** YOUR CODE HERE ************ \\
    
    private void computerPlay() {
        List<Object> result = alphaBeta(board, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        board = ((Connect4Game) result.get(0));
    }
    /**
     * Human plays
     *
     * @return the column the human played in
     */
    private void humanPlay() {
        Scanner s = new Scanner(System.in);
        int type;
        int col;
        int col1 = 0;
        while (true) {
            System.out.print("Enter move type: ");
            type = s.nextInt();
            System.out.print("Enter column: ");
            col = s.nextInt();
            if (type == 2) {
                System.out.print("Enter other column: ");
                col1 = s.nextInt();
            }
            System.out.println();
            if ((col > 0) && (col - 1 < board.getWidth())) {
                if (type == 0) {
                    if (board.addPiece(human, col - 1)) {
                        return;
                    }
                } else if (type == 1) {
                    if (board.takePiece(col - 1)) {
                        return;
                    }
                } else if (type == 2) {
                    if (board.swapPieces(col - 1, col1 - 1)) {
                        return;
                    }
                }
                System.out.println("Invalid Column: Column " + col + " is full!, try agine");
            }
            System.out.println("Invalid Column: out of range " + board.getWidth() + ", try agine");
        }
    }

    private List<Object> alphaBeta(Connect4Game b, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (b.isFinished() || depth == 0) {
            List<Object> list = new ArrayList<>();
            list.add(b);
            list.add(b.evaluate(computer));
            return list;
        }

        if (isMaximizingPlayer) {
            return maxMove(b, depth, alpha, beta, 0);
        } else {
            return minMove(b, depth, alpha, beta, 0);
        }
    }

    private List<Object> maxMove(Connect4Game b, int depth, int alpha, int beta, int curDepth) {
        if (b.isFinished() || curDepth == depth) {
            List<Object> list = new ArrayList<>();
            list.add(b);
            list.add(b.evaluate(computer));
            return list;
        }

        int max = Integer.MIN_VALUE;
        Connect4Game resultConnect4Game = null;

        for (Connect4Game board : b.allNextMoves(computer)) {
            List<Object> list = minMove(board, depth, alpha, beta, curDepth + 1);
            int evaluation = (int) list.get(1);

            if (evaluation > max) {
                max = evaluation;
                resultConnect4Game = board;
            }

            alpha = Math.max(alpha, evaluation);
            if (beta <= alpha) {
                break;
            }
        }

        List<Object> list = new ArrayList<>();
        list.add(resultConnect4Game);
        list.add(max);
        return list;
    }

    private List<Object> minMove(Connect4Game b, int depth, int alpha, int beta, int curDepth) {
        if (b.isFinished() || curDepth == depth) {
            List<Object> list = new ArrayList<>();
            list.add(b);
            list.add(b.evaluate(computer));
            return list;
        }

        int min = Integer.MAX_VALUE;
        Connect4Game resultConnect4Game = null;

        for (Connect4Game board : b.allNextMoves(human)) {
            List<Object> list = maxMove(board, depth, alpha, beta, curDepth + 1);
            int evaluation = (int) list.get(1);

            if (evaluation < min) {
                min = evaluation;
                resultConnect4Game = board;
            }

            beta = Math.min(beta, evaluation);
            if (beta <= alpha) {
                break;
            }
        }

        List<Object> list = new ArrayList<>();
        list.add(resultConnect4Game);
        list.add(min);
        return list;
    }
    public static void main(String[] args) {
        Controller g = new Controller();
        g.play();
    }

}