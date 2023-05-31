package glorydark.simpleteleport;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Location;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleTeleportCommand extends Command {
    public SimpleTeleportCommand(String command) {
        super(command);
        List<String> levels = new ArrayList<>();
        Server.getInstance().getOnlinePlayers().values().forEach(level -> levels.add(level.getName()));
        this.commandParameters.put("->Player", new CommandParameter[]{CommandParameter.newType("destination", CommandParamType.TARGET)});
        this.commandParameters.put("->Pos", new CommandParameter[]{CommandParameter.newType("destination", CommandParamType.POSITION), CommandParameter.newType("yRot", true, CommandParamType.VALUE), CommandParameter.newType("xRot", true, CommandParamType.VALUE)});
        this.commandParameters.put("Player->Player", new CommandParameter[]{CommandParameter.newType("player", CommandParamType.TARGET), CommandParameter.newType("destination", CommandParamType.TARGET)});
        this.commandParameters.put("Player->Pos", new CommandParameter[]{CommandParameter.newType("player", CommandParamType.TARGET), CommandParameter.newType("pos", CommandParamType.POSITION), CommandParameter.newEnum("levelName", true, levels.toArray(new String[0]))});
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender.isPlayer() && !commandSender.isOp()){
            commandSender.sendMessage("您没有权限！");
            return true;
        }
        switch (strings.length){
            case 1:
                if(commandSender.isPlayer()) {
                    teleportFromPlayerToPlayer(commandSender.getName(), strings[0], commandSender);
                }else{
                    commandSender.sendMessage("请在游戏内使用！");
                }
                break;
            case 2:
                teleportFromPlayerToPlayer(strings[0], strings[1], commandSender);
                break;
            case 3:
                if(commandSender.isPlayer()) {
                    teleportByPosition((Player) commandSender, strings, commandSender, 0);
                }else{
                    commandSender.sendMessage("请在游戏内使用！");
                }
                break;
            case 4:
            case 5:
                teleportByPosition(strings[0], commandSender, strings);
                break;
            default:
                commandSender.sendMessage("/tp player x y z level");
                return false;
        }
        return true;
    }

    public void teleportFromPlayerToPlayer(String from, String destination, CommandSender sender){
        Player p1 = Server.getInstance().getPlayer(from);
        Player p2 = Server.getInstance().getPlayer(destination);
        if(p1 == null){
            switch (from){
                case "@a":
                    getSelectPlayer("@a").forEach(p -> teleportFromPlayerToPlayer(p.getName(), destination, sender));
                    break;
                case "@p":
                    if(sender.isPlayer()) {
                        getSelectPlayer("@p", (Player) sender).forEach(p -> teleportFromPlayerToPlayer(p.getName(), destination, sender));
                    }else{
                        sender.sendMessage("请在游戏中使用@p");
                    }
                    break;
                case "@r":
                    getSelectPlayer("@r").forEach(p -> teleportFromPlayerToPlayer(p.getName(), destination, sender));
                    break;
                case "@s":
                    if(sender.isPlayer()) {
                        teleportFromPlayerToPlayer(sender.getName(), destination, sender);
                    }else{
                        sender.sendMessage("请在游戏中使用@s");
                    }
                    break;
            }
            return;
        }

        if(p2 == null){
            switch (destination){
                case "@a":
                    getSelectPlayer("@a").forEach(p -> teleportFromPlayerToPlayer(from, p.getName(), sender));
                    break;
                case "@p":
                    if(sender.isPlayer()) {
                        getSelectPlayer("@p", (Player) sender).forEach(p -> teleportFromPlayerToPlayer(from, p.getName(), sender));
                    }else{
                        sender.sendMessage("请在游戏中使用@p");
                    }
                    break;
                case "@r":
                    getSelectPlayer("@r").forEach(p -> teleportFromPlayerToPlayer(from, p.getName(), sender));
                    break;
                case "@s":
                    if(sender.isPlayer()) {
                        p1.teleportImmediate(((Player) sender).getLocation());
                        p1.sendMessage("成功将"+p1.getName()+"传送至"+sender.getName());
                    }else{
                        sender.sendMessage("请在游戏中使用@s");
                    }
                    break;
            }
            return;
        }

        p1.teleportImmediate(p2.getLocation());
        p1.sendMessage("成功将"+p1.getName()+"传送至"+p2.getName());
    }

    public String[] checkStrings(CommandSender sender, String[] strings){
        return checkStrings(sender, strings, 1);
    }

    public String[] checkStrings(CommandSender sender, String[] strings, Integer startIndex){
        if(strings[startIndex].equals("~")){
            if(sender.isPlayer()){
                strings[startIndex] = String.valueOf(((Player) sender).getFloorX());
            }else{
                return null;
            }
        }
        if(strings[startIndex+1].equals("~")){
            if(sender.isPlayer()){
                strings[startIndex+1] = String.valueOf(((Player) sender).getFloorY());
            }else{
                return null;
            }
        }
        if(strings[startIndex+2].equals("~")){
            if(sender.isPlayer()){
                strings[startIndex+2] = String.valueOf(((Player) sender).getFloorZ());
            }else{
                return null;
            }
        }
        if(strings.length > startIndex + 3) {
            if (strings[startIndex + 3].equals("~")) {
                if (sender.isPlayer()) {
                    strings[startIndex + 3] = String.valueOf(((Player) sender).getLevel().getName());
                } else {
                    return null;
                }
            }
        }
        return strings;
    }

    public List<Player> getSelectPlayer(String prefix){
        switch (prefix){
            case "@a":
                return new ArrayList<>(Server.getInstance().getOnlinePlayers().values());
            case "@r":
                return Collections.singletonList(getRandomPlayer());
        }
        return new ArrayList<>();
    }

    public List<Player> getSelectPlayer(String prefix, Player player){
        if ("@p".equals(prefix)) {
            return Collections.singletonList(getNearestPlayer(player));
        }
        if ("@s".equals(prefix)) {
            return Collections.singletonList(player);
        }
        return new ArrayList<>();
    }

    public Player getNearestPlayer(Player player){
        AtomicReference<Player> nearest = new AtomicReference<>();
        AtomicReference<Double> nearestDistance = new AtomicReference<>();
        Server.getInstance().getOnlinePlayers().values().forEach(player1 -> {
            if(!player1.getName().equals(player.getName())) {
                if (player1.getLevel().equals(player.getLevel())) {
                    if (nearest.get() == null) {
                        nearest.set(player1);
                        nearestDistance.set(player.distance(player1));
                    } else {
                        Double dis = player.distance(player1);
                        if (dis > nearestDistance.get()) {
                            nearest.set(player1);
                            nearestDistance.set(dis);
                        }
                    }
                }
            }
        });
        if (nearest.get() != null) {
            return nearest.get();
        }
        return player;
    }

    public Player getRandomPlayer(){
        Collection<Player> collection = Server.getInstance().getOnlinePlayers().values();
        if(collection.size() > 0) {
            Player[] players = collection.toArray(new Player[0]);
            Random random = new Random(System.currentTimeMillis());
            int index = random.nextInt(players.length);
            return players[index];
        }
        return null;
    }

    public void teleportByPosition(Player player, String[] strings, CommandSender sender){
        teleportByPosition(player, strings, sender, 1);
    }

    public void teleportByPosition(Player player, String[] strings, CommandSender sender, Integer startIndex){
        Location location = null;
        strings = checkStrings(player, strings, startIndex);
        if(strings == null) {
            sender.sendMessage("请在游戏内执行此指令！");
            return;
        }
        switch (strings.length){
            case 3:
                location = new Location(Double.parseDouble(strings[startIndex]), Double.parseDouble(strings[startIndex+1]), Double.parseDouble(strings[startIndex+2]), player.getLevel());
                break;
            case 4:
                if(startIndex == 0) {
                    location = new Location(Double.parseDouble(strings[startIndex]), Double.parseDouble(strings[startIndex + 1]), Double.parseDouble(strings[startIndex + 2]), Server.getInstance().getLevelByName(strings[startIndex+3]));
                }else{
                    location = new Location(Double.parseDouble(strings[startIndex]), Double.parseDouble(strings[startIndex + 1]), Double.parseDouble(strings[startIndex + 2]), player.getLevel());
                }
                break;
            case 5:
                location = new Location(Double.parseDouble(strings[startIndex]), Double.parseDouble(strings[startIndex+1]), Double.parseDouble(strings[startIndex+2]), Server.getInstance().getLevelByName(strings[startIndex+3]));
                break;
        }
        if(location == null){
            return;
        }
        player.teleportImmediate(location, null);
        sender.sendMessage("成功传送玩家"+player.getName()+"到"+ location);
    }

    public void teleportByPosition(String player, CommandSender sender, String[] strings){
        Location location = null;
        switch (player){
            case "@a":
                if(sender.isPlayer()){
                    strings = checkStrings(sender, strings);
                }
                String[] finalStrings = strings;
                if(finalStrings != null) {
                    getSelectPlayer("@a").forEach(p -> teleportByPosition(p, finalStrings, sender));
                }
                return;
            case "@p":
                if(sender.isPlayer()){
                    strings = checkStrings(sender, strings);
                }
                if(strings != null) {
                    String[] finalStrings1 = strings;
                    getSelectPlayer("@p", (Player) sender).forEach(p -> teleportByPosition(p, finalStrings1,sender));
                }else{
                    sender.sendMessage("请在游戏内使用！");
                }
                return;
            case "@r":
                if(sender.isPlayer()){
                    strings = checkStrings(sender, strings);
                }
                String[] finalStrings2 = strings;
                if(strings != null) {
                    getSelectPlayer("@r").forEach(p -> teleportByPosition(p, finalStrings2, sender));
                }
                return;
            case "@s":
                if(sender.isPlayer()){
                    strings = checkStrings(sender, strings);
                    switch (strings.length){
                        case 4:
                            location = new Location(Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3]), ((Player) sender).getLevel());
                            break;
                        case 5:
                            location = new Location(Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3]), Server.getInstance().getLevelByName(strings[4]));
                            break;
                    }
                    if(location == null){
                        return;
                    }
                    sender.sendMessage("成功将玩家"+sender.getName()+"传送到"+ location);
                    ((Player) sender).teleportImmediate(location);
                }
                return;
        }
        Player p = Server.getInstance().getPlayer(player);
        if(p == null || !p.isOnline()){
            if(Server.getInstance().isLevelLoaded(strings[3]) || strings[3].equals("~")){
                if(sender.isPlayer()) {
                    teleportByPosition((Player) sender, strings, sender, 0);
                    return;
                }
            }
            sender.sendMessage("该玩家不在线！");
            return;
        }
        if(sender.isPlayer()){
            strings = checkStrings(sender, strings);
        }
        if(strings == null) {
            return;
        }
        switch (strings.length){
            case 4:
                location = new Location(Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3]), p.getLevel());
                break;
            case 5:
                location = new Location(Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3]), Server.getInstance().getLevelByName(strings[4]));
                break;
        }
        if(location == null){
            return;
        }
        p.teleportImmediate(location, null);
        sender.sendMessage("成功传送玩家"+player+"到"+ location);
    }
}
