package glorydark.SimpleDialogues;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dialogue {
    private String speakerName;
    private List<String> speakerContent;
    private Integer changeInterval;
    private List<String> consoleExecuteCommands;
    private List<String> playerExecuteCommands;
    private List<String> finishMessages;
    private Double charmPoint;
    private Boolean movable;
    private String dialogueName;
    private List<String> finishPlayers = new ArrayList<>();
    private Boolean canRegainRewards;
    private Boolean canRegainCharmPoint;
    private Boolean isShowFinishedWindow;
    private List<String> requirements;
    private List<String> needItem;
    private List<String> costItem;
    private Boolean isOnce;

    private List<String> playerMustExecuteCommands;

    private HashMap<String, List<String>> commandsDuringPlayingExecutedByConsole;

    private HashMap<String, List<String>> commandsDuringPlayingExecutedByPlayer;


    public Dialogue(String dialogName, String speakerName, List<String> speakerContent, Integer changeInterval, List<String> consoleExecuteCommands, List<String> playerExecuteCommands, List<String> finishMessages, Double charmPoint, Boolean movable, Boolean isShowFinishedWindow, Boolean isOnce, List<String> requirements, List<String> needItem, List<String> costItem, List<String> playerMustExecuteCommands, HashMap<String, List<String>> commandsDuringPlayingExecutedByConsole, HashMap<String, List<String>> commandsDuringPlayingExecutedByPlayer){
        this.dialogueName = dialogName;
        this.speakerName = speakerName;
        this.speakerContent = speakerContent;
        this.changeInterval = changeInterval;
        this.consoleExecuteCommands = consoleExecuteCommands;
        this.playerExecuteCommands = playerExecuteCommands;
        this.finishMessages = finishMessages;
        this.charmPoint = charmPoint;
        this.movable = movable;
        this.isShowFinishedWindow = isShowFinishedWindow;
        this.requirements = requirements;
        this.needItem = needItem;
        this.costItem = costItem;
        this.isOnce = isOnce;
        this.playerMustExecuteCommands = playerMustExecuteCommands;
        this.commandsDuringPlayingExecutedByConsole = commandsDuringPlayingExecutedByConsole;
        this.commandsDuringPlayingExecutedByPlayer = commandsDuringPlayingExecutedByPlayer;
    }

    public static Dialogue parseConfig(Config config){
        //check whether config is valid or not
        Dialogue dialogue = new Dialogue("dialogueName","speakerName", new ArrayList<>(), 0,new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),0d,false,false, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>());
        if(config.exists("对话名称") && config.exists("对话人物") && config.exists("对话内容") && config.exists("内容切换间隔") && config.exists("控制台执行指令") && config.exists("玩家执行指令") && config.exists("完成消息") && config.exists("魅力值") && config.exists("允许移动") && config.exists("完成玩家") && config.exists("重复执行指令") && config.exists("重复获取魅力值")  && config.exists("是否弹出结束窗口")){
            dialogue.speakerName = config.getString("对话人物");
            dialogue.movable = config.getBoolean("允许移动");
            dialogue.charmPoint = config.getDouble("魅力值");
            dialogue.finishMessages = config.getStringList("完成消息");
            dialogue.consoleExecuteCommands = config.getStringList("控制台执行指令");
            dialogue.playerExecuteCommands = config.getStringList("玩家执行指令");
            dialogue.changeInterval = config.getInt("内容切换间隔");
            dialogue.speakerContent = config.getStringList("对话内容");
            dialogue.dialogueName = config.getString("对话名称");
            dialogue.finishPlayers = config.getStringList("完成玩家");
            dialogue.canRegainCharmPoint = config.getBoolean("重复获取魅力值");
            dialogue.canRegainRewards = config.getBoolean("重复执行指令");
            dialogue.isShowFinishedWindow = config.getBoolean("是否弹出结束窗口");
            dialogue.requirements = config.getStringList("需要完成对话");
            dialogue.costItem = config.getStringList("需要消耗物品");
            dialogue.needItem = config.getStringList("需要拥有物品");
            dialogue.isOnce = config.getBoolean("是否仅限播放一次");
            dialogue.playerMustExecuteCommands = config.getStringList("玩家必定执行指令");
            HashMap<String, List<String>> commandsDuringPlayingExecutedByConsole = (HashMap<String, List<String>>) config.get("对话中途玩家执行指令");
            if(commandsDuringPlayingExecutedByConsole != null){
                dialogue.commandsDuringPlayingExecutedByConsole = commandsDuringPlayingExecutedByConsole;
            }
            HashMap<String, List<String>> commandsDuringPlayingExecutedByPlayer = (HashMap<String, List<String>>) config.get("对话中途控制台执行指令");
            if(commandsDuringPlayingExecutedByPlayer != null){
                dialogue.commandsDuringPlayingExecutedByPlayer = commandsDuringPlayingExecutedByPlayer;
            }
            return dialogue;
        }else{
            MainClass.plugin.getLogger().error("Can not parse the config");
            return null;
        }
    }

    public List<String> getNeedItem() {
        return needItem;
    }

    public List<String> getCostItem() {
        return costItem;
    }

    public Boolean isOnce(){
        return isOnce;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public Boolean getMovable() {
        return movable;
    }

    public Double getCharmPoint() {
        return charmPoint;
    }

    public List<String> getConsoleExecuteCommands() {
        return consoleExecuteCommands;
    }

    public List<String> getPlayerExecuteCommands() {
        return playerExecuteCommands;
    }

    public List<String> getFinishMessages() {
        return finishMessages;
    }

    public Integer getChangeInterval() {
        return changeInterval;
    }

    public List<String> getSpeakerContent() {
        return speakerContent;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public String getDialogueName() {
        return dialogueName;
    }

    public List<String> getFinishPlayers() {
        return finishPlayers;
    }

    public Boolean canRegainRewards() {
        return canRegainRewards;
    }

    public Boolean canRegainCharmPoint() {
        return canRegainCharmPoint;
    }

    public void addFinishedPlayer(Player player){
        if(player != null && !finishPlayers.contains(player.getName())){
            finishPlayers.add(player.getName());
        }
    }

    public Boolean isShowFinishedWindow() {
        return isShowFinishedWindow;
    }

    public List<String> getPlayerMustExecuteCommands() {
        return playerMustExecuteCommands;
    }

    public List<String> getCommandsDuringPlayingExecutedByConsole(Integer index) {
        return commandsDuringPlayingExecutedByConsole.getOrDefault(String.valueOf(index), new ArrayList<>());
    }

    public List<String> getCommandsDuringPlayingExecutedByPlayer(Integer index) {
        return commandsDuringPlayingExecutedByPlayer.getOrDefault(String.valueOf(index), new ArrayList<>());
    }
}
