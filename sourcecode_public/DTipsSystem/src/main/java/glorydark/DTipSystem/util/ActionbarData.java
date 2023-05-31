package glorydark.DTipSystem.util;

public class ActionbarData {
    public final String content;
    public final int fadein;
    public final int fadeout;
    public final int duration;

    public ActionbarData(String content, int fadein, int fadeout, int duration) {
        this.content = content;
        this.fadein = fadein;
        this.fadeout = fadeout;
        this.duration = duration;
    }
}
