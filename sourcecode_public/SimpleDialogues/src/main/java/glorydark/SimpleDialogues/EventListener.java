package glorydark.SimpleDialogues;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.charmsystem.AchievementMain;
import glorydark.SimpleDialogues.charmsystem.Window;
import glorydark.SimpleDialogues.charmsystem.WindowType;
import glorydark.SimpleDialogues.dialogue.DialogueMain;

public class EventListener implements Listener {
    //提示文本测试显示
    /*
    @EventHandler
    public void test(PlayerInteractEvent event){
         show part
        Integer gapLength = 64 - Tools.getStringCharCount("威廉");
        String blank = "                                                                ";
        blank = blank.substring(0,gapLength-1);
        String text = "[ 威廉§f ]" + blank + "\n  test1";
        event.getPlayer().sendTip(text);


        dialogueMain.showPlayerDialogue(event.getPlayer(), "dialogue1");
    }

     */


    @EventHandler
    public void move(PlayerMoveEvent event) {
        if (MainClass.inDialogues.containsKey(event.getPlayer())) {
            Dialogue dialogue = MainClass.inDialogues.get(event.getPlayer());
            if (dialogue.getMovable()) {
                return;
            }
            Location from = event.getFrom();
            Location to = event.getTo();
            // 防止视角移动不断拉回影响体验感
            if (from.getFloorX() != to.getFloorX() || from.getFloorY() != to.getFloorY() || from.getFloorZ() != to.getFloorZ()) {
                event.setCancelled(true);
            }
        }
    }

    /*
    @EventHandler
    public void executeCommand(PlayerCommandPreprocessEvent event){
        if(MainClass.inDialogues.containsKey(event.getPlayer())) {
            event.getPlayer().sendMessage("对话过程中不可执行指令嗷！");
            event.setCancelled(true);
        }
    }
     */

    @EventHandler
    public void teleport(PlayerTeleportEvent event) {
        if (MainClass.inDialogues.containsKey(event.getPlayer())) {
            Dialogue dialogue = MainClass.inDialogues.get(event.getPlayer());
            if (dialogue.getMovable()) {
                return;
            }
            event.getPlayer().sendMessage("对话过程中不可执行传送嗷！");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (p != null) {
            Window.windows.remove(p);
            MainClass.inDialogues.remove(p);
        }
    }

    @EventHandler
    public void formRespondEvent(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (event.getWindow().getResponse() == null) {
            if (player != null) {
                Window.windows.remove(player);
                Window.checks.remove(player);
            }
            return;
        }
        if (player != null) {
            WindowType type = Window.windows.getOrDefault(event.getPlayer(), null);
            if (type != null) {
                switch (type) {
                    case Achievement:
                        FormResponseSimple responseSimple = (FormResponseSimple) event.getResponse();
                        int clickButtonId = responseSimple.getClickedButtonId();
                        Achievement achievement = (Achievement) AchievementMain.achievements.values().toArray()[clickButtonId];
                        if (achievement != null) {
                            if (achievement.isPlayerQualified(player)) {
                                if (achievement.getRecords().contains(player.getName())) {
                                    if (achievement.canRegainRewards()) {
                                        for (String cmd : achievement.getCommands()) {
                                            String command = cmd.replace("@p", player.getName());
                                            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command);
                                        }
                                        achievement.addRecord(player);
                                        player.sendMessage(TextFormat.GREEN + "您成功完成了成就:" + achievement.getName());
                                    } else {
                                        player.sendMessage(TextFormat.RED + "您已经完成了该成就！");
                                    }
                                } else {
                                    for (String cmd : achievement.getCommands()) {
                                        String command = cmd.replace("@p", player.getName());
                                        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command);
                                    }
                                    achievement.addRecord(player);
                                    player.sendMessage(TextFormat.GREEN + "您成功完成了成就:" + achievement.getName());
                                }
                            }
                        }
                        break;
                    case CheckBeforeDialoguePlay:
                        FormResponseModal responseModal = (FormResponseModal) event.getResponse();
                        if(responseModal.getClickedButtonId() == 0){
                            DialogueMain.showPlayerDialogue(player, Window.checks.get(player));
                        }
                        Window.checks.remove(player);
                        break;
                }
                Window.windows.remove(player);
            }
        }
    }
}
