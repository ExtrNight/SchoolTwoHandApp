package com.school.twohand.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.login.LoginActivity;
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

public class DetailGoodsActivity extends AppCompatActivity {

    @InjectView(R.id.like_message)
    Button likeMessage;
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

    TextView tvMessageNumber;  //留言数量
    ImageView ivNoComment;     //没有留言的图片
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
    MyApplication myApplication;
    String goodsMessageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_goods);
        ButterKnife.inject(this);
        myApplication = (MyApplication) getApplication();
        //获取到上界面传来的商品详情
        Intent intent = getIntent();
        goodsMessageString = intent.getStringExtra("goodsMessage");
        position = intent.getIntExtra("position", 0) - 1;

        //从“收到的赞”页面跳转过来的，那个页面传来了一个Goods对象
        String goodsJson = intent.getStringExtra("goodsJson");
        Gson gson = new Gson();
        if(goodsMessageString!=null){
            goodsMessages = gson.fromJson(goodsMessageString, new TypeToken<List<Goods>>() {}.getType());
            goods = goodsMessages.get(position);
        }else if(goodsJson!=null){
            goods = gson.fromJson(goodsJson,Goods.class);
        }

        initHeadView();

        //数据库获取对应商品留言表
        RequestParams requestParams = new RequestParams(NetUtil.url + "QueryMessageServlet");
        requestParams.addQueryStringParameter("goodsId", goods.getGoodsId() + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                messageBoards = gson.fromJson(result, new TypeToken<List<MessageBoard>>() {}.getType());
                tvMessageNumber.setText("热门留言 ("+messageBoards.size()+")");
                if(messageBoards.size()==0){
                    ivNoComment.setVisibility(View.VISIBLE);
                }else{
                    ivNoComment.setVisibility(View.GONE);
                }
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

    public void initHeadView() {
        if (goods != null) {
            headView = LayoutInflater.from(this).inflate(R.layout.detail_goods, null);
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
            tvMessageNumber = (TextView) headView.findViewById(R.id.tv_message_number);
            ivNoComment = (ImageView) headView.findViewById(R.id.iv_no_comment);
            final TextView readNumber = (TextView) headView.findViewById(R.id.readNumber_m);
            //服务器找头像
            String headUrl = NetUtil.imageUrl + goods.getGoodsUser().getUserHead();
            ImageOptions headImageOptions = new ImageOptions.Builder()
                    .setCircular(true)
                    .build();
            x.image().bind(headImage, headUrl, headImageOptions);
            //用户名
            userName.setText(goods.getGoodsUser().getUserName());
            //商品价格
            if (goods.getGoodsAuction() == 0) {
                goodsPrice.setText("￥" + goods.getGoodsPrice() + "");
            }
//            if (goods.getGoodsAuction()==1){
//                goodsPrice.setText("起拍￥"+goods.getGoodsPrice()+"");
//                iWantTo.setText("出价");
//            }
            //商品详情
            goodsDetail.setText(goods.getGoodsTitle() + "  " + goods.getGoodsDescribe());
            //服务器找商品图片信息
            for (int i = 0; i < goods.getGoodsImages().size(); i++) {
                String goodsUrl = NetUtil.imageUrl + goods.getGoodsImages().get(i).getImageAddress();
                ImageOptions goodsImageOptions = new ImageOptions.Builder().build();
                if (i == 0) {
                    x.image().bind(goodsImageA, goodsUrl, goodsImageOptions);
                    goodsImageA.setVisibility(View.VISIBLE);
                }
                if (i == 1) {
                    x.image().bind(goodsImageB, goodsUrl, goodsImageOptions);
                    goodsImageB.setVisibility(View.VISIBLE);
                }
                if (i == 2) {
                    x.image().bind(goodsImageC, goodsUrl, goodsImageOptions);
                    goodsImageC.setVisibility(View.VISIBLE);
                }
            }
            //点赞数
            likeNumber.setText("点赞:" + goods.getGoodsLikes().size());
            //浏览数量加一
            RequestParams requestParams = new RequestParams(NetUtil.url + "AddGoodsPVServlet");
            //判断是否是游客
            Integer userId;
            if (myApplication.getUser() == null) {
                //是游客
                userId = 0;
            } else {
                userId = myApplication.getUser().getUserId();
            }
            //访问服务器让商品浏览数加一
            requestParams.addQueryStringParameter("userId", userId + "");
            requestParams.addQueryStringParameter("goodsId", goods.getGoodsId() + "");
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    readNumber.setText("浏览:" + result);
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
            amoyName.setVisibility(View.GONE);
            //淘圈
            if(goods.getGoodsAmoyCircle()!=null){
                amoyName.setVisibility(View.VISIBLE);
                amoyName.setText("淘圈 | " + goods.getGoodsAmoyCircle().getCircleName());
            }
            //点赞重新服务器获取
            RequestParams requestParams2 = new RequestParams(NetUtil.url + "QueryZZZZServlet");
            requestParams2.addQueryStringParameter("goodsId", goods.getGoodsId() + "");
            x.http().get(requestParams2, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    //点赞人的头像
                    Gson gson = new Gson();
                    List<LikeTbl> likeTbls = gson.fromJson(result, new TypeToken<List<LikeTbl>>() {}.getType());
                    likeNumber.setText("点赞" + likeTbls.size());

                    for (int i = 0; i < likeTbls.size(); i++) {
                        String goodsUrl = NetUtil.imageUrl + likeTbls.get(i).getLikeUserMe().getUserHead();
                        Log.i("likeMessage", "onSuccess: " + goodsUrl);
                        ImageOptions goodsImageOptions = new ImageOptions.Builder()
                                .setCircular(true)
                                .build();
                        if (i == 0) {
                            x.image().bind(headImageA, goodsUrl, goodsImageOptions);
                            headImageA.setVisibility(View.VISIBLE);
                        }
                        if (i == 1) {
                            x.image().bind(headImageB, goodsUrl, goodsImageOptions);
                            headImageB.setVisibility(View.VISIBLE);
                        }
                        if (i == 2) {
                            x.image().bind(headImageC, goodsUrl, goodsImageOptions);
                            headImageC.setVisibility(View.VISIBLE);
                        }
                        if (likeTbls.get(i).getLikeUserMe().getUserId() == ((MyApplication) getApplication()).getUser().getUserId()) {
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
        listView.addHeaderView(headView);
    }

    public void initData() {
        //留言
        if (commonAdapter == null) {
            commonAdapter = new CommonAdapter<MessageBoard>(this, messageBoards, R.layout.message_board_item) {
                @Override
                public void convert(ViewHolder viewHolder, MessageBoard messageBoard, int position) {
                    ImageView headView = viewHolder.getViewById(R.id.message_user_head);
                    TextView headName = viewHolder.getViewById(R.id.message_user_name);
                    TextView messagedetail = viewHolder.getViewById(R.id.message_content);
                    //留言头像获取
                    String headImageUrl = NetUtil.imageUrl + messageBoard.getMessageBoardUserMe().getUserHead();
                    ImageOptions imageOptions = new ImageOptions.Builder()
                            .setCircular(true)
                            .build();
                    x.image().bind(headView, headImageUrl, imageOptions);
                    //留言用户名
                    headName.setText(messageBoard.getMessageBoardUserMe().getUserName());
                    //留言内容
                    String content = messageBoard.getMessageBoardContent();

                    if (messageBoard.getMessageBoardUserOther() != null) {
                        messagedetail.setText("@" + messageBoard.getMessageBoardUserOther().getUserName() + " " + content);
                    } else {
                        messagedetail.setText(content);
                    }
                }
            };
            listView.setAdapter(commonAdapter);

        } else {
            commonAdapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //判断是否是游客
                Integer userId;
                if (myApplication.getUser() == null) {
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    //弹出键盘
                    say.setFocusable(true);
                    say.setFocusableInTouchMode(true);
                    say.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(say, InputMethodManager.SHOW_FORCED);

                    if (position == 0) {
                        say.setHint("输入你想对TA说的话");
                    } else {
                        say.setHint("@" + messageBoards.get(position - 1).getMessageBoardUserMe().getUserName());
                    }
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messageBoard.setMessageBoardUserMe(((MyApplication) getApplication()).getUser());
                            Log.i("likeMessage", "onClick: " + position);
                            if (position == 0) {
                                messageBoard.setMessageBoardUserOther(null);
                            } else {
                                messageBoard.setMessageBoardUserOther(messageBoards.get(position - 1).getMessageBoardUserMe());
                            }
                            Goods goods1 = new Goods();
                            goods1.setGoodsId(goods.getGoodsId());
                            messageBoard.setMessageBoardGoods(goods1);
                            messageBoard.setMessageBoardContent(say.getText().toString());
                            Gson gson1 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            String messageBoardString = gson1.toJson(messageBoard);
                            RequestParams requestParams = new RequestParams(NetUtil.url + "AddMessageServlet");
                            requestParams.addQueryStringParameter("messageBoard", messageBoardString);
                            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    say.setText("");
                                    RequestParams requestParams = new RequestParams(NetUtil.url + "QueryMessageServlet");
                                    requestParams.addQueryStringParameter("goodsId", goods.getGoodsId() + "");
                                    x.http().get(requestParams, new CommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            Gson gson = new Gson();
                                            List<MessageBoard> newMessageBoards = gson.fromJson(result, new TypeToken<List<MessageBoard>>() {}.getType());
                                            messageBoards.clear();
                                            messageBoards.addAll(newMessageBoards);
                                            initData();
                                            //listView定位到你发送的那一条
                                            listView.setSelection(1);
                                            //关闭键盘
                                            InputMethodManager imm =
                                                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(say.getWindowToken(), 0);
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
                }
                sayLinearLayout.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick({R.id.message_detail, R.id.returnRel, R.id.send, R.id.like_message, R.id.iWantTo, R.id.goods_detail_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goods_detail_finish:
                finish();
                break;
            case R.id.message_detail:
                if (myApplication.getUser() == null) {
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    relativeLayout.setVisibility(View.INVISIBLE);
                    sayLinearLayout.setVisibility(View.VISIBLE);

                    say.setHint("输入你想对TA说的话");
                    //弹出键盘
                    say.setFocusable(true);
                    say.setFocusableInTouchMode(true);
                    say.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(say, InputMethodManager.SHOW_FORCED);
                }
                break;
            case R.id.returnRel:
                relativeLayout.setVisibility(View.VISIBLE);
                sayLinearLayout.setVisibility(View.INVISIBLE);
                //关闭键盘的方法
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(say.getWindowToken(), 0);
                break;
            case R.id.send:
                if (myApplication.getUser() == null) {
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    //如果输入框不为空
                    if (say.getText().toString().trim().length() != 0) {
                        say.setHint("");
                        messageBoard.setMessageBoardUserMe(((MyApplication) getApplication()).getUser());
                        Goods goods1 = new Goods();
                        goods1.setGoodsId(goods.getGoodsId());
                        messageBoard.setMessageBoardGoods(goods1);
                        messageBoard.setMessageBoardContent(say.getText().toString());
                        Gson gson1 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        String messageBoardString = gson1.toJson(messageBoard);
                        RequestParams requestParams = new RequestParams(NetUtil.url + "AddMessageServlet");
                        requestParams.addQueryStringParameter("messageBoard", messageBoardString);
                        x.http().get(requestParams, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                //从数据库拿最新的消息记录
                                say.setText("");
                                RequestParams requestParams = new RequestParams(NetUtil.url + "QueryMessageServlet");
                                requestParams.addQueryStringParameter("goodsId", goods.getGoodsId() + "");
                                x.http().get(requestParams, new CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(messageBoards.size()==0){
                                            ivNoComment.setVisibility(View.GONE);//将没有评论的图片隐藏
                                        }
                                        Gson gson = new Gson();
                                        List<MessageBoard> newMessageBoards = gson.fromJson(result, new TypeToken<List<MessageBoard>>() {}.getType());
                                        messageBoards.clear();
                                        messageBoards.addAll(newMessageBoards);

                                        initData();
                                        //listView定位到你发送的那一条
                                        listView.setSelection(1);
                                        //关闭键盘
                                        InputMethodManager imm =
                                                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(say.getWindowToken(), 0);

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
                    } else {//输入框为空
                        Toast.makeText(DetailGoodsActivity.this, "亲还没输入呢", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.like_message:
                if (myApplication.getUser() == null) {
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    //取消赞
                    if (likeMessage.isSelected()) {
                        RequestParams requestParams2 = new RequestParams(NetUtil.url + "ZZZZZZServlet");
                        requestParams2.addQueryStringParameter("userMeId", ((MyApplication) getApplication()).getUser().getUserId() + "");
                        requestParams2.addQueryStringParameter("likeOtherId", goods.getGoodsUser().getUserId() + "");
                        requestParams2.addQueryStringParameter("likeGoodsId", goods.getGoodsId() + "");
                        requestParams2.addQueryStringParameter("flag", 2 + "");
                        x.http().get(requestParams2, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                RequestParams requestParams2 = new RequestParams(NetUtil.url + "QueryZZZZServlet");
                                requestParams2.addQueryStringParameter("goodsId", goods.getGoodsId() + "");
                                x.http().get(requestParams2, new CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Toast.makeText(DetailGoodsActivity.this, "居然不爱我了，好受伤", Toast.LENGTH_SHORT).show();
                                        likeMessage.setSelected(false);
                                        //点赞人的头像
                                        Gson gson = new Gson();
                                        List<LikeTbl> likeTbls = gson.fromJson(result, new TypeToken<List<LikeTbl>>() {
                                        }.getType());
                                        Log.i("likeMessage", "onSuccess: " + likeTbls);
                                        headImageA.setVisibility(View.GONE);
                                        headImageB.setVisibility(View.GONE);
                                        headImageC.setVisibility(View.GONE);
                                        likeNumber.setText("点赞" + likeTbls.size());
                                        for (int i = 0; i < likeTbls.size(); i++) {
                                            String goodsUrl = NetUtil.imageUrl + likeTbls.get(i).getLikeUserMe().getUserHead();
                                            Log.i("likeMessage", "onSuccess: " + goodsUrl);
                                            ImageOptions goodsImageOptions = new ImageOptions.Builder()
                                                    .setCircular(true)
                                                    .build();
                                            if (i == 0) {
                                                x.image().bind(headImageA, goodsUrl, goodsImageOptions);
                                                headImageA.setVisibility(View.VISIBLE);
                                            }
                                            if (i == 1) {
                                                x.image().bind(headImageB, goodsUrl, goodsImageOptions);
                                                headImageB.setVisibility(View.VISIBLE);
                                            }
                                            if (i == 2) {
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

                    } else {
                        //赞
                        RequestParams requestParams2 = new RequestParams(NetUtil.url + "ZZZZZZServlet");
                        requestParams2.addQueryStringParameter("userMeId", ((MyApplication) getApplication()).getUser().getUserId() + "");
                        requestParams2.addQueryStringParameter("likeOtherId", goods.getGoodsUser().getUserId() + "");
                        requestParams2.addQueryStringParameter("likeGoodsId", goods.getGoodsId() + "");
                        requestParams2.addQueryStringParameter("flag", 1 + "");
                        x.http().get(requestParams2, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                RequestParams requestParams2 = new RequestParams(NetUtil.url + "QueryZZZZServlet");
                                requestParams2.addQueryStringParameter("goodsId", goods.getGoodsId() + "");
                                x.http().get(requestParams2, new CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Toast.makeText(DetailGoodsActivity.this, "您慷慨大方的送出一枚赞", Toast.LENGTH_SHORT).show();
                                        likeMessage.setSelected(true);

                                        //点赞人的头像
                                        Gson gson = new Gson();
                                        List<LikeTbl> likeTbls = gson.fromJson(result, new TypeToken<List<LikeTbl>>() {
                                        }.getType());
                                        likeNumber.setText("点赞" + likeTbls.size());
                                        Log.i("likeMessage", "onSuccess: " + likeTbls);
                                        headImageA.setVisibility(View.GONE);
                                        headImageB.setVisibility(View.GONE);
                                        headImageC.setVisibility(View.GONE);
                                        for (int i = 0; i < likeTbls.size(); i++) {
                                            String goodsUrl = NetUtil.imageUrl + likeTbls.get(i).getLikeUserMe().getUserHead();
                                            ImageOptions goodsImageOptions = new ImageOptions.Builder()
                                                    .setCircular(true)
                                                    .build();
                                            if (i == 0) {
                                                x.image().bind(headImageA, goodsUrl, goodsImageOptions);
                                                headImageA.setVisibility(View.VISIBLE);
                                            }
                                            if (i == 1) {
                                                x.image().bind(headImageB, goodsUrl, goodsImageOptions);
                                                headImageB.setVisibility(View.VISIBLE);
                                            }
                                            if (i == 2) {
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
                }
                break;
            case R.id.iWantTo:
                if (myApplication.getUser() == null) {
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    //进入与你对话人的聊天
                    JMessageClient.enterSingleConversation(goods.getGoodsUser().getUserAccount());
                    myApplication.setOtherAccount(goods.getGoodsUser().getUserAccount());
                    //页面跳转到聊天室
                    Intent intent = new Intent(DetailGoodsActivity.this, ChatActivity.class);
                    intent.putExtra("image", goods.getGoodsImages().get(0).getImageAddress());
                    intent.putExtra("price", goods.getGoodsPrice() + "");
                    intent.putExtra("userName", goods.getGoodsUser().getUserName());

                    intent.putExtra("goodsMessage", goodsMessageString);
                    intent.putExtra("position", position + "");
                    startActivity(intent);
                }

        }
    }
}
