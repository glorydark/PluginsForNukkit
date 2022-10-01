package glorydark.showtoast;

import cn.nukkit.network.protocol.DataPacket;

public class ToastRequestPacket extends DataPacket {
    public String title = "";
    public String content = "";

    public ToastRequestPacket() {
    }

    public byte pid() {
        return -70;
    }

    public void decode() {
        this.title = this.getString();
        this.content = this.getString();
    }

    public void encode() {
        this.reset();
        this.putString(this.title);
        this.putString(this.content);
    }

    public String toString() {
        return "ToastRequestPacket(title=" + this.title + ", content=" + this.content + ")";
    }
}
