# Game2048<br>
##功能齐全的2048.<br>
###使用说明：<br>
####step 1：实现游戏所需的功能<br>
    您需要一个回调接口(GameCommunicate)的实例，请按照提示实现接口中的所有方法。<br>
####step 2：初始化游戏<br>
    将上一步中的接口实例作为参数，与游戏的默认参数一起作为构造器的签名，创建游戏实例。<br>
####Step 3：开始游戏<br>
    调用startGame();方法即可。并将自动为你载入上次的存档（如果存在的话）<br>
    你也可以通过replay(boolean isKeepLevel);方法来开始游戏，但不被推荐你这么做。<br>
####Step 4：运行游戏<br>
    循环调用action(int direction);参数为玩家移动方向。<br>
####Step 5：退出游戏<br>
    调用quitGame();方法，您可能需要判断该方法返回值来确认是否已经安全退出。<br>
####Other：
    您可能需要更多的游戏功能：<br>
        replay(boolean isKeepLevel); 重新游戏，可选择是否保留当前关卡。<br>
        backStep(); 后退一步，即悔棋~<br>
        cleanGrid(int height, int width); 清除某一格，即炸弹~<br>
<br>
版本:<br>
1.0<br>
作者:<br>
正逍遥0716 QQ:1499383852<br>
