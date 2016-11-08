package com.school.twohand.myApplication;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.school.twohand.entity.ClassTbl;
import com.school.twohand.entity.MusicDataInfro;
import com.school.twohand.entity.User;
import org.xutils.x;
import java.util.List;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2016/10/14 0014.
 */
public class MyApplication extends Application {

    //当前使用者的 User
    private User user;
    //被访问对象的name
    private String otherAccount ;

    private ClassTbl classTbl = new ClassTbl();

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        //极光聊天客户端初始化
        JMessageClient.init(getApplicationContext());
        JPushInterface.setDebugMode(true);
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);

        //从本地取用户信息
        /*
        同样根据Context对象获取SharedPreference对象；
        直接使用SharedPreference的getXXX(key)方法获取数据。
         */
        SharedPreferences sp = getSharedPreferences("USER",MODE_PRIVATE);
        String userString = sp.getString("user",null);
        Log.i("MyApplication", "onCreate: "+userString);
        if (userString!=null){
            Gson gson = new Gson();
            User user = gson.fromJson(userString,User.class);
            setUser(user);
            JMessageClient.login(user.getUserAccount(), user.getUserPassword(), new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Log.i("MyApplication", "gotResult: "+i);
                }
            });
        }


    }
    //获取和设置被访问对象的
    public String getOtherAccount() {
        return otherAccount;
    }
    public void setOtherAccount(String otherAccount){
        this.otherAccount = otherAccount;
    }

    //获取设置当前使用者的User对象
    public User getUser() {
        return user;
    }
    public void setUser(User user){
        this.user = user;
    }
}
