package glorydark.lotterybox.languages;

import cn.nukkit.utils.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Lang {
    private Map<String, Object> map = new HashMap<>();

    public Lang(File file){
        if(file.exists()){
            Config config = new Config(file, Config.YAML);
            map = config.getAll();
        }
    }

    public String getTranslation(String key, String subKey, Object... params){
        Map<String, String> getMap = (Map<String, String>) map.get(key);
        if(getMap != null) {
            String s = getMap.getOrDefault(subKey, "null").replace("\\n", "\n");
            for(int i =1; i<= params.length; i++){
                s = s.replaceAll("%"+i+"%", params[i-1].toString());
            }
            return s;
        }else{
            return "null";
        }
    }
}
