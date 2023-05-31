package glorydark.itemusecmd;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemUseCmdMain extends PluginBase implements Listener {

    public HashMap<String, UseItem> useItemHashMap = new HashMap<>();

    @Override
    public void onEnable() {
        File folder = new File(this.getDataFolder().getPath());
        folder.mkdirs();
        for(File file: Objects.requireNonNull(folder.listFiles())){
            Config config = new Config(file, Config.YAML);
            String identifier = file.getName().replace(".yml", "");
            useItemHashMap.put(identifier, new UseItem(identifier, config.getRootSection()));
            this.getLogger().info("成功加载物品: "+ identifier);
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getCommandMap().register("", new Commands("itemusecmd"));
        this.getLogger().info("ItemUseCmd onEnable");
    }

    public class Commands extends Command {

        public Commands(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if(commandSender.isPlayer() && !commandSender.isOp()){
                 return false;
            }
            if(strings.length == 0){ return false; }
            switch (strings[0]){
                case "give":
                    if(strings.length == 4){
                        UseItem useItem = useItemHashMap.getOrDefault(strings[2], null);
                        if(useItem != null){
                            Item item = useItem.getItem();
                            int amount = Integer.parseInt(strings[3]);
                            item.setCount(amount);
                        }
                    }
                    break;
            }
            return true;
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Item item = event.getPlayer().getInventory().getItemInHand();
        if(item != null && item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains("ItemUseCmd")) {
                if (tag.contains("lastMillis")) {
                    long millis = tag.getLong("lastMillis");
                    if (System.currentTimeMillis() - millis <= 500) {
                        event.getPlayer().sendMessage("您点的太快了！");
                        return;
                    }
                }
                UseItem useItem = useItemHashMap.getOrDefault(tag.getString("ItemUseCmd"), null);
                if(useItem != null){
                    useItem.execute(event.getPlayer());
                }
            }
        }
    }

    @Data
    public static class UseItem{
        private String identifier;
        private List<String> commands;
        private List<String> messages;

        private Item item;

        public UseItem(String identifier, ConfigSection config){
            this.identifier = identifier;
            this.commands = new ArrayList<>(config.getStringList("commands"));
            this.messages = new ArrayList<>(config.getStringList("messages"));
            this.item = Item.get(config.getInt("id", 0), config.getInt("meta", 0));
            item.setLore(config.getString("lore"));
            item.setCustomName(config.getString("customName"));
        }

        public void execute(Player player){
            commands.forEach(command -> {
                if(command.startsWith("console#")){
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command.replaceFirst("console#", ""));
                }else{
                    Server.getInstance().dispatchCommand(player, command);
                }
            });
            messages.forEach(player::sendMessage);
        }

        public Item getItem(){
            Item back = item.clone();
            CompoundTag tag;
            if(back.hasCompoundTag()){
                tag = back.getNamedTag();
            }else{
                tag = new CompoundTag();
            }
            tag.putString("ItemUseCmd", identifier);
            back.setCompoundTag(tag);
            return back;
        }
    }
}
