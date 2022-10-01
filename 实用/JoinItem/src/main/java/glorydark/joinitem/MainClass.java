package glorydark.joinitem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainClass extends PluginBase implements Listener {

    public List<JoinItem> items = new ArrayList<>();
    public HashMap<String, Long> coolDown = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Config config = new Config(this.getDataFolder().getPath()+"/config.yml", Config.YAML);
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) config.get("items");
        for(Map<String, Object> itemMap: itemsMap){
            items.add(new JoinItem(new Item((Integer) itemMap.get("id"), (Integer) itemMap.get("meta"), (Integer) itemMap.get("count")), (String) itemMap.get("name"), (String) itemMap.get("lore"), (List<String>) itemMap.get("messages"), (List<String>) itemMap.get("commands")));
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getCommandMap().register("", new Commands(config.getString("command", "joinitem")));
        this.getLogger().info("JoinItem onLoad");
    }

    public class Commands extends Command {

        public Commands(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if(commandSender.isPlayer()){
                Player player = (Player) commandSender;
                player.getInventory().getContents().forEach((integer, item1) -> {
                    if(item1.hasCompoundTag() && item1.getNamedTag().contains("JoinItem")){
                        player.getInventory().remove(item1);
                    }
                });
                items.forEach(joinItem -> joinItem.giveItem(player));
                commandSender.sendMessage("给予成功！");
            }
            return true;
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.getInventory().getContents().forEach((integer, item1) -> {
            if(item1.hasCompoundTag() && item1.getNamedTag().contains("JoinItem")){
                player.getInventory().remove(item1);
            }
        });
        items.forEach(joinItem -> joinItem.giveItem(player));
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Item check = event.getItem();
        AtomicReference<JoinItem> item = new AtomicReference<>();
        items.stream().filter(joinItem -> joinItem.checkItem(check)).sorted().findFirst().ifPresent(item::set);
        if(item.get() != null){
            item.get().execute(event.getPlayer());
        }
    }

    public class JoinItem{
        private final Item item;
        private final String name;
        private final String lore;
        private final List<String> messages;
        private final List<String> commands;

        public JoinItem(Item item, String name, String lore, List<String> messages, List<String> commands){
            this.item = item;
            this.name = name;
            this.lore = lore.replace("\\n", "\n");
            this.messages = messages;
            this.commands = commands;
        }

        public void execute(Player player){
            if(System.currentTimeMillis() - coolDown.getOrDefault(player.getName(), 0L) >= 2000L) {
                messages.forEach(player::sendMessage);
                commands.forEach(command -> Server.getInstance().dispatchCommand(player, command.replace("%player%", player.getName())));
                coolDown.put(player.getName(), System.currentTimeMillis());
            }else{
                player.sendMessage("§c您操作太快了，请稍后再试！");
            }
        }

        public void giveItem(Player player){
            Item give = item.clone();
            give.setCustomName(name);
            give.setLore(lore);
            CompoundTag tag = give.getNamedTag();
            tag.putString("JoinItem", name).putBoolean("Unbreakable", true);
            give.setCompoundTag(tag);
            player.getInventory().addItem(give);
        }

        public boolean checkItem(Item item){
            if(item != null && item.hasCompoundTag()) {
                if (item.getNamedTag().contains("JoinItem")) {
                    return item.getNamedTag().getString("JoinItem").equals(name);
                }
            }
            return false;
        }
    }
}
