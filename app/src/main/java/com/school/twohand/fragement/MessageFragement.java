package com.school.twohand.fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.DemoActivity;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CircleImageView;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import cn.jpush.im.android.api.JMessageClient;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MessageFragement extends Fragment {
    CommonAdapter<Conversation> commonAdapter;
    ListView chatUser;
    MyApplication exampleApplication;
    List<Conversation> conver = new ArrayList<>();

    //定时器Timer和TimerTask 参考 http://www.jb51.net/article/85040.htm


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                //受到消息后 刷新数据initMessage
                if (conver !=null) {
                    if (conver.size() != 0) {
                        initMessage();
                    }
                }
            }
        }
    };
    private Timer timer = new Timer();

    //定时要做的事情，发送给handle
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            msg.what = 1;
            handler.sendEmptyMessage(1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, null);
        chatUser = (ListView) view.findViewById(R.id.chatUser);

        //获取application对象
        exampleApplication = (MyApplication) getActivity().getApplication();

        //从本地取用户信息
        /*
        同样根据Context对象获取SharedPreference对象；
        直接使用SharedPreference的getXXX(key)方法获取数据。
         */
        SharedPreferences sp = getActivity().getSharedPreferences("USER",getActivity().MODE_PRIVATE);
        String userString = sp.getString("user",null);
        Log.i("MyApplication", "onCreate: "+userString);
        if (userString!=null){
            Gson gson = new Gson();
            User user = gson.fromJson(userString,User.class);
            exampleApplication.setUser(user);
            JMessageClient.login(user.getUserAccount(), user.getUserPassword(), new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (JMessageClient.getConversationList()!=null){
                        conver = JMessageClient.getConversationList();
                        initMessage();
                    }
                }
            });
        }


        //定时
        dingShi(getView());

        //注册监听，监听有没有人发消息
        JMessageClient.registerEventReceiver(this);


        return view;

    }

    /**
     * 将messages中的数据展示到listview
     */
    public void initMessage() {
        Log.i("MessageFragement", "initMessage: "+conver);
        if (conver!=null) {
            if (commonAdapter == null) {
                commonAdapter = new CommonAdapter<Conversation>(getActivity(), conver, R.layout.message_user_item) {
                    @Override
                    public void convert(ViewHolder viewHolder, Conversation conversation, int position) {
                        //Conversation{type=single, targetId='18906992571', latestText='明年', latestType=text, lastMsgDate=1478252235004, unReadMsgCnt=2, msgTableName='msg337395036', targetAppkey='530b86b0928b7315c440867b'}
                        //Log.i("message", "usermessage: " + conversation);
                        //找控件
                        if (conversation.getType()==ConversationType.single) {
                            final ImageView head = viewHolder.getViewById(R.id.messageUserHead);//用户头像
                            final TextView userName = viewHolder.getViewById(R.id.messageUserName);//用户名
                            TextView lastMessage = viewHolder.getViewById(R.id.lastMessageContent);//最后一条消息内容
                            TextView time = viewHolder.getViewById(R.id.timeText);//时间显示
                            TextView messageNumber = viewHolder.getViewById(R.id.messageNumber);//消息数量
                            CircleImageView circleImageView = viewHolder.getViewById(R.id.circle);//红圆圈
                            //给控件赋值

                            //获取对话列表用户信息,用户头像赋值,用户昵称赋值
                            String userId = conversation.getTargetId();
                            JMessageClient.getUserInfo(userId, new GetUserInfoCallback() {
                                @Override
                                public void gotResult(int i, String s, UserInfo userInfo) {
                                    if (i == 0) {
                                        userName.setText(userInfo.getNickname());
                                        userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                            @Override
                                            public void gotResult(int i, String s, Bitmap bitmap) {
                                                if (i == 0) {
                                                    //获取到头像
                                                    head.setImageBitmap(bitmap);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            //最后一条聊天记录
                            lastMessage.setText(conversation.getLatestText());
                            //最后一条聊天记录的时间
                            time.setText(passTime(conversation.getLastMsgDate()));
                            //未回复的消息数量
                            if (conversation.getUnReadMsgCnt() == 0) {
                                circleImageView.setVisibility(View.INVISIBLE);
                                messageNumber.setVisibility(View.INVISIBLE);
                            } else {
                                circleImageView.setVisibility(View.VISIBLE);
                                messageNumber.setVisibility(View.VISIBLE);
                                messageNumber.setText(conversation.getUnReadMsgCnt() + "");
                            }
                        }
                    }
                };
                chatUser.setAdapter(commonAdapter);
            } else {
                commonAdapter.notifyDataSetChanged();
            }
            chatUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DemoActivity.class);
                    //intent.putExtra("userId",conver.get(position).getTargetId());
                    //Log.i("userId", "onItemClick: "+conver.get(position).getTargetId());
                    exampleApplication.setOtherAccount(conver.get(position).getTargetId());
                    Log.i("userId", "onItemClick: "+exampleApplication.getOtherAccount());
                    JMessageClient.enterSingleConversation(conver.get(position).getTargetId());
                    startActivity(intent);
                }
            });
        }
    }



    /**
     * 如果有消息发送过来则回调该方法
     *
     * @param event
     */
    public void onEventMainThread(MessageEvent event) {
        Message msg = event.getMessage();
        switch (msg.getContentType()) {

            case text:
                if (msg.getTargetType()== ConversationType.single){
                    conver.clear();
                    conver.addAll(JMessageClient.getConversationList());
                    //commonAdapter = null;
                    initMessage();
                    break;
                }
        }
    }


    /**
     * long类型时间
     * 不足一个小时的，只取分钟。
     * 不足一天的，大于一小时的，只取小时。
     * 不足一个月的，大于一天的，只取天。
     * 不足一年的，大于一个月的，只取月。
     */
    public String passTime(Long longTime) {
        Calendar newCalendar = Calendar.getInstance();//声明日历类，当前系统时间

        Calendar oldCalendar = Calendar.getInstance();//最后一条记录时间
        oldCalendar.setTime(new Date(longTime));
        if (newCalendar.get(Calendar.YEAR) > oldCalendar.get(Calendar.YEAR)) {
            return longTimeToStringTime(longTime);//如果相隔时间超过一年显示准确时间
        } else if (newCalendar.get(Calendar.MONTH) > oldCalendar.get(Calendar.MONTH)) {
            return (newCalendar.get(Calendar.MONTH) - oldCalendar.get(Calendar.MONTH)) + "月前";//不足一年的，大于一个月的，只取月。
        } else if (newCalendar.get(Calendar.DAY_OF_MONTH) > oldCalendar.get(Calendar.DAY_OF_MONTH)) {
            return (newCalendar.get(Calendar.DAY_OF_MONTH) - oldCalendar.get(Calendar.DAY_OF_MONTH)) + "天前";//不足一个月的，大于一天的，只取天。
        } else if (newCalendar.get(Calendar.HOUR) > oldCalendar.get(Calendar.HOUR)) {
            return (newCalendar.get(Calendar.HOUR) - oldCalendar.get(Calendar.HOUR)) + "小时前";//不足一天的，大于一小时的，只取小时。
        } else if (newCalendar.get(Calendar.MINUTE) > oldCalendar.get(Calendar.MINUTE)) {
            return (newCalendar.get(Calendar.MINUTE) - oldCalendar.get(Calendar.MINUTE)) + "分钟前";//不足一个小时的，只取分钟。
        } else {
            return "刚刚";
        }
    }


    /**
     * 将long类型的时间转化成String类型的工具类
     */
    public String longTimeToStringTime(Long longTime) {
        //将long类型转化成Date时间类型
        Date date = new Date(longTime);
        String timeStr = "";//时间字符串
        try {
            //formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //进行格式化,转化成str
            timeStr = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    /**
     * 开启定时
     */
    public void dingShi(View view) {
        //后面的两个参数为第一次执行的时间，后面的是间隔时间
        timer.schedule(timerTask, 1000, 1000);
    }


    //取消定时
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    //重新开启定时
    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
    }
}
