package glorydark.summonentity;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.PluginTask;

public class ChangeSkinTask extends PluginTask {
    private DefaultEntity entity;
    private Integer duration;

    private String skinName= "";

    private Integer showSkinId = 0;

    public ChangeSkinTask(Plugin owner, DefaultEntity entity, Integer duration, String skinName) {
        super(owner);
        this.entity = entity;
        this.duration = duration;
        this.skinName = skinName;
    }

    @Override
    public void onRun(int i) {
        if(!entity.isAlive()){
            entity.close();
            MainClass.entityList.remove(entity);
            Server.getInstance().removePlayerListData(entity.getUniqueId());
            this.onCancel();
            return;
        }
        entity.addRecordTick(1);
        if(entity.getRecordTick() >= duration*20){
            MainClass.entityList.remove(entity);
            Server.getInstance().removePlayerListData(entity.getUniqueId());
            entity.kill();
            entity.close();
            this.cancel();
        }else{
            if(entity.getRecordTick()%entity.getChangeInterval()==0 && entity.getRecordTick()/entity.getChangeInterval() >= 1){
                if(showSkinId + 1 < MainClass.variedSkinList.get(skinName).size()){
                    showSkinId++;
                }else{
                    showSkinId=0;
                }
                entity.setSkin(MainClass.variedSkinList.get(skinName).get(showSkinId));
                Server.getInstance().removePlayerListData(entity.getUniqueId());
                Server.getInstance().updatePlayerListData(entity.getUniqueId(), entity.getId(), entity.getOwner().getName()+"的称号", entity.getSkin());
                //entity.respawnToAll();
            }
        }
    }
}
