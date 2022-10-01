package glorydark.sudo;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class SudoCommand extends Command {
    public SudoCommand(String command) {
        super(command);
        this.commandParameters.put("default", new CommandParameter[]{CommandParameter.newType("player", CommandParamType.TARGET),CommandParameter.newType("sudo content", CommandParamType.STRING)});
        this.setUsage("/sudo <player> <command/message> | [Example] /sudo steve /me 233");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(strings.length < 2){ return false; }
        if(!commandSender.isPlayer()){
            StringBuilder builder = new StringBuilder();
            for(String parameter: strings){
                if(strings[strings.length-1].equals(parameter)){
                    builder.append(parameter);
                }else {
                    builder.append(parameter + " ");
                }
            }
            parseSudoCommand(commandSender, strings[0], builder.toString().replace(strings[0]+ " ", ""));
        }else{
            if(commandSender.isOp()){
                StringBuilder builder = new StringBuilder();
                for(String parameter: strings){
                    if(strings[strings.length-1].equals(parameter)){
                        builder.append(parameter);
                    }else {
                        builder.append(parameter + " ");
                    }
                }
                parseSudoCommand(commandSender, strings[0], builder.toString().replace(strings[0]+ " ", ""));
            }
        }
        return true;
    }

    public void parseSudoCommand(CommandSender sender, String player, String sudo){
        switch (player){
            case "@a":
                Server.getInstance().getOnlinePlayers().values().forEach(p -> parseSudoCommand(sender, p.getName(), sudo));
                return;
            case "@p":
                if(!sender.isPlayer()){ sender.sendMessage("[§cSudo§f] You should use '@p' in game!"); return; }
                AtomicReference<Player> nearest = new AtomicReference<>();
                AtomicReference<Double> nearestDistance = new AtomicReference<>();
                AtomicReference<Player> relativeNearest = new AtomicReference<>();
                AtomicReference<Double> relativeNearestDistance = new AtomicReference<>();
                Server.getInstance().getOnlinePlayers().values().forEach(player1 -> {
                    if(!player1.getName().equals(sender.getName())) {
                        if (player1.getLevel().equals(((Player) sender).getLevel())) {
                            if (nearest.get() == null) {
                                nearest.set(player1);
                                nearestDistance.set(((Player) sender).distance(player1));
                            } else {
                                Double dis = ((Player) sender).distance(player1);
                                if (dis > nearestDistance.get()) {
                                    nearest.set(player1);
                                    nearestDistance.set(dis);
                                }
                            }
                        } else {
                            if (relativeNearest.get() == null) {
                                relativeNearest.set(player1);
                            } else {
                                Double dis = ((Player) sender).distance(player1);
                                if (dis > relativeNearestDistance.get()) {
                                    relativeNearest.set(player1);
                                    relativeNearestDistance.set(dis);
                                }
                            }
                        }
                    }
                });
                if (nearest.get() != null) {
                    parseSudoCommand(sender, nearest.get().getName(), sudo);
                    return;
                }
                if (relativeNearest.get() != null) {
                    parseSudoCommand(sender, nearest.get().getName(), sudo);
                    return;
                }
                parseSudoCommand(sender, sender.getName(), sudo);
                return;
            case "@r":
                Collection<Player> collection = Server.getInstance().getOnlinePlayers().values();
                if(collection.size() > 0) {
                    Player[] players = collection.toArray(new Player[collection.size() - 1]);
                    Random random = new Random();
                    Integer index = random.nextInt(players.length);
                    parseSudoCommand(sender, players[index].getName(), sudo);
                }else{
                    sender.sendMessage("[§cSudo§f] §l§cThere is no player online now!");
                }
                return;
        }
        if(sudo.startsWith("/")){
            Player p = Server.getInstance().getPlayer(player);
            if(p != null && p.isOnline()){
                String command = sudo.replaceFirst("/","");
                Server.getInstance().dispatchCommand(p, command);
                sender.sendMessage("[§aSudo§f] Successfully ran command [§a"+command+"§f] as [§a"+player+"§f]");
            }else{
                sender.sendMessage("[§cSudo§f] §l§cPlayer is not online or not existed!");
            }
        }else{
            Server.getInstance().getOnlinePlayers().values().forEach(pl -> pl.sendMessage("<"+player+"> "+ sudo));
            if(!sender.isPlayer()){ sender.sendMessage("<"+player+"> "+ sudo); }
            sender.sendMessage("[§aSudo§f] Successfully said [§a"+sudo+"§f] as [§a"+player+"§f]");
        }
    }
}
