package com.zhengxiaoyao0716.game2048;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * 功能齐全的2048.
 * <P>使用说明：<br>
 * step 1：实现游戏所需的功能<br>
 * ____您需要一个回调接口(GameCommunicate)的实例，请按照提示实现接口中的所有方法。<br>
 * step 2：初始化游戏<br>
 * ____将上一步中的接口实例作为参数，与游戏的默认参数一起作为构造器的签名，创建游戏实例。<br>
 * Step 3：开始游戏<br>
 * ____调用startGame();方法即可。并将自动为你载入上次的存档（如果存在的话）<br>
 * Step 4：运行游戏<br>
 * ____循环调用action(int direction);参数为玩家移动方向。<br>
 * Step 5：退出游戏<br>
 * ____调用finishGame();方法，您可能需要根据该方法返回值来确认是否已经安全退出。<br>
 * Other：<br>
 * ____您可能需要更多的游戏功能：<br>
 * ________replay(boolean isKeepLevel);			重新游戏，可选择是否保留当前关卡。<br>
 * ________backStep();							后退一步，即悔棋~<br>
 * ________cleanGrid(int height, int width);	清除某一格，即炸弹~<br>
 * ____你应当按自然的顺序来进行游戏，不要试图做一些违反常识的行为<br>
 * ________例如，你显然不应该在未开始化游戏前结束游戏，如果你那样做了，将会收到一个IllegalStateException异常<br>
 * @author 正逍遥0716 QQ:1499383852
 * @version 1.0
 */
public class Game2048 {
	private Game2048Communicate communicate;
	private enum GameState
	{
		SLEEPING, RUNNING, SAVE_FAILED, GAME_END, LEVEL_UP
	}
	private GameState gameState;
	private int boardH, boardW, aimNum;
	
	private int level;
	private int score;
	private int[][] board, lastBoard;
	/**
	 * 初始化游戏体.
	 * @param communicate 游戏回调接口
	 * @param boardH 默认的棋盘高度，不小于2
	 * @param boardW 默认的棋盘宽度, 不小于2
	 * @param aimNum 默认的目标数字, 不小于8.
	 * @throws NullPointerException communicate为null
	 * @throws IllegalArgumentException 参数不符合限制
	 */
	public Game2048(Game2048Communicate communicate,
			int boardH, int boardW, int aimNum) throws NullPointerException, IllegalArgumentException
	{
		if (communicate == null)
			throw new NullPointerException("communicate==null");
		if (boardH < 2)
			throw new IllegalArgumentException("boardH < 2");
		if (boardW < 2)
			throw new IllegalArgumentException("boardW < 2");
		if (aimNum < 8)
			throw new IllegalArgumentException("boardH < 8");

		gameState = GameState.SLEEPING;

		this.communicate = communicate;
		this.boardH = boardH;
		this.boardW = boardW;
		this.aimNum = aimNum;
	}
	
	/**
	 * 开始游戏.
	 * <p>调用这个方法将使游戏从SLEEPING进入RUNNING状态，并且将会自动加载存档<br>
	 * 如果加载成功，棋盘高度、宽度、游戏目标等以存档为准<br>
	 * 如果加载失败则以默认参数开始新游戏<br>
	 * @throws IllegalStateException 你仅应当在游戏处于结束状态时调用这个方法，否则会收这个错误
	 */
	public synchronized void startGame() throws IllegalStateException
	{
		if (gameState != GameState.SLEEPING)
			throw new IllegalStateException("Game is already running.");
		else gameState = GameState.RUNNING;

		lastBoard = null;
		
		if (!loadData())
		{
			board = new int[boardH][boardW];
			level = 1;
			score = 0;
			
			birthNew();
		}
		
		showData(level, score, board);
	}
	
