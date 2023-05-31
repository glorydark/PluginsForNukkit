package glorydark.customform;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import glorydark.customform.forms.FormCreator;
import glorydark.customform.forms.FormListener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class CustomFormMain extends PluginBase {

    public String path;

    public static Plugin plugin;

    public static boolean enableTips = false;

    public static long coolDownMillis;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        path = this.getDataFolder().getPath();
        plugin = this;
        Config config = new Config(path+"/config.yml",Config.YAML);
        coolDownMillis = config.getLong("coolDown", 200L);
        File file = new File(path+"/forms/");
        if(!file.exists()){
            if(!file.mkdirs()){
                this.getLogger().warning("Can not make dictionary for this plugin.");
                this.setEnabled(false);
            }
        }
        Plugin tips =this.getServer().getPluginManager().getPlugin("Tips");
        enableTips = (tips != null);
        if(enableTips){
            this.getLogger().info("§a检测到您安装了Tips前置，已经自动开启适配功能！");
        }else{
            this.getLogger().info("§c检测到您未安装了Tips前置，未开启适配功能！");
        }
        this.loadScriptWindows();
        this.getLogger().info("CustomForm onLoad");
        this.getServer().getCommandMap().register("", new Commands("form"));
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
    }

    public void loadScriptWindows(){
        FormCreator.windowScripts.clear();
        FormCreator.UI_CACHE.clear();
        File dic = new File(path+"/forms/");
        for(File file: Objects.requireNonNull(dic.listFiles())){
            if(file.getName().endsWith(".json")){
                InputStream stream;
                try {
                    stream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8); //一定要以utf-8读取
                JsonReader reader = new JsonReader(streamReader);
                Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {}.getType(), new GsonAdapter()).create();
                Map<String, Object> mainMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());

                String identifier = file.getName().replace(".json","");
                if(FormCreator.loadWindow(identifier, mainMap)){
                    this.getLogger().info(TextFormat.YELLOW+"成功加载窗口:"+identifier);
                }else{
                    this.getLogger().error("脚本窗口加载失败，脚本名：" + identifier);
                }
                try {
                    reader.close();
                    streamReader.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(file.getName().endsWith(".yml")){
                String identifier = file.getName().replace(".yml","");
                Map<String, Object> mainMap = new Config(file, Config.YAML).getAll();
                if(FormCreator.loadWindow(identifier, mainMap)){
                    this.getLogger().info(TextFormat.YELLOW+"成功加载窗口:"+identifier);
                }else{
                    this.getLogger().error("脚本窗口加载失败，脚本名：" + identifier);
                }
            }
        }
        this.getLogger().info(TextFormat.GREEN+"成功加载"+ FormCreator.windowScripts.keySet().size()+"个脚本窗口");
    }

    private class Commands extends Command {

        public Commands(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if(strings.length == 0){ return false; }
            switch (strings[0].toLowerCase()){
                case "reload":
                    if(commandSender.isOp() || !commandSender.isPlayer()){
                        loadScriptWindows();
                        commandSender.sendMessage(TextFormat.GREEN + "Reload configurations successfully！");
                    }else{
                        commandSender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", s));
                    }
                    break;
                case "show":
                    if(commandSender.isPlayer()){
                        if(strings.length == 2) {
                            FormCreator.showScriptGui((Player) commandSender, strings[1]);
                        }
                    }else{
                        commandSender.sendMessage("Please use it in game!");
                    }
                    break;
            }
            return true;
        }
    }
}