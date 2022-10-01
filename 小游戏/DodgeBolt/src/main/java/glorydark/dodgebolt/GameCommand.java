package glorydark.dodgebolt;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.window.FormWindow;
import gameapi.room.Room;
import glorydark.dodgebolt.forms.CreateGui;

import java.util.HashMap;

public class GameCommand extends Command {

    public static HashMap<Player, FormWindow> windowHashMap = new HashMap<>();
    public GameCommand(String cmd) {
        super(cmd);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(strings.length == 0) {
            if (commandSender.isPlayer()) {
                CreateGui.showMainMenu((Player) commandSender);
            }
        }else{
            if(commandSender.isPlayer()) {
                if ("quit".equals(strings[0])) {
                    Room room = Room.getRoom("DodgeBolt", (Player) commandSender);
                    if (room != null) {
                        room.removePlayer((Player) commandSender, true);
                    } else {
                        ((Player) commandSender).teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());
                    }
                }
            }
        }
        return true;
    }
}
