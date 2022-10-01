package glorydark.SimpleDialogues;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tools {
    /*引用代码: 由于Java是基于Unicode编码的，因此，一个汉字的长度为1，而不是2。
     * 但有时需要以字节单位获得字符串的长度。例如，“123abc长城”按字节长度计算是10，而按Unicode计算长度是8。
     * 为了获得10，需要从头扫描根据字符的Ascii来获得具体的长度。如果是标准的字符，Ascii的范围是0至255，如果是汉字或其他全角字符，Ascii会大于255。
     * 因此，可以编写如下的方法来获得以字节为单位的字符串长度。*/
    public static Integer getStringCharCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;
        }
        return length;
    }

    // update configs (Dialogues' Commands Update)
    public static void fixDialoguesFiles() {
        File path = new File(MainClass.path+"/dialogues/");
        if(path.exists()) {
            File[] files = path.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    Config config = new Config(file, Config.YAML);
                    if (!config.exists("对话名称")) {
                        config.set("对话名称", "对话名称");
                    }
                    if (!config.exists("对话人物")) {
                        config.set("对话人物", "对话人物");
                    }
                    if (!config.exists("对话内容")) {
                        config.set("对话内容", new ArrayList<>());
                    }
                    if (!config.exists("对话中途玩家执行指令")) {
                        HashMap<String, List<String>> out = new HashMap<>();
                        out.put("default", new ArrayList<>());
                        config.set("对话中途玩家执行指令", out);
                    }
                    if (!config.exists("对话中途控制台执行指令")) {
                        HashMap<String, List<String>> out = new HashMap<>();
                        out.put("default", new ArrayList<>());
                        config.set("对话中途控制台执行指令", out);
                    }
                    if (!config.exists("内容切换间隔")) {
                        config.set("内容切换间隔", 50);
                    }
                    if (!config.exists("玩家执行指令")) {
                        config.set("玩家执行指令", new ArrayList<>());
                    }
                    if (!config.exists("控制台执行指令")) {
                        config.set("控制台执行指令", new ArrayList<>());
                    }
                    if (!config.exists("完成消息")) {
                        config.set("完成消息", new ArrayList<>());
                    }
                    if (!config.exists("魅力值")) {
                        config.set("魅力值", new ArrayList<>());
                    }
                    if (!config.exists("允许移动")) {
                        config.set("允许移动", new ArrayList<>());
                    }
                    if (!config.exists("完成玩家")) {
                        config.set("完成玩家", new ArrayList<>());
                    }
                    if (!config.exists("重复执行指令")) {
                        config.set("重复执行指令", false);
                    }
                    if (!config.exists("重复获取魅力值")) {
                        config.set("重复获取魅力值", false);
                    }
                    if (!config.exists("是否弹出结束窗口")) {
                        config.set("是否弹出结束窗口", false);
                    }
                    if (!config.exists("需要完成对话")) {
                        config.set("需要完成对话", new ArrayList<>());
                    }
                    if (!config.exists("需要消耗物品")) {
                        config.set("需要消耗物品", new ArrayList<>());
                    }
                    if (!config.exists("需要拥有物品")) {
                        config.set("需要拥有物品", new ArrayList<>());
                    }
                    if (!config.exists("是否仅限播放一次")) {
                        config.set("是否仅限播放一次", false);
                    }
                    if (!config.exists("玩家必定执行指令")) {
                        config.set("玩家必定执行指令", new ArrayList<>());
                    }
                    config.save();
                }
            }
        }

        /*
        Config config1 = new Config(MainClass.path + "/config.yml", Config.YAML);
        if(!config1.exists("对话显示方式")){
            config1.set("对话显示方式", "tip");
        }
        config1.set("配置版本",2022051401);
        config1.save();
         */
    }

    public static void playerExecuteCommand(Player player, String command){
        String exeCommand = command.replace("@p", player.getName());
        Server.getInstance().dispatchCommand(player, command);
    }

    public static void playerExecuteCommands(Player player, List<String> commands){
        for(String cmd: commands){
            playerExecuteCommand(player, cmd);
        }
    }
}
