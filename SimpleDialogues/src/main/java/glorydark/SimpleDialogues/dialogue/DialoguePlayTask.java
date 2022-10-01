package glorydark.SimpleDialogues.dialogue;

import cn.nukkit.Player;
import cn.nukkit.Server;
import glorydark.SimpleDialogues.ToastRequestPacket;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;
import glorydark.SimpleDialogues.CreateFireworkApi;
import glorydark.SimpleDialogues.Dialogue;
import glorydark.SimpleDialogues.MainClass;
import glorydark.SimpleDialogues.Tools;
import glorydark.SimpleDialogues.charmsystem.CharmPoint;
import glorydark.SimpleDialogues.charmsystem.Window;
import glorydark.SimpleDialogues.particle.Particle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DialoguePlayTask extends Task {
    private final Player player;
    private final Dialogue dialogue;
    private Integer tick = 0;
    private Integer lastIndex = -1;
    @Override
    public void onRun(int i) {
        if(player == null || !player.isOnline()){
            MainClass.inDialogues.remove(player);
            this.cancel();
        }
        if(!MainClass.inDialogues.containsKey(player)){
            this.cancel();
            return;
        }
        List<String> speakerContent = dialogue.getSpeakerContent();
        Integer interval = dialogue.getChangeInterval();
        String roleName = dialogue.getSpeakerName();
        // 64 bytes per line = 32 characters per line;
        // text left/right-align method : (64 - wholeCharLength) -> add " "
        int gapLength = 64 - Tools.getStringCharCount(roleName);
        String blank = "                                                                ";
        blank = blank.substring(0,gapLength-1);
        int index;
        if(tick != 0){
            index = tick/interval;
        }else{
            index = 0;
        }

        if(player != null && player.isOnline()) {
            if(index + 1 <= speakerContent.size()) {
                Boolean bool = false;
                if(lastIndex != index) {
                    if(dialogue.getCommandsDuringPlayingExecutedByConsole(index).size() > 0){
                        for(String s: dialogue.getCommandsDuringPlayingExecutedByConsole(index)){
                            String command = s.replace("@p", player.getName());
                            Server.getInstance().dispatchCommand(player, command);
                        }
                    }
                    if(dialogue.getCommandsDuringPlayingExecutedByPlayer(index).size() > 0){
                        for(String s: dialogue.getCommandsDuringPlayingExecutedByPlayer(index)){
                            String command = s.replace("@p", player.getName());
                            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command);
                        }
                    }
                    lastIndex = index;
                }
                String finalRoleName = roleName;
                String content = speakerContent.get(index);
                if(content.split("\\|").length == 2){
                    finalRoleName = content.split("\\|")[0];
                    finalRoleName = finalRoleName.replace("@p", player.getName());
                    content = content.split("\\|")[1];
                }
                content = content.replace("@p", player.getName());
                if (player.level != null) {
                    content = content.replace("@level", player.getLevel().getName());
                }
                content = content.replace("@charmpoint", CharmPoint.getCharmPointCache(player).toString());
                Integer textLen = Tools.getStringCharCount(content); // 4  20
                int len = (int) ((double) (tick % interval) / interval * textLen);
                //showText = speakerContent.get(index).substring(0, len == 0? 1 : len-1);
                if (content.length() >= len && content.length() != textLen) {
                    if ((double) (tick % interval) / interval < 0.9 && interval >= 40 && (!MainClass.showType.equals("message") && !(MainClass.showType.equals("toast")))) {
                        showText("[ " + finalRoleName + "§f ]" + blank + "\n  " + content.substring(0, len), bool);
                    } else {
                        if(MainClass.showType.equals("message") || MainClass.showType.equals("toast")){
                            if(MainClass.showType.equals("toast")){
                                showText("[ " + finalRoleName + "§f ]", "  "+content, bool);
                            }else{
                                showText("[ " + finalRoleName + "§f ]" + "\n  " + content, bool);
                            }
                        }else {
                            showText("[ " + finalRoleName + "§f ]" + blank + "\n  " + content, bool);
                        }
                    }
                }
                if (len <= textLen && content.length() == textLen) {
                    if ((double) (tick % interval) / interval < 0.9 && interval >= 40 && (!MainClass.showType.equals("message") && !(MainClass.showType.equals("toast")))) {
                        showText("[ " + finalRoleName + "§f ]" + blank + "\n  " + content.substring(0, len), bool);
                    } else {
                        if(MainClass.showType.equals("message") || MainClass.showType.equals("toast")){
                            if(MainClass.showType.equals("toast")){
                                showText("[ " + finalRoleName + "§f ]", "  "+content, bool);
                            }else{
                                showText("[ " + finalRoleName + "§f ]" + "\n  " + content, bool);
                            }
                        }else {
                            showText("[ " + finalRoleName + "§f ]" + blank + "\n  " + content, bool);
                        }
                    }
                }
                Particle.CreateParticle(player, 0, player.getPosition(), 2);
            }
        }
        if(speakerContent.size()*interval < tick){
            //MainClass.plugin.getLogger().alert("dialogue test complete!");
            MainClass.inDialogues.remove(player);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Integer i1 = random.nextInt(14);
            int i2 = random.nextInt(4);
            CreateFireworkApi.spawnFirework(player.getPosition(), CreateFireworkApi.getColorByInt(i1), CreateFireworkApi.getExplosionTypeByInt(i2));
            if (dialogue.getFinishPlayers().contains(player.getName())) {
                if(dialogue.isShowFinishedWindow()){
                    Window.showDialogueClearMenu(player, dialogue);
                }else{
                    String showMsg;
                    if (dialogue.canRegainCharmPoint()) {
                        if(MainClass.isCharmPointAllowed()) {
                            showMsg = "恭喜您完成了对话:" + dialogue.getDialogueName() + TextFormat.WHITE;
                            CharmPoint.addCharmPoint(player, dialogue.getCharmPoint());
                        }else{
                            showMsg = "恭喜您完成了对话:" + dialogue.getDialogueName() + TextFormat.WHITE;
                        }
                    }else{
                        showMsg = "恭喜您完成了对话:" + dialogue.getDialogueName() + "!\n";
                        if(MainClass.charmPoint) {
                            showMsg += TextFormat.RED + "* 重复本剧情无魅力值奖励" + TextFormat.WHITE;
                        }
                    }
                    if(!dialogue.canRegainRewards()){
                        showMsg += "\n" + TextFormat.RED+"* 重复本剧情无指令奖励" + TextFormat.WHITE;
                    }else{
                        if(dialogue.getConsoleExecuteCommands().size() > 0) {
                            for (String cmd : dialogue.getConsoleExecuteCommands()) {
                                String command = cmd.replace("@p", player.getName());
                                Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command);
                            }
                        }
                        if(dialogue.getPlayerExecuteCommands().size() > 0) {
                            for (String cmd : dialogue.getPlayerExecuteCommands()) {
                                String command = cmd.replace("@p", player.getName());
                                Server.getInstance().dispatchCommand(player, command);
                            }
                        }
                    }
                    StringBuilder showMsgBuilder = new StringBuilder(showMsg);
                    for(String s:dialogue.getFinishMessages()){
                        showMsgBuilder.append("\n").append(s);
                    }
                    showMsg = showMsgBuilder.toString();
                    showMsg = showMsg.replace("@p", player.getName());
                    showMsg = showMsg.replace("@level", player.getLevel().getName());
                    showMsg = showMsg.replace("@charmpoint", CharmPoint.getCharmPointCache(player).toString()); //魅力值系统待接入
                    player.sendMessage(showMsg);
                }
            } else {
                if(dialogue.isShowFinishedWindow()){
                    Window.showDialogueClearBonusMenu(player, dialogue);
                }else{
                    StringBuilder showMsgBuilder = new StringBuilder("恭喜您完成了对话:" + dialogue.getDialogueName() + TextFormat.WHITE);
                    for(String s:dialogue.getFinishMessages()){
                        showMsgBuilder.append("\n").append(s);
                    }
                    String showMsg = showMsgBuilder.toString();
                    showMsg = showMsg.replace("@p", player.getName());
                    showMsg = showMsg.replace("@level", player.getLevel().getName());
                    showMsg = showMsg.replace("@charmpoint", CharmPoint.getCharmPointCache(player).toString()); //魅力值系统待接入
                    player.sendMessage(showMsg);
                }
                if(dialogue.getConsoleExecuteCommands().size() > 0) {
                    for (String cmd : dialogue.getConsoleExecuteCommands()) {
                        String command = cmd.replace("@p", player.getName());
                        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command);
                    }
                }
                if(dialogue.getPlayerExecuteCommands().size() > 0) {
                    for (String cmd : dialogue.getPlayerExecuteCommands()) {
                        String command = cmd.replace("@p", player.getName());
                        Server.getInstance().dispatchCommand(player, command);
                    }
                }
                if(MainClass.isCharmPointAllowed()) {
                    CharmPoint.addCharmPoint(player, dialogue.getCharmPoint());
                }
                dialogue.addFinishedPlayer(player);
            }
            this.cancel();
        }
        tick++;
    }

    public DialoguePlayTask(Player player, Dialogue dialogue){
        this.player = player;
        this.dialogue = dialogue;
    }

    public void showText(String title, String text, Boolean bool){
        if(MainClass.showType.equals("toast")){
            if(bool) {
                ToastRequestPacket pk = new ToastRequestPacket();
                pk.title = title;
                pk.content = text;
                player.dataPacket(pk);
            }
        }
    }

    public void showText(String text, Boolean bool){
        switch (MainClass.showType){
            case "actionbar":
                player.sendActionBar(text);
                break;
            case "tip":
                player.sendTip(text);
                break;
            case "message":
                if(bool) {
                    player.sendMessage(text);
                }
                break;
            case "popup":
            default:
                player.sendPopup(text);
                break;
        }
    }
}
