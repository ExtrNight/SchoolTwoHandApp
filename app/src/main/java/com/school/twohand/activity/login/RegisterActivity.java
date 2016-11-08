package com.school.twohand.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.school.twohand.entity.User;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class RegisterActivity extends AppCompatActivity {
    Toolbar toolbar ;
    EditText userName;
    EditText userPassword;
    EditText userPassword2;
    Button register;
    TextView returnLogin;
    //填写信息
    String name;
    String password;
    String password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //找控件
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        userName = (EditText) findViewById(R.id.registerUserName);
        userPassword = (EditText) findViewById(R.id.registerUserPassword);
        userPassword2 = (EditText) findViewById(R.id.registerUserPassword2);
        register = (Button) findViewById(R.id.registerButton);
        returnLogin = (TextView) findViewById(R.id.returnLogin);
        //toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        /*
        在极光上注册
            public static void register(java.lang.String userName,
            java.lang.String password,
            cn.jpush.im.api.BasicCallback callback)
         */
       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               name = userName.getText().toString();//用户账号
               password = userPassword.getText().toString();//用户密码
               password2 = userPassword2.getText().toString();//确认用户密码
               if (name.trim().length()!=0&&password.trim().length()!=0&&password2.trim().length()!=0){//判断输入账号或密码不为空
                   if (password.equals(password2)){//如果两次密码一样则注册
                       final ProgressDialog pdialog = new ProgressDialog(RegisterActivity.this);
                       pdialog.setMessage("帮您注册中，同学请稍等。。");
                       pdialog.show();
                       JMessageClient.register(name, password, new BasicCallback() {
                           @Override
                           public void gotResult(int i, String s) {
                               if (i == 0 ){
                                   //也在自己的服务器上插入注册账号密码
                                   RequestParams requestParams = new RequestParams(NetUtil.url+"RegisterServlet");
                                   User user = new User();
                                   user.setUserAccount(name);
                                   user.setUserPassword(password);
                                   Gson gson = new Gson();
                                   String userString = gson.toJson(user);
                                   requestParams.addBodyParameter("user",userString);
                                   x.http().post(requestParams, new Callback.CommonCallback<String>() {
                                       @Override
                                       public void onSuccess(String result) {
                                           if (result!=null) {
                                               pdialog.cancel();
                                               Intent intent = new Intent();
                                               intent.putExtra("userName", name);
                                               intent.putExtra("userId",result);
                                               setResult(RESULT_OK, intent);
                                               finish();
                                           }else {
                                               Toast.makeText(RegisterActivity.this, "注册失败请重试", Toast.LENGTH_SHORT).show();
                                           }
                                       }

                                       @Override
                                       public void onError(Throwable ex, boolean isOnCallback) {

                                       }

                                       @Override
                                       public void onCancelled(CancelledException cex) {

                                       }

                                       @Override
                                       public void onFinished() {

                                       }
                                   });
                               }else {
                                   pdialog.cancel();
                                   Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                   }else{
                       Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                   }
               }else{
                   Toast.makeText(RegisterActivity.this,"填写不完整，请重新填写", Toast.LENGTH_SHORT).show();
               }
           }
       });
        //返回登录
        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
