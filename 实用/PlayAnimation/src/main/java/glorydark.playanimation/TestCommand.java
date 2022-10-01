package glorydark.playanimation;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.network.protocol.AddEntityPacket;

public class TestCommand extends Command {
    public TestCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player) {
            long id = Entity.entityCount++;
            Player p = ((Player) commandSender).getPlayer();
            AddEntityPacket packet = new AddEntityPacket();
            packet.entityUniqueId = id;
            packet.entityRuntimeId = id;
            packet.id = strings[0];
            packet.x = (float) p.x;
            packet.y = (float) p.y;
            packet.z = (float) p.z;
            packet.metadata.putString(Entity.DATA_NAMETAG, "测试");
            p.dataPacket(packet);
        }
        return true;
    }
}