	/**
	 * 退出游戏.
	 * <p>当成功退出游戏后游戏将进入SLEEPING状态，你只能通过startGame();方法来唤醒它<br>
	 * @return true:保存成功 false:保存失败，游戏将处于SAVE_FAILED状态，你必须通过saveFailedRespond();方法来唤醒它。
	 * @throws IllegalStateException 你仅应当在游戏处于运行状态时调用这个方法，否则会收这个错误
	 */
	public synchronized boolean finishGame() throws IllegalStateException
	{
		if (gameState != GameState.RUNNING) throw new IllegalStateException("Game isn't running");
		else if (saveData()) {
			board = lastBoard = null;
			level = score = 0;
			gameState = GameState.SLEEPING;
			return true;
		}
		else {
			gameState = GameState.SAVE_FAILED;
			return false;
		}
	}
	
	/**
	 * 重新开始.
	 * 这个方法会将游戏从任何状态置为RUNNING状态。<br>
	 * 但你最好不要轻易这么做，尤其不要以此来将游戏从SLEEPING唤醒，<br>
	 * 这会使得上次的游戏进度无法自动读取，但却仍然存在。<br>
	 * 它本意主要是用来在RUNNING状态下重新开始游戏。
	 * @param isKeepLevel 是否保留当前关卡
	 */
	public synchronized void replay(boolean isKeepLevel)
	{
		lastBoard = null;
		
		if (isKeepLevel && level > 1)
		{
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
					if (board[height][width]<=aimNum)
						board[height][width] = 0;
			score = 0;
		}
		else
		{
			board = new int[boardH][boardW];
			score = 0;
			level = 1;
		}
		
		birthNew();
		showData(level, score, board);
	}
	
	private enum ChangeResult
	{
		NO_CHANGE, MOVED, MERGED, LEVEL_UP
	}
	private ChangeResult changeResult;

	public static final int UP		= 0;
	public static final int LEFT	= 1;
	public static final int RIGHT	= 2;
	public static final int DOWN	= 3;
	
	/**
	 * 一次完整动作.
	 * <p>一次完整动作后，游戏可能会进入GAME_END、LEVEL_UP状态<br>
	 * 你应当针对在作完相应的事件处理后调用相应的方法：<br>
	 * gameEndRespond();、levelUpRespond();，来回到RUNNING状态<br>
	 * 当然也可能继续处于普通的RUNNING状态下。<br>
	 * 建议你直接传入这个类定义的常量，UP, LEFT, RIGHT, DOWN。<br>
	 * 如果有需要可以传入其对应的int值0, 1, 2, 3。<br>
	 * 你可用这个算法简单计算得到期望的值：<br>
	 * 设有直角坐标系，height为纵轴，width为横轴<br>
	 * downH、downW、upH、upW分别为按下/抬起点的纵/横坐标 <br>
	 * 纵/横轴移动偏移量moveH = upH - downH; moveW = upW - downW;<br>
	 * 则传入action的移动方向参数可表达为：<br>
	 * {@code direction = ((moveH + moveW) > 0 ? 0 : 2) + (moveH > moveW) ? 0: 1);}<br>
	 * @param direction 0:down 1:right 2:left 3:up
	 * @throws IllegalStateException 你仅应当在游戏处于运行状态时调用这个方法，否则会收这个错误
	 * @throws IllegalArgumentException 参数direction不符合限制
	 */
	public synchronized void action(int direction) throws IllegalStateException, IllegalArgumentException
	{
		if (gameState != GameState.RUNNING) throw new IllegalStateException("Game isn't running");

		int[][] lastBoard = board.clone();
		
		changeResult = ChangeResult.NO_CHANGE;
		//change
		changeBoard(direction);
		cleanZero();
		mergeGrid();
		cleanZero();
		changeBoard(direction);
		
		switch (changeResult)
		{
		case NO_CHANGE:
		{
			if (isGameOver(direction))
			{
				gameState = GameState.GAME_END;
				return;
			}
			else
			{
				communicate.noChangeRespond();
				return;
			}
		}
		case MOVED:
			communicate.movedRespond();
			break;
		case MERGED:
			communicate.mergedRespond();
			break;
		case LEVEL_UP:
		{
			lastBoard = null;
			showData(level, score, board);
			score = 0;
		}break;
		}

		this.lastBoard = lastBoard;
		birthNew();
		showData(level, score, board);
	}

