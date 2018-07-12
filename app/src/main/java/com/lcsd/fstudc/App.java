package com.lcsd.fstudc;

import android.app.Application;
import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.lcsd.fstudc.ui.moudle.UserInfo;
import com.lcsd.fstudc.utils.Utils;
import com.tsy.sdk.myokhttp.MyOkHttp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by jie on 2018/5/18.
 */
public class App extends Application {
    private static App appContext;
    private static MyOkHttp mMyOkHttp;
    private static Context context;

    public static App getInstance() {
        if (appContext == null) {
            appContext=new App();
        }
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext=this;
        context = this.getApplicationContext();
    }

    public MyOkHttp getmMyOkHttp() {
        if (mMyOkHttp == null) {
            //持久化存储cookie
            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            //自定义OkHttp
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                    .cookieJar(cookieJar)       //设置开启cookie
                    .build();
            mMyOkHttp = new MyOkHttp(okHttpClient);
        }
        return mMyOkHttp;
    }
    /**
     * 获取缓存用户信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        return Storage.GetUserInfo() == null ? new UserInfo() : Storage
                .GetUserInfo();
    }

    /**
     * 保存缓存用户信息
     *
     * @param user
     */
    public void saveUserInfo(final UserInfo user) {
        if (user != null) {
            Storage.ClearUserInfo();
            Storage.saveUsersInfo(user);
        }
    }

    /**
     * 用户存在是ture 否则是false
     *
     * @return
     */
    public boolean checkUser() {
        if (Utils.isEmpty(getUserInfo().getId())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 清除缓存用户信息
     *
     * @param
     */
    public void cleanUserInfo() {
        Storage.ClearUserInfo();
    }
}
