package glorydark.fasteneditem;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;

public class FastenItemCommand extends Command {
    public FastenItemCommand(String command) {
        super(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        //fastenitem player index type("&"隔开)
        if(strings.length != 3){ commandSender.sendMessage("Wrong command format!");return false;}
        if (!(commandSender instanceof Player) || commandSender.isOp()) {
            String[] strs = strings[2].split("&");
            Player p = Server.getInstance().getPlayer(strings[0]);
            if(p == null){ commandSender.sendMessage("Player not found!");return true;}
            int index = Integer.parseInt(strings[1]);
            Item item = p.getInventory().getItem(index);
            CompoundTag tag;
            if(item.hasCompoundTag()){
                tag = item.getNamedTag();
            }else{
                tag = new CompoundTag();
            }
            for(String str: strs){
                int integer = Integer.parseInt(str);
                switch (integer){
                    case 1:
                        tag.putByte("minecraft:item_lock", 1);
                        break;
                    case 2:
                        tag.putByte("minecraft:item_lock", 2);
                        break;
                    case 3:
                        tag.putByte("minecraft:keep_on_death", 1);
                        break;
                }
            }
            item.setCompoundTag(tag);
            p.getInventory().setItem(index, item); //end
            ListTag listTag = new ListTag("CanDestroy");
            StringTag stringtag = new StringTag("CanDestroy");
            stringtag.data = "minecraft:grass";
            listTag.add(stringtag);
            tag.putList(listTag);
            item.setCompoundTag(tag);
            p.getInventory().addItem(item);
        }
        return true;
    }
}
