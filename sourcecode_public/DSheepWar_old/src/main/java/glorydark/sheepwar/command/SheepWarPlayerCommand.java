package glorydark.sheepwar.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookEnchanted;
import gameapi.room.Room;

public class SheepWarPlayerCommand extends cn.nukkit.command.Command {
    public SheepWarPlayerCommand() {
        super("sheepwar");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender.isPlayer())){
            commandSender.sendMessage("请在游戏内执行！");
            return true;
        }

        if(strings.length > 0){
            switch (strings[0]){
                case "joinroom":
                case "加入房间":
                    if(strings.length > 1){
                        Room room = Room.getRoom("SheepWar", strings[1]);
                        if(room != null){
                            room.addPlayer(((Player) commandSender));
                            Item addItem1 = new ItemBookEnchanted();
                            addItem1.setCustomName("§l§c退出房间");
                            ((Player) commandSender).getInventory().setItem(0,addItem1);
                        }
                    }
                    break;
            }
        }else{
            return false;
        }
        return true;
    }
}
