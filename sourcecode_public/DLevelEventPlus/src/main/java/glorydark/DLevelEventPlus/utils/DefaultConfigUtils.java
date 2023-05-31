package glorydark.DLevelEventPlus.utils;

import cn.nukkit.utils.Config;
import glorydark.DLevelEventPlus.MainClass;

import java.io.File;
import java.util.LinkedHashMap;

public class DefaultConfigUtils {

    public Config config;

    public DefaultConfigUtils(Config config){
        this.config = config;
    }

    public static Boolean isYaml(String name){
        String[] formatSplit = name.split("\\.");
        if(formatSplit.length > 1){
            return formatSplit[1].equals("yml");
        }
        return false;
    }

    public boolean writeAll(int type, String fileName){
        File file;
        switch (type){
            case 0:
                file = new File(MainClass.path+"/worlds/"+fileName+".yml");
                if(!file.exists()) {
                    Config save = new Config(file, Config.YAML);
                    save.setAll((LinkedHashMap<String, Object>) config.getAll());
                    save.save();
                }else{
                    return false;
                }
                break;
            case 1:
                file = new File(MainClass.path+"/templates/"+fileName+".yml");
                if(!file.exists()) {
                    Config save = new Config(file, Config.YAML);
                    save.setAll((LinkedHashMap<String, Object>) config.getAll());
                    save.save();
                }else{
                    return false;
                }
                break;
        }
        return false;
    }

    public void checkAll(String fileName, Config check){
        boolean isUpdated = false;
        int changes = 0;
        for(String key: config.getKeys(false)){
            if(check.exists(key)){
                for(String subKey: config.getSection(key).getKeys(false)){
                    String getKey = key+"."+subKey;
                    if(!check.exists(getKey)){
                        isUpdated = true;
                        changes+=1;
                        check.set(getKey, config.get(getKey));
                        check.save();
                    }
                }

                for(String subKey: check.getSection(key).getKeys(false)){
                    String getKey = key+"."+subKey;
                    if(!config.exists(getKey)){
                        isUpdated = true;
                        changes+=1;
                        check.getSection(key).remove(subKey);
                        check.save();
                    }
                }
            }else{
                isUpdated = true;
                changes+=1;
                check.set(key, config.get(key));
                check.save();
            }
        }
        if(isUpdated){
            MainClass.plugin.getLogger().info("§a成功更新配置："+fileName+"，共更新"+changes+"处！");
        }else{
            MainClass.plugin.getLogger().info("§e配置已为最新，文件名："+fileName);
        }
    }
}
