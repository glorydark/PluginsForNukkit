package glorydark.DTipSystem.util;

public class TitleData {

    public final String content;
    public final String subTitle;
    public final int fadein;
    public final int fadeout;
    public final int duration;

    public TitleData(String content, String subTitle, int fadein, int fadeout, int duration) {
        this.content = content;
        this.subTitle = subTitle;
        this.fadein = fadein;
        this.fadeout = fadeout;
        this.duration = duration;
    }
}
