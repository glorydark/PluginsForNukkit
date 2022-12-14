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
        if(config.exists("????????????") && config.exists("????????????") && config.exists("????????????") && config.exists("??????????????????") && config.exists("?????????????????????") && config.exists("??????????????????") && config.exists("????????????") && config.exists("?????????") && config.exists("????????????") && config.exists("????????????") && config.exists("??????????????????") && config.exists("?????????????????????")  && config.exists("????????????????????????")){
            dialogue.speakerName = config.getString("????????????");
            dialogue.movable = config.getBoolean("????????????");
            dialogue.charmPoint = config.getDouble("?????????");
            dialogue.finishMessages = config.getStringList("????????????");
            dialogue.consoleExecuteCommands = config.getStringList("?????????????????????");
            dialogue.playerExecuteCommands = config.getStringList("??????????????????");
            dialogue.changeInterval = config.getInt("??????????????????");
            dialogue.speakerContent = config.getStringList("????????????");
            dialogue.dialogueName = config.getString("????????????");
            dialogue.finishPlayers = config.getStringList("????????????");
            dialogue.canRegainCharmPoint = config.getBoolean("?????????????????????");
            dialogue.canRegainRewards = config.getBoolean("??????????????????");
            dialogue.isShowFinishedWindow = config.getBoolean("????????????????????????");
            dialogue.requirements = config.getStringList("??????????????????");
            dialogue.costItem = config.getStringList("??????????????????");
            dialogue.needItem = config.getStringList("??????????????????");
            dialogue.isOnce = config.getBoolean("????????????????????????");
            dialogue.playerMustExecuteCommands = config.getStringList("????????????????????????");
            HashMap<String, List<String>> commandsDuringPlayingExecutedByConsole = (HashMap<String, List<String>>) config.get("??????????????????????????????");
            if(commandsDuringPlayingExecutedByConsole != null){
                dialogue.commandsDuringPlayingExecutedByConsole = commandsDuringPlayingExecutedByConsole;
            }
            HashMap<String, List<String>> commandsDuringPlayingExecutedByPlayer = (HashMap<String, List<String>>) config.get("?????????????????????????????????");
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
