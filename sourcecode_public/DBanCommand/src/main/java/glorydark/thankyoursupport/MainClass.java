package glorydark.thankyoursupport;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.util.*;

public class MainClass extends PluginBase implements Listener {

    public static List<String> bancommands = new ArrayList<>();
    public static HashMap<String, Object> worldBannedCommands = new HashMap<>();

    public static Map<String, Object> lang = new LinkedHashMap<>();


    @Override
    public void onEnable() {
        this.saveResource("bancommands.yml",false);
        this.saveResource("lang.yml",false);
        this.getServer().getPluginManager().registerEvents(this, this);
        Config config = new Config(this.getDataFolder()+"/bancommands.yml",Config.YAML);
        updateConfig(config);
        bancommands = config.getStringList("全局禁用指令");
        worldBannedCommands = config.get("单世界禁用指令", new HashMap<>());
        lang = new Config(this.getDataFolder().getPath()+"/lang.yml", Config.YAML).getAll();
    }

    @EventHandler
    public void CommandProcess(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if(player == null){return;}
        if(player.isOp()){ return; }
        String command = event.getMessage().toLowerCase();
        if(isWorldBannedCommand(player, command)){
            event.setCancelled(true);
        }
        if(isGlobalBannedCommand(player, command)){
            event.setCancelled(true);
        }
    }

    public String getLang(String key, Object... params){
        String out = (String) lang.get(key);
        for(int i = 1; i <= params.length; i++){
            out = out.replace("%"+i+"%", ((String) params[i-1]));
        }
        return out;
    }

    public void updateConfig(Config config){
        if(config.exists("bancommands")){
            config.set("全局禁用指令", config.getStringList("bancommands"));
            config.set("单世界禁用指令", new ArrayList<>());
            config.remove("bancommands");
            config.save();
            this.getLogger().info("您的配置文件已更新！");
        }else{
            this.getLogger().info("您的配置文件已为最新！");
        }
    }

    public Boolean isWorldBannedCommand(Player player, String command){
        Object object =  worldBannedCommands.get(player.getLevel().getName());
        if(object != null) {
            List<String> strings = (List<String>) object;
            if(strings.size() < 1){return false;}
            for(String verifyString: strings){
                verifyString = ("/"+verifyString).toLowerCase();
                if(command.startsWith(verifyString)){player.sendMessage(getLang("world_ban", command.replaceFirst("/", ""))); return false; }
            }
        }
        return false;
    }

    public Boolean isGlobalBannedCommand(Player player, String command){
        if(bancommands.size()<1){return false;}
        for(String verifyString: bancommands){
            verifyString = ("/"+verifyString).toLowerCase();
            if(command.startsWith(verifyString)){
                player.sendMessage(getLang("global_ban", command.replaceFirst("/", "")));
                return true;
            }
        }
        return false;
    }
}
