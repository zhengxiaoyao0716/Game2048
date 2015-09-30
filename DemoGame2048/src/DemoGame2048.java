import java.util.Map;
import java.util.Scanner;

import com.zhengxiaoyao0716.game2048.*;

/**
 * SimpleDemo
 * @author zhengxiaoyao0716 QQ:1499383852
 */
public class DemoGame2048 {
    private static boolean isGameRunning;
    private static Scanner scanner;

    public static void main(String[] args)
    {
        Game2048 game2048 = null;
        try {
            game2048 = new Game2048(gameCommunicate, 4, 4, 2048);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        scanner = new Scanner(System.in);
        GAME_LOOP:
        while (true)
        {
            if (isGameRunning)
            {
                System.out.println("Game is Running.");
                switch (scanner.next())
                {
                    case "w":
                    case "up":
                        try {
                            game2048.action(Game2048.UP);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case "a":
                    case "left":
                        try {
                            game2048.action(Game2048.LEFT);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case "d":
                    case "right":
                        try {
                            game2048.action(Game2048.RIGHT);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case "s":
                    case "down":
                        try {
                            game2048.action(Game2048.DOWN);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;

                    case "f":
                    case "replay":
                    {
                        System.out.println("Keep level?");
                        String input = scanner.next();
                        game2048.replay(
                                input.equals("g") || input.equals("yes"));
                        System.out.println("Replay.");
                    }break;
                    case "g":
                    case "quit":
                    {
                        if (game2048.quitGame())
                        {
                            System.out.println("Quit.");
                            isGameRunning = false;
                        }
                        else System.out.println("Quit game failed");
                    }break;

                    case "h":
                    case "backStep":
                    {
                        if (!game2048.backStep()) System.out.println("Back failed.");
                        else System.out.println("backStep.");
                    }break;
                    case "j":
                    case "cleanGrid":
                    {
                        System.out.println("Witch row?");
                        int rowPos = scanner.nextInt();
                        System.out.println("Witch col?");
                        int colPos = scanner.nextInt();
                        if (!game2048.cleanGrid(rowPos - 1, colPos - 1)) System.out.println("Clean failed.");
                        else System.out.printf("The num in grid[%d, %d] has been cleaned.\n", rowPos, colPos);
                    }break;

                    default :
                        System.out.println("Unknown input!");
                        break;
                }
            }
            else
            {
                System.out.println("Game is waiting.");
                System.out.println("You can input 'f' or 'exit' to exit.");
                System.out.println("Input 'g' or 'start' to start.");
                switch (scanner.next())
                {
                    case "f":
                    case "exit":
                        System.out.println("Game end.");
                        break GAME_LOOP;
                    case "g":
                    case "start":
                    {
                        game2048.startGame();
                        isGameRunning = true;
                        System.out.printf("Game start.\n"
                                + "Commend list:\n"
                                + "w->up\n"
                                + "s->down\n"
                                + "a->left\n"
                                + "d->right\n"
                                + "f->replay\n"
                                + "g->quit\n"
                                + "h->backStep\n"
                                + "j->cleanGrid\n");
                    }break;
                    default :
                        System.out.println("Unknown input!");
                        break;
                }
            }
        }
        scanner.close();
    }

    private static final Game2048Communicate gameCommunicate = new Game2048Communicate()
    {
        @Override
        public Map<String, Object> loadData() {
            // TODO Auto-generated method stub
            System.out.println("Loading data");
            return null;
        }

        @Override
        public boolean saveData(Map<String, Object> data) {
            // TODO Auto-generated method stub
            System.out.println("Saving data");
            return false;
        }

        @Override
        public void showData(int level, int score, int[][] board) {
            // TODO Auto-generated method stub
            System.out.printf("level:\t%d\n", level);
            System.out.printf("score:\t%d\n", score);

            System.out.print("==");
            for (int width  = 0; width < board[0].length; width++)
                System.out.print("========");

            System.out.printf("\n||");
            for (int width  = 0; width < board[0].length; width++)
                System.out.printf("%4c\t", ' ');
            System.out.printf("||\n");

            for (int[] row : board)
            {
                System.out.print("||");
                for (int grid : row)
                {
                    if (grid!=0) System.out.printf("%4d\t", grid);
                    else System.out.printf("    \t");
                }

                System.out.printf("||\n||");
                for (int width  = 0; width < board[0].length; width++)
                    System.out.printf("%4c\t", ' ');
                System.out.printf("||\n");
            }

            System.out.print("==");
            for (int width  = 0; width < board[0].length; width++)
                System.out.print("========");
            System.out.println();
        }

        @Override
        public boolean levelUpIsEnterNextLevel(int level, int score) {
            // TODO Auto-generated method stub
            System.out.println("Level up!");
            System.out.println("Are you willing to play hard-mode?");
            String input = scanner.next();
            return input.equals("g")||input.equals("yes");
        }

        @Override
        public boolean gameOverIsReplay(int level, int score) {
            // TODO Auto-generated method stub
            System.out.println("Game over!");
            System.out.println("Continue?");
            String input = scanner.next();
            if (input.equals("g")||input.equals("yes")) return true;
            else
            {
                isGameRunning = false;
                System.out.println("Quit.");
                return false;
            }
        }

        @Override
        public boolean saveFailedIsStillQuit() {
            // TODO Auto-generated method stub
            System.out.println("Save failed!");
            System.out.println("Still quit game?");
            String input = scanner.next();
            if (input.equals("g")||input.equals("yes"))
            {
                isGameRunning = false;
                return true;
            }
            else return false;
        }

        @Override
        public void noChangeRespond() {
            // TODO Auto-generated method stub
            System.out.println("Invalid action!");
        }

        @Override
        public void movedRespond() {
            // TODO Auto-generated method stub
            System.out.println("Moved.");
        }

        @Override
        public void mergedRespond() {
            // TODO Auto-generated method stub
            System.out.println("Merged.");
        }
    };
}