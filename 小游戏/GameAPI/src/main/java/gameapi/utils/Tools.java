package gameapi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
    //https://blog.csdn.net/weixin_39975055/article/details/115082818
    public static String dateToString(Date date) {
        String str = "yyyyMMdd_hhmmss";
        SimpleDateFormat format = new SimpleDateFormat(str);
        String dateFormat = format.format(date);
        return dateFormat;

    }
    //https://blog.csdn.net/weixin_39975055/article/details/115082818
    public static Date stringToDate(String string) {
        String str = "yyyy-MM-dd-hh-mm-ss";
        SimpleDateFormat format = new SimpleDateFormat(str);
        Date date = new Date();
        try {
            date = format.parse(string);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return date;
    }
}