	/*游戏道具*/
	/**
	 * 撤销上一动作.
	 * @return true:撤销成功 false:撤销失败（没有可撤销的步骤）
	 * @throws IllegalStateException 你仅应当在游戏处于运行状态时调用这个方法，否则会收这个错误
	 */
	public synchronized boolean backStep() throws IllegalStateException
	{
		if (gameState != GameState.RUNNING) throw new IllegalStateException("Game isn't running");

		if (lastBoard==null) return false;
		board = lastBoard.clone();
		showData(level, score, board);
		return true;
	}
	/**
	 * 清除某个格子.
	 * @param height	[0, boardH)
	 * @param width		[0, boardW)
	 * @return true:清除成功 false:清除失败（该格子不存在或值为0）
	 * @throws IllegalStateException 你仅应当在游戏处于运行状态时调用这个方法，否则会收这个错误
	 */
	public synchronized boolean cleanGrid(int height, int width) throws IllegalStateException
	{
		if (gameState != GameState.RUNNING) throw new IllegalStateException("Game isn't running");

		if (height < 0 || height>=boardH || width < 0 || width>=boardW)
			return false;
		else if (board[height][width]==0) return false;
		board[height][width] = 0;
		showData(level, score, board);
		return true;
	}

	/*状态响应*/
	/**
	 * 保存失败的响应.
	 * @param forceEnd 是否强制结束游戏
	 * @throws IllegalStateException 当游戏并未处于'SAVE_FAILED'状态时错误的调用会抛出这个异常
	 */
	public void saveFailedRespond(boolean forceEnd) throws IllegalStateException
	{
		if (gameState != GameState.SAVE_FAILED)
			throw new IllegalStateException("Game isn't in 'SAVE_FAILED' state.");
		else if (forceEnd)
		{
			board = lastBoard = null;
			level = score = 0;
			gameState = GameState.SLEEPING;
		}
		else gameState = GameState.RUNNING;
	}
	/**
	 * 游戏结束的响应.
	 * @param isReplay 是否重玩当前关卡，false则什么也不做。
	 * @throws IllegalStateException 当游戏并未处于'GAME_END'状态时错误的调用会抛出这个异常
	 */
	public void gameOverRespond(boolean isReplay) throws IllegalStateException
	{
		if (gameState != GameState.GAME_END)
			throw new IllegalStateException("Game isn't in 'GAME_END' state.");
		else if (isReplay) replay(true);
		gameState = GameState.RUNNING;
	}
	/**
	 * 达到目标的响应.
	 * @param goNextLevel 是否进入下一难度关卡，false则重玩当前关卡
	 * @throws IllegalStateException 当游戏并未处于'LEVEL_UP'状态时错误的调用会抛出这个异常
	 */
	public void levelUpRespond(boolean goNextLevel) throws IllegalStateException
	{
		if (gameState != GameState.LEVEL_UP)
			throw new IllegalStateException("Game isn't in 'LEVEL_UP' state.");
		else if (goNextLevel)
		{
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
				{
					if (board[height][width]==aimNum)
						board[height][width]+=level;
					else if (board[height][width] <aimNum)
						board[height][width] = 0;
				}
			level++;
		}
		else
		{
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
					if (board[height][width]<=aimNum)
						board[height][width] = 0;
		}
		gameState = GameState.RUNNING;
	}

