package com.school.twohand.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.message.ChatActivity;
import com.school.twohand.entity.Goods;
import com.school.twohand.entity.LikeTbl;
import com.school.twohand.entity.MessageBoard;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class DetailGoodsActivity extends AppCompatActivity {


    @InjectView(R.id.view)
    Toolbar view;
    @InjectView(R.id.message_detail)
    Button messageDetail;
    @InjectView(R.id.like_message)
    Button likeMessage;
    @InjectView(R.id.iWantTo)
    TextView iWantTo;
    @InjectView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @InjectView(R.id.say)
    EditText say;
    @InjectView(R.id.send)
    Button send;
    @InjectView(R.id.sayLinearLayout)
    LinearLayout sayLinearLayout;
    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.returnRel)
    Button returnRel;
    ImageView goodsImageA;
    ImageView goodsImageB;
    ImageView goodsImageC;
    ImageView headImageA;
    ImageView headImageB;
    ImageView headImageC;
    CommonAdapter<MessageBoard> commonAdapter;
    List<Goods> goodsMessages;
    List<MessageBoard> messageBoards;
    Goods goods;//显示在头部
    int position;
    View headView;
    TextView likeNumber;
     MessageBoard messageBoard = new MessageBoard();
    MyApplication exampleApplication ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_goods);
        ButterKnife.inject(this);
        //获取到上界面传来的商品详情
        Intent intent = getIntent();
        String goodsMessageString = intent.getStringExtra("goodsMessage");
        position = intent.getIntExtra("position",0)-1;
        Gson gson = new Gson();
        goodsMessages = gson.fromJson(goodsMessageString, new TypeToken<List<Goods>>() {
        }.getType());
        goods = goodsMessages.get(position);

        //数据库获取对应商品留言表
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryMessageServlet");
        requestParams.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                messageBoards = gson.fromJson(result,new TypeToken<List<MessageBoard>>(){}.getType());
                initData();
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
        initHeadView();
        listView.addHeaderView(headView);
    }
    public void initHeadView(){
        if (goods!=null){
            headView =  LayoutInflater.from(this).inflate(R.layout.detail_goods,null);
            ImageView headImage = (ImageView) headView.findViewById(R.id.user_head_m);
            TextView userName = (TextView) headView.findViewById(R.id.user_name_m);
            TextView goodsPrice = (TextView) headView.findViewById(R.id.goods_price_m);
            TextView goodsDetail = (TextView) headView.findViewById(R.id.detail_m);
            goodsImageA = (ImageView) headView.findViewById(R.id.image_a_m);
            goodsImageB = (ImageView) headView.findViewById(R.id.image_b_m);
            goodsImageC = (ImageView) headView.findViewById(R.id.image_c_m);
            headImageA = (ImageView) headView.findViewById(R.id.image_h_a_m);
            headImageB = (ImageView) headView.findViewById(R.id.image_h_b_m);
            headImageC = (ImageView) headView.findViewById(R.id.image_h_c_m);
            likeNumber = (TextView) headView.findViewById(R.id.likeNumber_m);
            TextView amoyName = (TextView) headView.findViewById(R.id.amoy_name_m);
            final TextView readNumber = (TextView) headView.findViewById(R.id.readNumber_m);
            //服务器找头像
            String headUrl = NetUtil.imageUrl+goods.getGoodsUser().getUserHead();
            ImageOptions headImageOptions = new ImageOptions.Builder()
                    .setCircular(true)
                    .build();
            x.image().bind(headImage,headUrl,headImageOptions);
            //用户名
            userName.setText(goods.getGoodsUser().getUserName());
            //商品价格
            if (goods.getGoodsAuction()==0){
                goodsPrice.setText("￥"+goods.getGoodsPrice()+"");
            }
            if (goods.getGoodsAuction()==1){
                goodsPrice.setText("起拍￥"+goods.getGoodsPrice()+"");
                iWantTo.setText("出价");
            }

            //商品详情
            goodsDetail.setText("<"+goods.getGoodsTitle()+">"+goods.getGoodsDescribe());
            //服务器找商品图片信息
            for (int i = 0 ; i < goods.getGoodsImages().size();i++) {
                String goodsUrl = NetUtil.imageUrl + goods.getGoodsImages().get(i).getImageAddress();
                ImageOptions goodsImageOptions = new ImageOptions.Builder()
                        .build();
                if (i == 0){
                    x.image().bind(goodsImageA, goodsUrl, goodsImageOptions);
                    goodsImageA.setVisibility(View.VISIBLE);
                }
                if (i == 1){
                    x.image().bind(goodsImageB, goodsUrl, goodsImageOptions);
                    goodsImageB.setVisibility(View.VISIBLE);
                }
                if (i == 2){
                    x.image().bind(goodsImageC, goodsUrl, goodsImageOptions);
                    goodsImageC.setVisibility(View.VISIBLE);
                }
            }
            //点赞数
            likeNumber.setText("点赞:"+goods.getGoodsLikes().size());
            //浏览数量加一
            RequestParams requestParams = new RequestParams(NetUtil.url+"AddGoodsPVServlet");
            MyApplication myApplication = (MyApplication) getApplication();
            requestParams.addQueryStringParameter("userId",myApplication.getUser().getUserId()+"");
            requestParams.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    readNumber.setText("浏览:"+result);
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
            //淘圈
            amoyName.setText("淘圈|"+goods.getGoodsAmoyCircle().getCircleName());
            //点赞重新服务器获取
            RequestParams requestParams2 = new RequestParams(NetUtil.url+"QueryZZZZServlet");
            requestParams2.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
            x.http().get(requestParams2, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    //点赞人的头像
                    Gson gson = new Gson();
                    List<LikeTbl> likeTbls = gson.fromJson(result,new TypeToken<List<LikeTbl>>(){}.getType());
                    likeNumber.setText("点赞"+likeTbls.size());

                    for (int i = 0 ; i < likeTbls.size();i++) {
                        String goodsUrl = NetUtil.imageUrl +  likeTbls.get(i).getLikeUserMe().getUserHead();
                        Log.i("likeMessage", "onSuccess: "+goodsUrl);
                        ImageOptions goodsImageOptions = new ImageOptions.Builder()
                                .setCircular(true)
                                .build();
                        if (i == 0){
                            x.image().bind(headImageA, goodsUrl, goodsImageOptions);
                            headImageA.setVisibility(View.VISIBLE);
                        }
                        if (i == 1){
                            x.image().bind(headImageB, goodsUrl, goodsImageOptions);
                            headImageB.setVisibility(View.VISIBLE);
                        }
                        if (i == 2){
                            x.image().bind(headImageC, goodsUrl, goodsImageOptions);
                            headImageC.setVisibility(View.VISIBLE);
                        }
                        if (likeTbls.get(i).getLikeUserMe().getUserId()==((MyApplication)getApplication()).getUser().getUserId()){
                             likeMessage.setSelected(true);
                            Log.i("likeMessage", "onSuccess: setSelected");
                        }
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




        }
    }


    public void initData(){

        //留言
        if (commonAdapter == null){
            commonAdapter = new CommonAdapter<MessageBoard>(this,messageBoards,R.layout.message_board_item) {
                @Override
                public void convert(ViewHolder viewHolder, MessageBoard messageBoard, int position) {
                    ImageView headView = viewHolder.getViewById(R.id.message_user_head);
                    TextView headName = viewHolder.getViewById(R.id.message_user_name);
                    TextView messagedetail = viewHolder.getViewById(R.id.message_content);
                    //留言头像获取
                    String headImageUrl= NetUtil.imageUrl+messageBoard.getMessageBoardUserMe().getUserHead();
                    ImageOptions imageOptions = new ImageOptions.Builder()
                            .setCircular(true)
                            .build();
                    x.image().bind(headView,headImageUrl,imageOptions);
                    //留言用户名
                    headName.setText(messageBoard.getMessageBoardUserMe().getUserName());
                    //留言内容
                    String content = messageBoard.getMessageBoardContent();

                    if (messageBoard.getMessageBoardUserOther()!=null){
                        messagedetail.setText("@"+messageBoard.getMessageBoardUserOther().getUserName()+" "+content);
                    }else {
                        messagedetail.setText(content);
                    }
                }
            };
            listView.setAdapter(commonAdapter);


        }else{
            commonAdapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (position==0){
                    say.setHint("");
                }else{
                    say.setHint("@"+messageBoards.get(position-1).getMessageBoardUserMe().getUserName());

                }
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageBoard.setMessageBoardUserMe(((MyApplication)getApplication()).getUser());
                        Log.i("likeMessage", "onClick: "+position);
                        if (position==0){
                            messageBoard.setMessageBoardUserOther(null);
                        }else {
                            messageBoard.setMessageBoardUserOther(messageBoards.get(position-1).getMessageBoardUserMe());
                        }

                        Goods goods1 =new Goods();
                        goods1.setGoodsId(goods.getGoodsId());
                        messageBoard.setMessageBoardGoods(goods1);
                        messageBoard.setMessageBoardContent(say.getText().toString());
                        Gson gson1 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        String messageBoardString = gson1.toJson(messageBoard);
                        RequestParams requestParams = new RequestParams(NetUtil.url+"AddMessageServlet");
                        requestParams.addQueryStringParameter("messageBoard",messageBoardString);
                        x.http().get(requestParams, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                say.setText("");
                                RequestParams requestParams = new RequestParams(NetUtil.url+"QueryMessageServlet");
                                requestParams.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
                                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Gson gson = new Gson();
                                        List<MessageBoard> newMessageBoards = gson.fromJson(result,new TypeToken<List<MessageBoard>>(){}.getType());
                                        messageBoards.clear();
                                        messageBoards.addAll(newMessageBoards);
                                        initData();
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
                });
                sayLinearLayout.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);

            }
        });




    }

    @OnClick({R.id.message_detail, R.id.returnRel,R.id.send,R.id.like_message,R.id.iWantTo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_detail:
                relativeLayout.setVisibility(View.INVISIBLE);
                sayLinearLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.returnRel:
                relativeLayout.setVisibility(View.VISIBLE);
                sayLinearLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.send:
                say.setHint("");
                messageBoard.setMessageBoardUserMe(((MyApplication)getApplication()).getUser());
                Goods goods1 =new Goods();
                goods1.setGoodsId(goods.getGoodsId());
                messageBoard.setMessageBoardGoods(goods1);
                messageBoard.setMessageBoardContent(say.getText().toString());
                Gson gson1 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                String messageBoardString = gson1.toJson(messageBoard);
                RequestParams requestParams = new RequestParams(NetUtil.url+"AddMessageServlet");
                requestParams.addQueryStringParameter("messageBoard",messageBoardString);
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        //从数据库拿最新的消息记录
                        say.setText("");
                        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryMessageServlet");
                        requestParams.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
                        x.http().get(requestParams, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Gson gson = new Gson();
                                List<MessageBoard> newMessageBoards = gson.fromJson(result,new TypeToken<List<MessageBoard>>(){}.getType());
                                messageBoards.clear();
                                messageBoards.addAll(newMessageBoards);

                                initData();
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
                break;
            case R.id.like_message:
                //取消赞
                if (likeMessage.isSelected()){


                    RequestParams requestParams2 = new RequestParams(NetUtil.url+"ZZZZZZServlet");
                    requestParams2.addQueryStringParameter("userMeId",((MyApplication)getApplication()).getUser().getUserId()+"");
                    requestParams2.addQueryStringParameter("likeOtherId",goods.getGoodsUser().getUserId()+"");
                    requestParams2.addQueryStringParameter("likeGoodsId",goods.getGoodsId()+"");
                    requestParams2.addQueryStringParameter("flag",2+"");
                    x.http().get(requestParams2, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {

                            RequestParams requestParams2 = new RequestParams(NetUtil.url+"QueryZZZZServlet");
                            requestParams2.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
                            x.http().get(requestParams2, new CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    likeMessage.setSelected(false);
                                    //点赞人的头像
                                    Gson gson = new Gson();
                                    List<LikeTbl> likeTbls = gson.fromJson(result,new TypeToken<List<LikeTbl>>(){}.getType());
                                    Log.i("likeMessage", "onSuccess: "+likeTbls);
                                    headImageA.setVisibility(View.GONE);
                                    headImageB.setVisibility(View.GONE);
                                    headImageC.setVisibility(View.GONE);
                                    likeNumber.setText("点赞"+likeTbls.size());
                                    for (int i = 0 ; i < likeTbls.size();i++) {
                                        String goodsUrl = NetUtil.imageUrl +  likeTbls.get(i).getLikeUserMe().getUserHead();
                                        Log.i("likeMessage", "onSuccess: "+goodsUrl);
                                        ImageOptions goodsImageOptions = new ImageOptions.Builder()
                                                .setCircular(true)
                                                .build();
                                        if (i == 0){
                                            x.image().bind(headImageA, goodsUrl, goodsImageOptions);
                                            headImageA.setVisibility(View.VISIBLE);
                                        }
                                        if (i == 1){
                                            x.image().bind(headImageB, goodsUrl, goodsImageOptions);
                                            headImageB.setVisibility(View.VISIBLE);
                                        }
                                        if (i == 2){
                                            x.image().bind(headImageC, goodsUrl, goodsImageOptions);
                                            headImageC.setVisibility(View.VISIBLE);
                                        }
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
                    //赞

                    RequestParams requestParams2 = new RequestParams(NetUtil.url+"ZZZZZZServlet");
                    requestParams2.addQueryStringParameter("userMeId",((MyApplication)getApplication()).getUser().getUserId()+"");
                    requestParams2.addQueryStringParameter("likeOtherId",goods.getGoodsUser().getUserId()+"");
                    requestParams2.addQueryStringParameter("likeGoodsId",goods.getGoodsId()+"");
                    requestParams2.addQueryStringParameter("flag",1+"");
                    x.http().get(requestParams2, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {

                            RequestParams requestParams2 = new RequestParams(NetUtil.url+"QueryZZZZServlet");
                            requestParams2.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
                            x.http().get(requestParams2, new CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    likeMessage.setSelected(true);

                                    //点赞人的头像
                                    Gson gson = new Gson();
                                    List<LikeTbl> likeTbls = gson.fromJson(result,new TypeToken<List<LikeTbl>>(){}.getType());
                                    likeNumber.setText("点赞"+likeTbls.size());
                                    Log.i("likeMessage", "onSuccess: "+likeTbls);
                                    headImageA.setVisibility(View.GONE);
                                    headImageB.setVisibility(View.GONE);
                                    headImageC.setVisibility(View.GONE);
                                    for (int i = 0 ; i < likeTbls.size();i++) {
                                        String goodsUrl = NetUtil.imageUrl +  likeTbls.get(i).getLikeUserMe().getUserHead();
                                        ImageOptions goodsImageOptions = new ImageOptions.Builder()
                                                .setCircular(true)
                                                .build();
                                        if (i == 0){
                                            x.image().bind(headImageA, goodsUrl, goodsImageOptions);
                                            headImageA.setVisibility(View.VISIBLE);
                                        }
                                        if (i == 1){
                                            x.image().bind(headImageB, goodsUrl, goodsImageOptions);
                                            headImageB.setVisibility(View.VISIBLE);
                                        }
                                        if (i == 2){
                                            x.image().bind(headImageC, goodsUrl, goodsImageOptions);
                                            headImageC.setVisibility(View.VISIBLE);
                                        }
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
                break;

            case R.id.iWantTo:
                if (goods.getGoodsUser().getUserId()== 2) {
                    exampleApplication = (MyApplication) getApplication();
                    final ProgressDialog dia = new ProgressDialog(DetailGoodsActivity.this);
                    dia.setMessage("跳转中");
                    dia.show();
                    //用本人用户名登录到极光服务器自己的账号
                    JMessageClient.login(exampleApplication.getUserName(), "abc123", new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                //进入与你对话人的聊天
                                JMessageClient.enterSingleConversation(exampleApplication.getOtherName());
                                dia.cancel();
                                //页面跳转到聊天室
                                Intent intent = new Intent(DetailGoodsActivity.this, ChatActivity.class);
                                intent.putExtra("image",goods.getGoodsImages().get(0).getImageAddress());
                                intent.putExtra("price",goods.getGoodsPrice()+"");
                                intent.putExtra("goodsName",goods.getGoodsTitle()+"--"+goods.getGoodsDescribe());
                                startActivity(intent);
                            }
                        }
                    });
                }
        }
    }
}
