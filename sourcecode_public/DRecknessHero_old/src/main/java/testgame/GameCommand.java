package testgame;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import gameapi.room.Room;
import gameapi.room.RoomRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static testgame.MainClass.roomListHashMap;

public class GameCommand extends Command {
    public GameCommand(String name) {
        super(name,"暴走英雄","/drh");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if(strings.length > 0){
            switch (strings[0]){
                case "joinrandom":
                    if(sender.isPlayer()) {
                        Player player = (Player) sender;
                        for (Room room : MainClass.roomListHashMap) {
                            if (room.getTemporary()) {
                                if (room.addPlayer(player)) {
                                    sender.sendMessage("已为您加入房间！");
                                    MainClass.processJoin(room, player);
                                    return true;
                                }
                            }
                        }
                        MainClass.processJoin(loadRoom(), player);
                    }
                    break;
                case "join":
                    if(strings.length > 1) {
                        if (Server.getInstance().getPlayer(sender.getName()) != null) {
                            Player player = Server.getInstance().getPlayer(sender.getName());
                            Room room = Room.getRoom("DRecknessHero", strings[1]);
                            MainClass.processJoin(room, player);
                        } else {
                            sender.sendMessage("§l§c请在游戏内使用！");
                        }
                    }
                    break;
                case "quit":
                    if (Server.getInstance().getPlayer(sender.getName()) != null) {
                        Player player = Server.getInstance().getPlayer(sender.getName());
                        Room room = Room.getRoom(player);
                        if(room != null){
                            room.removePlayer(player,true);
                            if(room.getPlayers().size() == 0){
                                roomListHashMap.remove(room);
                            }
                            player.sendMessage("§l§c您已退出房间！");
                        }else{
                            player.sendMessage("§l§c您不在房间内！");
                        }
                    } else {
                        sender.sendMessage("§l§c请在游戏内使用！");
                    }
                    break;
                case "list":
                    sender.sendMessage("§l§e>> 游戏房间列表 <<");
                    for(Room room:MainClass.roomListHashMap){
                        switch (room.getRoomStatus()){
                            case ROOM_STATUS_Ceremony:
                            case ROOM_STATUS_GameEnd:
                            case ROOM_STATUS_End:
                                sender.sendMessage("房间名:"+room.getRoomName()+" §l§6【即将结束】");
                                break;
                            case ROOM_STATUS_PreStart:
                            case ROOM_STATUS_GameStart:
                                sender.sendMessage("房间名:"+room.getRoomName()+" §l§c【游戏中】");
                                break;
                            case ROOM_STATUS_GameReadyStart:
                            case ROOM_STATUS_NextRoundPreStart:
                            case ROOM_STATUS_WAIT:
                                sender.sendMessage("房间名:"+room.getRoomName()+"【"+room.getPlayers().size()+"/"+room.getMaxPlayer()+"】 §l§a【等待中】");
                                break;
                        }
                    }
                    break;
                case "help":
                    sender.sendMessage("§l§e>> 指令帮助 <<");
                    sender.sendMessage("§l§e/drh join 房间名 - §l§r加入某个房间");
                    sender.sendMessage("§l§e/drh quit - §l§r退出房间");
                    sender.sendMessage("§l§e/drh list - §l§r查看房间列表");
                    break;
            }
        }else{
            if (Server.getInstance().getPlayer(sender.getName()) != null) {
                Window.showPlayerRoomListWindow((Player) sender);
            } else {
                sender.sendMessage("请在游戏内使用！");
            }
        }
        return true;
    }

    public Room loadRoom(){
        RoomRule roomRule = new RoomRule(0);
        roomRule.allowBreakBlock = false;
        roomRule.allowPlaceBlock = false;
        roomRule.allowFallDamage = false;
        roomRule.allowDamagePlayer = false;
        roomRule.allowHungerDamage = false;
        roomRule.allowFoodLevelChange = false;
        roomRule.noStartWalk = false;
        roomRule.canBreakBlocks.add("100:14");
        roomRule.canPlaceBlocks.add("152:0");
        roomRule.canBreakBlocks.addAll(MainClass.effectHashMap.keySet());
        Room room = new Room("DRecknessHero", roomRule, "", 1);
        room.setTemporary(true);
        room.setResetMap(false);
        room.setRoomRule(roomRule);
        room.setMinPlayer(1);
        room.setWaitTime(30);
        Map<String, Integer> mapRanks = new HashMap<>();
        for(String mapName: MainClass.maps){
            mapRanks.put(mapName, 0);
        }
        room.setRoomProperties("mapRanks", mapRanks);
        Event.roomFinishPlayers.put(room,new ArrayList<>());
        MainClass.roomListHashMap.add(room);
        Room.loadRoom(room);
        return room;
    }
}
