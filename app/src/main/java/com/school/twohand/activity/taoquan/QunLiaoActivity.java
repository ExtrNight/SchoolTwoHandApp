package com.school.twohand.activity.taoquan;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class QunLiaoActivity extends AppCompatActivity {
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
    Long groupId = 0L;

    MyApplication myApplication;
    CommonAdapter<Bean> commonAdapter;
    List<Message> messages=new ArrayList<>();
    //bean包装的对话内容
    List<Bean> beans = new ArrayList<>();

    private class Bean{
        String fromName;
        String fromId ;
        Long creaTime ;
        String text ;

        public String getFromName() {
            return fromName;
        }

        public void setFromName(String fromName) {
            this.fromName = fromName;
        }

        public String getFromId() {
            return fromId;
        }

        public void setFromId(String fromId) {
            this.fromId = fromId;
        }

        public Long getCreaTime() {
            return creaTime;
        }

        public void setCreaTime(Long creaTime) {
            this.creaTime = creaTime;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Bean(String fromName, String fromId, Long creaTime, String text) {
            this.fromName = fromName;
            this.fromId = fromId;
            this.creaTime = creaTime;
            this.text = text;
        }

        public Bean() {
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "fromName='" + fromName + '\'' +
                    ", fromId='" + fromId + '\'' +
                    ", creaTime=" + creaTime +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.inject(this);
       /* public static Message createGroupTextMessage(long groupID,
        java.lang.String text)*/
        //获取上界面传来的群id
        Intent intent = getIntent();
        String groupIdString = intent.getStringExtra("groupId");
        groupId = Long.parseLong(groupIdString);

         //获取application对象
        myApplication = (MyApplication) getApplication();

        //获取对象单聊记录，如果不为空就将所有的messages得到
        Conversation conversation = JMessageClient.getGroupConversation(groupId);
        /*JMessageClient.getGroupInfo(groupId, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int i, String s, GroupInfo groupInfo) {
               // Log.i("conversation", "gotResult: "+groupInfo);
            }
        });*/
        if (conversation != null) {
            messages = conversation.getAllMessage();
            //初始化消息显示在listview上
            //initMessage();
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    // 写子线程中的操作
                }
            }).start();*/

            for (int i = 0; i<messages.size();i++){
                //Log.i("conversation", "onCreate0: "+messages.get(i).getFromName()+messages.get(i).getFromID());
                String fromName = messages.get(i).getFromName();
                String fromId = messages.get(i).getFromID();
                Long creaTime = messages.get(i).getCreateTime();
                String text = ((TextContent)(messages.get(i).getContent())).getText();
                Bean bean = new Bean(fromName, fromId,creaTime, text);
                beans.add(bean);
               /*  Gson gson = new Gson();
               Map<String,Object> hashMap = gson.fromJson(messages.get(i).getContent().toJson(),new TypeToken<Map<String,Object>>(){}.getType());
                if (((ArrayList<String>)hashMap.get("userDisplayNames"))!=null) {
                    Log.i("conversation", "onCreate3: " + ((ArrayList<String>) hashMap.get("userDisplayNames")).get(0));
                    Log.i("conversation", "onCreate4: " + ((ArrayList<String>) hashMap.get("userNames")).get(0));
                }*/
                //Log.i("conversation", "onCreate1: "+(messages.get(i).getContent()));
                //Message message = conversation.getAllMessage().get(i);
                /*TextContent textContent = (TextContent) message.getContent();*/
                //Log.i("conversation", "onCreate: "+conversation.getAllMessage());
                //Log.i("conversation", "onCreate2: "+messages.get(i).getCreateTime());
                //Log.i("conversation", "onCreate2: "+((TextContent)(messages.get(i).getContent())).getText());

            }
            Log.i("conversation", "onCreate: "+beans);
            /**
             *  Message{_id=1,
             *  messageId=0,
             *  direct=receive,
             *  status=created,
             *  content={"containsGroupOwner":false,"eventNotificationType":"group_member_added","groupID":10463487,"operator":18132219,"groupMemberUserNames":[],"userDisplayNames":["秦孤寂"],"userNames":["18906992571"],"extras":{}},
             *  version=1,
             *  fromName='',
             *  contentType=eventNotification,
             *  contentTypesString='eventNotification',
             *  createTimeInMillis=1478463828000,
             *  targetType=group,
             *  targetID='10463487',
             *  targetName='',
             *  fromType='user',
             *  fromID=系统消息,
             *  notification=null}
             */
            /*Iterator iter = map.keySet().iterator();
            while (iter.hasNext()){
                String key = (String) iter.next();
                String val = map.get("key");
                Log.i("conversation", "onCreate2: "+key+val);
            }*/

        }

        initMessage();
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

                    if (commonAdapter == null) {

                        commonAdapter = new CommonAdapter<Bean>(QunLiaoActivity.this, beans, R.layout.chat_item) {
                            @Override
                            public void convert(ViewHolder viewHolder, Bean bean, int position) {
                                //获取单聊内容
                                String singleText = bean.getText();
                                //该消息的发送者
                                String name = bean.getFromName();
                                //该消息的发送时间
                                long longTime  = bean.getCreaTime();
                                Date date = new Date(longTime); // 根据long类型的毫秒数生命一个date类型的时间
                                String timeString = new SimpleDateFormat("HH:mm:ss").format(date);


                                //获取控件A为对方 ，B为自己
                                TextView tvA = viewHolder.getViewById(R.id.textA);//内容显示区
                                TextView tvB = viewHolder.getViewById(R.id.textB);
                                TextView userA = viewHolder.getViewById(R.id.userA);//用户名显示区
                                TextView userB = viewHolder.getViewById(R.id.userB);
                                TextView timeA = viewHolder.getViewById(R.id.timeA);//时间显示区
                                TextView timeB = viewHolder.getViewById(R.id.timeB);
                                final ImageView leftImage= viewHolder.getViewById(R.id.leftImage);//头像
                                final ImageView rightImage= viewHolder.getViewById(R.id.rightImage);
                                //定义布局用来判断是否显示
                                RelativeLayout leftRel = viewHolder.getViewById(R.id.leftRel);
                                RelativeLayout rightRel = viewHolder.getViewById(R.id.rightRel);

                                if (!name.equals(myApplication.getUser().getUserName())) {//显示对方消息内容在左边
                                    leftRel.setVisibility(View.VISIBLE);
                                    rightRel.setVisibility(View.GONE);
                                    //给控件赋值
                                    tvA.setText(singleText);
                                    userA.setText(name);
                                    timeA.setText(timeString);
                                    JMessageClient.getUserInfo(bean.fromId, new GetUserInfoCallback() {
                                        @Override
                                        public void gotResult(int i, String s, UserInfo userInfo) {
                                            if (i == 0){
                                                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                                    @Override
                                                    public void gotResult(int i, String s, Bitmap bitmap) {
                                                        if (i == 0){
                                                            leftImage.setImageBitmap(bitmap);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                } else{//显示自己的消息内容在右边
                                    leftRel.setVisibility(View.GONE);
                                    rightRel.setVisibility(View.VISIBLE);
                                    //给控件赋值
                                    tvB.setText(singleText);
                                    userB.setText(name);
                                    timeB.setText(timeString);
                                    JMessageClient.getUserInfo(bean.fromId, new GetUserInfoCallback() {
                                        @Override
                                        public void gotResult(int i, String s, UserInfo userInfo) {
                                            if (i == 0){
                                                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                                    @Override
                                                    public void gotResult(int i, String s, Bitmap bitmap) {
                                                        if (i == 0){
                                                            rightImage.setImageBitmap(bitmap);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
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
                    Message message = JMessageClient.createGroupTextMessage(groupId,content);
                    JMessageClient.sendMessage(message);
                    String fromName = message.getFromName();
                    String fromId = message.getFromID();
                    Long creaTime = message.getCreateTime();
                    String text = ((TextContent)(message.getContent())).getText();
                    Bean bean = new Bean(fromName, fromId,creaTime, text);
                    beans.add(bean);
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
                if (msg.getTargetID().equals(groupId.toString())) {
                    //处理文字消息
                    String fromName = msg.getFromName();
                    String fromId = msg.getFromID();
                    Long creaTime = msg.getCreateTime();
                    String text = ((TextContent) (msg.getContent())).getText();
                    Bean bean = new Bean(fromName, fromId, creaTime, text);
                    beans.add(bean);

                    initMessage();
                    break;
                }
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
