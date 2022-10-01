package simpleworldsteleport;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

// SimpleWorldsTeleport By Glorydark

public class MainClass extends PluginBase {
    public static Map<String, Object> config = new TreeMap<>();

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getCommandMap().register("", new WorldTeleportCommand());
        this.saveResource("config.yml", false);
        this.loadAllWorlds();
        this.loadConfig();
        try {
            Class.forName("com.smallaswater.npc.variable.VariableManage");
            com.smallaswater.npc.variable.VariableManage.addVariableV2("SheepWarVariable", RsNpcXVariableV2.class);
        } catch (Exception ignored) {
            this.getLogger().info("您还未安装Rsnpcx！");
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void loadAllWorlds(){
        File file = new File(this.getServer().getFilePath()+"/worlds/");
        File[] files = file.listFiles();
        for(File worldFolder: files){
            if(worldFolder.isDirectory()){
                String worldName = worldFolder.getName();
                this.getLogger().info(TextFormat.GREEN+"Preparing for the level: "+ worldName);
                if(!Server.getInstance().isLevelLoaded(worldName)) {
                    if (Server.getInstance().loadLevel(worldName)) {
                        this.getLogger().info(TextFormat.GREEN + "Level Loaded Successfully: " + worldName);
                    } else {
                        this.getLogger().info(TextFormat.RED + "Level Loaded Failed: " + worldName);
                    }
                }else{
                    this.getLogger().info(TextFormat.RED + "Level Has Been Loaded: " + worldName);
                }
            }
        }
    }

    private void loadConfig(){
        config = new Config(this.getDataFolder()+"/config.yml", Config.YAML).getAll();
        this.getLogger().info(TextFormat.GREEN+"Config Loaded!");
    }
}
