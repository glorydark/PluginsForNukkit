package glorydark.joinitem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameUseEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.joinitem.utils.Inventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class JoinItemMain extends PluginBase implements Listener {

    public List<JoinItem> items = new ArrayList<>();
    public HashMap<String, Long> coolDown = new HashMap<>();

    public boolean keepOnDeath;

    public String path;

    public String commandName;

    public int triggerType;

    public int cooldownTick;

    public boolean sendMessageTip;

    @Override
    public void onEnable() {
        path = this.getDataFolder().getPath();
        this.saveDefaultConfig();
        Config config = new Config(path + "/config.yml", Config.YAML);
        keepOnDeath = config.getBoolean("keep_on_death", true);
        triggerType = config.getInt("trigger_type", 0);
        cooldownTick = config.getInt("cooldown_tick", 10);
        sendMessageTip = config.getBoolean("send_message_tip", false);
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) config.get("items");
        Config nbtCacheConfig = new Config(path + "/nbt.yml", Config.YAML);
        for (Map<String, Object> itemMap : itemsMap) {
            Item getItem = Item.fromString((String) itemMap.get("id"));
            getItem.setDamage((Integer) itemMap.get("meta"));
            getItem.setCount((Integer) itemMap.get("count"));
            String nbtKey = (String) itemMap.getOrDefault("nbt", "null");
            String nbt = nbtCacheConfig.getString(nbtKey, "null");
            items.add(new JoinItem(getItem, (String) itemMap.get("name"), (String) itemMap.get("lore"), nbt, (List<String>) itemMap.get("messages"), (List<String>) itemMap.get("commands")));
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        commandName = config.getString("command", "joinitem");
        this.getServer().getCommandMap().register("", new Commands(commandName));
        this.getLogger().info("JoinItem onLoad");
    }

    public class Commands extends Command {

        public Commands(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if (strings.length == 0) {
                commandSender.sendMessage("§e输入/" + commandName + " help 查看帮助");
                return false;
            }
            switch (strings[0]) {
                case "regain":
                    if (commandSender.isPlayer()) {
                        Player player = (Player) commandSender;
                        player.getInventory().getContents().forEach((integer, item1) -> {
                            if (item1.hasCompoundTag() && item1.getNamedTag().contains("JoinItem")) {
                                player.getInventory().remove(item1);
                            }
                        });
                        items.forEach(joinItem -> joinItem.giveItem(player));
                        commandSender.sendMessage("§a给予成功！");
                    }
                    break;
                case "savenbt":
                    if (strings.length != 2) {
                        commandSender.sendMessage("§c使用方法：/" + commandName + " savenbt 名称  §e将手持物品nbt保存至nbt.yml");
                        return false;
                    }
                    if (commandSender.isPlayer() && commandSender.isOp()) {
                        Player player = commandSender.asPlayer();
                        Item item = player.getInventory().getItemInHand();
                        String string = Inventory.bytesToHexString(item.getCompoundTag());
                        if (string.equals("null")) {
                            commandSender.sendMessage("§c该物品没有nbt！");
                            return true;
                        }
                        Config config = new Config(path + "/nbt.yml", Config.YAML);
                        config.set(strings[1], string);
                        config.save();
                        commandSender.sendMessage("§a成功保存该物品nbt至nbt.yml, 储存键名:" + strings[1] + "！");
                    }
                    break;
                case "reload":
                    if (commandSender.isOp() || !commandSender.isPlayer()) {
                        Config config = new Config(path + "/config.yml", Config.YAML);
                        keepOnDeath = config.getBoolean("keep_on_death", true);
                        triggerType = config.getInt("trigger_type", 0);
                        cooldownTick = config.getInt("cooldown_tick", 10);
                        sendMessageTip = config.getBoolean("send_message_tip", false);
                        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) config.get("items");
                        Config nbtCacheConfig = new Config(path + "/nbt.yml", Config.YAML);
                        for (Map<String, Object> itemMap : itemsMap) {
                            Item getItem = Item.fromString((String) itemMap.get("id"));
                            getItem.setDamage((Integer) itemMap.get("meta"));
                            getItem.setCount((Integer) itemMap.get("count"));
                            String nbtKey = (String) itemMap.getOrDefault("nbt", "null");
                            String nbt = nbtCacheConfig.getString(nbtKey, "null");
                            items.add(new JoinItem(getItem, (String) itemMap.get("name"), (String) itemMap.get("lore"), nbt, (List<String>) itemMap.get("messages"), (List<String>) itemMap.get("commands")));
                        }
                        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                            player.getInventory().getContents().forEach((integer, item1) -> {
                                if (item1.hasCompoundTag() && item1.getNamedTag().contains("JoinItem")) {
                                    player.getInventory().remove(item1);
                                }
                            });
                            items.forEach(joinItem -> joinItem.giveItem(player));
                            commandSender.sendMessage("§a由于系统重载，已经更新您的物品！");
                        }
                        commandSender.sendMessage("§a重载完成！");
                    }
                    break;
                case "help":
                    commandSender.sendMessage("§a/" + commandName + " regain  §e重新获取手上物品（无需重新进服）");
                    if (commandSender.isOp() || !commandSender.isPlayer()) {
                        commandSender.sendMessage("§a/" + commandName + " savenbt 名称  §e将手持物品nbt保存至nbt.yml");
                        commandSender.sendMessage("§a/" + commandName + " reload  §e重载数据（修改指令请关服重启）");
                    }
                    break;
            }
            return true;
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerLocallyInitializedEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory() != null) {
            player.getInventory().getContents().forEach((integer, item1) -> {
                if (item1.hasCompoundTag() && item1.getNamedTag().contains("JoinItem")) {
                    player.getInventory().remove(item1);
                }
            });
            items.forEach(joinItem -> joinItem.giveItem(player));
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        boolean b = event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK;
        boolean c = event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR;
        if ((triggerType == 0) || (triggerType == 1 && b) || (triggerType == 2 && c)) {
            Item check = event.getItem();
            AtomicReference<JoinItem> item = new AtomicReference<>();
            items.stream().filter(joinItem -> joinItem.checkItem(check)).sorted().findFirst().ifPresent(item::set);
            if (item.get() != null) {
                item.get().execute(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        Item check = event.getItem();
        AtomicReference<JoinItem> item = new AtomicReference<>();
        items.stream().filter(joinItem -> joinItem.checkItem(check)).sorted().findFirst().ifPresent(item::set);
        if (item.get() != null) {
            item.get().execute(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ItemFrameUseEvent(ItemFrameUseEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Item check = event.getItem();
        AtomicReference<JoinItem> item = new AtomicReference<>();
        items.stream().filter(joinItem -> joinItem.checkItem(check)).sorted().findFirst().ifPresent(item::set);
        if (item.get() != null) {
            item.get().execute(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Item check = event.getItem();
        AtomicReference<JoinItem> item = new AtomicReference<>();
        items.stream().filter(joinItem -> joinItem.checkItem(check)).sorted().findFirst().ifPresent(item::set);
        if (item.get() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        if (!keepOnDeath) {
            return;
        }
        List<Item> drops = new ArrayList<>(Arrays.asList(event.getDrops()));
        drops.removeIf(item -> item.hasCompoundTag() && item.getNamedTag().contains("JoinItem"));
        event.setDrops(drops.toArray(new Item[0]));
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player != null && player.getInventory() != null) {
            for (Item item : player.getInventory().getContents().values()) {
                if (item.hasCompoundTag() && item.getNamedTag().contains("JoinItem")) {
                    player.getInventory().remove(item);
                }
            }
        }
    }

    public class JoinItem {
        private final Item item;
        private final String name;
        private final String lore;
        private final List<String> messages;
        private final List<String> commands;

        public JoinItem(Item item, String name, String lore, String nbtString, List<String> messages, List<String> commands) {
            this.item = item;
            this.name = name;
            if (!nbtString.equals("null")) {
                this.item.setNamedTag(Item.parseCompoundTag(Inventory.hexStringToBytes(nbtString)));
            }
            this.lore = lore.replace("\\n", "\n");
            this.messages = messages;
            this.commands = commands;
        }

        public void execute(Player player) {
            if (System.currentTimeMillis() - coolDown.getOrDefault(player.getName(), 0L) >= cooldownTick * 50L) {
                for (String message : messages) {
                    player.sendMessage(message.replace("%player%", player.getName()));
                }
                for (String command : commands) {
                    Server.getInstance().executeCommand(player, command.replace("%player%", player.getName()));
                }
                coolDown.put(player.getName(), System.currentTimeMillis());
            } else {
                if (sendMessageTip) {
                    player.sendMessage("§c您操作太快了，请稍后再试！");
                }
            }
        }

        public void giveItem(Player player) {
            Item give = item.clone();
            give.setCustomName(name);
            give.setLore(lore);
            CompoundTag tag = give.getNamedTag();
            tag.putString("JoinItem", name).putBoolean("Unbreakable", true);
            give.setCompoundTag(tag);
            player.getInventory().addItem(give);
        }

        public boolean checkItem(Item item) {
            if (item != null && item.hasCompoundTag()) {
                if (item.getNamedTag().contains("JoinItem")) {
                    return item.getNamedTag().getString("JoinItem").equals(name);
                }
            }
            return false;
        }
    }
}
