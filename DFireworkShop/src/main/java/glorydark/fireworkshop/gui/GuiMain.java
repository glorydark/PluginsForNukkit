package glorydark.fireworkshop.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import glorydark.fireworkshop.utils.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiMain {
    public static final HashMap<Player, HashMap<Integer, guitype>> UI_CACHE = new HashMap<>();

    public static void createMainMenu(Player p){
        FormWindowSimple form = new FormWindowSimple("烟花商店","请选择您要购买的烟花");
        Config ShopCfg = config.getConfig("shops.yml");
        String[] StringList = ShopCfg.getAll().keySet().toArray(new String[0]);
        for(int i = 1; i<=StringList.length; i++){
            form.addButton(new ElementButton(StringList[i-1]));
        }
        showFormWindow(p,form,guitype.MainMenu);
    }

    public static void createInformationMenu(String GoodsName,Player p){
        Config GoodsCfg = config.getConfig("shops.yml");
        if(GoodsCfg.getInt(GoodsName+".类型") == 0) {
            List<String> position = GoodsCfg.get(GoodsName + ".位置", new ArrayList<>());
            List<String> CostMoney = GoodsCfg.getStringList(GoodsName + ".货币花费");
            String description = GoodsCfg.getString(GoodsName + ".description");
            String WorldName = GoodsCfg.getString(GoodsName + ".所在世界");
            List<String> FireworkType = GoodsCfg.get(GoodsName + ".烟花类型", new ArrayList<>());
            String duration = GoodsCfg.getString(GoodsName + ".延续时间");
            String time = GoodsCfg.getString(GoodsName + ".连发次数");
            FormWindowSimple form = new FormWindowSimple(GoodsName, "");
            form.setContent("发射位置:" + position.toString() + ":" + WorldName + "\n" + "货币花费(单个):" + CostMoney.toString() + "\n" + "介绍:" + description + "\n" + "烟花类型:" + FireworkType.toString() + "\n" + "持续时间:" + duration + "\n" + "连发次数:" + time);
            form.addButton(new ElementButton("购买"));
            form.addButton(new ElementButton("返回"));
            showFormWindow(p, form, guitype.InformationMenu);
        }
        if(GoodsCfg.getInt(GoodsName+".类型") == 1){
            FormWindowSimple form = new FormWindowSimple(GoodsName, "");
            String description = GoodsCfg.getString(GoodsName + ".description");
            List<String> CostMoney = GoodsCfg.getStringList(GoodsName + ".货币花费");
            form.setContent("介绍:" + description + "\n"  + "货币花费(单个):" + CostMoney.toString());
            form.addButton(new ElementButton("购买"));
            form.addButton(new ElementButton("返回"));
            showFormWindow(p, form, guitype.InformationMenu);
        }
    }

    public static void showFormWindow(Player player, FormWindow window, guitype guiType) {
        UI_CACHE.computeIfAbsent(player, i -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }
}