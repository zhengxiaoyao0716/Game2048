package com.zhengxiaoyao0716.game2048;

import java.util.Map;

/**
 * 回调接口，与游戏进行交互.
 * <p>请实例化该接口，并将这个实现传递给游戏主体Game2048的构造器<br>
 * @author zhengxiaoyao0716	QQ:1499383852
 */
public interface Game2048Communicate
{
	/**
	 * 读取游戏数据.
	 * <p>返回值可以为null，来表示空的存档。这将使游戏以默认构造参数重新开始<br>
	 * 但如果Map不为null，你应当保证其内部几个键值对存在且有效。<br>
	 * 否则仍将视作读档失败处理，幸运的是这意味着并不会造成实质的损害。<br>
	 * 保险的做法是将saveData();方法的参数原样传回，虽然Map内部键值对顺序并不重要<br>
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
	 * 达到目标的分数时调用.
	 * @param level 当前等级（升级结算前）
	 * @param score 当前得分，被设计用来满足记录在某一等级上的最高得分的需求
	 * @return true:进入下一难度关卡 false:重玩当前关卡
	 */
	boolean levelUpIsEnterNextLevel(int level, int score);
	
	/**
	 * 本局游戏失败时调用.
	 * @param level	可以用来保存游戏最高等级记录等
	 * @param score	可以用来保存游戏最高得分记录等
	 * @return true:重新开始（保留关卡） false:不做任何处理
	 */
	boolean gameOverIsReplay(int level, int score);
	
	/**
	 * 退出时保存失败调用.
	 * @return true:仍然退出游戏 false:取消退出操作
	 */
	boolean saveFailedIsStillQuit();
	
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
}