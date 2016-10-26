package com.school.twohand.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.DemoActivity;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ViewHolder;

import java.util.List;


import cn.jpush.im.android.api.JMessageClient;

import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MessageFragement extends Fragment {
    CommonAdapter<Conversation> commonAdapter;
    ListView chatUser;
    MyApplication exampleApplication;
    List<Conversation> conver;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment,null);
        chatUser = (ListView) view.findViewById(R.id.chatUser);

        //获取application对象
        exampleApplication = (MyApplication) getActivity().getApplication();
        conver = JMessageClient.getConversationList();
        Log.i("diannao", "onCreateView: "+conver);
        //注册监听，监听有没有人发消息

        JMessageClient.registerEventReceiver(this);

        initMessage();
        return view;

    }
    /**
     * 将messages中的数据展示到listview
     */
    public void initMessage() {

        if (commonAdapter == null) {
            commonAdapter = new CommonAdapter<Conversation>(getActivity(),conver,R.layout.chat_user_item) {
                @Override
                public void convert(ViewHolder viewHolder, Conversation conversation, int position) {
                    TextView userName = viewHolder.getViewById(R.id.userName);
                    TextView messageNumber = viewHolder.getViewById(R.id.messageNumber);
                    userName.setText(conversation.getTargetId());
                    messageNumber.setText(conversation.getUnReadMsgCnt()+"");
                }
            };
            chatUser.setAdapter(commonAdapter);
        }else {
            commonAdapter.notifyDataSetChanged();
        }
        chatUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DemoActivity.class);
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
        switch (msg.getContentType()) {
            case text:
                initMessage();
                break;
        }
    }
}
