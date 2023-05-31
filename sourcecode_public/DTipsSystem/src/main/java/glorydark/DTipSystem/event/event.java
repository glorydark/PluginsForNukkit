package glorydark.DTipSystem.event;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.utils.Config;
import glorydark.DTipSystem.MainClass;
import glorydark.DTipSystem.title;
import glorydark.DTipSystem.util.ActionbarData;
import glorydark.DTipSystem.util.PopupData;
import glorydark.DTipSystem.util.TitleData;

import java.util.ArrayList;
import java.util.List;

public class event implements Listener {

    @EventHandler
    public void Login(PlayerLocallyInitializedEvent event){
        final Player player = event.getPlayer();
        Config config = title.getConfig("config.yml");
        switch (config.getInt("addtype")) {
            case 0:
                String content = config.getString("addContent");
                String subTitle = config.getString("addSubTitle");
                int duration = config.getInt("duration");
                int fadein = config.getInt("fadein");
                int fadeout = config.getInt("fadeout");
                content = title.ModifyText(content,player);
                subTitle = title.ModifyText(subTitle,player);
                TitleData titledata = new TitleData(content,subTitle,fadein,fadeout,duration);
                player.getServer().getScheduler().scheduleDelayedTask(MainClass.getPlugin(), () -> player.sendTitle(titledata.content, titledata.subTitle, titledata.fadein, titledata.duration, titledata.fadeout), 2 * 20);
                /*
                commands = new ArrayList<>(config.getStringList("executecommand"));
                if(commands.size() > 0){
                    for(String command: commands){
                        String output = command.replace("%p",p.getName());
                        p.getServer().dispatchCommand(new ConsoleCommandSender(),output);
                    }
                }

                 */
                break;
            case 1:
                content = config.getString("addContent");
                duration = config.getInt("duration");
                fadein = config.getInt("fadein");
                fadeout = config.getInt("fadeout");
                content = title.ModifyText(content,player);
                ActionbarData actionbarData = new ActionbarData(content,fadein,fadeout,duration);
                player.getServer().getScheduler().scheduleDelayedTask(MainClass.getPlugin(), () -> player.sendActionBar(actionbarData.content,actionbarData.fadein,actionbarData.duration,actionbarData.fadeout), 2 * 20);
                /*
                commands = new ArrayList<>(config.getStringList("executecommand"));
                if(commands.size() > 0){
                    for(String command: commands){
                        String output = command.replace("%p",p.getName());
                        p.getServer().dispatchCommand(new ConsoleCommandSender(),output);
                    }
                }

                 */
                break;
            case 2:
                String message = config.get("addContent",title.ModifyText("欢迎 %p% 进入 %level%",player));
                message = title.ModifyText(message, player);
                String finalMessage = message;
                player.getServer().getScheduler().scheduleDelayedTask(MainClass.getPlugin(), () -> title.sendMessage(player, finalMessage), 2 * 20);
                break;
            case 3:
                content = config.get("addContent",title.ModifyText("欢迎 %p% 进入 %level%",player));
                subTitle = config.getString("addSubTitle");
                content = title.ModifyText(content, player);
                PopupData popupData = new PopupData(content,subTitle);
                player.getServer().getScheduler().scheduleDelayedTask(MainClass.getPlugin(), () ->player.sendPopup(popupData.content, popupData.subTitle), 2 * 20);
                break;
        }
    }

    @EventHandler
    public void LevelChangeEvent(EntityLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Config config = title.getConfig("world.yml");
        Player p = event.getEntity().getServer().getPlayer(event.getEntity().getName());
        String LevelName = event.getTarget().getName();
        if(!config.exists(LevelName)){ return; }
        switch (config.getInt(LevelName+".addtype")) {
            case 0:
                String content = config.getString(LevelName+".addContent");
                String subTitle = config.getString(LevelName+".addSubTitle");
                List<String> commands = new ArrayList<>(config.getStringList(LevelName+".executecommand"));
                int duration = config.getInt(LevelName+".duration");
                int fadein = config.getInt(LevelName+".fadein");
                int fadeout = config.getInt(LevelName+".fadeout");
                p.sendTitle(title.ModifyTextByLevel(content,p,event.getTarget()),title.ModifyTextByLevel(subTitle,p,event.getTarget()),fadein,duration,fadeout);
                /*
                commands = new ArrayList<>(config.getStringList(LevelName+".executecommand"));
                if(commands.size() > 0){
                    for(String command: commands){
                        String output = command.replace("%p",p.getName());
                        p.getServer().dispatchCommand(new ConsoleCommandSender(),output);
                    }
                }

                 */
                break;
            case 1:
                content = config.getString(LevelName+".addContent");
                duration = config.getInt(LevelName+".duration");
                fadein = config.getInt(LevelName+".fadein");
                fadeout = config.getInt(LevelName+".fadeout");
                p.sendActionBar(title.ModifyTextByLevel(content,p, event.getTarget()),fadein,duration,fadeout);
                 /*
                commands = new ArrayList<>(config.getStringList(LevelName+".executecommand"));
                if(commands.size() > 0){
                    for(String command: commands){
                        String output = command.replace("%p",p.getName());
                        p.getServer().dispatchCommand(new ConsoleCommandSender(),output);
                    }
                }

                 */
                break;
            case 2:
                String message = config.get(LevelName+".addContent",title.ModifyTextByLevel("欢迎 %p% 进入 %level%",p, event.getTarget()));
                p.sendMessage(title.ModifyTextByLevel(message, p, event.getTarget()));
                /*
                commands = new ArrayList<>(config.getStringList(LevelName+".executecommand"));
                if(commands.size() > 0){
                    for(String command: commands){
                        String output = command.replace("%p",p.getName());
                        p.getServer().dispatchCommand(new ConsoleCommandSender(),output);
                    }
                }

                 */
            case 3:
                content = config.get(LevelName+".addContent",title.ModifyTextByLevel("欢迎 %p% 进入 %level%",p, event.getTarget()));
                subTitle = config.getString(LevelName+".addSubTitle");
                p.sendPopup(title.ModifyTextByLevel(content, p, event.getTarget()),title.ModifyTextByLevel(subTitle,p, event.getTarget()));
                 /*
                commands = new ArrayList<>(config.getStringList(LevelName+".executecommand"));
                if(commands.size() > 0){
                    for(String command: commands){
                        String output = command.replace("%p",p.getName());
                        p.getServer().dispatchCommand(new ConsoleCommandSender(),output);
                    }
                }

                 */
                break;
        }
    }
}
