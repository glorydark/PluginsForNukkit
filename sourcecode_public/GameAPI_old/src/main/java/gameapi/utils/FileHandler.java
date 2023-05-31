package gameapi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("Unused")
public class FileHandler {
    public FileHandler() {
    }

    /**
     * 复制文件
     * @param fromDir
     * @param toDir
     * @throws IOException
     */
    public static void copyDir(String fromDir,String toDir) throws IOException{
        //创建目录的File对象
        File dirSource = new File(fromDir);
        //判断源目录是不是一个目录
        if (!dirSource.isDirectory()) {
            //如果不是目录那就不复制
            return;
        }
        //创建目标目录的File对象
        File destDir = new File(toDir);
        //如果目的目录不存在
        if(!destDir.exists()){
            //创建目的目录
            destDir.mkdir();
        }
        //获取源目录下的File对象列表
        File[]files = dirSource.listFiles();
        for (File file : files) {
            //拼接新的fromDir(fromFile)和toDir(toFile)的路径
            String strFrom = fromDir + File.separator + file.getName();
            System.out.println(strFrom);
            String strTo = toDir + File.separator + file.getName();
            System.out.println(strTo);
            //判断File对象是目录还是文件
            //判断是否是目录
            if (file.isDirectory()) {
                //递归调用复制目录的方法
                copyDir(strFrom,strTo);
            }
            //判断是否是文件
            if (file.isFile()) {
                //递归调用复制文件的方法
                copyFile(strFrom,strTo);
            }
        }
    }
    /**
     * 复制文件
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copyFile(String fromFile,String toFile) throws IOException{
        //字节输入流——读取文件
        FileInputStream in = new FileInputStream(fromFile);
        //字节输出流——写入文件
        FileOutputStream out = new FileOutputStream(toFile);
        //把读取到的内容写入新文件
        //把字节数组设置大一些   1*1024*1024=1M
        byte[] bs = new byte[1*1024*1024];
        int count = 0;
        while((count = in.read(bs))!=-1){
            out.write(bs,0,count);
        }
        //关闭流
        in.close();
        out.flush();
        out.close();
    }

    public static void delete(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                delete(file);
            }
        }
        dir.delete();
    }
}