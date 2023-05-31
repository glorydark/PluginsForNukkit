package testgame;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.*;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import gameapi.arena.Arena;
import gameapi.effect.Effect;
import gameapi.event.room.RoomEndEvent;
import gameapi.event.room.RoomGameEndEvent;
import gameapi.event.room.RoomGameStartEvent;
import gameapi.listener.RoomGameProcessingListener;
import gameapi.listener.RoomPreStartListener;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.scoreboard.ScoreboardAPI;
import gameapi.sound.Sound;
import gameapi.utils.GameRecord;
import testgame.scripts.CustomSkill;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static testgame.MainClass.path;

public class Event implements Listener {
    public static HashMap<Room, List<Player>> roomFinishPlayers = new HashMap<>();
    public static ConcurrentHashMap<Player, FormWindowSimple> playerFormWindowSimpleHashMap = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void touch(PlayerInteractEvent event){
        Room room = Room.getRoom("DRecknessHero", event.getPlayer());
        Player player = event.getPlayer();
        Item item = event.getItem();
        if(room == null){ return; }
        if(item instanceof ItemBookEnchanted && item.getCustomName().equals("§l§c退出房间")){
            room.removePlayer(player,true);
            player.sendMessage("§l§c您已退出房间！");
            return;
        }

        if(item instanceof ItemEmerald && item.getCustomName().equals("§l§a历史战绩")){
            Window.showPlayerHistoryWindow(player);
            return;
        }

        if (item instanceof ItemTotem && item.getCustomName().equals("§l§e选择职业")) {
            if(MainClass.skillEnabled) {
                Window.showPlayerSkillSelectWindow(player);
                return;
            }
        }

        if(item instanceof ItemPaper && item.getCustomName().equals("§l§e选择地图")){
            Window.showVoteForMap(player);
            return;
        }

        if(event.getBlock().getId() == Block.EMERALD_BLOCK && room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameStart) {
            if (!roomFinishPlayers.get(room).contains(event.getPlayer())) {
                roomFinishPlayers.get(room).add(event.getPlayer());
                int lastSec = room.getGameTime() - room.getTime();
                ScoreboardAPI.drawScoreBoardEntry(event.getPlayer(),MainClass.getScoreboardSetting("scoreboard_objective_name"),MainClass.getScoreboardSetting("scoreboard_display_name"),MainClass.getScoreboardSetting("rank_format").replace("%rank%", String.valueOf(roomFinishPlayers.get(room).indexOf(event.getPlayer())+1)),MainClass.getScoreboardSetting("time_format").replace("%time%",ScoreboardAPI.secToTime(lastSec)));
                if (room.getTime() < room.getGameTime() - 15) {
                    room.setTime(room.getGameTime() - 15);
                }
                for (Player p : room.getPlayers()) {
                    p.sendMessage(TextFormat.LIGHT_PURPLE+"%s 到达终点！".replace("%s", event.getPlayer().getName()));
                }
                /*
                switch (roomFinishPlayers.get(room).size()){
                    case 1:
                        if (room.getTime() < room.getGameTime() - 15) {
                            room.setTime(room.getGameTime() - 15);
                            for (Player p : room.getPlayers()) {
                                p.sendMessage("%s 玩家获得冠军！".replace("%s", event.getPlayer().getName()));
                                UIScoreboard.drawScoreBoardEntry(p,MainClass.getScoreboardSetting("scoreboard_objective_name"),MainClass.getScoreboardSetting("scoreboard_display_name"),"\uE176 Rank: 1st");
                            }
                        }
                        break;
                    case 2:
                        for (Player p : room.getPlayers()) {
                            p.sendMessage("%s 玩家获得亚军！".replace("%s", event.getPlayer().getName()));
                            UIScoreboard.drawScoreBoardEntry(p,MainClass.getScoreboardSetting("scoreboard_objective_name"),MainClass.getScoreboardSetting("scoreboard_display_name"),"\uE176 Rank: 2nd");
                        }
                        break;
                    case 3:
                        for (Player p : room.getPlayers()) {
                            p.sendMessage("%s 玩家获得季军！".replace("%s", event.getPlayer().getName()));
                            UIScoreboard.drawScoreBoardEntry(p,MainClass.getScoreboardSetting("scoreboard_objective_name"),MainClass.getScoreboardSetting("scoreboard_display_name"),"\uE176 Rank: 3rd");
                        }
                        break;
                    default:
                        for (Player p : room.getPlayers()) {
                            p.sendMessage("%s 玩家到达终点！".replace("%s", event.getPlayer().getName()));
                            UIScoreboard.drawScoreBoardEntry(p,MainClass.getScoreboardSetting("scoreboard_objective_name"),MainClass.getScoreboardSetting("scoreboard_display_name"),"\uE176 Rank: "+(roomFinishPlayers.get(room).indexOf(event.getPlayer())+1)+"th");
                        }
                        break;
                }

                 */
            }
        }
    }


