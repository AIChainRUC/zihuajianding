package com.lingyun_chain.zihua.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * 文件工具类
 */
public class FileUtil {
    public static final String Cache = "data/data/com.lingyun.zihua";

    //本APP存放的目录
    public static String getPath() {
        String ROOT = "Lingyun_chain";
        StringBuilder path = new StringBuilder();
        if (OSutil.isSdExist()) {
            path.append(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
            path.append(File.separator);// '/'
            path.append(ROOT);// /mnt/sdcard/JZElec
            //path.append(File.separator);// /data/data/包名/cache/
        } else {
            File filesDir = UiUtils.getContext().getCacheDir(); // cache
            // getFileDir
            // file
            path.append(filesDir.getAbsolutePath());// /data/data/包名/cache
            //path.append(File.separator);// /data/data/包名/cache/
        }
        return path.toString();
    }

    /**
     * 在内存卡中新建一个目录（如果内存卡存在的话）
     *
     * @param cache 想要建立的文件目录
     * @return 建立的File文件 SD卡路径/ZiHua/传入的参数值
     */
    public static File createDirs(String cache) {
        String path = getPath();
        StringBuilder builderPath = new StringBuilder(path);

        builderPath.append(cache);
        File file = new File(builderPath.toString());
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();// 创建文件夹
        }
        return file;
    }


    /**
     * 得到cache文件夹
     *
     * @return SD卡目录/PandaDrive/cache
     */
    public static File getCacheDir() {
        return createDirs("cache");
    }

    /**
     * 得到photo文件夹
     *
     * @return SD卡目录/PandaDrive/photo
     */
    public static File getPhotoDir() {
        return createDirs("photo");
    }


    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取指定文件大小
     *
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 递归删除文件和文件夹
     */
    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
        }
    }
}
