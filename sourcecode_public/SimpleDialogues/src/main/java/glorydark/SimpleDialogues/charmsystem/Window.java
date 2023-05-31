package glorydark.SimpleDialogues.charmsystem;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.Achievement;
import glorydark.SimpleDialogues.Dialogue;
import glorydark.SimpleDialogues.MainClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Window {
    public static HashMap<Player, WindowType> windows = new HashMap<>();

    public static HashMap<Player, String> checks = new HashMap<>();

    public static void showDialogueClearBonusMenu(Player player, Dialogue dialogue){
        if(player != null && player.isOnline()){
            if(windows.containsKey(player)){
                return;
            }
            FormWindowModal form = new FormWindowModal("剧情完成界面","","确认","退出");
            StringBuilder showMsgBuilder;
            if(MainClass.charmPoint) {
                showMsgBuilder = new StringBuilder("恭喜您完成了对话:" + dialogue.getDialogueName() + ",获得了" + TextFormat.LIGHT_PURPLE + "魅力值*" + dialogue.getCharmPoint() + TextFormat.WHITE);
            }else{
                showMsgBuilder = new StringBuilder("恭喜您完成了对话:" + dialogue.getDialogueName() + TextFormat.WHITE);
            }
            for(String s:dialogue.getFinishMessages()){
                showMsgBuilder.append("\n").append(s);
            }
            String showMsg = showMsgBuilder.toString();
            showMsg = showMsg.replace("@p", player.getName());
            showMsg = showMsg.replace("@level", player.getLevel().getName());
            showMsg = showMsg.replace("@charmpoint", CharmPoint.getCharmPointCache(player).toString()); //魅力值系统待接入
            form.setContent(showMsg);
            player.showFormWindow(form);
            windows.put(player, WindowType.DialogueCompleted);
        }
    }

    public static void showDialogueClearMenu(Player player, Dialogue dialogue){
        if(player != null && player.isOnline()){
            if(windows.containsKey(player)){
                return;
            }
            FormWindowModal form = new FormWindowModal("剧情完成界面","","确认","退出");
            String showMsg;
            if (dialogue.canRegainCharmPoint()) {
                showMsg = "恭喜您完成了对话:" + dialogue.getDialogueName() + ",获得了" + TextFormat.LIGHT_PURPLE + "魅力值*" + dialogue.getCharmPoint() + TextFormat.WHITE;
            }else{
                showMsg = "恭喜您完成了对话:" + dialogue.getDialogueName() + "!\n";
                if(MainClass.charmPoint) {
                    showMsg += TextFormat.RED + "* 重复本剧情无魅力值奖励" + TextFormat.WHITE;
                }
            }
            if(!dialogue.canRegainRewards()){
                showMsg += "\n" + TextFormat.RED+"* 重复本剧情无指令奖励" + TextFormat.WHITE;
            }
            StringBuilder showMsgBuilder = new StringBuilder(showMsg);
            for(String s:dialogue.getFinishMessages()){
                showMsgBuilder.append("\n").append(s);
            }
            showMsg = showMsgBuilder.toString();
            showMsg = showMsg.replace("@p", player.getName());
            showMsg = showMsg.replace("@level", player.getLevel().getName());
            showMsg = showMsg.replace("@charmpoint", CharmPoint.getCharmPointCache(player).toString()); //魅力值系统待接入
            form.setContent(showMsg);
            player.showFormWindow(form);
            windows.put(player, WindowType.DialogueCompleted);
        }
    }

    public static void showRankingList(Player player){
        if(player != null && player.isOnline()) {
            if (windows.containsKey(player)) {
                return;
            }
            FormWindowSimple simple = new FormWindowSimple("排行榜", "* 排行榜只记录前十名玩家!");
            HashMap<String, Double> list = CharmPoint.getRankingList();
            if(list.keySet().size() > 0) {
                String content = "";
                String[] array = list.keySet().toArray(new String[0]);
                for (int i = 0; i <= list.keySet().size(); i++) {
                    if (i > 9) {
                        break;
                    }
                    switch (i) {
                        case 1:
                            content = TextFormat.GOLD + "【1】 " + array[0] + " | 魅力值:" + list.get(array[0]) +TextFormat.WHITE + "\n";
                            break;
                        case 2:
                            content = TextFormat.GRAY + "【2】 " + array[0] + " | 魅力值:" + list.get(array[0]) +TextFormat.WHITE + "\n";
                            break;
                        case 3:
                            content = TextFormat.DARK_GREEN + "【3】 " + array[0] + " | 魅力值:" + list.get(array[0]) +TextFormat.WHITE + "\n";
                            break;
                    }
                    if (i > 3) {
                        content = TextFormat.WHITE + "【1】 " + array[0] + " | 魅力值:" + list.get(array[0]) +TextFormat.WHITE + "\n";
                    }
                }
                List<String> stringList = new ArrayList<>(list.keySet());
                if (stringList.contains(player.getName())) {
                    content += "--------------------------" + "\n";
                    content += "您的排名:" + (new ArrayList<>(list.keySet()).indexOf(player.getName()) + 1) + "/" + list.keySet().size() + "\n";
                    content += "--------------------------" + "\n";
                }
                simple.setContent(content);
            }else{
                simple.setContent(TextFormat.GREEN + ">> 暂无数据！ <<");
            }
            player.showFormWindow(simple);
            windows.put(player, WindowType.RankingList);
        }
    }

    public static void showAchievementMenu(Player player){
        if(player != null && player.isOnline()) {
            if (windows.containsKey(player)) {
                return;
            }
            FormWindowSimple simple = new FormWindowSimple("成就系统", "下面是你能领取的奖励！");
            for (Achievement achievement : AchievementMain.achievements.values()) {
                if (achievement.isPlayerQualified(player)) {
                    if (achievement.getRecords().contains(player.getName())) {
                        if (achievement.canRegainRewards()) {
                            simple.addButton(new ElementButton(achievement.getName() + "\n" + TextFormat.YELLOW + "[ 可领取 ]"));
                        } else {
                            simple.addButton(new ElementButton(achievement.getName() + "\n" + TextFormat.DARK_GRAY + "[ 已领取 ]"));
                        }
                    } else {
                        simple.addButton(new ElementButton(achievement.getName() + "\n" + TextFormat.YELLOW + "[ 可领取 ]"));
                    }
                } else {
                    simple.addButton(new ElementButton(achievement.getName() + "\n" + TextFormat.RED + "[ 不可领取 ]"));
                }
            }
        /*
        simple.addButton(new ElementButton("测试奖励 \n"+TextFormat.YELLOW+"[ 可领取 ]"));
        simple.addButton(new ElementButton("测试奖励 \n"+TextFormat.RED+"[ 不可领取 ]"));
        simple.addButton(new ElementButton("测试奖励 \n"+TextFormat.DARK_GRAY+"[ 已领取 ]"));

         */
            player.showFormWindow(simple);
            windows.put(player, WindowType.Achievement);
        }
    }

    public static void showCheckBeforeDialoguePlayMenu(Player player, String dialogue){
        if(player != null && player.isOnline()) {
            if(windows.containsKey(player)){
                return;
            }
            FormWindowModal modal = new FormWindowModal("播放对话确认","你是否要进入此对话?", "确认", "退出");
            checks.put(player, dialogue);
            player.showFormWindow(modal);
            windows.put(player, WindowType.CheckBeforeDialoguePlay);
        }
    }
}
