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
        Game2048 game2048 = new Game2048(gameCommunicate, 4, 4, 2048);

        scanner = new Scanner(System.in);
        while (true)
        {
            if (isGameRunning)
            {
                System.out.println("Game is Running.");

                String s = scanner.next();
                if (s.equals("w") || s.equals("up")) {
                    game2048.action(Game2048.UP);

                } else if (s.equals("a") || s.equals("left")) {
                    game2048.action(Game2048.LEFT);

                } else if (s.equals("d") || s.equals("right")) {
                    game2048.action(Game2048.RIGHT);

                } else if (s.equals("s") || s.equals("down")) {
                    game2048.action(Game2048.DOWN);

                } else if (s.equals("f") || s.equals("replay")) {
                    System.out.println("Keep level?");
                    String input = scanner.next();
                    game2048.replay(
                            input.equals("g") || input.equals("yes"));
                    System.out.println("Replay.");
                } else if (s.equals("g") || s.equals("quit")) {
                    if (game2048.finishGame()) {
                        System.out.println("Quit.");
                        isGameRunning = false;
                    }
                } else if (s.equals("h") || s.equals("backStep")) {
                    if (!game2048.backStep()) System.out.println("Back failed.");
                    else System.out.println("backStep.");
                } else if (s.equals("j") || s.equals("cleanGrid")) {
                    System.out.println("Witch row?");
                    int rowPos = scanner.nextInt();
                    System.out.println("Witch col?");
                    int colPos = scanner.nextInt();
                    if (!game2048.cleanGrid(rowPos - 1, colPos - 1)) System.out.println("Clean failed.");
                    else System.out.printf("The num in grid[%d, %d] has been cleaned.\n", rowPos, colPos);
                } else {
                    System.out.println("Unknown input!");

                }
            }
            else
            {
                System.out.println("Game is waiting.");
                System.out.println("You can input 'f' or 'exit' to exit.");
                System.out.println("Input 'g' or 'start' to start.");

                String s = scanner.next();
                if (s.equals("f") || s.equals("exit")) {
                    System.out.println("Game end.");
                    break;
                } else if (s.equals("g") || s.equals("start")) {
                    if (game2048.startGame())
                    {
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
                    }
                } else {
                    System.out.println("Unknown input!");

                }
            }
        }
        scanner.close();
    }

    private static final Game2048Communicate gameCommunicate = new Game2048Communicate()
    {
        @Override
        public Map<String, Object> loadData() {
            System.out.println("Loading data...");
            return null;
        }

        @Override
        public boolean saveData(Map<String, Object> data) {
            System.out.println("Saving data...");
            return false;
        }

        @Override
        public void showData(int level, int score, int[][] board) {
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
                    else System.out.printf("%4c\t", ' ');
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
        public void noChangeRespond() {
            System.out.println("Invalid action!");
        }

        @Override
        public void movedRespond() {
            System.out.println("Moved.");
        }

        @Override
        public void mergedRespond() {
            System.out.println("Merged.");
        }

        @Override
        public void loadFailedIsStartNew(Informer informer) {
            System.out.println("Load failed!");
            System.out.println("Start new game?");
            String input = scanner.next();
            if (input.equals("g")||input.equals("yes"))
            {
                isGameRunning = true;
                informer.commit(true);
            }
            else informer.commit(false);
        }

        @Override
        public void saveFailedIsStillFinish(Informer informer) {
            System.out.println("Save failed!");
            System.out.println("Still quit game?");
            String input = scanner.next();
            if (input.equals("g")||input.equals("yes"))
            {
                isGameRunning = false;
                informer.commit(true);
            }
            else informer.commit(false);
        }

        @Override
        public void gameEndReplayThisLevel(int level, int score, Informer informer) {
            System.out.println("Game over!");
            System.out.println("Continue?");
            String input = scanner.next();
            if (input.equals("g")||input.equals("yes")) informer.commit(true);
            else
            {
                isGameRunning = false;
                System.out.println("Quit.");
                informer.commit(false);
            }
        }

        @Override
        public void levelUpEnterNextLevel(int level, int score, Informer informer) {
            System.out.println("Level up!");
            System.out.println("Are you willing to play hard-mode?");
            String input = scanner.next();
            informer.commit(input.equals("g") || input.equals("yes"));
        }
    };
}