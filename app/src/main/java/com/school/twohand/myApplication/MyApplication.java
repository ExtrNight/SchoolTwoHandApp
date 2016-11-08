package com.school.twohand.myApplication;
import android.app.Application;
import com.school.twohand.entity.ClassTbl;
import com.school.twohand.entity.MusicDataInfro;
import com.school.twohand.entity.User;
import org.xutils.x;
import java.util.List;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
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

//        user = new User();
//        user.setUserId(1);
//        user.setUserName("Jack");
//        user.setUserHead("1/1475660662253user.jpg");

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
