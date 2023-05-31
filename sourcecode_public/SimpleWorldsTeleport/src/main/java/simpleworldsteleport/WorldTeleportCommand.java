package simpleworldsteleport;

//import AwakenSystem.AwakenSystem;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

public class WorldTeleportCommand extends Command {

    public WorldTeleportCommand() {
        super("世界传送");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage(TextFormat.RED + "请在游戏内使用！");
            return true;
        }
        Player player = ((Player) commandSender).getPlayer();
        if(player == null){
            return true;
        }
        if(strings.length > 0){
            String worldName = strings[0];
            if(Server.getInstance().isLevelLoaded(worldName)){
                Level level = Server.getInstance().getLevelByName(worldName);
                if(level != null){
                    if(player.isOp()){
                        player.teleportImmediate(level.getSpawnLocation().getLocation());
                        player.sendMessage(TextFormat.GREEN+"已将您传送到："+worldName);
                        return true;
                    }
                    //Map<String, Object> levelLimit = (Map<String, Object>) MainClass.config.get("level-limit");
                    Map<String, Object> playersLimit = (Map<String, Object>) MainClass.config.get("players-limit");
                    if(/*levelLimit == null || */ playersLimit == null){
                        player.sendMessage(TextFormat.RED+"config.yml数据读取出现问题！");
                        return true;
                    }
                    /*
                    if(levelLimit.containsKey(level.getName())){
                        Integer playerLevel = AwakenSystem.getMain().getPlayerConfig(player).get("等级", 0);
                        String levelNeed = (String) levelLimit.get(level.getName());
                        if(levelNeed.split("-").length > 0){
                            String[] split = levelNeed.split("-");
                            Integer min = Integer.parseInt(split[0]);
                            Integer max = Integer.parseInt(split[1]);
                            if (playerLevel < min) {
                                player.sendMessage(TextFormat.RED + "需要等级:" + min + ", 您的等级：" + playerLevel);
                                return true;
                            }
                            if (playerLevel > max) {
                                player.sendMessage(TextFormat.RED + "需要等级:" + min + "-" + max + ", 您的等级：" + playerLevel);
                                return true;
                            }
                        }else {
                            if (playerLevel < Integer.parseInt(levelNeed)) {
                                player.sendMessage(TextFormat.RED + "需要等级:" + levelNeed + ", 您的等级：" + playerLevel);
                                return true;
                            }
                        }
                    }

                     */
                    if(playersLimit.containsKey(level.getName())){
                        Integer maxPlayers = (Integer) playersLimit.get(level.getName());
                        int players = level.getPlayers().size();
                        if(players >= maxPlayers){
                            player.sendMessage(TextFormat.RED+"人数已满【"+players+"/"+maxPlayers+"】");
                            return true;
                        }
                    }
                    player.teleportImmediate(level.getSpawnLocation().getLocation());
                    player.sendMessage(TextFormat.GREEN+"已将您传送到："+worldName);
                }else{
                    commandSender.sendMessage(TextFormat.RED + "Can not find the world:" + worldName);
                    return true;
                }
            }
        }else{
            commandSender.sendMessage(TextFormat.RED + "/世界传送 世界名");
        }
        return true;
    }
}
