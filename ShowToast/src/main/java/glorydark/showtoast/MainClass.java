package glorydark.showtoast;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

public class MainClass extends PluginBase {
    @Override
    public void onLoad() {
        this.getLogger().info(TextFormat.GREEN+"ShowToast 加载中");
        this.getServer().getCommandMap().register("", new ToastCommand("sendtoast"));
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.GREEN+"ShowToast 加载成功");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.RED+"ShowToast 卸载中");
    }
}
