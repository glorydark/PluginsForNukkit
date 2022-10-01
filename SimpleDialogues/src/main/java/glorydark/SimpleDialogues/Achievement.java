package glorydark.SimpleDialogues;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import glorydark.SimpleDialogues.charmsystem.AchievementMain;
import glorydark.SimpleDialogues.charmsystem.CharmPoint;

import java.util.ArrayList;
import java.util.List;

public class Achievement {
    private String name;
    private String description;
    private List<String> requirements;
    private List<String> records;
    private List<String> commands;
    private Boolean canRegainRewards;

    public Achievement(String name, String description, List<String> requirement, List<String> command, Boolean canRegainRewards, List<String> record){
        this.name = name;
        this.description = description;
        this.requirements = requirement;
        this.records = record;
        this.commands = command;
        this.canRegainRewards = canRegainRewards;
    }

    public String getName() {
        return name;
    }

    public Boolean canRegainRewards() {
        return canRegainRewards;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRecords() {
        return records;
    }

    public void addRecord(Player player){
        if(!records.contains(player.getName())){
            records.add(player.getName());
        }
    }

    public static Achievement parseConfig(Config config){
        //check whether config is valid or not
        if(config.exists("成就名称") && config.exists("描述") && config.exists("领取条件") && config.exists("执行指令") && config.exists("重复领取") && config.exists("领取记录")){
            Achievement achievement = new Achievement("loading...", "loading...", new ArrayList<>(), new ArrayList<>(), false, new ArrayList<>());
            achievement.canRegainRewards = config.getBoolean("重复领取");
            achievement.commands = config.getStringList("执行指令");
            achievement.description = config.getString("描述");
            achievement.name = config.getString("成就名称");
            achievement.requirements = config.getStringList("领取条件");
            achievement.records = config.getStringList("领取记录");
            return achievement;
        }else{
            MainClass.plugin.getLogger().error("Can not parse the config");
            return null;
        }
    }

    public Boolean isPlayerQualified(Player player){
        if(this.requirements.size() > 0){
            for (String s:this.requirements){
                String[] str = s.split("\\|");
                if(str.length > 1){
                    switch (str[0]){
                        case "rank":
                            String[] rankStr = str[1].split("-");
                            int prank = CharmPoint.getRank(player);
                            if(prank != 0) {
                                if (rankStr.length == 1) {
                                    if (prank > Integer.parseInt(rankStr[0])) {
                                        return false;
                                    }
                                } else {
                                    if (prank > Integer.parseInt(rankStr[1])) {
                                        return false;
                                    }
                                }
                            }
                            break;
                        case "charmpoint":
                            if(CharmPoint.getCharmPointCache(player) < Double.parseDouble(str[1])){
                                return false;
                            }
                            break;
                    }
                }
            }
        }
        return true;
    }

    public Boolean isPlayerQualifiedByName(Player player, String achievementName){
        if(AchievementMain.achievements.containsKey(achievementName)){
            Achievement achievement = AchievementMain.achievements.get(achievementName);
            if(achievement != null) {
                if(achievement.requirements.size() > 0){
                    for (String s:achievement.requirements){
                        String[] str = s.split("\\|");
                        if(str.length > 1){
                            switch (str[0]){
                                case "rank":
                                    String[] rankStr = str[1].split("-");
                                    int prank = CharmPoint.getRank(player);
                                    if(prank != 0) {
                                        if (rankStr.length == 1) {
                                            if (prank > Integer.parseInt(rankStr[0])) {
                                                return false;
                                            }
                                        } else {
                                            if (prank > Integer.parseInt(rankStr[1])) {
                                                return false;
                                            }
                                        }
                                    }
                                    break;
                                case "charmpoint":
                                    if(CharmPoint.getCharmPointCache(player) < Integer.parseInt(str[0])){
                                        return false;
                                    }
                                    break;
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}
