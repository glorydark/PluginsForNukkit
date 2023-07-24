package glorydark.floatingtext;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainClass extends PluginBase {

    public static String path;

    public void onLoad() {
        this.getLogger().info("FloatingText onLoad");
    }

    public void onEnable() {
        path = getDataFolder().getPath();
        this.saveDefaultConfig();
        this.getServer().getCommandMap().register("", new CreateTextCommand("ctc"));
        this.getServer().getScheduler().scheduleRepeatingTask(this, () -> loadAll(true), 100);
        this.loadAll(false);
        this.getLogger().info("FloatingText onEnable");
    }

    public void loadAll(boolean isKill) {
        if (isKill) {
            for (Level level : Server.getInstance().getLevels().values()) {
                for (Entity e : level.getEntities()) {
                    if (e instanceof TextEntity) {
                        e.kill();
                        e.close();
                    }
                }
            }
        }
        Config config = new Config(path + "/config.yml", 2);
        for (String pos : config.getKeys(false)) {
            StringBuilder text = new StringBuilder();
            List<String> stringList = new ArrayList<>(config.getStringList(pos));
            int index = 0;
            for (String s : stringList) {
                index++;
                if (index < stringList.size()) {
                    text.append(s).append("\n");
                } else {
                    text.append(s);
                }
            }
            this.spawnTextEntity(pos, text.toString());
        }
    }

    public void spawnTextEntity(String pos, String text) {
        String[] splits = pos.split(":");
        Position position = new Position(Double.parseDouble(splits[0]), Double.parseDouble(splits[1]), Double.parseDouble(splits[2]), Server.getInstance().getLevelByName(splits[3]));
        CompoundTag nbt = Entity.getDefaultNBT(new Vector3(position.x, position.y, position.z));
        TextEntity entity = new TextEntity(position.getChunk(), nbt, text);
        entity.setLevel(position.getLevel());
        entity.spawnToAll();
    }

    public void onDisable() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity e : level.getEntities()) {
                if (e instanceof TextEntity) {
                    e.kill();
                    e.close();
                }
            }
        }
        this.getLogger().info("FloatingText onDisable");
    }

    private class CreateTextCommand extends Command {
        public CreateTextCommand(String command) {
            super(command);
        }

        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            if (!commandSender.isPlayer() || commandSender.isOp()) {
                switch (strings[0]) {
                    case "reload":
                        MainClass.this.loadAll(true);
                        commandSender.sendMessage("重载成功！");
                        break;
                    case "add":
                        if (strings.length == 2 && commandSender.isPlayer()) {
                            Config config = new Config(MainClass.path + "/config.yml", 2);
                            Player player = (Player) commandSender;
                            String pos = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ() + ":" + player.getLevel().getName();
                            if (config.exists(pos)) {
                                commandSender.sendMessage("此位置已有浮空字");
                                return true;
                            }
                            List<String> stringList = Arrays.asList(strings[1].split("%n%"));
                            StringBuilder text = new StringBuilder();
                            int index = 0;
                            for (String string : stringList) {
                                index++;
                                if (index < stringList.size()) {
                                    text.append(string).append("\n");
                                } else {
                                    text.append(string);
                                }
                            }
                            MainClass.this.spawnTextEntity(pos, text.toString());
                            config.set(pos, stringList);
                            config.save();
                            commandSender.sendMessage("创建成功！");
                            return true;
                        }
                        break;
                }
            }
            return false;
        }
    }
}