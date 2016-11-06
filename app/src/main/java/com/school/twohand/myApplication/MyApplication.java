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
    // 保存当前播放器  数据源 |position (第几首)| 播放状态
    private List<MusicDataInfro> musicDataInfros ;
    private int position;

    public User getUser() {
        return user;
    }

    private String playState;
    private User user = new User();
    private ClassTbl classTbl = new ClassTbl();
    private String userName = "aaaaa";
    private String otherName = "bbbbb";
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        user.setUserId(2);
        user.setUserName("秦孤寂");
        user.setUserHead("image/a.jpg");
        JMessageClient.init(getApplicationContext());
        JPushInterface.setDebugMode(true);
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);
    }

    public String getOtherName() {
        return otherName;
    }

    public String getUserName() {
        return userName;
    }

    public String getState() {
        return playState;
    }

    public void setState(String playState) {
        this.playState = playState;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<MusicDataInfro> getMusicDataInfros() {
        return musicDataInfros;
    }

    public void setMusicDataInfros(List<MusicDataInfro> musicDataInfros) {
        this.musicDataInfros = musicDataInfros;
    }
}
