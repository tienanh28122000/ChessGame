package chess;

import chess.piece.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Game implements Serializable {
    private static final String WRONG_EXPRESSION = "Wrong expression! Try again: ";

    private static final String START_GAME_INTRO = "\nHello Chess-Players! Welcome to a simple chess game by " +
            "Nguyen Thai Ha Anh.\n" +
            "Let's start! Please enter your names.";

    private static final String ENTER_NAME_BLACK = "Player 1 BLACK : ";
    private static final String ENTER_NAME_WHITE = "Player 1 WHITE : ";

    private static final String ENTER_MOVE = "Enter your move (e.g. a7 to a5): ";

    private Player playerBlack, playerWhite;
    private Board board;

    private ArrayList<String> moves;

    private String moveStr = "";
    private boolean pieceThrown = false;
    private String winningPlayer = "";


    public static void main(String[] args) {
        startingMethod();
    }

    /**
     * Method này xử lý đầu vào của người chơi để bắt đầu trò chơi.
     */
    private static void startingMethod() {
        new Game();
    }



    /**
     * Constructor để bắt đầu trò chơi
     */
    private Game() {
        moves = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        startGame(scanner);
        startGameRounds(scanner);
        endGame(scanner);

    }

    /**
     * Starting method cho game
     * @param scanner bao gồm scanner được sử dụng hiện tại.
     */
    private void startGame(Scanner scanner) {
        System.out.println(START_GAME_INTRO);

        System.out.print(ENTER_NAME_BLACK);
        playerBlack = new Player(scanner.nextLine(), Color.BLACK);
        System.out.print(ENTER_NAME_WHITE);
        playerWhite = new Player(scanner.nextLine(), Color.WHITE);
        System.out.println("\n Let's start the game: " + playerBlack.getName() + "(BLACK)" + " vs " + playerWhite.getName() + "(WHITE)");
        board = new Board();

        System.out.println(board.toString());

    }


    /**
     * Method bao gồm việc xử lý trò chơi.
     * @param scanner bao gồm scanner được sử dụng hiện tại.
     */
    private void startGameRounds(Scanner scanner) {
        while (!board.isGameOver()) {
            movePlayer(scanner, playerBlack);
            if (board.isGameOver()) {
                break;
            }
            movePlayer(scanner, playerWhite);
        }
    }

    /**
     * method được gọi khi trò chơi kết thúc.
     * @param scanner bao gồm scanner được sử dụng hiện tại.
     */
    private void endGame(Scanner scanner) {
        scanner.close();
        System.out.println("Game Over! " + winningPlayer + " has won the game! Congratulations!");
    }

    /**
     * Xử lý việc di chuyển của người chơi
     * @param scanner bao gồm scanner được sử dụng hiện tại.
     * @param player người chơi hiện tại
     */
    private void movePlayer(Scanner scanner, Player player) {
        System.out.println("It's your turn " + player.getName() + "!");
        System.out.print(ENTER_MOVE);
        String playerMove = scanner.nextLine();
        while (!isValidMove(getStartPos(playerMove), getEndPos(playerMove), board, player)) {
            System.out.println(WRONG_EXPRESSION);
            System.out.print(ENTER_MOVE);
            playerMove = scanner.nextLine();
        }
        handleMovement(playerMove, player, scanner);
        System.out.println(moves);
        System.out.println(board.toString());
    }

    /**
     * Mwthod xử lý chuyển động của người chơi đã di chuyển.
     * Khi đã được kiểm tra, xử lý pawn promotion is valid.
     */
    private void handleMovement(String playerMove, Player player, Scanner scanner) {
        if (isPawnPromotionValid(playerMove)) {
            board.move(getStartPos(playerMove)[0], getStartPos(playerMove)[1], getEndPos(playerMove)[0], getEndPos(playerMove)[1]);
            checkPawnPromotion(player, getEndPos(playerMove)[0], getEndPos(playerMove)[1], scanner, playerMove);
        } else {
            addMoveToArrayList(playerMove, null);
            board.move(getStartPos(playerMove)[0], getStartPos(playerMove)[1], getEndPos(playerMove)[0], getEndPos(playerMove)[1]);
        }
    }


    /**
     * kiểm tra nếu a pawn promotion is valid, đưa the position is correct.
     */
    private boolean isPawnPromotionValid(String playerMove) {
        if (board.getBoard()[getStartPos(playerMove)[0]][getStartPos(playerMove)[1]].getClass().equals(Pawn.class)) {
            return getEndPos(playerMove)[0] == 7 || getEndPos(playerMove)[0] == 0;
        } else {
            return false;
        }
    }

    /**
     * kiểm tra nếu the pawn promotion is valid cho color cụ thể.
     */
    private void checkPawnPromotion(Player player, int destRow, int destCol, Scanner scanner, String playerMove) {
        if (player.getColor() == Color.BLACK) {
            if (destRow == 7) {
                promotePawn(player, scanner, destRow, destCol, playerMove);
            }
        } else {
            if (destRow == 0) {
                promotePawn(player, scanner, destRow, destCol, playerMove);
            }
        }

    }

    /**
     * method này thực thi the pawn promotion (phong cấp cho tốt)
     */
    private void promotePawn(Player player, Scanner scanner, int destRow, int destCol, String playerMove) {
        String[] validInputs = {"bishop", "queen", "rook", "knight"};
        System.out.println(player.getName() + ", you can change your pawn!");
        System.out.print("Enter the type of a ChessPiece you want your pawn to be transformed to (Queen, Knight, Rook or Bishop): ");
        String pieceTrans = scanner.nextLine().toLowerCase();
        while (!Arrays.asList(validInputs).contains(pieceTrans)) {
            System.out.print("Wrong input! Try again: ");
            pieceTrans = scanner.nextLine().toLowerCase();
        }
        switch (pieceTrans) {
            case "queen":
                board.getBoard()[destRow][destCol] = new Queen(player.getColor());
                addMoveToArrayList(playerMove, new Queen(Color.WHITE));
                break;
            case "knight":
                board.getBoard()[destRow][destCol] = new Knight(player.getColor());
                addMoveToArrayList(playerMove, new Knight(Color.WHITE));
                break;
            case "bishop":
                board.getBoard()[destRow][destCol] = new Bishop(player.getColor());
                addMoveToArrayList(playerMove, new Bishop(Color.WHITE));
                break;
            case "rook":
                board.getBoard()[destRow][destCol] = new Rook(player.getColor());
                addMoveToArrayList(playerMove, new Rook(Color.WHITE));
                break;
        }
    }

    /**
     * Thêm một move vào ArrayList "moves"
     * @param playerMove bao gồm String of player đã move
     */
    private void addMoveToArrayList(String playerMove, ChessPiece pawnChangePiece) {
        if (pawnChangePiece != null) {
            moveStr += new Pawn(Color.WHITE).toString();
        } else {
            moveStr += whiteUniCodeChessPiece(playerMove);
        }

        if (pieceThrown) {
            moveStr += playerMove.split(" ")[0] + "x" + playerMove.split(" ")[2];
        } else {
            moveStr += playerMove.split(" ")[0] + "-" + playerMove.split(" ")[2];
            pieceThrown = false;
        }
        if (pawnChangePiece != null) {
            moveStr += pawnChangePiece.toString();
        }
        moves.add(moveStr);
        moveStr = "";
    }

    /**
     * chuyển đổi một quân cờ thành đại diện white color của nó
     * @param playerMove bao gồm String of player đã move
     * @return đại diện white color của một quân cờ
     */
    private String whiteUniCodeChessPiece(String playerMove) {
        int currentRow = getStartPos(playerMove)[0];
        int currentCol = getStartPos(playerMove)[1];

        ChessPieceType chessPiece = board.getBoard()[currentRow][currentCol].getChessPieceType();
        String unicode = "";
        switch (chessPiece) {
            case KING:
                unicode = "\u2654";
                break;
            case QUEEN:
                unicode = "\u2655";
                break;
            case ROOK:
                unicode = "\u2656";
                break;
            case BISHOP:
                unicode = "\u2657";
                break;
            case KNIGHT:
                unicode = "\u2658";
                break;
            case PAWN:
                unicode = "\u2659";
                break;
        }

        return unicode;

    }

    /**
     * Kiểm tra nếu player nhập di chuyển là một di chuyển hợp lệ
     * @param startPos bao gồm the player đã nhập start-position
     * @param endPos bao gồm the player đã nhập destination
     * @param board bao gồm các board obj hiện tại
     * @param player bao gồm player hiện tại mà có thể move.
     * @return nếu đó là một valid move, nó sẽ return về true
     */
    private boolean isValidMove(int[] startPos, int[] endPos, Board board, Player player) {
        if (startPos[0] != -1 && startPos[1] != -1 && endPos[0] != -1 && endPos[1] != -1) {

            ChessPiece chessPieceStart = board.getBoard()[startPos[0]][startPos[1]];
            if (chessPieceStart == null) {
                return false;
            }

            if (chessPieceStart.getColor() == player.getColor()) {
                if (chessPieceStart.getPossibleDestinations(board)[endPos[0]][endPos[1]]) {
                    if (board.getBoard()[endPos[0]][endPos[1]] != null) {
                        ChessPiece chessPieceEnd = board.getBoard()[endPos[0]][endPos[1]];
                        pieceThrown = true;
                        if (chessPieceEnd.getChessPieceType() == ChessPieceType.KING) {
                            board.setGameOver(true);
                            winningPlayer = player.getName();
                        }
                    }
                    return true;
                } else return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * lọc vị trí bắt đầu ra khỏi chuỗi người dùng đã nhập và đặt vị trí trong một mảng số nguyên.
     * nếu mảng bao gồm -1 thì position ko hợp lệ
     * @param move bao gồm player nhập move
     * @return Mảng số nguyên bao gồm 2 giá trị đại diện cho hàng và cột bắt đầu
     */
    private static int[] getStartPos(String move) {
        int[] startPos = {-1, -1};

        setPosNormalGame(move, startPos, 0);

        return startPos;
    }

    /**
     * lọc vị trí đích ra khỏi chuỗi người dùng đã nhập và đặt vị trí trong một mảng số nguyên.
     * nếu mảng bao gồm -1 thì position ko hợp lệ
     * @param move bao gồm player nhập move
     * @return Mảng số nguyên bao gồm 2 giá trị đại diện cho row và column đích
     */
    private static int[] getEndPos(String move) {
        int[] endPos = {-1, -1};

        setPosNormalGame(move, endPos, 2);

        return endPos;
    }


    /**
     * Method trợ giúp cho getStartPos và getEndPos. Method này được gọi khi moves từ new game.
     * @param move Biến này bao gồm vị trí bắt đầu hoặc vị trí đích của đầu vào player.
     * @param pos mehod này đại diện cho IntegerArray được sử dụng trong getEndPos và getStartPos
     * @param startOrEnd method được sử dụng cho start-position hoặc end-position
     */
    private static void setPosNormalGame(String move, int[] pos, int startOrEnd) {
        if (move.split(" ").length == 3) {
            String moveEnd = move.split(" ")[startOrEnd];
            if (moveEnd.length() == 2) {
                char col = moveEnd.charAt(0);
                char row = moveEnd.charAt(1);

                for (int i = 0; i < Board.LETTERS.length; i++) {
                    if (Board.LETTERS[i] == col) {
                        pos[1] = i;
                    }
                }

                for (int i = 7; i >= 0; i--) {
                    if (Board.NUMS[i] == row) {
                        pos[0] = i;
                    }
                }
            }
        }
    }
}

