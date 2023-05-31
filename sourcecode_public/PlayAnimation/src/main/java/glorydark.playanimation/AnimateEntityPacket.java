package glorydark.playanimation;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author iGxnon
 * @date 2021/8/30
 */
@Getter
@Setter
public class AnimateEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    private String animation;
    private String nextState;
    private String stopExpression = "";
    private String controller = "query.any_animation_finished";
    private float blendOutTime = 0;
    private Set<Long> entityRuntimeIDs = new HashSet<>();

    private int stopExpressionVersion = 0; // 需要这个才能正常播放！

    // 客户端一般不会发这个包
    @Override
    public void decode() {
        this.animation = this.getString();
        this.nextState = this.getString();
        this.stopExpression = this.getString();
        this.controller = this.getString();
        this.blendOutTime = this.getLFloat();
        for (int i = 0, len = (int) this.getUnsignedVarInt(); i < len; i++) {
            this.entityRuntimeIDs.add(this.getEntityRuntimeId());
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.animation);
        this.putString(this.nextState);
        this.putString(this.stopExpression);
        this.putLInt(this.stopExpressionVersion); //Added (1.17.40版本以上需要这个包，帮原作者加上)
        this.putString(this.controller);
        this.putLFloat(this.blendOutTime);
        this.putUnsignedVarInt(this.entityRuntimeIDs.size());
        for (long entityRuntimeId : this.entityRuntimeIDs){
            this.putEntityRuntimeId(entityRuntimeId);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}