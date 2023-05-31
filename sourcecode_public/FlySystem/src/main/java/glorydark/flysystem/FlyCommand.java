package glorydark.flysystem;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;

import java.util.Calendar;
import java.util.Locale;

public class FlyCommand extends Command {
    public FlyCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.isOp() && commandSender.isPlayer()){ commandSender.sendMessage("您没有权限"); return false; }
        if(strings.length == 0){
            commandSender.sendMessage("指令格式错误！");
            commandSender.sendMessage("/fly give player years months days hours minutes seconds");
            commandSender.sendMessage("/fly remove player");
            return false;
        }
        Config config = new Config(MainClass.path+"/config.yml", Config.YAML);
        switch (strings[0]){
            case "give":
                if(strings.length != 8){ commandSender.sendMessage("指令格式错误！/fly give player years months days hours minutes seconds");return false;}
                float year = Float.parseFloat(strings[2]);
                float month = Float.parseFloat(strings[3]);
                float day = Float.parseFloat(strings[4]);
                float hour = Float.parseFloat(strings[5]);
                float min = Float.parseFloat(strings[6]);
                float sec = Float.parseFloat(strings[7]);
                config.set("playerRecords." + strings[1], getDuration(year,month,day,hour,min,sec).getTimeInMillis());
                config.save();
                commandSender.sendMessage("给予成功！");
                break;
            case "remove":
                if(strings.length != 2){ commandSender.sendMessage("指令格式错误！/fly remove player");return false;}
                config.remove("playerRecords." + strings[1]);
                config.save();
                commandSender.sendMessage("移除成功！");
                break;
        }
        return true;
    }

    public Calendar getDuration(Float y, Float m, Float d, Float h, Float min, Float sec){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.add(Calendar.YEAR, y.intValue());
        calendar.add(Calendar.MONTH, m.intValue());
        calendar.add(Calendar.DATE, d.intValue());
        calendar.add(Calendar.HOUR, h.intValue());
        calendar.add(Calendar.MINUTE, min.intValue());
        calendar.add(Calendar.SECOND, sec.intValue());
        return calendar;
    }
}