	/*过程方法*/
	//方向转换
	private void changeBoard(int direction) throws IllegalArgumentException
	{
		int boardH = board.length;
		int boardW = board[0].length;
		switch (direction)
		{
		case UP:			//up
		{
			int[][] tempBoard = new int[boardW][boardH];
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
					tempBoard[width][height]
							= board[height][width];
			board = tempBoard;
		}break;
		case LEFT:			//left
			break;
		case RIGHT:			//right
		{
			int[][] tempBoard = new int[boardH][boardW];
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
					tempBoard[height][boardW - width - 1]
							= board[height][width];
			board = tempBoard;
		}break;
		case DOWN:			//down
		{
			int[][] tempBoard = new int[boardW][boardH];
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
					tempBoard[boardW - width - 1][boardH - height - 1]
							= board[height][width];
			board = tempBoard;
		}break;
		default :
			throw new IllegalArgumentException("direction < 0 || direction > 3");
		}
	}
	//清除零格
	private void cleanZero()
	{
		for (int height = 0; height < board.length; height++)
		{
			ArrayList<Integer> tempRow = new ArrayList<>(board[0].length);
			int noneZeroPos = 0;
			for (int width = 0; width < board[0].length; width++)
				if (board[height][width]!=0)
				{
					noneZeroPos = width + 1;
					tempRow.add(board[height][width]);
				}
			if (tempRow.size() < noneZeroPos && changeResult==ChangeResult.NO_CHANGE)
				changeResult = ChangeResult.MOVED;
			
			for (int width = 0; width < tempRow.size(); width++)
				board[height][width] = tempRow.get(width);
			for (int width = tempRow.size(); width < board[0].length; width++)
				board[height][width] = 0;
		}
	}
	//合并格子
	private void mergeGrid()
	{
		for (int height = 0; height < board.length; height++)
			for (int width = 1; width < board[height].length; width++)
				if (board[height][width]!=0
				&& board[height][width]==board[height][width - 1])
				{
					changeResult = ChangeResult.MERGED;
					score+=board[height][width];
										
					board[height][width - 1]<<=1;
					board[height][width] = 0;
					
					if (board[height][width - 1]>=aimNum)
					{
						changeResult = ChangeResult.LEVEL_UP;
						return;
					}
				}
	}
	//生成新的
	private void birthNew()
	{
		ArrayList<int[]> zeroPoints = new ArrayList<>();
		for (int height = 0; height < boardH; height++ )
		{
			for (int width = 0; width < boardW; width++)
			{
				if (board[height][width]==0)
				{
					zeroPoints.add(new int[]{height, width});
					width++;
				}
			}
		}
		int randPoint = new Random().nextInt(zeroPoints.size());
		board[zeroPoints.get(randPoint)[0]][zeroPoints.get(randPoint)[1]]
				= (1 + new Random().nextInt(2))<<1;
	}
	//是否结束
	private boolean isGameOver(int direction) throws IllegalArgumentException
	{
		changeBoard(direction);
		boolean isGameOver = true;
		for (int height = 0; height < boardH; height++)
			if (board[height][boardW - 1]==0) isGameOver = false;
		for (int width = 0; width < boardW; width++)
			for (int height = 1; height < boardH; height++)
				if (board[height][width]==board[height - 1][width])
					isGameOver = false;
		changeBoard(direction);
		return isGameOver;
	}

	/*辅助方法*/
	//读档
	private synchronized boolean loadData()
	{
		//load
		HashMap<String, Object> dataMap
		= (HashMap<String, Object>) communicate.loadData();
		if (dataMap==null) return false;
		
		int aimNumBackup = aimNum;
		int levelBackup = level;
		int scoreBackup = score;
		int[][] boardBackup = board;
		try {
			aimNum = (Integer) dataMap.get("aimNum");
			level = (Integer) dataMap.get("level");
			score = (Integer) dataMap.get("score");
			board = (int[][]) dataMap.get("board");
			board = board.clone();
		} catch (Exception e) {
			aimNum = aimNumBackup;
			level = levelBackup;
			score = scoreBackup;
			board = boardBackup;
			return false;
		}
		boardH = board.length;
		boardW = board[0].length;
		return true;
	}
	//存档
	private synchronized boolean saveData()
	{
		//save
		HashMap<String, Object> dataMap = new HashMap<>();
		dataMap.put("aimNum", aimNum);
		dataMap.put("level", level);
		dataMap.put("score", score);
		dataMap.put("board", board.clone());
		return communicate.saveData(dataMap);
	}
	//显示
	private synchronized void showData(int level, int score, int[][] board)
	{
		communicate.showData(level, score, board.clone());
	}
}