package glorydark.playanimation;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Glorydark
 * Thanks for iGxnon's SquareLottery!
 */
public class PlayAnimationCommand extends Command {
    public PlayAnimationCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.isPlayer()){
            commandSender.sendMessage("Please use it in game!");
            return true;
        }
        if(strings.length != 2){
            commandSender.sendMessage("/playanimation AnimationName Duration");
            commandSender.sendMessage("AnimationName: https://minecraft.fandom.com/wiki/Commands/playanimation#List_of_animations");
            return true;
        }
        sendPacket((Player) commandSender, strings[0], Float.parseFloat(strings[1]));
        Server.getInstance().getScheduler().scheduleDelayedTask(MainClass.instance, ()->{
            sendPacket((Player) commandSender, "animation.player.first_person.walk", 0F); //恢复原动作
        }, 80);
        return true;
    }

    public void senPacket(AnimateEntityPacket packet, Player target) {
        target.dataPacket(packet);
    }

    public void sendPacket(Player player, String animation, Float duration){
        //By iGxnon
        // (Now added an entry to support v1.17.40 anim)
        AnimateEntityPacket packet = new AnimateEntityPacket();
        packet.setAnimation(animation);
        packet.setBlendOutTime(duration);
        packet.setNextState("");
        packet.setController("query.any_animation_finished");
        packet.setStopExpression("");
        packet.setStopExpressionVersion(0); //1.17.40 and newer needed!
        Set<Long> set = new HashSet<>();
        set.add(player.getId());
        packet.setEntityRuntimeIDs(set);
        Server.getInstance().getOnlinePlayers().values().forEach(p -> senPacket(packet, player));
    }
}
