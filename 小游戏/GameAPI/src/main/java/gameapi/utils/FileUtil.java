package gameapi.utils;

import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * @author lt_name (CrystalWar)
 */

public class FileUtil {

    private FileUtil() {
        throw new RuntimeException("error");
    }

    public static boolean delete(String file) {
        return delete(new File(file));
    }

    public static boolean delete(File file) {
        try {
            if (!file.exists()) {
                return true;
            }
            File[] files = file.listFiles();
            if (files != null) {
                for (File getFile : files) {
                    if (getFile.isDirectory()) {
                        delete(getFile);
                    }else if (!getFile.delete()) {
                        throw new IOException("文件: " + getFile.getName() + " 删除失败！");
                    }
                }
            }
            if (!file.delete()) {
                throw new IOException("文件: " + file.getName() + " 删除失败！");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copy(String from, String to) {
        return copy(new File(from), new File(to));
    }

    public static boolean copy(String from, File to) {
        return copy(new File(from), to);
    }

    public static boolean copy(File from, String to) {
        return copy(from, new File(to));
    }

    public static boolean copy(File from, File to) {
        try {
            File [] files = from.listFiles();
            if (files != null) {
                if (!to.exists() && !to.mkdirs()) {
                    throw new IOException("文件夹: " + to.getName() + " 创建失败！");
                }
                for (File file : files) {
                    if (file.isDirectory()) {
                        copy(file, new File(to, file.getName()));
                    }else {
                        Utils.copyFile(file, new File(to, file.getName()));
                    }
                }
                return true;
            }else {
                Utils.copyFile(from, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}