package com.school.twohand.activity.message;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.school.twohand.activity.GoPayActivity;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class ChatActivity extends AppCompatActivity {

    ListView content;
    EditText userInput;
    Button userSend;
    TextView pay;
    TextView chatGoodsPrice;
    MyApplication myApplication;
    CommonAdapter<Message> commonAdapter;
    ImageView chatGoodsImage;
    List<Message> messages = new ArrayList<>();
    TextView chatTitleName;
    String goodsMessage;
    String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        content = (ListView) findViewById(R.id.chatContent);
        userInput = (EditText) findViewById(R.id.chatInput);
        userSend = (Button) findViewById(R.id.chatSend);
        pay = (TextView) findViewById(R.id.iWantTo);
        chatGoodsPrice = (TextView) findViewById(R.id.chatGoodsPrice);
        chatGoodsImage = (ImageView) findViewById(R.id.chatGoodsImage);
        chatTitleName = (TextView) findViewById(R.id.chatTitleName);
        //上一个页面传来的值
        Intent intent1 = getIntent();
        String image = intent1.getStringExtra("image");
        String price = intent1.getStringExtra("price");
        String userName = intent1.getStringExtra("userName");
        goodsMessage = intent1.getStringExtra("goodsMessage");
        position = intent1.getStringExtra("position");

        //给控件赋值
        chatGoodsPrice.setText(" ￥ " + price);
        String goodsUrl = NetUtil.imageUrl + image;
        ImageOptions goodsImageOptions = new ImageOptions.Builder()
                .build();
        x.image().bind(chatGoodsImage, goodsUrl, goodsImageOptions);
        chatTitleName.setText(userName);
        chatGoodsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //获取application对象
        myApplication = (MyApplication) getApplication();
        //获取对象单聊记录，如果不为空就将所有的messages得到
        Conversation conversation = JMessageClient.getSingleConversation(myApplication.getOtherAccount());
        if (conversation != null) {
            messages = conversation.getAllMessage();
            //初始化消息显示在listview上
            initMessage();
        }
        //注册监听，监听有没有人发消息
        JMessageClient.registerEventReceiver(this);
        userSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果为空就不发送消息
                if (userInput.getText().toString().trim().length() == 0) {
                    Toast.makeText(ChatActivity.this, "还未输入内容哦", Toast.LENGTH_SHORT).show();
                } else {
                    //用户输入的内容
                    String content = userInput.getText().toString();
                    //发送用户输入的内容到指定用户
                    Message message = JMessageClient.createSingleTextMessage(myApplication.getOtherAccount(), "530b86b0928b7315c440867b", content);
                    JMessageClient.sendMessage(message);
                    messages.add(message);
                    initMessage();
                    userInput.setText("");
                }
            }
        });
        //付款
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, GoPayActivity.class);
                //将商品详情信息传入订单界面
                intent.putExtra("goodsMessage", goodsMessage);
                intent.putExtra("position", position);
                startActivity(intent);
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
                if (i == 0) {
                    //头像的获取，之后给控件赋值
                    File file = userInfo.getAvatarFile();
                    File file2 = JMessageClient.getMyInfo().getAvatarFile();
                    Uri otherUri = null;
                    Uri meUri = null;
                    if (file != null) {
                        otherUri = Uri.parse(file.toString());
                    }
                    if (file2 != null) {
                        meUri = Uri.parse(file2.toString());
                    }
                    if (commonAdapter == null) {
                        final Uri finalOtherUri = otherUri;
                        final Uri finalMeUri = meUri;
                        commonAdapter = new CommonAdapter<Message>(ChatActivity.this, messages, R.layout.chat_item) {
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
                }
            }
        });
        //让listView总是在最底
        content.smoothScrollToPosition(content.getCount() - 1);

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
                //处理文字消息
                messages.add(msg);
                initMessage();
                break;
        }
    }
}
