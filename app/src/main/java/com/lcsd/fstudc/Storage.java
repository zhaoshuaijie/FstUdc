package com.lcsd.fstudc;

import com.lcsd.fstudc.ui.moudle.UserInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

public class Storage {
    /**
     * 清除缓存用户信息数据
     */
    public static void ClearUserInfo() {
        new File(App.getInstance().getCacheDir().getPath() + "/"
                + "ft_jizhe.bean").delete();
    }

    /**
     * 保存用户信息至缓存
     *
     * @param userInfo
     */
    public static void saveUsersInfo(UserInfo userInfo) {
        try {
            new ObjectOutputStream(new FileOutputStream(new File(App
                    .getInstance().getCacheDir().getPath()
                    + "/" + "ft_jizhe.bean"))).writeObject(userInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取缓存数据
     *
     * @return
     */
    public static UserInfo GetUserInfo() {
        File file = new File(App.getInstance().getCacheDir().getPath()
                + "/" + "ft_jizhe.bean");
        if (!file.exists())
            return null;

        if (file.isDirectory())
            return null;

        if (!file.canRead())
            return null;

        try {
            @SuppressWarnings("resource")
            UserInfo userInfo = (UserInfo) new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(file)))
                    .readObject();
            return userInfo;
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
