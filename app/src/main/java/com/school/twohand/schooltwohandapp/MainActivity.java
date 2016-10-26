package com.school.twohand.schooltwohandapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.school.twohand.myApplication.MyApplication;

import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;


public class MainActivity extends AppCompatActivity {

    String TAG = "JPush";
    MyApplication exampleApplication ;
    Button login;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        exampleApplication = (MyApplication) getApplication();

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dia = new ProgressDialog(MainActivity.this);
                dia.setMessage("跳转中");
                dia.show();
                //用本人用户名登录到极光服务器自己的账号
                JMessageClient.login(exampleApplication.getUserName(), "abc123", new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        Log.i(TAG, "登录: " + i + "--+" + s);
                        if(i == 0){
                            //进入与你对话人的聊天
                            JMessageClient.enterSingleConversation(exampleApplication.getOtherName());
                            dia.cancel();
                            //页面跳转到聊天室
                            Intent intent = new Intent(MainActivity.this,DemoActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });




    }


}
