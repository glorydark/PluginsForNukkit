package glorydark.lotterybox.forms;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import glorydark.lotterybox.MainClass;
import glorydark.lotterybox.event.LotteryForceCloseEvent;
import glorydark.lotterybox.tasks.nonWeight.InventoryChangeTask;
import glorydark.lotterybox.tasks.weight.InventoryChangeTaskV2;
import glorydark.lotterybox.tools.BasicTool;
import glorydark.lotterybox.tools.LotteryBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static glorydark.lotterybox.forms.CreateGui.showLotteryBoxWindowV2;
import static glorydark.lotterybox.forms.CreateGui.showPESelectSpinWindow;

public class GuiListener implements Listener {

    @EventHandler
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event) throws SQLException {
        Player p = event.getPlayer();
        FormWindow window = event.getWindow();
        if (p == null || window == null) {
            return;
        }
        GuiType guiType = CreateGui.UI_CACHE.containsKey(p) ? CreateGui.UI_CACHE.get(p).get(event.getFormID()) : null;
        if(guiType == null){
            return;
        }
        CreateGui.UI_CACHE.get(p).remove(event.getFormID());
        if (event.getResponse() == null) {
            return;
        }
        if (event.getWindow() instanceof FormWindowSimple) {
            this.onSimpleClick(p, (FormWindowSimple) window, guiType);
        }
        if (event.getWindow() instanceof FormWindowCustom) {
            this.onCustomClick(p, (FormWindowCustom) window, guiType);
        }
    }

    private void onSimpleClick(Player player, FormWindowSimple simple, GuiType guiType) {
        if(simple.getResponse() == null){ return; }
        if(MainClass.playingPlayers.contains(player)){ return;}
        switch (guiType){
            case SelectLotteryBox:
                if(!BasicTool.isPE(player) && !player.isOnGround()){
                    player.sendMessage(MainClass.lang.getTranslation("Tips","NoOnGround"));
                    return;
                }
                LotteryBox box = MainClass.lotteryBoxList.get(simple.getResponse().getClickedButtonId());
                MainClass.playerLotteryBoxes.put(player, box);
                if(!BasicTool.isPE(player) && !MainClass.forceDefaultMode) {
                    if(box.isWeightEnabled()){
                        CreateGui.showLotteryPossibilityWindow(player, box);
                        //showPESelectSpinWindow(player);
                    }else{
                        showLotteryBoxWindowV2(player, box);
                    }
                }else{
                    CreateGui.showLotteryPossibilityWindow(player, box);
                    //showPESelectSpinWindow(player);
                }
                break;
            case LotteryPossibility:
                if(simple.getResponse().getClickedButtonId() == 0){
                    showPESelectSpinWindow(player);
                }
                break;
        }
    }

    private void onCustomClick(Player player, FormWindowCustom custom, GuiType guiType){
        if(custom.getResponse() == null){ return; }
        if(guiType == GuiType.SelectLotterySpin){
            int spin = (int) custom.getResponse().getSliderResponse(1);
            LotteryBox box = MainClass.playerLotteryBoxes.get(player);
            if(box.checkLimit(player.getName(), spin)) {
                if (box.deductNeeds(player, spin)) {
                    if(box.isWeightEnabled()){
                        Server.getInstance().getScheduler().scheduleRepeatingTask(new InventoryChangeTaskV2(player, MainClass.playerLotteryBoxes.get(player), spin), MainClass.default_speed_ticks);
                    }else {
                        Server.getInstance().getScheduler().scheduleRepeatingTask(new InventoryChangeTask(player, MainClass.playerLotteryBoxes.get(player), spin), MainClass.default_speed_ticks);
                    }
                } else {
                    player.sendMessage(MainClass.lang.getTranslation("Tips", "LackOfItemsOrTickets"));
                    Server.getInstance().getPluginManager().callEvent(new LotteryForceCloseEvent(player));
                }
            }else{
                player.sendMessage(MainClass.lang.getTranslation("Tips","TimesLimit"));
                Server.getInstance().getPluginManager().callEvent(new LotteryForceCloseEvent(player));
            }
        }
    }
}
