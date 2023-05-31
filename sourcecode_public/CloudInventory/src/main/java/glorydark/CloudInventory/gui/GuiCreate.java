package glorydark.CloudInventory.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import glorydark.CloudInventory.CloudBag;
import glorydark.CloudInventory.Lang;

import java.util.HashMap;

public class GuiCreate {
    public static final HashMap<Player, HashMap<Integer, GuiType>> UI_CACHE = new HashMap<>();

    public static void showMainMenu(Player player){
        FormWindowSimple form = new FormWindowSimple(Lang.getTranslation("MainGui.Title"),Lang.getTranslation("MainGui.Description").replace("%d1", CloudBag.getPlayerBag(player).size()+"").replace("%d2", CloudBag.getMaxCloudSlot(player)+""));
        form.addButton(new ElementButton(Lang.getTranslation("MainGui.Button1")));
        form.addButton(new ElementButton(Lang.getTranslation("MainGui.Button2")));
        form.addButton(new ElementButton(Lang.getTranslation("MainGui.Button3")));
        GuiCreate.showFormWindow(player,form,GuiType.Main);
    }

    public static void showPlayerBagGui(Player player){
        FormWindowCustom form = new FormWindowCustom(Lang.getTranslation("MinorGui.ShowBagTitle"));
        for(String itemString: CloudBag.getPlayerBag(player)){
            Item item = CloudBag.parseString(itemString);
            form.addElement(new ElementLabel(item.getName() + "*" + item.getCount()));
        }
        GuiCreate.showFormWindow(player,form,GuiType.SeeInv);
    }

    public static void showGetBagItemGui(Player player){
        FormWindowCustom form = new FormWindowCustom(Lang.getTranslation("MinorGui.PickItemTitle"));
        ElementDropdown elementDropdown = new ElementDropdown(Lang.getTranslation("MinorGui.PickItemDropDownName"));
        for(String itemString:CloudBag.getPlayerBag(player)){
            Item item = CloudBag.parseString(itemString);
            elementDropdown.addOption(item.getName() + "*" + item.getCount());
        }
        if(elementDropdown.getOptions().size() > 0) {
            form.addElement(elementDropdown);
        }else{
            form.addElement(new ElementLabel(Lang.getTranslation("Message.CloudInventoryIsEmpty")));
        }
        GuiCreate.showFormWindow(player, form, GuiType.PickItem);
    }

    public static void showPutBagItemGui(Player player){
        FormWindowCustom form = new FormWindowCustom(Lang.getTranslation("MinorGui.PutItemTitle"));
        ElementDropdown elementDropdown = new ElementDropdown(Lang.getTranslation("MinorGui.PutItemDropDownName"));
        for(Item item:player.getInventory().getContents().values()){
            elementDropdown.addOption(item.getName() + "*" + item.getCount());
        }
        if(elementDropdown.getOptions().size() > 0) {
            form.addElement(elementDropdown);
        }else{
            form.addElement(new ElementLabel(Lang.getTranslation("Message.InventoryIsEmpty")));
        }
        GuiCreate.showFormWindow(player, form, GuiType.PutItem);
    }

    public static void showResultGui(Player player, String tips, GuiType guiType){
        FormWindowModal form = new FormWindowModal(Lang.getTranslation("MinorGui.ResultTitle"), tips, Lang.getTranslation("MinorGui.ModalGuiButton1"),Lang.getTranslation("MinorGui.ModalGuiButton2"));
        GuiCreate.showFormWindow(player, form, guiType);
    }

    public static void showFormWindow(Player player, FormWindow window, GuiType guiType) {
        UI_CACHE.computeIfAbsent(player, i -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }
}
