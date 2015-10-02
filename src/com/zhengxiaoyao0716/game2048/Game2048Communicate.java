package com.zhengxiaoyao0716.game2048;

import java.util.Map;

/**
 * 委托你实现的接口.<br>
 * <p>
 * 你需要实现这个接口，并将这个接口的一个实例作为构造器签名，<br>
 * 传递给游戏主体Game2048的构造器，以供游戏主体调用它的方法，<br>
 * 从而实现游戏逻辑与玩家的交流。<br>
 * </p>
 * @author zhengxiaoyao0716	QQ:1499383852
 */
public interface Game2048Communicate
{
	/**
	 * 读取游戏数据.
	 * <p>
	 * 返回值可以为null，来表示空的存档。这将使游戏以默认构造参数重新开始<br>
	 * 但如果Map不为null，你应当保证其内部几个键值对存在且有效。<br>
	 * 否则仍将视作读档失败处理，幸运的是这意味着并不会造成实质的损害。<br>
	 * 保险的做法是将saveData();方法的参数原样传回，虽然Map内部键值对顺序并不重要<br>
	 * </p>
	 * @return {"aimNum":Integer, "level":Integer, "score":Integer, "board":int[][]}
	 */
	Map<String, Object> loadData();
	/**
	 * 保存游戏数据.
	 * @param dataMap {"aimNum":Integer, "level":Integer, "score":Integer, "board":int[][]}
	 * @return true:保存成功 false:保存失败
	 */
	boolean saveData(Map<String, Object> dataMap);
	/**
	 * 显示游戏数据.
	 * @param level 当前关卡
	 * @param score 当前分数
	 * @param board 棋盘
	 */
	void showData(int level, int score, int[][] board);
	
	/**
	 * 棋盘未发生变化调用.
	 */
	void noChangeRespond();
	/**
	 * 当且仅当移动时调用.
	 */
	void movedRespond();
	/**
	 * 发生有效合并时调用.
	 */
	void mergedRespond();

	/**
	 * 你需要回调的接口.<br>
	 * <p>
	 * 游戏逻辑的具体方法里实现了这个接口，<br>
	 * 并作为参数传给了communicate的几个方法，<br>
	 * 你需要回调它的commit方法提交玩家的选择。<br>
	 * </p>
	 */
	interface Informer
	{
		/**
		 * 你需要在传出的informer上调用这个方法，将玩家的选择作为参数传回.
		 * @param decision 玩家的选择，具体含义以传入informer参数的方法的说明为准<br>
		 */
		void commit(boolean decision);
	}
	/**
	 * 开始游戏读档失败时调用.
	 * @param informer 调用其commit();方法提交玩家的选择，true:开始新的游戏 false:什么也不做
	 */
	void loadFailedIsStartNew(final Informer informer);
	/**
	 * 结束游戏保存失败时调用.
	 * @param informer 调用其commit();方法提交玩家的选择，true:强制结束游戏 false:继续游戏
	 */
	void saveFailedIsStillFinish(final Informer informer);
	/**
	 * 本局游戏失败时调用.
	 * @param level	可以用来保存游戏最高等级记录等
	 * @param score	可以用来保存游戏最高得分记录等
	 * @param informer 调用其commit();方法提交玩家的选择，true:重玩当前关卡 false:返回查看原因
	 */
	void gameEndReplayThisLevel(int level, int score, final Informer informer);
	/**
	 * 达到目标的分数时调用.
	 * @param level 当前等级（升级结算前）
	 * @param score 当前得分，被设计用来满足记录在某一等级上的最高得分的需求
	 * @param informer 调用其commit();方法提交玩家的选择，true:进入下一难度关卡 false:重玩当前关卡
	 */
	void levelUpEnterNextLevel(int level, int score, final Informer informer);
}