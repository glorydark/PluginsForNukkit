package glorydark.DLevelEventPlus.gui;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import glorydark.DLevelEventPlus.MainClass;
import glorydark.DLevelEventPlus.event.PlayerEventListener;
import glorydark.DLevelEventPlus.utils.ConfigUtil;

public class GuiMain {
    //设置选择世界
    public static void showSettingChooseWorldMenu(Player player){
        FormWindowSimple window = new FormWindowSimple(ConfigUtil.getLang("SettingChooseWorldMenu","SelectWorld"), ConfigUtil.getLang("SettingChooseWorldMenu","SelectWorldContent"));
        for(Level level: Server.getInstance().getLevels().values()){
            window.addButton(new ElementButton(level.getName()));
        }
        window.addButton(new ElementButton(ConfigUtil.getLang("General","ReturnButton")));
        PlayerEventListener.showFormWindow(player,window, GuiType.Edit_ChooseWorld);
    }

    //权限设置主页面
    public static void showPowerMainMenu(Player player){
        if(ConfigUtil.isAdmin(player)){
            FormWindowSimple window = new FormWindowSimple(ConfigUtil.getLang("PowerMainMenu","Title"), ConfigUtil.getLang("PowerMainMenu","Content"));
            window.addButton(new ElementButton(ConfigUtil.getLang("PowerMainMenu","AddPower")));
            window.addButton(new ElementButton(ConfigUtil.getLang("PowerMainMenu","RemovePower")));
            window.addButton(new ElementButton(ConfigUtil.getLang("General","ReturnButton")));
            PlayerEventListener.showFormWindow(player,window,GuiType.Power_Main);
        }
    }

    //主页面
    public static void showMainMenu(Player player){
        if(ConfigUtil.isAdmin(player)){
            FormWindowSimple window = new FormWindowSimple(ConfigUtil.getLang("MainMenu","Title"), ConfigUtil.getLang("MainMenu","Content"));
            window.addButton(new ElementButton(ConfigUtil.getLang("MainMenu","ManageWorld")));
            window.addButton(new ElementButton(ConfigUtil.getLang("MainMenu","ManagePower")));
            window.addButton(new ElementButton(ConfigUtil.getLang("MainMenu","SaveInit")));
            window.addButton(new ElementButton(ConfigUtil.getLang("MainMenu","ReloadInit")));
            window.addButton(new ElementButton(ConfigUtil.getLang("MainMenu","ManageTemplate")));
            PlayerEventListener.showFormWindow(player,window,GuiType.ADMIN_Main);
        }
        if(ConfigUtil.isOperator(player,player.getLevel())){
            showEditMenu(player,player.getLevel().getName());
        }
    }

    //权限添加
    public static void showPowerAddMenu(Player player){
        FormWindowCustom window = new FormWindowCustom(ConfigUtil.getLang("PowerMenu","AddPowerTitle"));
        ElementDropdown powerDropDown = new ElementDropdown(ConfigUtil.getLang("PowerMenu","AddPowerDropdownTitle"));
        powerDropDown.addOption(ConfigUtil.getLang("PowerMenu","ManagerOption"));
        powerDropDown.addOption(ConfigUtil.getLang("PowerMenu","OperatorOption"));
        powerDropDown.addOption(ConfigUtil.getLang("PowerMenu","WhitelistOption"));
        ElementInput playerNameInput = new ElementInput(ConfigUtil.getLang("PowerMenu","PlayerName"));
        ElementInput worldNameInput = new ElementInput(ConfigUtil.getLang("PowerMenu","WorldName"));
        window.addElement(powerDropDown);
        window.addElement(playerNameInput);
        window.addElement(worldNameInput);
        PlayerEventListener.showFormWindow(player,window,GuiType.Power_Add);
    }

    //取消权限
    public static void showPowerDeleteMenu(Player player){
        FormWindowCustom window = new FormWindowCustom(ConfigUtil.getLang("PowerMenu","DelPowerTitle"));
        ElementDropdown powerDropDown = new ElementDropdown(ConfigUtil.getLang("PowerMenu","DelPowerDropdownTitle"));
        powerDropDown.addOption(ConfigUtil.getLang("PowerMenu","ManagerOption"));
        powerDropDown.addOption(ConfigUtil.getLang("PowerMenu","OperatorOption"));
        powerDropDown.addOption(ConfigUtil.getLang("PowerMenu","WhitelistOption"));
        ElementInput playerNameInput = new ElementInput(ConfigUtil.getLang("PowerMenu","PlayerName"));
        ElementInput worldNameInput = new ElementInput(ConfigUtil.getLang("PowerMenu","WorldName"));
        window.addElement(powerDropDown);
        window.addElement(playerNameInput);
        window.addElement(worldNameInput);
        PlayerEventListener.showFormWindow(player,window,GuiType.Power_Delete);
    }

