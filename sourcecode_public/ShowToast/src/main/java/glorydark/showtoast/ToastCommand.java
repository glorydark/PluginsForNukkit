package glorydark.showtoast;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class ToastCommand extends Command {
    public ToastCommand(String sendtoast) {
        super(sendtoast);
    }

    @Override //sendtoast player content
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player && !commandSender.isOp()){ commandSender.sendMessage(TextFormat.RED+"您没有权限！");return true;}
        if(strings.length == 3){
            Player player = Server.getInstance().getPlayer(strings[0]);
            if(player != null){
                if(player.isOnline()) {
                    ToastRequestPacket packet = new ToastRequestPacket();
                    packet.title = strings[1];
                    packet.content = strings[2];
                    player.dataPacket(packet);
                    commandSender.sendMessage(TextFormat.GREEN + "发送toast成功！");
                }else{
                    commandSender.sendMessage(TextFormat.RED + "玩家不在线！");
                }
            }
        }else{
            commandSender.sendMessage("格式错误，应为：/sendtoast player title content");
        }
        return true;
    }
}
