package glorydark.sheepwar.gui;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import glorydark.sheepwar.SheepWarMain;
import glorydark.sheepwar.settings.SetGameInit;
import glorydark.sheepwar.utils.SheepType;

import java.util.ArrayList;
import java.util.HashMap;

public class GuiListener implements Listener {
    public static final HashMap<Player, HashMap<Integer, GuiType>> UI_CACHE = new HashMap<>();
    public static void showFormWindow(Player player, FormWindow window, GuiType guiType) {
        UI_CACHE.computeIfAbsent(player, i -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }

    @EventHandler
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event){
        Player p = event.getPlayer();
        FormWindow window = event.getWindow();
        if (p == null || window == null) {
            return;
        }
        GuiType guiType = UI_CACHE.containsKey(p) ? UI_CACHE.get(p).get(event.getFormID()) : null;
        if(guiType == null){
            return;
        }
        UI_CACHE.get(p).remove(event.getFormID());
        if (event.getResponse() == null) {
            return;
        }
        if (window instanceof FormWindowSimple) {
            this.formWindowSimpleOnClick(p, (FormWindowSimple) window, guiType);
        }
        if (window instanceof FormWindowCustom) {
            this.formWindowCustomOnClick(p, (FormWindowCustom) window, guiType);
        }
        /*
        if (window instanceof FormWindowModal) {
            this.formWindowModalOnClick(p, (FormWindowModal) window, guiType);
        }
         */
    }
    private void formWindowSimpleOnClick(Player p, FormWindowSimple window, GuiType guiType) {
        if(window.getResponse() == null){ return; }
        int id = window.getResponse().getClickedButtonId();
        if (guiType == GuiType.roomList) {
            Room[] rooms = SheepWarMain.rooms.keySet().toArray(new Room[0]);
            Room room = rooms[id];
            if (room != null) {
                room.addPlayer(p);
            } else {
                p.sendMessage(TextFormat.RED + "房间不存在！");
            }
        }
    }

    private void formWindowCustomOnClick(Player p, FormWindowCustom window, GuiType guiType) {
        if(window.getResponse() == null){ return; }
        if (guiType == GuiType.roomSettings) {
            Room room = SetGameInit.getRoom();
            String response0 = window.getResponse().getInputResponse(0);
            String response1 = window.getResponse().getInputResponse(1);
            String response2 = window.getResponse().getInputResponse(2);
            String response3 = window.getResponse().getInputResponse(3);
            String response4 = window.getResponse().getInputResponse(4);
            String response5 = window.getResponse().getInputResponse(5);
            String response6 = window.getResponse().getInputResponse(6);
            if (response0 != null && !response0.replace(" ", "").equals("")) {
                if (!new Config(SheepWarMain.path + "/rooms.yml", Config.YAML).exists(response0)) {
                    room.setRoomName(response0);
                } else {
                    p.sendMessage("房间名称不能为空且不可重复，请重试");
                    SetGameInit.reset();
                    return;
                }
            } else {
                p.sendMessage("房间名称不能为空且不可重复，请重试");
                SetGameInit.reset();
                return;
            }
            if (response1 != null && !response1.replace(" ", "").equals("")) {
                room.setWaitTime(Integer.parseInt(response1));
            } else {
                room.setWaitTime(60);
            }
            if (response2 != null && !response2.replace(" ", "").equals("")) {
                room.setGameWaitTime(Integer.parseInt(response2));
            } else {
                room.setGameWaitTime(10);
            }
            if (response3 != null && !response3.replace(" ", "").equals("")) {
                room.setGameTime(Integer.parseInt(response3));
            } else {
                room.setGameTime(60);
            }
            if (response4 != null && !response4.replace(" ", "").equals("")) {
                room.setCeremonyTime(Integer.parseInt(response4));
            } else {
                room.setCeremonyTime(5);
            }
            if (response5 != null && !response5.replace(" ", "").equals("")) {
                room.setMaxPlayer(Integer.parseInt(response5));
            } else {
                room.setMaxPlayer(16);
            }
            if (response6 != null && !response6.replace(" ", "").equals("")) {
                room.setMinPlayer(Integer.parseInt(response6));
            } else {
                room.setMinPlayer(1);
            }
            SheepWarMain.rooms.put(SetGameInit.getRoom(), SetGameInit.getExtendRoomData());
            Room.loadRoom(room);
            //保存数据
            Config config = new Config(SheepWarMain.path + "/rooms.yml", Config.YAML);
            config.set(response0+".房间名", response0);
            config.set(response0+".等待时间", room.getWaitTime());
            config.set(response0+".预备时间", room.getGameWaitTime());
            config.set(response0+".游戏时间", room.getGameTime());
            config.set(response0+".颁奖时间", room.getCeremonyTime());
            config.set(response0+".最大人数", room.getMaxPlayer());
            config.set(response0+".最小人数", room.getMinPlayer());
            Position wait = room.getWaitSpawn().getLocation();
            config.set(response0+".等待地点", "\\\"" + wait.x + ":" + wait.y + ":" + wait.z +"\\\"");
            Position end = room.getEndSpawn().getLocation();
            config.set(response0+".结束地点", "\\\""+end.x + ":" + end.y + ":" + end.z +"\\\"");
            config.set(response0+".随机出生点", SetGameInit.getExtendRoomData().getRandomSpawnStrings()); //error
            config.set(response0+".游戏世界", SetGameInit.getPlayLevel().getName());
            config.set(response0+".白羊羊刷新坐标", SetGameInit.getExtendRoomData().getRandomSheepSpawnStrings(SheepType.NormalSheep));
            config.set(response0+".黑羊羊刷新坐标", SetGameInit.getExtendRoomData().getRandomSheepSpawnStrings(SheepType.Black_Sheep));
            config.set(response0+".红羊羊刷新坐标", SetGameInit.getExtendRoomData().getRandomSheepSpawnStrings(SheepType.Red_Sheep));
            config.set(response0+".蓝羊羊刷新坐标", SetGameInit.getExtendRoomData().getRandomSheepSpawnStrings(SheepType.Blue_Sheep));
            config.set(response0+".绿羊羊刷新坐标", SetGameInit.getExtendRoomData().getRandomSheepSpawnStrings(SheepType.Green_Sheep));
            config.set(response0+".紫羊羊刷新坐标", SetGameInit.getExtendRoomData().getRandomSheepSpawnStrings(SheepType.Purple_Sheep));
            config.set(response0+".金羊羊刷新坐标", SetGameInit.getExtendRoomData().getRandomSheepSpawnStrings(SheepType.Gold_Sheep));
            config.set(response0+".胜利控制台指令", new ArrayList<>());
            config.set(response0+".失败控制台指令", new ArrayList<>());
            config.save();
            Inventory.loadBag(p);
            SetGameInit.reset();
            p.sendMessage("设置完成！");
        }
    }

    /*
    private void formWindowModalOnClick(Player p, FormWindowModal window, GuiType guiType) {
        if(window.getResponse() == null){ return; }
        switch (guiType){

        }
    }
     */
}