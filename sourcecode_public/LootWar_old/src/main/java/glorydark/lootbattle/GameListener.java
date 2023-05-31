package glorydark.lootbattle;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.item.ItemCompass;
import cn.nukkit.item.ItemEmerald;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import gameapi.event.base.RoomPlayerRespawnEvent;
import gameapi.event.base.RoomPlayerVisualDeathEvent;
import gameapi.event.room.RoomGameEndEvent;
import gameapi.event.room.RoomGameStartEvent;
import gameapi.listener.RoomGameProcessingListener;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.room.Team;
import gameapi.scoreboard.ScoreboardAPI;
import gameapi.utils.GameRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameListener implements Listener {

    public static ConcurrentHashMap<Player, FormWindowSimple> playerFormWindowSimpleHashMap = new ConcurrentHashMap<>();

    @EventHandler
    public void GuiRespondedEvent(PlayerFormRespondedEvent event){
        if(event.getResponse() == null){
            playerFormWindowSimpleHashMap.remove(event.getPlayer());
            return;
        }
        if(playerFormWindowSimpleHashMap.containsKey(event.getPlayer())){
            if(event.getWindow() != playerFormWindowSimpleHashMap.get(event.getPlayer())){ return; }
            if(!(event.getWindow() instanceof FormWindowSimple)){ return; }
            playerFormWindowSimpleHashMap.remove(event.getPlayer());
            String title = ((FormWindowSimple)event.getWindow()).getTitle();
            FormResponseSimple formResponseSimple = (FormResponseSimple) event.getResponse();
            Player player = event.getPlayer();
            if ("LootBattle - §l§e选择房间".equals(title)) {
                Room room = MainClass.roomList.get(formResponseSimple.getClickedButtonId());
                if (room.addPlayer(player)) {
                    event.getPlayer().sendMessage("§e本游戏来源于中元节的一项习俗：抢孤");
                    event.getPlayer().sendMessage("§c游戏规则：你需要奋力冲向高台，与你的队友一同去争夺高台的贡品以获得分数，死亡将会§c失去§f你自身的贡品分数");
                    event.getPlayer().sendMessage("§a据说当鬼魂看到一群比自己还要凶猛抢夺祭品的人时，会被吓得逃开哦。");
                    Item addItem1 = new ItemBookEnchanted();
                    addItem1.setCustomName("§l§c退出房间");
                    player.getInventory().setItem(0, addItem1);
                    Item addItem2 = new ItemEmerald();
                    addItem2.setCustomName("§l§a历史战绩");
                    player.getInventory().setItem(7, addItem2);
                }else {
                    event.getPlayer().teleportImmediate(room.getPlayers().get(0).getLocation());
                    event.getPlayer().setGamemode(3);
                    Item addItem1 = new ItemCompass();
                    addItem1.setCustomName("§l§c观战助手");
                    player.getInventory().setItem(0, addItem1);
                    event.getPlayer().sendMessage("您已进入观战模式！可使用 /lootbattle quit 退出");
                }
            }
            if("§l§a观战助手".equals(title)){
                String[] s = ((FormResponseSimple) event.getResponse()).getClickedButton().getText().split("\n");
                if(s.length == 2) {
                    Player p = Server.getInstance().getPlayer(s[1]);
                    event.getPlayer().teleportImmediate(p.getLocation());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void touch(PlayerInteractEvent event) {
        Room room = Room.getRoom("LootBattle", event.getPlayer());
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (room == null) {
            return;
        }
        if (item instanceof ItemBookEnchanted && item.getCustomName().equals("§l§c退出房间")) {
            room.removePlayer(player, true);
            player.sendMessage("§l§c您已退出房间！");
            return;
        }

        if (item instanceof ItemEmerald && item.getCustomName().equals("§l§a历史战绩")) {
            Window.showPlayerHistoryWindow(player);
        }
    }

    @EventHandler
    public void gameStart(RoomGameStartEvent event){
        if(event.getRoom().getGameName().equals("LootBattle")) {
            event.getRoom().getPlayers().forEach(player -> {
                player.getInventory().clearAll();
                player.sendTitle("§6§l冲向高台吧，去争取属于你的荣耀！");
                Team team = event.getRoom().getPlayerTeam(player);
                player.sendMessage("你被分配到：" + team.getPrefix() + team.getRegistryName());
                player.getInventory().addItem(Item.get(Item.STONE_SWORD), Item.get(Item.ENDER_PEARL), Item.get(Item.SNOWBALL, 0, 4), Item.get(Item.FISHING_ROD));
            });
        }
    }

    @EventHandler
    public void RoomPlayerRespawnEvent(RoomPlayerRespawnEvent event){
        if(event.getRoom().getGameName().equals("LootBattle")) {
            event.getPlayer().getInventory().addItem(Item.get(Item.STONE_SWORD), Item.get(Item.ENDER_PEARL), Item.get(Item.SNOWBALL, 0, 4), Item.get(Item.FISHING_ROD));
        }
    }

    @EventHandler
    public void InventoryPickupItemEvent(InventoryPickupItemEvent event){
        for(Player player: event.getViewers()){
            Room room = Room.getRoom("LootBattle", player);
            if(room != null) {
                switch (event.getItem().getNameTag()){
                    case "§6§l金色贡品":
                        player.sendMessage("恭喜你抢夺到§e§l金色贡品§f，获得积分*1000");
                        room.setPlayerProperties(player.getName(), "score", ((int) room.getPlayerProperties(player.getName(), "score", 0)) + 1000);
                        break;
                    case "§e§l普通贡品":
                        player.sendMessage("恭喜你抢夺到§a§l普通贡品§f，获得积分*200");
                        room.setPlayerProperties(player.getName(), "score", ((int) room.getPlayerProperties(player.getName(), "score", 0)) + 200);
                        break;
                }
                event.getItem().close();
                return;
            }
        }
    }

    @EventHandler
    public void RoomPlayerVisualDeathEvent(RoomPlayerVisualDeathEvent event){
        Room room = event.getRoom();
        if(!room.getGameName().equals("LootBattle")){ return; }
        if(room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart){ return; }
        Player victim = event.getPlayer();
        Player damager = event.getLastDamageSource();
        Team t1 = room.getPlayerTeam(victim);
        String victim_nick = t1.getPrefix()+"["+t1.getRegistryName()+"] "+victim.getName();
        String message = victim_nick+" 死了";
        switch (event.getCause()){
            case FALL:
                if(damager != null){
                    Team t2 = room.getPlayerTeam(damager);
                    String damager_nick = t2.getPrefix()+"["+t2.getRegistryName()+"] "+damager.getName();
                    message = victim_nick+" 被 "+damager_nick+" 推入无尽深渊！";
                }else{
                    message = victim_nick+"失足落入无尽深渊！";
                }
                break;
            case ENTITY_ATTACK:
                if(damager != null){
                    Team t2 = room.getPlayerTeam(damager);
                    String damager_nick = t2.getPrefix()+"["+t2.getRegistryName()+"] "+damager.getName();
                    message = damager_nick+" 亲手将 "+victim_nick+" 送入坟墓！";
                }
                break;
            case PROJECTILE:
                if(damager != null){
                    Team t2 = room.getPlayerTeam(damager);
                    String damager_nick = t2.getPrefix()+"["+t2.getRegistryName()+"] "+damager.getName();
                    message = victim_nick+" 被 "+damager_nick+" 射死！";
                }else{
                    message = victim_nick+"被未知的飞行物砸死了！";
                }
                break;
        }
        if(damager != null){
            room.setPlayerProperties(damager.getName(), "score", (int)(room.getPlayerProperties(damager.getName(), "score", 0)) + (int)(room.getPlayerProperties(victim.getName(), "score", 0)));
        }
        room.setPlayerProperties(victim.getName(), "score", 0);
        String finalMessage = message;
        room.getPlayers().forEach(player -> player.sendMessage(finalMessage));
    }

    @EventHandler
    public void RoomGameProcessingListener(RoomGameProcessingListener event){
        Room room = event.getRoom();
        if(!room.getGameName().equals("LootBattle")){ return; }
        int lastSec = room.getGameTime() - room.getTime();
        TributeData data = (TributeData) room.getRoomProperties("extend_data");
        data.checkSpawn(room);
        Scoreboard scoreboard = de.theamychan.scoreboard.api.ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "LootBattle_ScoreBoard", "§6§lLootBattle");
        scoreboardDisplay.addLine("剩余时间:" + ScoreboardAPI.secToTime(lastSec), 0);
        for (Team team : room.getTeams()) {
            int score = 0;
            for (Player player : team.getPlayerList()) {
                score += (int) room.getPlayerProperties(player.getName(), "score", 0);
            }
            scoreboardDisplay.addLine(team.getPrefix() + team.getRegistryName() + ": " + score, 0);
        }
        if(lastSec < room.getGameTime()) {
            for (Player p : room.getPlayers()) {
                ScoreboardAPI.drawScoreBoardEntry(p, scoreboard);
            }
        }
    }

    @EventHandler
    public void GameEnd(RoomGameEndEvent event){
        Room room = event.getRoom();
        if(!room.getGameName().equals("LootBattle")){ return; }
        for(Team team: room.getTeams()){
            int score = 0;
            for(Player player: team.getPlayerList()){
                score += (int) room.getPlayerProperties(player.getName(), "score", 0);
            }
            team.setScore(score);
        }
        List<Team> teamList = new ArrayList<>(room.getTeams());
        teamList.sort(Comparator.comparing(Team::getScore));
        List<Team> winners = new ArrayList<>();
        for(int i=0; i< (teamList.size()/2); i++){
            winners.add(teamList.get(i));
        }
        for(int i=(teamList.size()/2); i<teamList.size(); i++){
            if(teamList.get((teamList.size()/2)-1).getScore() == teamList.get(i).getScore()){
                winners.add(teamList.get(i));
            }
        }
        teamList.forEach(team -> team.getPlayerList().forEach(player -> {
            player.sendMessage("本局结算:");
            for(Team t: teamList){
                if(winners.contains(t)){
                    player.sendMessage(t.getPrefix()+t.getRegistryName()+" §6胜利, 积分:"+t.getScore());
                }else{
                    player.sendMessage(t.getPrefix()+t.getRegistryName()+" §b失败, 积分:"+t.getScore());
                }
            }
        }));
        teamList.removeAll(winners);
        winners.forEach(team -> team.getPlayerList().forEach(player -> {
            player.sendMessage("恭喜您获得了胜利！");
            GameRecord.addGameRecord("LootBattle",player.getName(), "win",1);
            room.executeWinCommands(player);
            player.sendMessage("§l§e您已成功完成比赛 §l§a"+ GameRecord.getGameRecord("LootBattle",player.getName(),"win")+" §l§e次");
        }));
        teamList.forEach(team -> team.getPlayerList().forEach(player -> {
            player.sendMessage("不要遗憾，再接再厉！");
            GameRecord.addGameRecord("LootBattle",player.getName(), "lose",1);
            player.sendMessage("§l§e您未完成比赛 §l§c"+GameRecord.getGameRecord("LootBattle",player.getName(),"lose")+" §l§e次");
            room.executeLoseCommands(player);
        }));
    }

    @EventHandler
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
        Room room = Room.getRoom("lootbattle", event.getPlayer());
        if(room == null){ return; }
        if(!event.getMessage().startsWith("/lootbattle")){
            event.getPlayer().sendMessage("游戏中无法使用局外指令！");
            event.setCancelled(true);
        }
    }
}
