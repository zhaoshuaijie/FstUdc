package com.lcsd.fstudc.utils;

import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/3/22.
 */
public class FileUtils {
    //剪切视频保存视频目录
    public static String VIDEO_PATH = "/sdcard/LcsdEdit";
    //录制视频保存视频目录
    public static String VIDEO_PATH1 = "/sdcard/LcsdEdit/luzhi";

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static float getDuration(String path) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);  //recordingFilePath（）为音频文件的路径
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float duration = player.getDuration();//获取音频的时间
        player.release();//记得释放资源
        return duration;
    }
}
