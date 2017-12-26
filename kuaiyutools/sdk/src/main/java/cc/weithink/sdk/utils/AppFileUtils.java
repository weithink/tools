package cc.weithink.sdk.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by weithink on 17/12/6.
 * 文件储存的工具类
 */

public class AppFileUtils {
    private static String AppName;
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static File getAppSdcardPathFile() {
        File tempFile = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            String saveDir = Environment.getExternalStorageDirectory() + File.separator
                    + AppName;
            tempFile = new File(saveDir);
            if (!tempFile.exists()) {
                tempFile.mkdir(); // 创建文件夹
            }
        }
        return tempFile;
    }

    public static File getTempPathFile() {
        return createDirs(getTempPath());
    }

    public static File getSavePathFile() {
        return createDirs(getSavePath());
    }

    public static File getCrashPathFile() {
        return createDirs(getCrashPath());
    }

    public static File getHprofPathFile() {
        return createDirs(getHprofPath());
    }

    public static String getTempPath() {
        return getAppSdcardPath() + File.separator + "temp/";
    }

    public static String getSavePath() {
        return getAppSdcardPath() + File.separator + "save/";
    }

    public static String getCrashPath() {
        return getAppSdcardPath() + File.separator + "log/";
    }

    public static String getHprofPath() {
        return getAppSdcardPath() + File.separator + "hprof/";
    }

    public static String getAppSdcardPath() {
        return getAppSdcardPathFile().getAbsolutePath();
    }

    /**
     * 这个在sdk config 调用初始化操作
     *
     * @param appName
     */
    public static void initAppFilePath(String appName) {
        AppName = appName;
        getAppSdcardPathFile();
    }

    private static File createDirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs(); // 创建文件夹
        }

        return file;
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    public String getFileMD5(File file) {
        if (file == null || !file.isFile()) {
            return "";
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            return MD5(ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // 关闭流产生的错误一般都可以忽略
                }
            }
        }
    }

    private String MD5(MappedByteBuffer buffer) {
        String s = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>>,
                // 逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }
}
