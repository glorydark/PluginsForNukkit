package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("魅力值帮助", "魅力值帮助", "/魅力值帮助");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(commandSender.isOp()){
                commandSender.sendMessage(TextFormat.YELLOW+">>      Helper      <<");
                commandSender.sendMessage(TextFormat.GREEN+"增加玩家魅力值: /增加魅力值 玩家名 魅力值数量");
                commandSender.sendMessage(TextFormat.GREEN+"减少玩家魅力值: /减少魅力值 玩家名 魅力值数量");
                commandSender.sendMessage(TextFormat.GREEN+"设置玩家魅力值: /设置魅力值 玩家名 魅力值数量");
                commandSender.sendMessage(TextFormat.GREEN+"重载数据: /重载对话数据");
                commandSender.sendMessage(TextFormat.GREEN+"查看成就: /对话成就");
                commandSender.sendMessage(TextFormat.GREEN+"查看排行榜: /魅力值排行榜");
                commandSender.sendMessage(TextFormat.GREEN+"播放对话（后台指令）: /播放对话 玩家名 对话名");
                commandSender.sendMessage(TextFormat.GREEN+"播放对话（游戏内指令）: /播放对话 对话名");
                commandSender.sendMessage(TextFormat.YELLOW+">>      Helper      <<");
            }else{
                commandSender.sendMessage(TextFormat.YELLOW+">>      Helper      <<");
                commandSender.sendMessage(TextFormat.GREEN+"查看成就: /对话成就");
                commandSender.sendMessage(TextFormat.GREEN+"查看排行榜: /魅力值排行榜");
                commandSender.sendMessage(TextFormat.GREEN+"播放对话（游戏内指令）: /播放对话 对话名");
                commandSender.sendMessage(TextFormat.YELLOW+">>      Helper      <<");
            }
        }else{
            commandSender.sendMessage(TextFormat.YELLOW+">>      Helper      <<");
            commandSender.sendMessage(TextFormat.GREEN+"增加玩家魅力值: /增加魅力值 玩家名 魅力值数量");
            commandSender.sendMessage(TextFormat.GREEN+"减少玩家魅力值: /减少魅力值 玩家名 魅力值数量");
            commandSender.sendMessage(TextFormat.GREEN+"设置玩家魅力值: /设置魅力值 玩家名 魅力值数量");
            commandSender.sendMessage(TextFormat.GREEN+"重载数据: /重载对话数据");
            commandSender.sendMessage(TextFormat.GREEN+"查看成就: /对话成就");
            commandSender.sendMessage(TextFormat.GREEN+"查看排行榜: /魅力值排行榜");
            commandSender.sendMessage(TextFormat.GREEN+"播放对话（后台指令）: /播放对话 玩家名 对话名");
            commandSender.sendMessage(TextFormat.GREEN+"播放对话（游戏内指令）: /播放对话 对话名");
            commandSender.sendMessage(TextFormat.YELLOW+">>      Helper      <<");
        }
        return true;
    }
}
