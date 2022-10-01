package glorydark.dcurrency.Commands;

import cn.nukkit.command.CommandSender;

/**
 * @author 若水
 */
abstract public class SubCommand {
    /**
     * 指令使用权限
     * @param sender CommandSender
     * @return boolean
     */
    public abstract boolean canUse(CommandSender sender);


    /**
     * 指令名称
     * @return string
     */
    public abstract String getName();
    /**
     * 指令别名
     * @return string[]
     */
    public abstract String[] getAliases();

    /**
     * 执行
     * @param sender the sender      - CommandSender
     * @param args   The arguments      - String[]
     * @return true if true
     */

    public abstract boolean execute(CommandSender sender, String[] args);

    /**
     * 指令帮助信息
     * @return 帮助
     * */
    public abstract String getHelp();
}