# Game2048<br> [![Join the chat at https://gitter.im/zhengxiaoyao0716/Digimon2048](https://badges.gitter.im/zhengxiaoyao0716/Digimon2048.svg)](https://gitter.im/zhengxiaoyao0716/Digimon2048?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
##功能齐全的2048.<br>
###使用说明：<br>
####step 1：实现游戏所需的功能<br>
    您需要一个回调接口(GameCommunicate)的实例，请按照提示实现接口中的所有方法。
####step 2：初始化游戏<br>
    将上一步中的接口实例作为参数，与游戏的默认参数一起作为构造器的签名，创建游戏实例。
####Step 3：开始游戏<br>
    调用startGame();方法即可。并将自动为你载入上次的存档（如果存在的话）
####Step 4：运行游戏<br>
    循环调用action(int direction);参数为玩家移动方向。
####Step 5：退出游戏<br>
____调用finishGame();方法，您可能需要根据该方法返回值来确认是否已经安全退出
####Other：
    您可能需要更多的游戏功能：
        replay(boolean isKeepLevel); 重新游戏，可选择是否保留当前关卡。
        backStep(); 后退一步，即悔棋~
        cleanGrid(int height, int width); 清除某一格，即炸弹~
    你应当按自然的顺序来进行游戏，不要试图做一些违反常识的行为
        例如，你显然不应该在未开始化游戏前结束游戏，如果你那样做了，将会收到一个IllegalStateException异常

版本: 2.0.0<br>
作者: 正逍遥0716 QQ:1499383852<br>
