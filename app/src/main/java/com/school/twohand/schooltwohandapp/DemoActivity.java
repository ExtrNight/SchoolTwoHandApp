package com.school.twohand.schooltwohandapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class DemoActivity extends AppCompatActivity {

    @InjectView(R.id.content)
    ListView content;
    @InjectView(R.id.userInput)
    EditText userInput;
    @InjectView(R.id.userSend)
    Button userSend;

    @InjectView(R.id.bottom)
    RelativeLayout bottom;
    @InjectView(R.id.relative)
    RelativeLayout relative;


    MyApplication myApplication;
    CommonAdapter<Message> commonAdapter;
    List<Message> messages=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.inject(this);

        //获取application对象
        myApplication = (MyApplication) getApplication();
        //获取对象单聊记录，如果不为空就将所有的messages得到
        Log.i("converDe", "onCreate: "+ JMessageClient.getMyInfo());
        Conversation conversation = JMessageClient.getSingleConversation(myApplication.getOtherAccount());
        if (conversation != null) {
            messages = conversation.getAllMessage();
            //初始化消息显示在listview上
            initMessage();
        }
        //注册监听，监听有没有人发消息
        JMessageClient.registerEventReceiver(this);



        //判断是否输入消息，输入则按钮变成发送
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //长度不等于0 按钮text变成发送
                if(s.length()!=0){
                    userSend.setText("发送");
                }else if (s.length() == 0 && relative.getVisibility()==View.GONE){
                    userSend.setText("弹出");
                }else if(s.length() == 0 && relative.getVisibility()==View.VISIBLE){
                    userSend.setText("收回");
                }
            }
        });
    }

    /**
     * 将messages中的数据展示到listview
     */
    public void initMessage() {
        JMessageClient.getUserInfo(myApplication.getOtherAccount(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, final UserInfo userInfo) {
                if (i == 0){
                    //头像的获取，之后给控件赋值
                    File file = userInfo.getAvatarFile();
                    File file2 = JMessageClient.getMyInfo().getAvatarFile();
                    Uri otherUri = null;
                    Uri meUri = null;
                    if (file!=null){
                        otherUri = Uri.parse(file.toString());
                    }
                    if (file2!=null){
                        meUri = Uri.parse(file2.toString());
                    }
                    if (commonAdapter == null) {
                        final Uri finalOtherUri = otherUri;
                        final Uri finalMeUri = meUri;
                        commonAdapter = new CommonAdapter<Message>(DemoActivity.this, messages, R.layout.chat_item) {
                            @Override
                            public void convert(ViewHolder viewHolder, Message message, int position) {
                                //获取单聊内容
                                TextContent textContent = (TextContent) message.getContent();
                                //该消息的发送者
                                String name = message.getFromUser().getUserName();
                                //该消息的发送时间
                                long longTime  = message.getCreateTime();
                                Date date = new Date(longTime); // 根据long类型的毫秒数生命一个date类型的时间
                                String timeString = new SimpleDateFormat("HH:mm:ss").format(date);

                                //获取控件A为对方 ，B为自己
                                TextView tvA = viewHolder.getViewById(R.id.textA);//内容显示区
                                TextView tvB = viewHolder.getViewById(R.id.textB);
                                TextView userA = viewHolder.getViewById(R.id.userA);//用户名显示区
                                TextView userB = viewHolder.getViewById(R.id.userB);
                                TextView timeA = viewHolder.getViewById(R.id.timeA);//时间显示区
                                TextView timeB = viewHolder.getViewById(R.id.timeB);
                                ImageView leftImage= viewHolder.getViewById(R.id.leftImage);//头像
                                ImageView rightImage= viewHolder.getViewById(R.id.rightImage);
                                //定义布局用来判断是否显示
                                RelativeLayout leftRel = viewHolder.getViewById(R.id.leftRel);
                                RelativeLayout rightRel = viewHolder.getViewById(R.id.rightRel);

                                if (name.equals(myApplication.getOtherAccount())) {//显示对方消息内容在左边
                                    leftRel.setVisibility(View.VISIBLE);
                                    rightRel.setVisibility(View.GONE);
                                    //给控件赋值
                                    tvA.setText(textContent.getText());
                                    userA.setText(userInfo.getNickname());
                                    timeA.setText(timeString);
                                    leftImage.setImageURI(finalOtherUri);

                                } else if (name.equals(JMessageClient.getMyInfo().getUserName())) {//显示自己的消息内容在右边
                                    leftRel.setVisibility(View.GONE);
                                    rightRel.setVisibility(View.VISIBLE);
                                    //给控件赋值
                                    tvB.setText(textContent.getText());
                                    userB.setText(JMessageClient.getMyInfo().getNickname());
                                    timeB.setText(timeString);
                                    Log.i("ozr", "meUri: "+finalMeUri);
                                    rightImage.setImageURI(finalMeUri);
                                }
                            }
                        };
                        content.setAdapter(commonAdapter);
                    } else {
                        commonAdapter.notifyDataSetChanged();
                    }
                    //让listView总是在最底
                    content.smoothScrollToPosition(content.getCount() - 1);
                }
            }
        });


    }

    /**
     * 发送消息到对方
     */
    @OnClick({R.id.userSend,R.id.userInput})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userSend:
                //用户输入的内容
                String content = userInput.getText().toString();
                if (content.trim().length()!=0){
                    //发送用户输入的内容到指定用户
                    Message message = JMessageClient.createSingleTextMessage(myApplication.getOtherAccount(), "ca0553f78e499fcf3bee3982", content);
                    JMessageClient.sendMessage(message);
                    messages.add(message);
                    initMessage();
                    userInput.setText("");
                    break;
                }else{

                    if (relative.getVisibility()==View.GONE){
                        userSend.setText("收起");
                        //收起键盘
                        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                        }
                        relative.setVisibility(View.VISIBLE);
                       /* //判断布局是否弹出
                        DemoActivity.this.addOnSoftKeyBoardVisibleListener(DemoActivity.this, new IKeyBoardVisibleListener() {
                            @Override
                            public void onSoftKeyBoardVisible(boolean visible, int windowBottom) {
                                if (!visible && (relative.getVisibility()==View.GONE)&& flag == 1) {

                                    flag = 0;
                                }
                            }
                        });*/


                    }else if(relative.getVisibility()==View.VISIBLE){
                        userSend.setText("弹出");
                        relative.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.userInput:
                userSend.setText("弹出");
                relative.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * 如果有消息发送过来则回调该方法
     * @param event
     */
    public void onEventMainThread(MessageEvent event) {
        Message msg = event.getMessage();
        switch (msg.getContentType()) {
            case text:
                //处理文字消息
                messages.add(msg);
                initMessage();
                break;
        }
    }

    //计算键盘高度,和是否被点击
    interface IKeyBoardVisibleListener{
        void onSoftKeyBoardVisible(boolean visible, int windowBottom);
    }

    boolean isVisiableForLast = false;
    public void addOnSoftKeyBoardVisibleListener(Activity activity, final IKeyBoardVisibleListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                //计算出可见屏幕的高度
                int displayHight = rect.bottom - rect.top;
                //获得屏幕整体的高度
                int hight = decorView.getHeight();
                //获得键盘高度
                int keyboardHeight = hight-displayHight;
                boolean visible = (double) displayHight / hight < 0.8;
                if(visible != isVisiableForLast){
                    listener.onSoftKeyBoardVisible(visible,keyboardHeight );
                }
                isVisiableForLast = visible;
            }
        });
    }
}
