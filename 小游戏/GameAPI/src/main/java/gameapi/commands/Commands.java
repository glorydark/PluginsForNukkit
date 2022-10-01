package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import gameapi.GameAPI;
import gameapi.entity.EntityUtils;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.sound.Sound;
import gameapi.utils.Tools;

import java.io.File;
import java.util.*;

/**
 * @author Glorydark
 * For in-game test
 */
public class Commands extends Command {
    public Commands(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isOp() || !commandSender.isPlayer()) {
            return false;
        }
        if (strings.length > 0) {
            switch (strings[0].toLowerCase()) {
                case "playressound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            Sound.playResourcePackOggMusic(player, strings[2]);
                        } else {
                            GameAPI.plugin.getLogger().warning("Can not find the chosen player!");
                        }
                    }
                    break;
                case "playambientsound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            cn.nukkit.level.Sound sound = cn.nukkit.level.Sound.valueOf(strings[2]);
                            if (sound.getSound() != null) {
                                Sound.addAmbientSound(player.level, player, sound);
                            } else {
                                GameAPI.plugin.getLogger().warning("Can not find the chosen sound!");
                            }
                        } else {
                            GameAPI.plugin.getLogger().warning("Can not find the chosen player!");
                        }
                    }
                    break;
                case "debug":
                    if (strings.length != 2) {
                        return false;
                    }
                    switch (strings[1]) {
                        case "true":
                            GameAPI.debug.add((Player) commandSender);
                            commandSender.sendMessage("已开启debug模式！");
                            break;
                        case "false":
                            GameAPI.debug.remove((Player) commandSender);
                            commandSender.sendMessage("已关闭debug模式！");
                    }
                    break;
                case "savebattles":
                    File saveDic = new File(GameAPI.path+"/saves/"+ Tools.dateToString(Calendar.getInstance().getTime()) + "/");
                    if(saveDic.exists() || saveDic.mkdirs()) {
                        for (String key : GameAPI.RoomHashMap.keySet()) {
                            for (Room room : GameAPI.RoomHashMap.get(key)) {
                                if (room.getRoomStatus().equals(RoomStatus.ROOM_STATUS_GameStart)) {
                                    File file = new File(saveDic.getPath() + "/" + key + "_" + room.getRoomName() + ".json");
                                    Config config = new Config(file, Config.JSON);
                                    LinkedHashMap<String, Object> players = new LinkedHashMap<>();
                                    room.getPlayers().forEach(player -> players.put(player.getName(), getPlayerDatas(player)));
                                    LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                                    objectMap.put("players", players);
                                    objectMap.put("players_properties", getPropertiesDatas(room.getPlayerProperties()));
                                    objectMap.put("room_properties", room.getRoomProperties());
                                    objectMap.put("room_datas", new Gson().fromJson(room.toString(), Map.class));
                                    config.setAll(objectMap);
                                    config.save();
                                }
                            }
                        }
                    }else{
                        GameAPI.plugin.getLogger().warning("无法创建文件夹："+saveDic.getPath());
                    }
                    break;
                case "addrank":
                    if(strings.length == 3) {
                        Player player = (Player) commandSender;
                        EntityUtils.addRankingList(player, strings[1], strings[2]);
                    }
                    break;
            }
        }
        return true;
    }

    public LinkedHashMap<String, Object> getPlayerDatas(Player player){
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        maps.put("Location", player.getX() + ":" + player.getY() + ":" + player.getZ()+ ":" + player.getLevel().getName());
        maps.put("Health", player.getHealth());
        List<String> invs = new ArrayList<>();
        player.getInventory().getContents().values().forEach(item -> invs.add(Inventory.getItemString(item)));
        maps.put("Inventory", invs);
        List<String> armors = new ArrayList<>();
        for(Item item: player.getInventory().getArmorContents()){
            armors.add(Inventory.getItemString(item));
        }
        maps.put("ArmorContents", armors);
        return maps;
    }

    public LinkedHashMap<String, Object> getPropertiesDatas(LinkedHashMap<String, LinkedHashMap<String, Object>> playerObjectMap){
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        for(String key: playerObjectMap.keySet()){
            maps.put(key, playerObjectMap.get(key));
        }
        return maps;
    }
}
