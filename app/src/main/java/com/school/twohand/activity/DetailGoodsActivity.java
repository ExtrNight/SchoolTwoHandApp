package com.school.twohand.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;

public class DetailGoodsActivity extends AppCompatActivity {

    @InjectView(R.id.view)
    Toolbar view;
    @InjectView(R.id.message_detail)
    Button messageDetail;
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
    @InjectView(R.id.returnRel)
    Button returnRel;
    ImageView goodsImageA;
    ImageView goodsImageB;
    ImageView goodsImageC;
    ImageView goodsImageD;
    ImageView goodsImageE;
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

    //判断用户相对应的按钮显示影藏
    TextView shengLueHao;
    TextView iWantTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_goods);
        ButterKnife.inject(this);
        //找控件
        shengLueHao = (TextView) findViewById(R.id.shengLueHao);
        iWantTo = (TextView) findViewById(R.id.iWantTo);

        myApplication = (MyApplication) getApplication();
        //获取到上界面传来的商品详情
        Intent intent = getIntent();

        goodsMessageString = intent.getStringExtra("goodsMessage");
        position = intent.getIntExtra("position",0)-1;

        Gson gson = new Gson();
        goodsMessages = gson.fromJson(goodsMessageString, new TypeToken<List<Goods>>() {}.getType());
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

        //有可能用户没有登录myApplication 中User对象wei null
        //判断当前商品是否是自己发布的，如果是显示三个省略号，（可进行的操作，跳到发布界面编辑，删除商品，取消）；
        //1，先判断商品是否是自己发布的,就应影藏我想要的按钮，替换成省略号
        if (myApplication.getUser()!=null) {
            if (myApplication.getUser().getUserId() == goods.getGoodsUser().getUserId()) {
                //为当前用户的状态
                iWantTo.setVisibility(View.INVISIBLE);
                shengLueHao.setVisibility(View.VISIBLE);
            } else {
                //访问其他商品的状态
                iWantTo.setVisibility(View.VISIBLE);
                shengLueHao.setVisibility(View.INVISIBLE);
            }
        }
        initEvent();
    }

    //点击事件，我想要按钮的点击和省略号按钮的点击
    public void initEvent(){
        iWantTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApplication.getUser()==null){
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this,LoginActivity.class);
                    startActivityForResult(intent,0);
                }else {
                    //进入与你对话人的聊天
                    JMessageClient.enterSingleConversation(goods.getGoodsUser().getUserAccount());
                    myApplication.setOtherAccount(goods.getGoodsUser().getUserAccount());
                    //页面跳转到聊天室
                    Intent intent = new Intent(DetailGoodsActivity.this, ChatActivity.class);
                    intent.putExtra("image",goods.getGoodsImages().get(0).getImageAddress());
                    intent.putExtra("price",goods.getGoodsPrice()+"");
                    intent.putExtra("userName",goods.getGoodsUser().getUserName());

                    intent.putExtra("goodsMessage",goodsMessageString);
                    intent.putExtra("position",position+"");
                    startActivity(intent);
                }
            }
        });

        shengLueHao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框，三个条目分别是 跳到发布界面编辑，删除商品，取消
                listDialog();
            }
        });
    }

    //1、判断有没有sdcard地址，有就返回。没有就用包名地址,返回地址字符串
    public String querySdcardAddress(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//有sdcard
            return Environment.getExternalStorageDirectory().toString()+"/savePictureFile";
        }else {
            return "/data/data/com.school.twohand.schooltwohandapp/savePictureFile";//格式/data/data/包名/自定义文件夹
        }
    }

    //2、将ImageView上的图片转化成bitmap
    public Bitmap convertImageViewTOBitmap(View view){
        //转化的三种模式
        /*
            AT_MOST：我们可以指定一个上限，要保存的图片的大小不会超过它。
            EXACTLY：我们指定了一个明确的大小，要求图片保存时满足这个条件。
            UNSPECIFIED：图片多大，我们就保存多大。
        */
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
    //3、创建文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }


    protected void listDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this,R.style.AlterDialog)
                .setItems(new String[]{"编辑", "删除", "取消"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){//做编辑操作
                            //编辑操作前先将ImageView上的图片存储起来
                            //1、判断有没有sdcard地址，有就返回。没有就用包名地址(图片的存储地址)
                            String imageAddress = querySdcardAddress();
                            //2、将ImageView上的图片（取得缓存数据转化成bitmap）
                            List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                            for (int i = 0;i < goods.getGoodsImages().size();i++){
                                if (i == 0){
                                    bitmaps.add(convertImageViewTOBitmap(goodsImageA));
                                }
                                if (i == 1){
                                    bitmaps.add(convertImageViewTOBitmap(goodsImageB));
                                }
                                if (i == 2){
                                    bitmaps.add(convertImageViewTOBitmap(goodsImageC));
                                }
                                if (i == 3){
                                    bitmaps.add(convertImageViewTOBitmap(goodsImageD));
                                }
                                if (i == 4){
                                    bitmaps.add(convertImageViewTOBitmap(goodsImageE));
                                }
                            }
                            //3、将bitmap用流存入本地
                            makeRootDirectory(imageAddress);//先创建其文件夹，才能创建文件
                            List<File> files = new ArrayList<File>();
                            for (int j = 0 ; j < bitmaps.size() ;j++){
                                File imageFile = new File(imageAddress + "/" + getPhotoFileName());
                                files.add(imageFile);
                                Log.i("imageFile", "onClick: "+imageFile);
                                try {
                                    imageFile.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(imageFile);
                                    Boolean b = bitmaps.get(j).compress(Bitmap.CompressFormat.JPEG, 50, fos);
                                    Log.i("imageFile", "onClick: "+b);
                                } catch (IOException e) {
                                    Log.i("imageFile", "onClick: "+e);
                                    e.printStackTrace();
                                }
                            }

                            //跳转到发布界面
                            Intent intent = new Intent(DetailGoodsActivity.this,PublicActivity.class);
                            Log.i("imageFile", "onClick: +e");
                            Gson gson = new Gson();
                            String goodsString = gson.toJson(goods);
                            String filesString = gson.toJson(files);
                            Log.i("Gson", "onClick: "+goodsString+"===");
                            intent.putExtra("goodsString",goodsString);
                            intent.putExtra("filesString",filesString);
                            intent.putExtra("DetailGoods","DetailGoods");
                            intent.putExtra("imageurl",imageAddress);
                            intent.putExtra("goodId",goods.getGoodsId()+"");
                            startActivity(intent);


                        }else if (which == 1){//做删除操作
                            //在跳出一个对话框确认删除
                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailGoodsActivity.this);
                            builder.setMessage("不想离开主淫。。");

                            builder.setTitle("废物的挣扎");

                            builder.setPositiveButton("收回", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    //做删除操作既改变商品状态
                                    RequestParams requestParams = new RequestParams(NetUtil.url + "DeleteGoodsServlet");
                                    requestParams.addQueryStringParameter("goodsId",goods.getGoodsId()+"");
                                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            if ("删除成功".equals(result)) {
                                                Toast.makeText(DetailGoodsActivity.this, "商品下架", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Toast.makeText(DetailGoodsActivity.this, "删除失败请检查网络配置", Toast.LENGTH_SHORT).show();
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
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();

                        }else {
                            dialog.cancel();
                        }
                    }
                })
                .show();


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
            goodsImageD = (ImageView) headView.findViewById(R.id.image_d_m);
            goodsImageE = (ImageView) headView.findViewById(R.id.image_e_m);
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
                ImageOptions goodsImageOptions = new ImageOptions.Builder().build();
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
                if (i == 3){
                    x.image().bind(goodsImageD, goodsUrl, goodsImageOptions);
                    goodsImageD.setVisibility(View.VISIBLE);
                }
                if (i == 4){
                    x.image().bind(goodsImageE, goodsUrl, goodsImageOptions);
                    goodsImageE.setVisibility(View.VISIBLE);
                }

            }
            //点赞数
            likeNumber.setText("点赞:"+goods.getGoodsLikes().size());
            //浏览数量加一
            RequestParams requestParams = new RequestParams(NetUtil.url+"AddGoodsPVServlet");
            //判断是否是游客
            Integer userId;
            if (myApplication.getUser()==null){
                //是游客
                userId = 0;
            }else{
                userId = myApplication.getUser().getUserId();
            }
            //访问服务器让商品浏览数加一
            requestParams.addQueryStringParameter("userId",userId+"");
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
            /*amoyName.setText("淘圈|"+goods.getGoodsAmoyCircle().getCircleName());*/
            amoyName.setVisibility(View.GONE);
            if(goods.getGoodsAmoyCircle()!=null){
                amoyName.setVisibility(View.VISIBLE);
                amoyName.setText("淘圈丨"+goods.getGoodsAmoyCircle().getCircleName());
            }
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

                //判断是否是游客
                Integer userId;
                if (myApplication.getUser()==null){
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this,LoginActivity.class);
                    startActivityForResult(intent,0);
                }else{

                    //弹出键盘
                    say.setFocusable(true);
                    say.setFocusableInTouchMode(true);
                    say.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(say,InputMethodManager.SHOW_FORCED);


                    if (position==0){
                        say.setHint("输入你想对TA说的话");
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
                                            //listView定位到你发送的那一条
                                            listView.setSelection(1);
                                            //关闭键盘
                                            InputMethodManager imm =
                                                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @OnClick({R.id.message_detail, R.id.returnRel,R.id.send,R.id.like_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_detail:
                if (myApplication.getUser()==null){
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this,LoginActivity.class);
                    startActivityForResult(intent,0);
                }else{
                    relativeLayout.setVisibility(View.INVISIBLE);
                    sayLinearLayout.setVisibility(View.VISIBLE);

                    say.setHint("输入你想对TA说的话");
                    //弹出键盘
                    say.setFocusable(true);
                    say.setFocusableInTouchMode(true);
                    say.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(say,InputMethodManager.SHOW_FORCED);

                }
                break;
            case R.id.returnRel:
                relativeLayout.setVisibility(View.VISIBLE);
                sayLinearLayout.setVisibility(View.INVISIBLE);
                //关闭键盘的方法
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(say.getWindowToken(), 0);
                break;
            case R.id.send:
                if (myApplication.getUser()==null){
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this,LoginActivity.class);
                    startActivityForResult(intent,0);
                }else{
                    //如果输入框不为空
                    if (say.getText().toString().trim().length()!=0){
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
                                        //listView定位到你发送的那一条
                                        listView.setSelection(1);
                                        //关闭键盘
                                        InputMethodManager imm =
                                                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    }else{//输入框为空
                        Toast.makeText(DetailGoodsActivity.this, "亲还没输入呢", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.like_message:
                if (myApplication.getUser()==null){
                    //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                    Intent intent = new Intent(DetailGoodsActivity.this,LoginActivity.class);
                    startActivityForResult(intent,0);
                }else {
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
                                        Toast.makeText(DetailGoodsActivity.this, "居然不爱我了，好受伤", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(DetailGoodsActivity.this, "您慷慨大方的送出一枚赞", Toast.LENGTH_SHORT).show();
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
                }
                break;



        }
    }


    //获取相片名字
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        return sdf.format(date) + ".png";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //有可能用户没有登录myApplication 中User对象wei null
        //判断当前商品是否是自己发布的，如果是显示三个省略号，（可进行的操作，跳到发布界面编辑，删除商品，取消）；
        //1，先判断商品是否是自己发布的,就应影藏我想要的按钮，替换成省略号
        Log.i("onActivityResult", "onActivityResult: "+myApplication.getUser());

        if (myApplication.getUser()!=null) {
            Log.i("onActivityResult", "onActivityResult: "+myApplication.getUser().getUserId()+goods.getGoodsUser().getUserId());
            if (myApplication.getUser().getUserId() == goods.getGoodsUser().getUserId()) {
                //为当前用户的状态
                iWantTo.setVisibility(View.INVISIBLE);
                shengLueHao.setVisibility(View.VISIBLE);
            } else {
                //访问其他商品的状态
                iWantTo.setVisibility(View.VISIBLE);
                shengLueHao.setVisibility(View.INVISIBLE);
            }
        }
    }
}