    @EventHandler
    public void breakBlocks(BlockBreakEvent event){
        Room room = Room.getRoom("DRecknessHero", event.getPlayer());
        if(room == null){return;}
        if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){ return; }
        List<Effect> effectList = MainClass.getBlockAddonsInit(event.getBlock());
        if(effectList != null){
            for(Effect effect:effectList){
                effect.giveEffect(event.getPlayer());
                event.getPlayer().sendMessage("获得"+ cn.nukkit.potion.Effect.getEffect(effect.getId()).getName() +"*"+effect.getAmplifier()+"*"+effect.getDuration()/20+"秒");
            }
        }
        if(event.getBlock().getId() == Block.RED_MUSHROOM_BLOCK){
            Item item = Item.get(Block.REDSTONE_BLOCK);
            item.setCount(5);
            event.getPlayer().getInventory().addItem(item);
            event.getPlayer().sendMessage("获得5个屏障方块！");
        }
        event.setDrops(new Item[0]);
        event.setDropExp(0);
    }

    @EventHandler
    public void RoomGameStartEvent(RoomGameStartEvent event){
        Room room = event.getRoom();
        if(!event.getRoom().getGameName().equals("DRecknessHero")){ return;}
        for(Player p:room.getPlayers()){
            Event.roomFinishPlayers.put(event.getRoom(),new ArrayList<>());
            Item pickaxe = Item.get(Item.DIAMOND_PICKAXE);
            pickaxe.setCount(1);
            pickaxe.setCustomName("英雄之镐");
            p.getInventory().setItem(0, pickaxe);
            if(MainClass.skillEnabled) {
                MainClass.skills.get((String) room.getPlayerProperties(p.getName(), "skill1")).giveSkillItem(p, true);
            }
            Sound.playResourcePackOggMusic(p, "game_begin");
        }
    }

    @EventHandler
    public void ceremony(RoomGameEndEvent event){
        if(!event.getRoom().getGameName().equals("DRecknessHero")){ return;}
        List<Player> players = roomFinishPlayers.get(event.getRoom());
        for(Player p:event.getRoom().getPlayers()){
            if(players.contains(p)){
                GameRecord.addGameRecord("DRecknessHero",p.getName(), "win",1);
                p.sendMessage("§l§e您已成功完成比赛 §l§a"+GameRecord.getGameRecord("DRecknessHero",p.getName(),"win")+" §l§e次");
                p.sendTitle("比赛结束","恭喜您获得了第"+(players.indexOf(p)+1)+"名",10,20,10);
                Sound.playResourcePackOggMusic(p,"winning");
                event.getRoom().executeWinCommands(p);
            }else{
                GameRecord.addGameRecord("DRecknessHero",p.getName(), "lose",1);
                p.sendMessage("§l§e您未完成比赛 §l§c"+GameRecord.getGameRecord("DRecknessHero",p.getName(),"lose")+" §l§e次");
                p.sendTitle("比赛结束","您未完成比赛！",10,20,10);
                Sound.playResourcePackOggMusic(p, "game_over");
                event.getRoom().executeLoseCommands(p);
            }
        }
    }

    @EventHandler
    public void end(RoomEndEvent event){
        if(!event.getRoom().getGameName().equals("DRecknessHero")){ return;}
        if(event.getRoom().getTemporary()){
            roomFinishPlayers.remove(event.getRoom());
            MainClass.roomListHashMap.remove(event.getRoom());
        }else {
            roomFinishPlayers.put(event.getRoom(), new ArrayList<>());
        }
    }

    @EventHandler
    public void RoomGameProcessingListener(RoomGameProcessingListener event){
        Room room = event.getRoom();
        if(!room.getGameName().equals("DRecknessHero")){ return;}
        int lastSec = room.getGameTime() - room.getTime();
        for(Player p:room.getPlayers()){
            if(lastSec < room.getGameTime()){
                if (!roomFinishPlayers.get(room).contains(p)) {
                    ScoreboardAPI.drawScoreBoardEntry(p, MainClass.getScoreboardSetting("scoreboard_objective_name"), MainClass.getScoreboardSetting("scoreboard_display_name"), MainClass.getScoreboardSetting("time_format").replace("%time%", ScoreboardAPI.secToTime(lastSec)));
                } else {
                    ScoreboardAPI.drawScoreBoardEntry(p, MainClass.getScoreboardSetting("scoreboard_objective_name"), MainClass.getScoreboardSetting("scoreboard_display_name"), MainClass.getScoreboardSetting("rank_format").replace("%rank%", String.valueOf(roomFinishPlayers.get(room).indexOf(p) + 1)), MainClass.getScoreboardSetting("time_format").replace("%time%", ScoreboardAPI.secToTime(lastSec)));
                }
            }
        }
    }

    @EventHandler
    public void GuiRespondedEvent(PlayerFormRespondedEvent event){
        if(event.getResponse() == null){
            playerFormWindowSimpleHashMap.remove(event.getPlayer());
            return;
        }
        if(Event.playerFormWindowSimpleHashMap.containsKey(event.getPlayer())){
            if(event.getWindow() != Event.playerFormWindowSimpleHashMap.get(event.getPlayer())){ return; }
            if(!(event.getWindow() instanceof FormWindowSimple)){ return; }
            playerFormWindowSimpleHashMap.remove(event.getPlayer());
            String title = ((FormWindowSimple)event.getWindow()).getTitle();
            FormResponseSimple formResponseSimple = (FormResponseSimple) event.getResponse();
            Player player = event.getPlayer();
            switch (title) {
                case "§l§e选择房间":
                    Room room = Room.getRoom("DRecknessHero", formResponseSimple.getClickedButton().getText());
                    MainClass.processJoin(room, player);
                    break;
                case "§l§e选择技能":
                    Room room1 = Room.getRoom("DRecknessHero", player);
                    if (room1 != null) {
                        player.sendMessage("您已选择技能: "+formResponseSimple.getClickedButton().getText());
                        room1.setPlayerProperties(player.getName(), "skill1", MainClass.skills.values().toArray(new CustomSkill[0])[formResponseSimple.getClickedButtonId()].getIdentifier());
                    }
                    break;
                case "§l§e选择地图":
                    Room room2 = Room.getRoom("DRecknessHero", player);
                    if (room2 != null) {
                        Map<String, Integer> map = (Map<String, Integer>) room2.getRoomProperties("mapRanks");
                        map.put(formResponseSimple.getClickedButton().getText(), map.getOrDefault(formResponseSimple.getClickedButton().getText(), 0) + 1);
                        room2.setRoomProperties("mapRanks", map);
                        player.sendMessage("您为地图【" + formResponseSimple.getClickedButton().getText() + "】投上一票！");
                        player.getInventory().setItem(1, new BlockAir().toItem());
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void RoomPreStartListener(RoomPreStartListener event){
        Room room = event.getRoom();
        if(!room.getGameName().equals("DRecknessHero")){ return;}
        if(room.getWaitTime() - room.getTime() == 10){
            if(room.getTemporary()) {
                Map<String, Integer> map = (Map<String, Integer>) room.getRoomProperties("mapRanks");
                List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
                list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                if (list.size() > 0) {
                    Map.Entry<String, Integer> first = list.get(0);
                    if(loadRoomMap(room, first.getKey())) {
                        room.getPlayers().forEach(player -> player.sendMessage("已选择地图： "+ first.getKey()+"【"+first.getValue()+"票】"));
                    }else{
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
                    }
                }else{
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
                }
            }
        }
    }

    public boolean loadRoomMap(Room room, String map){
        Config config = new Config(path+"/maps.yml", Config.YAML);
        if (config.exists(map + ".LoadWorld")) {
            String backup = config.getString(map + ".LoadWorld", "null");
            room.setRoomLevelBackup(backup);
            room.setRoomName(backup);
            if (Server.getInstance().getLevelByName(config.getString(map)) == null) {
                String newName = room.getGameName() + "_" + backup + "_" + UUID.randomUUID();
                if (Arena.copyWorldAndLoad(newName, backup)) {
                    if (Server.getInstance().isLevelLoaded(newName)) {
                        Server.getInstance().getLevelByName(newName).setAutoSave(false);
                        if(config.exists(map+".WaitSpawn")){
                            room.setWaitSpawn(config.getString(map+".WaitSpawn").replace(backup, newName));
                        }else{
                            return false;
                        }
                        if(config.exists(map+".StartSpawn")){
                            room.addStartSpawn(config.getString(map+".StartSpawn").replace(backup, newName));
                        }else{
                            return false;
                        }
                        if(config.exists(map+".WaitTime")){
                            room.setWaitTime(config.getInt(map+".WaitTime"));
                        }else{
                            return false;
                        }
                        if(config.exists(map+".GameTime")){
                            room.setGameTime(config.getInt(map+".GameTime"));
                        }else{
                            return false;
                        }
                        room.setEndSpawn(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation());
                        room.setWinConsoleCommands(new ArrayList<>(config.getStringList(map+".WinCommands")));
                        room.setLoseConsoleCommands(new ArrayList<>(config.getStringList(map+".FailCommands")));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
