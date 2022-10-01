package glorydark.lotterybox.tools;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import com.sun.org.glassfish.gmbal.Description;

@Description("Original Author: SmallAsWater") //引用若水的NBT物品保存代码

public class Inventory {

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String saveItemToString(Item item){
        if(item.hasCompoundTag()){
            return item.getId()+":"+item.getDamage()+":"+item.getCount()+":"+bytesToHexString(item.getCompoundTag());
        }else{
            return item.getId()+":"+item.getDamage()+":"+item.getCount()+":null";
        }
    }

    public static Item getItem(String itemString) {
        String[] a = itemString.split(":");
        if(a.length!=4){ return null; }
        Item item = Item.get(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
        if (!a[3].equals("null")) {
            CompoundTag tag = Item.parseCompoundTag(hexStringToBytes(a[3]));
            item.setNamedTag(tag);
        }
        return item;
    }
}