    //编辑世界
    public static void showEditMenu(Player player, String level){
        FormWindowCustom formWindowCustom = new FormWindowCustom(ConfigUtil.getLang("EditWindow","EditWorldTitle")+" - 【" + level + "】");
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","FarmProtect"), MainClass.getLevelSettingBooleanInit(level,"World","FarmProtect")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllExplodes"),MainClass.getLevelSettingBooleanInit(level,"World","AllExplodes")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","TntExplodes"),MainClass.getLevelSettingBooleanInit(level,"World","TntExplodes")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","PVP"),MainClass.getLevelSettingBooleanInit(level,"World","PVP")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","KeepInventory"),MainClass.getLevelSettingBooleanInit(level,"World","KeepInventory")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","KeepXp"),MainClass.getLevelSettingBooleanInit(level,"World","KeepXp")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowOpenChest"),MainClass.getLevelSettingBooleanInit(level,"Player","AllowOpenChest")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","CanUseFishingHook"),MainClass.getLevelSettingBooleanInit(level,"Player","CanUseFishingHook")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowInteractFrameBlock"),MainClass.getLevelSettingBooleanInit(level,"Player","AllowInteractFrameBlock")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Sneak"),MainClass.getLevelSettingBooleanInit(level,"Player","Sneak")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Fly"),MainClass.getLevelSettingBooleanInit(level,"Player","Fly")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Swim"),MainClass.getLevelSettingBooleanInit(level,"Player","Swim")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Glide"),MainClass.getLevelSettingBooleanInit(level,"Player","Glide")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Jump"),MainClass.getLevelSettingBooleanInit(level,"Player","Jump")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Sprint"),MainClass.getLevelSettingBooleanInit(level,"Player","Sprint")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Pick"),MainClass.getLevelSettingBooleanInit(level,"Player","Pick")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","ConsumeItem"),MainClass.getLevelSettingBooleanInit(level,"Player","ConsumeItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","PlayerDropItem"),MainClass.getLevelSettingBooleanInit(level,"Player","DropItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BedEnter"),MainClass.getLevelSettingBooleanInit(level,"Player","BedEnter")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Move"),MainClass.getLevelSettingBooleanInit(level,"Player","Move")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","EatFood"),MainClass.getLevelSettingBooleanInit(level,"Player","EatFood")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","CommandPreprocess"),MainClass.getLevelSettingBooleanInit(level,"Player","CommandPreprocess")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","GameModeChange"),MainClass.getLevelSettingBooleanInit(level,"Player","GameModeChange")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BanTeleport"),MainClass.getLevelSettingBooleanInit(level,"Player","AntiTeleport")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Interact"),MainClass.getLevelSettingBooleanInit(level,"Player","Interact")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","NoFallDamage"),MainClass.getLevelSettingBooleanInit(level,"Player","NoFallDamage")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Explosion"),MainClass.getLevelSettingBooleanInit(level,"Entity","Explosion")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","PortalEnter"),MainClass.getLevelSettingBooleanInit(level,"Entity","PortalEnter")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowPlaceBlock"),MainClass.getLevelSettingBooleanInit(level,"Block","AllowPlaceBlock")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowBreakBlock"),MainClass.getLevelSettingBooleanInit(level,"Block","AllowBreakBlock")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Burn"),MainClass.getLevelSettingBooleanInit(level,"Block","Burn")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Ignite"),MainClass.getLevelSettingBooleanInit(level,"Block","Ignite")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Fall"),MainClass.getLevelSettingBooleanInit(level,"Block","Fall")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Grow"),MainClass.getLevelSettingBooleanInit(level,"Block","Grow")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Spread"),MainClass.getLevelSettingBooleanInit(level,"Block","Spread")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Form"),MainClass.getLevelSettingBooleanInit(level,"Block","Form")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","LeavesDecay"),MainClass.getLevelSettingBooleanInit(level,"Block","LeavesDecay")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","LiquidFlow"),MainClass.getLevelSettingBooleanInit(level,"Block","LiquidFlow")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","ItemFrameDropItem"),MainClass.getLevelSettingBooleanInit(level,"Block","ItemFrameDropItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","SignChange"),MainClass.getLevelSettingBooleanInit(level,"Block","SignChange")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockRedstone"),MainClass.getLevelSettingBooleanInit(level,"Block","BlockRedstone")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockDropItem"), MainClass.getLevelSettingBooleanInit(level,"Block","DropItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockDropExp"), MainClass.getLevelSettingBooleanInit(level,"Block","DropExp")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockUpdate"), MainClass.getLevelSettingBooleanInit(level,"Block","Update")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockFade"), MainClass.getLevelSettingBooleanInit(level,"Block","Fade")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockPistonChange"), MainClass.getLevelSettingBooleanInit(level,"Block","PistonChange")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockFromToEvent"), MainClass.getLevelSettingBooleanInit(level,"Block","FromToEvent")));

        PlayerEventListener.showFormWindow(player,formWindowCustom, GuiType.Edit_Process);
    }

    //返回窗口
    public static void showReturnWindow(Player player, Boolean success, GuiType guiType){
        FormWindowModal window;
        if(success) {
            window = new FormWindowModal(ConfigUtil.getLang("General","Title"), ConfigUtil.getLang("General","SaveSuccess"), ConfigUtil.getLang("General","ReturnButton"), ConfigUtil.getLang("General","QuitButton"));
        }else{
            window = new FormWindowModal(ConfigUtil.getLang("General","Title"), ConfigUtil.getLang("General","SaveFailed"), ConfigUtil.getLang("General","ReturnButton"), ConfigUtil.getLang("General","QuitButton"));
        }
        PlayerEventListener.showFormWindow(player,window,guiType);
    }

    //选择模板系统选项
    public static void showTemplateMainMenu(Player player) {
        FormWindowSimple window = new FormWindowSimple(ConfigUtil.getLang("TemplateMainMenu","Title"), ConfigUtil.getLang("TemplateMainMenu","Content"));
        window.addButton(new ElementButton(ConfigUtil.getLang("TemplateMainMenu","AddTemplate")));
        window.addButton(new ElementButton(ConfigUtil.getLang("TemplateMainMenu","EditTemplate")));
        window.addButton(new ElementButton(ConfigUtil.getLang("General","ReturnButton")));
        PlayerEventListener.showFormWindow(player,window, GuiType.Template_Main);
    }

    //设置选择模板 -> 两类 ①选择模板创建世界 ②设置模板
    public static void showSettingChooseTemplateMenu(Player player, GuiType type){
        FormWindowSimple window = new FormWindowSimple(ConfigUtil.getLang("SettingChooseTemplateMenu","Title"), ConfigUtil.getLang("SettingChooseTemplateMenu","Content"));
        for(String buttonTitle: ConfigUtil.TemplateCache.keySet()){
            window.addButton(new ElementButton(buttonTitle));
        }
        window.addButton(new ElementButton(ConfigUtil.getLang("General","ReturnButton")));
        PlayerEventListener.showFormWindow(player,window, type);
    }

    //添加模板
    public static void showTemplateAddMenu(Player player){
        FormWindowCustom custom = new FormWindowCustom(ConfigUtil.getLang("TemplateAddMenu","Title"));
        custom.addElement(new ElementInput(ConfigUtil.getLang("TemplateAddMenu","TemplateName")));
        PlayerEventListener.showFormWindow(player, custom, GuiType.Template_Add);
    }

    //模板设置
    public static void showTemplateSettingMenu(Player player, String TemplateName, GuiType type){
        FormWindowCustom formWindowCustom = new FormWindowCustom(ConfigUtil.getLang("EditWindow","EditTemplateTitle") + " - "+ TemplateName);
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","FarmProtect"), ConfigUtil.getTemplateBooleanInit(TemplateName,"World","FarmProtect")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllExplodes"), ConfigUtil.getTemplateBooleanInit(TemplateName,"World","AllExplodes")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","TntExplodes"), ConfigUtil.getTemplateBooleanInit(TemplateName,"World","TntExplodes")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","PVP"), ConfigUtil.getTemplateBooleanInit(TemplateName,"World","PVP")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","KeepInventory"), ConfigUtil.getTemplateBooleanInit(TemplateName,"World","KeepInventory")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","KeepXp"), ConfigUtil.getTemplateBooleanInit(TemplateName,"World","KeepXp")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowOpenChest"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","AllowOpenChest")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","CanUseFishingHook"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","CanUseFishingHook")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowInteractFrameBlock"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","AllowInteractFrameBlock")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Sneak"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Sneak")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Fly"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Fly")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Swim"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Swim")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Glide"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Glide")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Jump"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Jump")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Sprint"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Sprint")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Pick"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Pick")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","ConsumeItem"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","ConsumeItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","PlayerDropItem"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","DropItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BedEnter"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","BedEnter")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Move"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Move")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","EatFood"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","EatFood")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","CommandPreprocess"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","CommandPreprocess")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","GameModeChange"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","GameModeChange")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BanTeleport"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","AntiTeleport")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Interact"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","Interact")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","NoFallDamage"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Player","NoFallDamage")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Explosion"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Entity","Explosion")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","PortalEnter"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Entity","PortalEnter")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowPlaceBlock"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","AllowPlaceBlock")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","AllowBreakBlock"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","AllowBreakBlock")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Burn"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Burn")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Ignite"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Ignite")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Fall"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Fall")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Grow"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Grow")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Spread"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Spread")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","Form"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Form")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","LeavesDecay"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","LeavesDecay")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","LiquidFlow"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","LiquidFlow")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","ItemFrameDropItem"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","ItemFrameDropItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","SignChange"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","SignChange")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockRedstone"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","BlockRedstone")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockDropItem"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","DropItem")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockDropExp"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","DropExp")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockUpdate"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Update")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockFade"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","Fade")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockPistonChange"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","PistonChange")));
        formWindowCustom.addElement(new ElementToggle(ConfigUtil.getLang("EditWindow","BlockFromToEvent"), ConfigUtil.getTemplateBooleanInit(TemplateName,"Block","FromToEvent")));
        PlayerEventListener.showFormWindow(player,formWindowCustom, type);
    }
}
