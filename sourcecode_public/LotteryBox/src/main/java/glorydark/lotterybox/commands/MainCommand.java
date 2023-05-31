package glorydark.lotterybox.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.lotterybox.MainClass;
import glorydark.lotterybox.forms.CreateGui;
import glorydark.lotterybox.languages.Lang;
import glorydark.lotterybox.tools.BasicTool;
import glorydark.lotterybox.tools.Inventory;

import java.io.File;
import java.util.ArrayList;

public class MainCommand extends Command {

    public MainCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(strings.length < 1){ return false; }
        switch (strings[0]){
            case "menu":
                if(commandSender instanceof Player){
                    CreateGui.showSelectLotteryBoxWindow(((Player) commandSender).getPlayer());
                }else{
                    commandSender.sendMessage(MainClass.lang.getTranslation("Tips","CommandShouldBeUsedInGame"));
                }
                break;
            case "give": //give player ticket amount
                if(!(commandSender instanceof Player) || commandSender.isOp()){
                    if(strings.length == 4){
                        if(Server.getInstance().lookupName(strings[1]).isPresent()) {
                            if (MainClass.registered_tickets.contains(strings[2])) {
                                BasicTool.changeTicketCounts(strings[1], strings[2], Integer.valueOf(strings[3]));
                                Player player = Server.getInstance().getPlayer(strings[1]);
                                if(player != null){
                                    player.sendMessage(MainClass.lang.getTranslation("Tips", "ReceiveTicket", strings[2], strings[3]));
                                }
                                commandSender.sendMessage(MainClass.lang.getTranslation("Tips","GiveTicketSuccess"));
                            }else{
                                commandSender.sendMessage(MainClass.lang.getTranslation("Tips","TicketWithoutRegistration", MainClass.registered_tickets));
                            }
                        }else{
                            commandSender.sendMessage(MainClass.lang.getTranslation("Tips","PlayerNotFound"));
                        }
                    }else{
                        commandSender.sendMessage(MainClass.lang.getTranslation("Tips","UseCommandInWrongFormat"));
                    }
                }else{
                    commandSender.sendMessage(MainClass.lang.getTranslation("Tips","NoPermission"));
                }
                break;
            case "help":
                if(!(commandSender instanceof Player) || commandSender.isOp()) {
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "Title"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "GiveCommand"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "OpenMenuCommand"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "HelpCommand"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "SaveItemCommand"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "ReloadCommand"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "CreateLotteryBoxCommand"));
                }else{
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "Title"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "OpenMenuCommand"));
                    commandSender.sendMessage(MainClass.lang.getTranslation("Helps", "HelpCommand"));
                }
                break;
            case "saveitem":
                if(commandSender instanceof Player){
                    if(commandSender.isOp()) {
                        if (strings.length == 2) {
                            Config config = new Config(MainClass.path + "/saveitem.yml", Config.YAML);
                            if (!config.exists(strings[1])) {
                                config.set(strings[1], Inventory.saveItemToString(((Player) commandSender).getInventory().getItemInHand()));
                                commandSender.sendMessage(MainClass.lang.getTranslation("Tips", "SaveItemSuccessfully"));
                            } else {
                                config.set(strings[1] + "-copy", Inventory.saveItemToString(((Player) commandSender).getInventory().getItemInHand()));
                                commandSender.sendMessage(MainClass.lang.getTranslation("Tips", "SaveItemExists",strings[1] + "-copy"));
                            }
                            config.save();
                        } else {
                            commandSender.sendMessage(MainClass.lang.getTranslation("Tips", "UseCommandInWrongFormat"));
                        }
                    }else{
                        commandSender.sendMessage(MainClass.lang.getTranslation("Tips","NoPermission"));
                    }
                }else{
                    commandSender.sendMessage(MainClass.lang.getTranslation("Tips","CommandShouldBeUsedInGame"));
                }
                break;
            case "reload":
                if(!(commandSender instanceof Player) || commandSender.isOp()){
                    Config config = new Config(MainClass.path+"/config.yml", Config.YAML);
                    MainClass.forceDefaultMode = config.getBoolean("force_default_mode", false);
                    MainClass.default_speed_ticks = config.getInt("default_speed_ticks", 4);
                    MainClass.chest_speed_ticks = config.getInt("chest_speed_ticks", 4);
                    MainClass.banWorlds = new ArrayList<>(config.getStringList("ban_worlds"));
                    MainClass.banWorldPrefix = new ArrayList<>(config.getStringList("ban_worlds_prefixs"));
                    MainClass.show_reward_window = config.getBoolean("show_reward_window", true);
                    MainClass.showType = config.getString("show_type", "actionbar");
                    MainClass.inventory_cache_paths = new ArrayList<>(config.getStringList("inventory_cache_paths"));
                    MainClass.save_bag_enabled = config.getBoolean("save_bag_enabled", true);
                    MainClass.registered_tickets = new ArrayList<>(config.getStringList("registered_tickets"));
                    String language = config.getString("language");
                    MainClass.lang = new Lang(new File(MainClass.path+"/languages/"+language+".yml"));
                    MainClass.loadBoxesConfig();
                    commandSender.sendMessage(MainClass.lang.getTranslation("Tips","ReloadFinish"));
                }else{
                    commandSender.sendMessage(MainClass.lang.getTranslation("Tips","NoPermission"));
                }
                break;
            case "createbox":
                if(!(commandSender instanceof Player) || commandSender.isOp()){
                    if (strings.length == 2) {
                        File file = new File(MainClass.path+"/boxes/"+strings[1]+".yml");
                        if(!file.exists()) {
                            MainClass.instance.saveResource("default.yml", "boxes/" + strings[1] + ".yml", false);
                            commandSender.sendMessage(MainClass.lang.getTranslation("Tips", "CreateLotteryBoxSuccessfully", strings[1]));
                        }else{
                            commandSender.sendMessage(MainClass.lang.getTranslation("Tips", "LotteryBoxExisted", strings[1]));
                        }
                    }else{
                        commandSender.sendMessage(MainClass.lang.getTranslation("Tips", "UseCommandInWrongFormat"));
                    }
                }else{
                    commandSender.sendMessage(MainClass.lang.getTranslation("Tips","NoPermission"));
                }
                break;
        }
        return true;
    }
}
