package com.school.twohand.activity.taoquan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.school.twohand.schooltwohandapp.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;

public class QunLiaoActivity extends AppCompatActivity {
    Button button ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qun_liao);
        findViewById();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * groupName - 群组名称
                 groupDesc - 群组描述
                 callback - 回调接口
                 */
                JMessageClient.createGroup("仰恩群", "仰恩大学第一校区", new CreateGroupCallback() {
                    @Override
                    public void gotResult(int i, String s, long l) {
                        if (i == 0){
                            Log.i("gotResult", "gotResult: "+i+"=="+s+"==="+l);
                        }
                    }
                });
            }
        });
    }

    public void findViewById(){
        button = (Button) findViewById(R.id.plus);
    }
}
