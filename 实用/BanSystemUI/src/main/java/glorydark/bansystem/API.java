package glorydark.bansystem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import com.sun.istack.internal.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class API {
    public static Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
    }

    public static String getDateString(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.HOUR) + "/" + calendar.get(Calendar.MINUTE) + "/" + calendar.get(Calendar.SECOND);
    }

    public static Calendar parseDateString(String string) {
        String[] strings = string.split("/");
        if (string.length() >= 6) {
            return new Calendar.Builder().set(Calendar.YEAR, Integer.parseInt(strings[0])).set(Calendar.MONTH, Integer.parseInt(strings[1]) - 1).set(Calendar.DATE, Integer.parseInt(strings[2])).set(Calendar.HOUR, Integer.parseInt(strings[3])).set(Calendar.MINUTE, Integer.parseInt(strings[4])).set(Calendar.SECOND, Integer.parseInt(strings[5])).build();
        } else {
            return new Calendar.Builder().setInstant(0).build();
        }
    }

    public static void updateReport(String reportPlayer, String beingReportedPlayer, String type, String reason, Boolean isAnonymous) {
        String string = reportPlayer + "_" + getDateString(getCalendar());
        Config config = new Config(MainClass.path + "/reportRecord.json", Config.JSON);
        config.set(string + ".举报人", reportPlayer);
        config.set(string + ".被举报人", beingReportedPlayer);
        config.set(string + ".类型", type);
        config.set(string + ".原因", reason);
        config.set(string + ".举报时间", getDateString(getCalendar()));
        config.set(string + ".处理状态", false);
        config.set(string + ".是否匿名", isAnonymous);
        config.save();
    }

    public static void sendEmail(String player, String title, String content, List<String> commands) {
        Config config = new Config(MainClass.path + "/emails/" + player + ".json",Config.JSON);
        config.set(getDateString(getCalendar()) + ".title", title);
        config.set(getDateString(getCalendar()) + ".content", content);
        config.set(getDateString(getCalendar()) + ".commands", commands);
        config.save();
    }

    public static String getConfig(Integer type, String key, String subKey) {
        if (type == 0) { //config.yml
            Map<String, Object> map = new Config(MainClass.path + "/config.yml").getAll();
            Map<String, Object> map1 = (Map<String, Object>) map.get(key);
            return (String) map1.get(subKey);
        }
        return "null";
    }

    public static void setPlayerConfig(String player, String key, Object o){
        Config config = new Config(MainClass.path + "/players/"+player+".json",Config.JSON);
        config.set(key,o);
        config.save();
    }

    public static Object getPlayerConfig(String player, String key){
        Config config = new Config(MainClass.path + "/players/"+player+".json",Config.JSON);
        return config.get(key);
    }

    public static void banPlayer(@NotNull Player player,String reason, int year, int month, int day, int hour, int minute, int second){
        Calendar calendar = API.getCalendar();
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, day);
        calendar.add(Calendar.HOUR, hour);
        calendar.add(Calendar.MINUTE, minute);
        calendar.add(Calendar.SECOND, second);
        API.setPlayerConfig(player.getName(), "pardonDate", API.getDateString(calendar));
        player.kick("\n [BanSystem] 您已被管理员操作封禁，原因为:"+"\n"+reason + "\n解禁时间为:" +API.getDateString(calendar));
    }

    public static void unbanPlayer(String string){
        Config config = new Config(MainClass.path + "/players/"+string+".json",Config.JSON);
        config.remove("pardonDate");
        config.save();
    }

    public static void banPlayer(String player,String reason, int year, int month, int day, int hour, int minute, int second){
        Calendar calendar = API.getCalendar();
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, day);
        calendar.add(Calendar.HOUR, hour);
        calendar.add(Calendar.MINUTE, minute);
        calendar.add(Calendar.SECOND, second);
        API.setPlayerConfig(player, "pardonDate", API.getDateString(calendar));
        if(Server.getInstance().getPlayer(player) != null){
            Server.getInstance().getPlayer(player).kick("\n [BanSystem] 您已被管理员操作封禁，原因为:"+"\n"+reason + "\n解禁时间为:" +API.getDateString(calendar));
        }
    }
}