package testgame;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import com.sun.istack.internal.NotNull;
import gameapi.room.Room;
import gameapi.utils.GameRecord;
import testgame.scripts.CustomSkill;

public class Window {
    public static void showPlayerRoomListWindow(@NotNull Player player){
        if(!Event.playerFormWindowSimpleHashMap.containsKey(player)) {
            FormWindowSimple simple = new FormWindowSimple("§l§e选择房间", "请选择您的房间！");
            for (Room room : MainClass.roomListHashMap) {
                simple.addButton(new ElementButton(room.getRoomName()));
            }
            Event.playerFormWindowSimpleHashMap.put(player, simple);
            player.showFormWindow(simple);
        }
    }

    public static void showPlayerSkillSelectWindow(@NotNull Player player) {
        if(!Event.playerFormWindowSimpleHashMap.containsKey(player)) {
            FormWindowSimple simple = new FormWindowSimple("§l§e选择技能", "请选择您的技能！");
            for(CustomSkill skill: MainClass.skills.values()){
                simple.addButton(new ElementButton(skill.getCustomName()));
            }
            Event.playerFormWindowSimpleHashMap.put(player, simple);
            player.showFormWindow(simple);
        }
    }

    public static void showPlayerHistoryWindow(@NotNull Player player){
        if(!Event.playerFormWindowSimpleHashMap.containsKey(player)) {
            FormWindowSimple window = new FormWindowSimple("§l§a历史战绩", "");
            window.setContent("Win(获胜次数): §6" + GameRecord.getGameRecord("DRecknessHero", player.getName(), "win") + "\n§fFail(失败次数): §c" + GameRecord.getGameRecord("DRecknessHero", player.getName(), "lose"));
            Event.playerFormWindowSimpleHashMap.put(player, window);
            player.showFormWindow(window);
        }
    }

    public static void showVoteForMap(@NotNull Player player){
        if(!Event.playerFormWindowSimpleHashMap.containsKey(player)) {
            FormWindowSimple window = new FormWindowSimple("§l§e选择地图", "");
            for(String map: MainClass.maps){
                window.addButton(new ElementButton(map));
            }
            Event.playerFormWindowSimpleHashMap.put(player, window);
            player.showFormWindow(window);
        }
    }
}
