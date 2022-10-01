package glorydark.CloudInventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import glorydark.CloudInventory.gui.GuiCreate;

import java.util.ArrayList;
import java.util.List;

public class InventoryCommand extends Command {
    public InventoryCommand(String command) {
        super(command,"云背包插件","/yun help");
        List<String> strings = new ArrayList<>();
        strings.add("云背包");
        setAliases(strings.toArray(new String[strings.size()]));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(strings.length < 1){
            if(commandSender instanceof Player){
                GuiCreate.showMainMenu(((Player) commandSender).getPlayer());
            }else{
                commandSender.sendMessage(Lang.getTranslation("Message.UseInGameOnly"));
            }
            return true;
        }
        switch (strings[0]){
            case "savenbt":
                if(!(commandSender instanceof Player)){ commandSender.sendMessage("请在游戏内使用！"); return true; }
                if(!(commandSender.isOp())){ commandSender.sendMessage("您没有权限！"); return true; }
                Config shopcfg = new Config(MainClass.plugin.getDataFolder() +"/nbtsave.yml",Config.YAML);
                List<String> stringList = new ArrayList<>(shopcfg.getStringList(commandSender.getName()));
                Item heldItem = ((Player) commandSender).getInventory().getItemInHand();
                String nbt = "null";
                if (heldItem.hasCompoundTag()) {
                    nbt = Inventory.bytesToHexString(heldItem.getCompoundTag());
                }
                stringList.add(heldItem.getId() + ":" + heldItem.getDamage() + ":" + heldItem.getCount() + ":" + nbt);
                shopcfg.set(commandSender.getName(),stringList);
                shopcfg.save();
                break;
            case "giveall":
                if(commandSender instanceof Player){ Lang.getTranslation("Message.UseInConsoleOnly");return true;}
                if(strings.length < 2){ return false;}
                switch (strings.length) {
                    case 4:
                        Item item = Item.get(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                            if (!CloudBag.addItemToCloud(player, item)) {
                                commandSender.sendMessage(Lang.getTranslation("Message.GiveItemFailed！"));
                            }
                        }
                        commandSender.sendMessage(Lang.getTranslation("Message.GiveAllOnlinePlayerItemSuccessfully").replace("%ItemName%",item.getId() + ":" + item.getDamage() + "*" + item.getCount()));
                        break;
                    case 2:
                        item = CloudBag.parseString(strings[1]);
                        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                            if (!CloudBag.addItemToCloud(player, item)) {
                                commandSender.sendMessage(Lang.getTranslation("Message.GiveItemFailed！"));
                            }
                        }
                        commandSender.sendMessage(Lang.getTranslation("Message.GiveAllOnlinePlayerItemSuccessfully").replace("%ItemName%",item.getId() + ":" + item.getDamage() + "*" + item.getCount()));
                        break;
                }
                break;
            case "give": //yun give xx id meta amount
                if(strings.length < 4){ return false;}
                if(commandSender instanceof Player){
                    if(commandSender.isOp()){
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if(!player.isOnline()){ commandSender.sendMessage(Lang.getTranslation("GiveItemFailed"));return true;}
                        Item item = new Item(Integer.parseInt(strings[2]),Integer.parseInt(strings[3]),Integer.parseInt(strings[4]));
                        if(CloudBag.addItemToCloud(player, item)){
                            commandSender.sendMessage(Lang.getTranslation("Message.GiveItemSuccessfully").replace("%ItemName%",item.getId() + ":" + item.getDamage() + "*" + item.getCount()));
                        }else{
                            commandSender.sendMessage(Lang.getTranslation("Message.GiveItemFailed！"));
                        }
                    }else{
                        commandSender.sendMessage(Lang.getTranslation("Message.NoPermission"));
                    }
                }else{
                    Player player = Server.getInstance().getPlayer(strings[1]);
                    if(!player.isOnline()){ commandSender.sendMessage(Lang.getTranslation("GiveItemFailed"));return true;}
                    Item item = Item.get(Integer.parseInt(strings[2]),Integer.parseInt(strings[3]),Integer.parseInt(strings[4]));
                    if(CloudBag.addItemToCloud(player, item)){
                        commandSender.sendMessage(Lang.getTranslation("Message.GiveItemSuccessfully").replace("%ItemName%",item.getId() + ":" + item.getDamage() + "*" + item.getCount()));
                    }else{
                        commandSender.sendMessage(Lang.getTranslation("Message.GiveItemFailed"));
                    }
                }
                break;
            case "s":
                if(commandSender instanceof Player){
                    CloudBag.uploadHeldItemToCloud((Player) commandSender);
                }else{
                    commandSender.sendMessage(Lang.getTranslation("Message.UseInGameOnly"));
                }
                break;
            case "q":
                if(commandSender instanceof Player){
                        CloudBag.getPlayerBagItem(Integer.parseInt(strings[1]), (Player) commandSender);
                }else{
                    commandSender.sendMessage(Lang.getTranslation("Message.UseInGameOnly"));
                }
                break;
        }
        return true;
    }
}
