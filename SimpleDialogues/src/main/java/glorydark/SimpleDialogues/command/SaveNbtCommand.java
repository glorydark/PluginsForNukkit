package glorydark.SimpleDialogues.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import glorydark.SimpleDialogues.MainClass;
import glorydark.SimpleDialogues.dialogue.DialogueMain;

import java.util.ArrayList;
import java.util.List;

public class SaveNbtCommand extends Command {
    public SaveNbtCommand() {
        super("savenbt");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!(commandSender instanceof Player)){ commandSender.sendMessage("请在游戏内使用！"); return true; }
        if(!(commandSender.isOp())){ commandSender.sendMessage("您没有权限！"); return true; }
        Config saveCfg = new Config(MainClass.path +"/nbtsave.yml",Config.YAML);
        List<String> stringList = new ArrayList<>(saveCfg.getStringList(commandSender.getName()));
        Item heldItem = ((Player) commandSender).getInventory().getItemInHand();
        String nbt = "null";
        if (heldItem.hasCompoundTag()) {
            nbt = DialogueMain.bytesToHexString(heldItem.getCompoundTag());
        }
        stringList.add(heldItem.getId() + ":" + heldItem.getDamage() + ":" + heldItem.getCount() + ":" + nbt);
        saveCfg.set(commandSender.getName(),stringList);
        saveCfg.save();
        return false;
    }
}
