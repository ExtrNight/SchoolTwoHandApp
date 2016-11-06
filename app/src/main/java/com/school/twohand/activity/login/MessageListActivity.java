package com.school.twohand.activity.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.DemoActivity;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ViewHolder;

import java.io.File;
import java.util.List;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class MessageListActivity extends AppCompatActivity {
    List<Conversation> conver;//聊天列表
    CommonAdapter<Conversation> commonAdapter;
    ListView chatUser;
    EditText findFriend;
    Button findFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        conver = JMessageClient.getConversationList();
        chatUser = (ListView)findViewById(R.id.messageList);
        findFriend = (EditText) findViewById(R.id.findFriend);
        findFriendButton = (Button) findViewById(R.id.findFriendButton);
        //找朋友的输入框输入，id，进入对应聊天界面
        if (findFriend.getText().toString()!=null){
            findFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JMessageClient.getUserInfo(findFriend.getText().toString(), new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0){
                                MyApplication myApplication = (MyApplication) getApplication();
                                //进入与你对话人的聊天
                                JMessageClient.enterSingleConversation(findFriend.getText().toString());
                                myApplication.setOtherAccount(findFriend.getText().toString());
                                //页面跳转到聊天室
                                Intent intent = new Intent(MessageListActivity.this,DemoActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            });
        }
        initMessage();
        //注册监听，监听有没有人发消息
        JMessageClient.registerEventReceiver(this);
    }

    /**
     * 将messages中的数据展示到listview
     */
    public void initMessage() {

        if (commonAdapter == null) {
            commonAdapter = new CommonAdapter<Conversation>(MessageListActivity.this,conver,R.layout.message_user_item) {
                @Override
                public void convert(ViewHolder viewHolder, Conversation conversation, int position) {
                    final TextView userName = viewHolder.getViewById(R.id.messageUserName);
                    TextView messageNumber = viewHolder.getViewById(R.id.messageNumber);
                    final ImageView userHead = viewHolder.getViewById(R.id.messageUserHead);

                    //获取id为conversation.getTargetId()的用户信息
                    JMessageClient.getUserInfo(conversation.getTargetId(), new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0){
                                userName.setText(userInfo.getNickname());
                                File file = userInfo.getAvatarFile();
                                if (file!=null){
                                    Uri uri = Uri.parse(file.toString());
                                    userHead.setImageURI(uri);
                                }
                            }
                        }
                    });
                    messageNumber.setText("未读: "+conversation.getUnReadMsgCnt()+"条");

                }
            };
            chatUser.setAdapter(commonAdapter);
        }else {
            commonAdapter.notifyDataSetChanged();
        }
        chatUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MessageListActivity.this, DemoActivity.class);
                //进入与你对话人的聊天
                MyApplication myApplication = (MyApplication) getApplication();
                JMessageClient.enterSingleConversation(conver.get(position).getTargetId());
                myApplication.setOtherAccount(conver.get(position).getTargetId());
                startActivity(intent);
            }
        });

    }


    /**
     * 如果有消息发送过来则回调该方法
     * @param event
     */
    public void onEventMainThread(MessageEvent event) {
        Message msg = event.getMessage();
        conver = JMessageClient.getConversationList();
        commonAdapter=null;
        switch (msg.getContentType()) {
            case text:
                initMessage();
                break;
        }
    }
}
