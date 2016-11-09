package com.school.twohand.activity.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class LoginActivity extends AppCompatActivity {
    TextView register = null;
    EditText userName = null;
    EditText password = null;
    ImageView headImage;
    Button login;
    String userAccount;
    String passwordString;
    MyApplication myApplication;
    String userId = null;
    static final int CODE = 10;//去注册界面的请求码
    public static final int ResultCode = 100;   //登录成功的结果码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //登录临时的用户
        JMessageClient.login("1111", "1111", new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.i("headImage", "gotResult: " + i);
            }
        });
        //找控件
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.userPassword);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        headImage = (ImageView) findViewById(R.id.headImage);
        //跳转到注册界面
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setTextColor(Color.GREEN);
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,CODE);
            }
        });
        //用户账号输入监听，有该账号则返回头像
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //登录临时的用户
                if (JMessageClient.getMyInfo()==null) {
                    JMessageClient.login("1111", "1111", new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            Log.i("headImage", "gotResult: " + i);
                        }
                    });
                }
                //判断该用户有没有头像
                /*
                public abstract void getAvatarBitmap(GetAvatarBitmapCallback callback)
                从本地获取用户头像的缩略头像bitmap，如果本地存在头像缩略图文件，直接返回；若不存在，会异步从服务器拉取。 下载完成后 会将头像保存至本地并返回。当用户未设置头像，或者下载失败时回调返回Null。
                所有的缩略头像bitmap在sdk内都会缓存， 并且会有清理机制，所以上层不需要对缩略头像bitmap做缓存。
                */
                String name = s.toString();//当前输入账户
                Log.i("headImage", "gotResult: "+name);
                JMessageClient.getUserInfo(name, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        Log.i("headImage", "gotResult: "+i+userInfo);
                        if (i == 0){//获取用户信息
                            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int i, String s, Bitmap bitmap) {
                                    if (i == 0){//获取头像
                                        headImage.setImageBitmap(bitmap);
                                    }
                                    Log.i("headImage", "gotResult: "+i+ "==="+bitmap);
                                }
                            });
                        }else{
                            headImage.setImageResource(R.mipmap.hugh);
                        }
                    }
                });
            }
        });

        //登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAccount = userName.getText().toString();//获取账户
                passwordString = password.getText().toString();//获取密码
                if (userAccount.trim().length()!=0&&passwordString.trim().length()!=0){//账户和密码输入不为0，登录
                    /*
                    登录
                    public static void login(java.lang.String userName,
                    java.lang.String password,
                    cn.jpush.im.api.BasicCallback callback)
                    */
                    final ProgressDialog pdilog = new ProgressDialog(LoginActivity.this);
                    pdilog.setMessage("正在努力登录中。。。");
                    pdilog.show();
                    JMessageClient.login(userAccount, passwordString, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            Log.i("login", "gotResult: "+userAccount+passwordString);
                            if (i==0){
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                pdilog.cancel();
                                final ProgressDialog pdilog1 = new ProgressDialog(LoginActivity.this);
                                pdilog1.setMessage("正在跳转。。。");
                                pdilog1.show();
                                //获取用户资料
                                JMessageClient.getUserInfo(userAccount, new GetUserInfoCallback() {
                                    @Override
                                    public void gotResult(int i, String s, UserInfo userInfo) {
                                        //判断头像或者昵称是否为空，为空则跳转到编辑头像昵称界面
                                        if (userInfo.getNickname().trim().length() == 0||userInfo.getAvatar()== null){
                                            pdilog1.cancel();
                                            Intent intent = new Intent(LoginActivity.this,FixProfileActivity.class);
                                            intent.putExtra("userId",userId);
                                            startActivity(intent);
                                        }else{
                                            pdilog1.cancel();

                                            /*Intent intent = new Intent(LoginActivity.this,MessageListActivity.class);
                                            startActivity(intent);*/
                                            RequestParams requestParams = new RequestParams(NetUtil.url+"LoginServlet");
                                            requestParams.addBodyParameter("userAccount",userAccount);
                                            requestParams.addBodyParameter("password",passwordString);
                                            x.http().post(requestParams, new Callback.CommonCallback<String>() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    Log.i("login", "onSuccess: "+result);
                                                    myApplication = (MyApplication) getApplication();
                                                    //在这里存入user对象到Application中
                                                    Gson gson = new Gson();
                                                    User user = gson.fromJson(result,User.class);
                                                    myApplication.setUser(user);
                                                    /*
                                                        修改和存储数据
                                                        根据Context的getSharedPrerences(key, [模式])方法获取SharedPreference对象；
                                                        利用SharedPreference的edit()方法获取Editor对象；
                                                        通过Editor的putXXX()方法，将键值对存储数据；
                                                        通过Editor的commit()方法将数据提交到SharedPreference内。
                                                     */
                                                    //存储到本地
                                                    SharedPreferences sp = LoginActivity.this.getSharedPreferences("USER",MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sp.edit();
                                                    editor.putString("user",result);
                                                    editor.commit();
                                                    setResult(ResultCode);
                                                    finish();
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
                                        }
                                    }
                                });
                            }else{
                                pdilog.cancel();
                                Toast.makeText(LoginActivity.this, "用户名与密码不匹配", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "填写不完整", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册页面的返回
        if (requestCode==CODE&&resultCode==RESULT_OK){
            //注册界面的用户名
            String name = data.getStringExtra("userName");
            Log.i("requestCode", "onActivityResult: "+name);
            userName.setText(name);
            //返回的userId
            userId = data.getStringExtra("userId");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (JMessageClient.getMyInfo()!=null) {
            if (JMessageClient.getMyInfo().getUserName().equals("1111")) {
                JMessageClient.logout();
            }
        }
    }


}
