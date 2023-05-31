package glorydark.DLevelEventPlus.event;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import glorydark.DLevelEventPlus.MainClass;
import glorydark.DLevelEventPlus.utils.ConfigUtil;

public class BlockEventListener implements Listener {
    //Block
    //方块放置
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","AllowPlaceBlock");
        if(bool == null){return;}
        if(ConfigUtil.isAdmin(event.getPlayer())){ return; }
        if(ConfigUtil.isOperator(event.getPlayer(), event.getPlayer().getLevel())){ return; }

        if (!bool) {
            event.getPlayer().setAllowModifyWorld(false);
            if(MainClass.show_actionbar_text) {
                event.getPlayer().sendActionBar(ConfigUtil.getLang("Tips","AntiPlaceBlock"));
            }
            event.setCancelled(true);
        }else{
            Block block = event.getBlock();
            String blockString = block.getId()+":"+block.getDamage();
            if(MainClass.getLevelStringListInit(block.getLevel().getName(),"Block","AntiPlaceBlocks").contains(blockString) && !MainClass.getLevelStringListInit(block.getLevel().getName(),"Block","CanPlaceBlocks").contains(blockString)){
                if(MainClass.show_actionbar_text) {
                    event.getPlayer().setAllowModifyWorld(false);
                    event.getPlayer().sendActionBar(ConfigUtil.getLang("Tips","AntiPlaceSpecificBlock"));
                }
                event.setCancelled(true);
            }else{
                event.getPlayer().setAllowModifyWorld(true);
            }
        }
    }

    //方块破坏
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","AllowBreakBlock");
        if(bool == null){return;}
        if(ConfigUtil.isAdmin(event.getPlayer())){ return; }
        if(ConfigUtil.isOperator(event.getPlayer(), event.getPlayer().getLevel())){ return; }
        if (!bool) {
            if(MainClass.show_actionbar_text) {
                event.getPlayer().sendActionBar(ConfigUtil.getLang("Tips","AntiBreakBlock"));
            }
            event.setCancelled(true);
        }else{
            Block block = event.getBlock();
            String blockString = block.getId()+":"+block.getDamage();
            if(MainClass.getLevelStringListInit(block.getLevel().getName(),"Block","AntiBreakBlocks").contains(blockString) && !MainClass.getLevelStringListInit(block.getLevel().getName(),"Block","CanBreakBlocks").contains(blockString)){
                if(MainClass.show_actionbar_text) {
                    event.getPlayer().sendActionBar(ConfigUtil.getLang("Tips","AntiBreakSpecificBlock"));
                }
                event.setCancelled(true);
            }else{
                if(!isDropItem(event.getPlayer().getLevel(), block)){
                    event.setDrops(new Item[0]);
                }
                if(!isDropExp(event.getPlayer().getLevel(), block)){
                    event.setDropExp(0);
                }
            }
        }
    }

    public boolean isDropItem(Level level, Block block){
        Boolean bool = MainClass.getLevelBooleanInit(level.getName(),"Block","DropItem");
        if(bool != null && !bool){
            return MainClass.getLevelStringListInit(level.getName(), "Block", "DropItemBlocks").contains(block.getId() + ":" + block.getDamage());
        }
        return true;
    }

    public boolean isDropExp(Level level, Block block){
        Boolean bool = MainClass.getLevelBooleanInit(level.getName(),"Block","DropExp");
        if(bool != null && !bool){
            return MainClass.getLevelStringListInit(level.getName(), "Block", "DropExpBlocks").contains(block.getId() + ":" + block.getDamage());
        }
        return true;
    }

    @EventHandler
    public void BlockBurnEvent(BlockBurnEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Burn");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockIgniteEvent(BlockIgniteEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Ignite");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockFallEvent(BlockFallEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Fall");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockGrowEvent(BlockGrowEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Grow");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockSpreadEvent(BlockSpreadEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Spread");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockFormEvent(BlockFormEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Form");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void LeavesDecayEvent(LeavesDecayEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","LeavesDecay");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void LiquidFlowEvent(LiquidFlowEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","LiquidFlow");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void RedstoneUpdateEvent(BlockUpdateEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","BlockRedstone");
        if(bool == null){return;}
        if (!bool) {
            if(event.getBlock().isPowerSource()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ItemFrameDropItemEvent(ItemFrameDropItemEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","ItemFrameDropItem");
        if(bool == null){return;}
        if(ConfigUtil.isAdmin(event.getPlayer())){ return; }
        if(ConfigUtil.isOperator(event.getPlayer(), event.getPlayer().getLevel())){ return; }
        Level level = event.getPlayer().getLevel();
        if (!bool) {
            if(MainClass.show_actionbar_text) {
                event.getPlayer().sendActionBar(ConfigUtil.getLang("Tips", "AntiDestroyFlameBlock").replace("%s", level.getName()));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void SignChangeEvent(SignChangeEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","SignChange");
        if(bool == null){return;}
        if(ConfigUtil.isAdmin(event.getPlayer())){ return; }
        if(ConfigUtil.isOperator(event.getPlayer(), event.getPlayer().getLevel())){ return; }
        Level level = event.getPlayer().getLevel();
        if (!bool) {
            if(MainClass.show_actionbar_text) {
                event.getPlayer().sendActionBar(ConfigUtil.getLang("Tips", "AntiChangeSignText").replace("%s", level.getName()));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockUpdateEvent(BlockUpdateEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Update");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockFadeEvent(BlockFadeEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","Fade");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockPistonChangeEvent(BlockPistonChangeEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","PistonChange");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockFromToEvent(BlockFromToEvent event){
        Boolean bool = MainClass.getLevelBooleanInit(event.getBlock().getLevel().getName(),"Block","FromToEvent");
        if(bool == null){return;}
        if (!bool) {
            event.setCancelled(true);
        }
    }
}
