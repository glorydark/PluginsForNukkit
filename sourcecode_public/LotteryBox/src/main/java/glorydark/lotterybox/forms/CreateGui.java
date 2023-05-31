package glorydark.lotterybox.forms;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGlassStained;
import cn.nukkit.block.BlockLever;
import cn.nukkit.entity.item.EntityMinecartChest;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.nbt.tag.ListTag;
import glorydark.lotterybox.MainClass;
import glorydark.lotterybox.tools.BasicTool;
import glorydark.lotterybox.tools.Inventory;
import glorydark.lotterybox.tools.LotteryBox;
import glorydark.lotterybox.tools.Prize;

import java.text.DecimalFormat;
import java.util.*;

import static glorydark.lotterybox.forms.GuiType.*;

public class CreateGui {

    public static final HashMap<Player, HashMap<Integer, GuiType>> UI_CACHE = new HashMap<>();

    public static void showFormWindow(Player player, FormWindow window, GuiType guiType) {
        UI_CACHE.computeIfAbsent(player, i -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }

    public static void showSelectLotteryBoxWindow(Player player){
        FormWindowSimple windowSimple = new FormWindowSimple(MainClass.lang.getTranslation("SelectLotteryBoxWindow","Title"), MainClass.lang.getTranslation("SelectLotteryBoxWindow","Content"));
        for(LotteryBox lotteryBox: MainClass.lotteryBoxList){
            windowSimple.addButton(new ElementButton(lotteryBox.getDisplayName()));
        }
        showFormWindow(player, windowSimple, SelectLotteryBox);
    }

    public static void showPESelectSpinWindow(Player player){
        FormWindowCustom custom = new FormWindowCustom(MainClass.lang.getTranslation("SelectSpinsWindow", "Title", MainClass.playerLotteryBoxes.get(player).getName()));
        custom.addElement(new ElementLabel(MainClass.lang.getTranslation("SelectSpinsWindow","Content")));
        custom.addElement(new ElementSlider(MainClass.lang.getTranslation("SelectSpinsWindow","Slider_Text"), 1, 30, 1, 1));
        showFormWindow(player, custom, SelectLotterySpin);
    }

    public static void showLotteryPossibilityWindow(Player player, LotteryBox box){
        FormWindowSimple simple = new FormWindowSimple(MainClass.lang.getTranslation("ShowPossibilityWindow","Title", box.getName()), "");
        StringBuilder builder = new StringBuilder();
        builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow", "Subtitle", BasicTool.getLotteryPlayTimes(player.getName(), box.getName()))).append("\n").append(MainClass.lang.getTranslation("ShowPossibilityWindow", "Subtitle_1", box.getName())).append("\n");
        for(String string: box.getNeeds()){
            if(string.startsWith("ticket|")) {
                String[] strings = string.replace("ticket|", "").split("@");
                int hasCount = BasicTool.getTicketCounts(player.getName(), strings[0]);
                if(hasCount >= Integer.parseInt(strings[1])){
                    builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow", "NeedTicket_Enough_Format", strings[0], strings[1], hasCount)).append("\n");
                }else{
                    builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow", "NeedTicket_NotEnough_Format", strings[0], strings[1], hasCount)).append("\n");
                }
            }
            if(string.startsWith("item|")) {
                String itemString = string.replace("item|", "");
                Item item = Inventory.getItem(itemString);
                if(item != null) {
                    int count = 0;
                    for(Item i: player.getInventory().getContents().values()){
                        if(i.equals(item, true)){
                            count+=i.getCount();
                        }
                    }
                    if(count >= item.getCount()){
                        builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow", "NeedItem_Enough_Format", item.getCustomName(), item.getCount(), count)).append("\n");
                    }else{
                        builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow", "NeedItem_NotEnough_Format", item.getCustomName(), item.getCount(), count)).append("\n");
                    }
                }else{
                    builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow", "NeedItem_Enough_Format", "Unknown Item", "1", "0")).append("\n");
                }
            }
        }
        builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow","Subtitle_2", box.getName())).append("\n");
        DecimalFormat format = new DecimalFormat("0.00%");
        if(box.isWeightEnabled()){
            int maxWeight = 0;
            for (Prize prize : box.getPrizes()) {
                maxWeight += prize.getPossibility();
            }
            for(Prize prize: box.getPrizes()){
                builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow","Prize_Format_V2", prize.getRarity(), prize.getName(), prize.getPossibility(), format.format((float) prize.getPossibility() / maxWeight) + "%", prize.getDescription())).append("\n");
            }
        }else{
            for(Prize prize: box.getPrizes()){
                builder.append(MainClass.lang.getTranslation("ShowPossibilityWindow","Prize_Format", prize.getRarity(), prize.getName(), format.format((float) prize.getPossibility() / 10000) + "%", prize.getDescription())).append("\n");
            }
        }
        simple.setContent(builder.toString());
        simple.addButton(new ElementButton("立即抽奖"));
        showFormWindow(player, simple, GuiType.LotteryPossibility);
    }

    public static void showRewardWindow(Player player, String content){
        if(MainClass.show_reward_window) {
            FormWindowCustom custom = new FormWindowCustom(MainClass.lang.getTranslation("RewardWindow", "Title"));
            custom.addElement(new ElementLabel(content));
            showFormWindow(player, custom, Reward);
        }
    }

    public static void showLotteryBoxWindowV2(Player player, LotteryBox box) {
        EntityMinecartChest chest = new EntityMinecartChest(player.getChunk(), EntityMinecartChest.getDefaultNBT(player.getPosition()));
        chest.setNameTag(box.getDisplayName());
        chest.namedTag.putList(new ListTag("Items"));
        chest.namedTag.putByte("Slot", 27);
        chest.namedTag.putBoolean("IsLotteryBox", true);
        Map<Integer, Item> contents = new LinkedHashMap<>();
        //modifying
        int index = 0;
        for(Prize prize: box.getPrizes()){
            Item place = prize.getDisplayitem();
            if(!prize.getDescription().equals("")) {
                place.setLore(prize.getDescription());
            }
            if(!prize.getShowOriginName()) {
                place.setCustomName(prize.getName());
            }
            place.setNamedTag(place.getNamedTag().remove("ench"));
            contents.put(index, place);
            index++;
            if(index == 4){
                index++;
            }
            if(index==12){
                index+=3;
            }
            if(index==22){
                index++;
            }
        }
        Item item = new Item(-161,0,1);
        item.setCustomName(MainClass.lang.getTranslation("PlayLotteryWindow","BlockedArea"));
        Item lever = new BlockLever().toItem();
        lever.setCustomName(MainClass.lang.getTranslation("PlayLotteryWindow","StartLotteryWithOneSpinsItemName"));
        Item lever1 = new BlockLever().toItem();
        lever1.setCustomName(MainClass.lang.getTranslation("PlayLotteryWindow","StartLotteryWithTenSpinsItemName"));
        Item book = new ItemBookEnchanted();
        book.setCustomName(MainClass.lang.getTranslation("PlayLotteryWindow","ShowDescriptionItemName"));
        StringBuilder out = new StringBuilder();
        List<String> stringList = box.getDescription();
        for(String s: stringList){
            if(stringList.indexOf(s) == stringList.size()-1){
                out.append(s);
            }else{
                out.append(s).append("\n");
            }
        }
        book.setLore(out.toString());
        contents.put(4, book);
        contents.put(12, lever);
        contents.put(13, item);
        contents.put(14, lever1);
        contents.put(22, book);
        for(int i=box.getPrizes().size(); i<22; i++){
            Item add = new BlockGlassStained().toItem();
            Integer[] arr = new Integer[]{0,1,2,3,5,6,7,8,9,10,11,15,16,17,18,19,20,21,23,24,25,26};
            List<Integer> allowIndex = Arrays.asList(arr);
            add.setCustomName(MainClass.lang.getTranslation("PlayLotteryWindow","BlockAir"));
            contents.put(allowIndex.get(i), add);
        }
        chest.getInventory().setContents(contents);
        chest.setNameTagVisible(false);
        chest.setNameTagAlwaysVisible(false);
        chest.setImmobile(true);
        chest.spawnTo(player);
        player.addWindow(chest.getInventory());
        MainClass.chestList.put(player, chest);
    }
}
