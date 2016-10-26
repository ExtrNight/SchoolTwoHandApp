package com.school.twohand.activity.taoquan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.customview.MyListView;
import com.school.twohand.entity.AmoyCircleDynamic;
import com.school.twohand.entity.AmoyCircleDynamicComment;
import com.school.twohand.entity.AmoyCircleDynamicImage;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 淘圈动态详情页面
 */
public class TaoquanDynamicDetailsActivity extends AppCompatActivity {

    MyApplication myApplication;
    User user;

    @InjectView(R.id.dynamic_finish)
    ImageView dynamicFinish;
    @InjectView(R.id.tv_dynamic_more)
    ImageView tvDynamicMore;
    @InjectView(R.id.RL_circle)
    RelativeLayout RLCircle;
    @InjectView(R.id.iv_taoquan_dynamic_userImage)
    ImageView ivTaoquanDynamicUserImage;
    @InjectView(R.id.tv_taoquan_dynamic_userName)
    TextView tvTaoquanDynamicUserName;
    @InjectView(R.id.dynamic_content)
    TextView dynamicContent;
    @InjectView(R.id.tv_likes_pv)
    TextView tvLikesPv;
    @InjectView(R.id.tv_message_number)
    TextView tvMessageNumber;
    @InjectView(R.id.tv_click_leave_message)
    TextView tvClickLeaveMessage;
    @InjectView(R.id.tv_click_like)
    TextView tvClickLike;
    @InjectView(R.id.LL_bottom)
    LinearLayout LLBottom;
    @InjectView(R.id.iv_keyboard)
    ImageView ivKeyboard;
    @InjectView(R.id.et_message_content)
    EditText etMessageContent;
    @InjectView(R.id.btn_send_message)
    Button btnSendMessage;
    @InjectView(R.id.LL_input_box)
    LinearLayout LLInputBox;
    @InjectView(R.id.LL_dynamic_image)
    LinearLayout LLDynamicImage;
    @InjectView(R.id.lv_comment)
    MyListView lvComment;
    @InjectView(R.id.tv_circle_name)
    TextView tvCircleName;
    @InjectView(R.id.iv_no_comment)
    ImageView ivNoComment;
    @InjectView(R.id.tv_click_like_isTrue)
    TextView tvClickLikeIsTrue;

    AmoyCircleDynamic amoyCircleDynamic;
    String circleName;
    int likesNumber;  //点赞者的数量，有可能不是数据库获取的数据，当用户点赞后又取消时
    private CommonAdapter<AmoyCircleDynamicComment> commentAdapter;
    private List<AmoyCircleDynamicComment> commentList = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Handler handler = new Handler();
    Integer fatherCommentId = null; //全局变量用来记录评论的父评论Id，在发送评论的时候会用到

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taoquan_dynamic_details);
        ButterKnife.inject(this);

        init();
        initData();
    }

    private void init() {
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
        Intent intent = getIntent();
        if (intent != null) {
            amoyCircleDynamic = intent.getParcelableExtra("amoyCircleDynamic");
            circleName = intent.getStringExtra("circleName");
        }


    }

    private void initData() {
        RLCircle.setFocusableInTouchMode(true);
        if(amoyCircleDynamic.getUser().getUserId()!=user.getUserId()){
            tvDynamicMore.setVisibility(View.GONE);
        }
        tvCircleName.setText(circleName);//给淘圈名赋值
        //设置用户头像
        String userImageUrl = NetUtil.imageUrl + amoyCircleDynamic.getUser().getUserHead();
        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .setLoadingDrawableId(R.mipmap.ic_launcher).setCrop(true).build();
        x.image().bind(ivTaoquanDynamicUserImage, userImageUrl, imageOptions);
        //设置用户名
        tvTaoquanDynamicUserName.setText(amoyCircleDynamic.getUser().getUserName());
        //设置动态文本内容
        dynamicContent.setText(amoyCircleDynamic.getAmoyCircleDynamicTitle() + "  "
                + amoyCircleDynamic.getAmoyCircleDynamicContent());
        //设置动态详情的图片
        List<AmoyCircleDynamicImage> imageList = amoyCircleDynamic.getImageList();
        if (imageList.size() != 0) { //有图片
            for (int i = 0; i < imageList.size(); i++) {
                View view = LayoutInflater.from(TaoquanDynamicDetailsActivity.this)
                        .inflate(R.layout.taoquan_dynamic_details_image_item, null);
                ImageView iv_details_image = (ImageView) view.findViewById(R.id.iv_taoquan_dynamic_details_image);
                String url2 = NetUtil.imageUrl + amoyCircleDynamic.getImageList().get(i).getCircleDynamicImageUrl();
                //设置图片样式
                ImageOptions imageOptions2 = new ImageOptions.Builder()
                        .setFailureDrawableId(R.mipmap.ic_launcher)
                        .setLoadingDrawableId(R.mipmap.ic_launcher)
                        .setCrop(true).build();          //是否裁剪？
                x.image().bind(iv_details_image, url2, imageOptions2);
                LLDynamicImage.addView(view);
            }
        }
        //设置点赞量和浏览量
        likesNumber = amoyCircleDynamic.getLikesList().size();
        tvLikesPv.setText("点赞" + likesNumber
                + " · 浏览" + (amoyCircleDynamic.getAmoyCircleDynamicPageviews() + 1));
        addAmoyCircleDynamicPv();   //界面上先把浏览量加1，然后将数据传到服务器
        //设置下面的点赞图标是显示没点赞还是已点赞
        if(likesNumber>0){ //表示该条动态有点赞的
            if(amoyCircleDynamic.getLikesList().contains(user.getUserId())){
                //如果点赞用户id的集合里有当前正在操作的用户Id，则设为已点赞
                tvClickLike.setVisibility(View.GONE);
                tvClickLikeIsTrue.setVisibility(View.VISIBLE);
            }
        }

        //设置评论相关信息
        initCommentData();

    }

    //设置评论
    private void initCommentData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryCircleDynamicCommentServlet");
        requestParams.addQueryStringParameter("circleDynamicId",amoyCircleDynamic.getAmoyCirlceDynamicId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type = new TypeToken<List<AmoyCircleDynamicComment>>(){}.getType();
                List<AmoyCircleDynamicComment> newCommentList = gson.fromJson(result,type);
                if(newCommentList.size()==0){ //如果没有评论
                    ivNoComment.setVisibility(View.VISIBLE);
                    //return;
                }
                commentList.clear();
                commentList.addAll(newCommentList);
                //设置评论数量
                tvMessageNumber.setText("热门留言 (" + commentList.size() + ")");
//                if(commentAdapter==null){ //每次都new新的适配器，给不同的评论item设置点击事件，否则评论item的点击事件会错乱
                    commentAdapter = new CommonAdapter<AmoyCircleDynamicComment>(TaoquanDynamicDetailsActivity.this,commentList,R.layout.taoquan_dynamic_comment_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, final AmoyCircleDynamicComment amoyCircleDynamicComment, int position) {
                            //显示评论者头像
                            ImageView iv_comment_user_image = viewHolder.getViewById(R.id.iv_dynamic_comment_item_user_image);
                            String url = NetUtil.imageUrl + amoyCircleDynamicComment.getUser().getUserHead();
                            ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true)
                                    .setFailureDrawableId(R.mipmap.ic_launcher)
                                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                                    .setCrop(true).build();          //是否裁剪？
                            x.image().bind(iv_comment_user_image, url, imageOptions);
                            //显示评论者名字
                            TextView tv_comment_user_name = viewHolder.getViewById(R.id.tv_dynamic_comment_item_user_name);
                            tv_comment_user_name.setText(amoyCircleDynamicComment.getUser().getUserName());
                            //设置是否显示"主"
                            TextView tv_isHost = viewHolder.getViewById(R.id.tv_isHost);
                            tv_isHost.setVisibility(View.GONE);
                            if(amoyCircleDynamicComment.getUser().getUserId()==amoyCircleDynamic.getUser().getUserId()){
                                tv_isHost.setVisibility(View.VISIBLE);
                            }
                            //设置评论内容
                            TextView tv_comment_content = viewHolder.getViewById(R.id.tv_dynamic_comment_item_content);
                            LinearLayout LL_sonComment = viewHolder.getViewById(R.id.LL_sonComment);
                            LL_sonComment.setVisibility(View.VISIBLE);
                            if(amoyCircleDynamicComment.getFatherCommentUserName()!=null){//表示有父评论
                                LL_sonComment.setVisibility(View.VISIBLE);//有父评论则有缩进效果
                                tv_comment_content.setText("回复@"+amoyCircleDynamicComment.getFatherCommentUserName()
                                        +":"+amoyCircleDynamicComment.getCircleDynamicCommentContent());
                            }else{
                                LL_sonComment.setVisibility(View.GONE);
                                tv_comment_content.setText(amoyCircleDynamicComment.getCircleDynamicCommentContent());
                            }

                            //设置时间
                            TextView tv_comment_time = viewHolder.getViewById(R.id.tv_dynamic_comment_item_time);
                            Timestamp ts = amoyCircleDynamicComment.getCircleDynamicCommentTime();
                            if(ts!=null){
                                tv_comment_time.setText(dateFormat.format(ts).toString());
                            }

                            //点击评论可回复评论，长按自己的评论是删除评论，点击别人的评论是给别人的评论进行评论
                            RelativeLayout RL_click_comment = viewHolder.getViewById(R.id.RL_click_comment);
                            if(amoyCircleDynamicComment.getUser().getUserId()==user.getUserId()){ //是自己的评论
                                Log.i("DynamicDetails", "convert: "+amoyCircleDynamicComment.getUser().getUserId());
                                RL_click_comment.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {//长按
                                        Log.i("DynamicDetails", "onLongClick: "+amoyCircleDynamicComment.getUser().getUserId());
                                        if(amoyCircleDynamicComment.getIsEnd()==1){//如果有子评论，则不能删除
                                            Toast.makeText(TaoquanDynamicDetailsActivity.this, "已有子评论，不能删除哦~", Toast.LENGTH_SHORT).show();
                                        }else{//弹出“删除”弹框
                                            String[] items = {"删除评论"};
                                            new AlertDialog.Builder(TaoquanDynamicDetailsActivity.this).setItems(items, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    new AlertDialog.Builder(TaoquanDynamicDetailsActivity.this).setMessage("确定删除该评论？")
                                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    deleteComment(amoyCircleDynamicComment.getCircleDynamicCommentId(),
                                                                            amoyCircleDynamicComment.getCircleDynamicCommentFatherId(),amoyCircleDynamicComment.getIsEnd());
                                                                }
                                                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).show();
                                                }
                                            }).show();
                                        }
                                        return false;
                                    }
                                });
                            }else{
                                RL_click_comment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //fatherCommentId = amoyCircleDynamicComment.getCircleDynamicCommentFatherId();
                                        fatherCommentId = amoyCircleDynamicComment.getCircleDynamicCommentId();//注意：上面是错的，其本身Id就是需要增加的评论的父评论Id
                                        LLBottom.setVisibility(View.GONE);
                                        LLInputBox.setVisibility(View.VISIBLE); //显示出输入框
                                        //弹出软键盘
                                        etMessageContent.requestFocus();
                                        etMessageContent.setHint("回复@"+amoyCircleDynamicComment.getUser().getUserName()+": ");
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(etMessageContent, InputMethodManager.RESULT_SHOWN);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    }
                                });
                            }


                        }
                    };
                    lvComment.setAdapter(commentAdapter);
//                }else{
//                    commentAdapter.notifyDataSetChanged();
//                }
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

    //增加该条动态的浏览量
    private void addAmoyCircleDynamicPv() {
        RequestParams requestParams = new RequestParams(NetUtil.url + "AddCircleDynamicPvServlet");
        requestParams.addQueryStringParameter("amoyCircleDynamicId", amoyCircleDynamic.getAmoyCirlceDynamicId() + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
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

    //增加或删除一条点赞记录,若requirement为1，则增加，若requirement为2，则删除
    private void insertOrCancelCircleDynamicLikes(int requirement){
        RequestParams requestParams = new RequestParams(NetUtil.url+"InsertOrCancelCircleDynamicLikesServlet");
        requestParams.addQueryStringParameter("requirement",requirement+"");
        requestParams.addQueryStringParameter("circleDynamicId",amoyCircleDynamic.getAmoyCirlceDynamicId()+"");
        requestParams.addQueryStringParameter("circleDynamicLikesUserId",user.getUserId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
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

    @OnClick({R.id.dynamic_finish, R.id.tv_dynamic_more, R.id.RL_circle, R.id.tv_click_leave_message, R.id.tv_click_like,
            R.id.LL_bottom, R.id.iv_keyboard, R.id.btn_send_message, R.id.LL_input_box,R.id.tv_click_like_isTrue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dynamic_finish:
                commentList = null;
                fatherCommentId = null;
                finish();
                break;
            case R.id.tv_dynamic_more:
                if(amoyCircleDynamic.getUser().getUserId()!=user.getUserId()){
                    return;
                }
                String[] items = {"删除动态"};
                new AlertDialog.Builder(TaoquanDynamicDetailsActivity.this).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(TaoquanDynamicDetailsActivity.this).setMessage("确定删除该动态？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteDynamic();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                }).show();
                break;
            case R.id.RL_circle:
                break;
            case R.id.tv_click_leave_message: //点击给这条动态留言
                fatherCommentId = null;
                LLBottom.setVisibility(View.GONE);
                //etMessageContent.setFocusable(true);
                etMessageContent.requestFocus();
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                    }
//                },300);
                //弹出软键盘，如果不行可以用上面的方式，开一个定时器进行操作，但是必须设置EditText获取焦点
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(etMessageContent, InputMethodManager.RESULT_SHOWN);
                imm1.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                etMessageContent.setHint("想对该动态说些什么...");
                LLInputBox.setVisibility(View.VISIBLE); //显示出输入框
                break;
            case R.id.tv_click_like: //没点赞状态
                if(amoyCircleDynamic.getUser().getUserId()==user.getUserId()){
                    //如果当前正在操作的用户是这条动态的发布者本身，则不能点赞
                    Toast.makeText(TaoquanDynamicDetailsActivity.this, "您不能给自己点赞噢", Toast.LENGTH_SHORT).show();
                }else{
                    tvClickLike.setVisibility(View.GONE);
                    tvClickLikeIsTrue.setVisibility(View.VISIBLE);
                    //界面上设置点赞数量加一
                    likesNumber++;
                    tvLikesPv.setText("点赞" + likesNumber
                            + " · 浏览" + (amoyCircleDynamic.getAmoyCircleDynamicPageviews() + 1));
                    //将数据上传服务器，数据库里增加一条点赞记录
                    insertOrCancelCircleDynamicLikes(1);
                }
                break;
            case R.id.tv_click_like_isTrue: //已点赞状态
                tvClickLikeIsTrue.setVisibility(View.GONE);
                tvClickLike.setVisibility(View.VISIBLE);
                //界面上设置点赞数量减一
                likesNumber--;
                tvLikesPv.setText("点赞" + likesNumber
                        + " · 浏览" + (amoyCircleDynamic.getAmoyCircleDynamicPageviews() + 1));
                //将数据上传服务器，数据库里删除一条点赞记录
                insertOrCancelCircleDynamicLikes(2);
            case R.id.iv_keyboard: //点击取消输入框
                etMessageContent.setHint("");
                //将软键盘关闭
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);//强制隐藏键盘,而不是HIDE_IMPLICIT_ONLY
                //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,InputMethodManager.HIDE_IMPLICIT_ONLY); //不需要
                LLInputBox.setVisibility(View.GONE);
                LLBottom.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_send_message:
                String contentStr = etMessageContent.getText().toString();
                if(contentStr.equals("")){
                    Toast.makeText(TaoquanDynamicDetailsActivity.this, "评论内容不能为空哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                //上传数据给服务器，添加一条评论
                AmoyCircleDynamicComment amoyCircleDynamicComment = new AmoyCircleDynamicComment(
                        amoyCircleDynamic.getAmoyCirlceDynamicId(),contentStr,fatherCommentId,user);
                insertComment(amoyCircleDynamicComment);
                break;
        }
    }

    //增加一条评论
    public void insertComment(AmoyCircleDynamicComment amoyCircleDynamicComment){
        Gson gson = new Gson();
        String amoyCircleDynamicCommentJson = gson.toJson(amoyCircleDynamicComment);
        RequestParams requestParams = new RequestParams(NetUtil.url+"InsertCircleDynamicCommentServlet");
        requestParams.addQueryStringParameter("amoyCircleDynamicCommentJson",amoyCircleDynamicCommentJson);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                initCommentData();
                Toast.makeText(TaoquanDynamicDetailsActivity.this, "留言成功~", Toast.LENGTH_SHORT).show();
                ivNoComment.setVisibility(View.GONE); //去掉没有留言时的图片
                etMessageContent.setText("");
                etMessageContent.setHint("");
                //将软键盘关闭
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);//强制隐藏键盘,而不是HIDE_IMPLICIT_ONLY
                LLInputBox.setVisibility(View.GONE);
                LLBottom.setVisibility(View.VISIBLE);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(TaoquanDynamicDetailsActivity.this, "发表评论失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    //删除一条评论,需要参数，需要删除的评论的Id，父评论的Id，isEnd属性，有子评论则不能删除
    public void deleteComment(int commentId,Integer fatherCommentId,int isEnd){
        RequestParams requestParams = new RequestParams(NetUtil.url+"DeleteCircleDynamicCommentServlet");
        requestParams.addQueryStringParameter("commentId",commentId+"");
        requestParams.addQueryStringParameter("fatherCommentId",fatherCommentId+"");
        requestParams.addQueryStringParameter("isEnd",isEnd+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(TaoquanDynamicDetailsActivity.this, "删除评论成功~", Toast.LENGTH_SHORT).show();
                initCommentData();
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(TaoquanDynamicDetailsActivity.this, "删除评论失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    //删除该动态
    public void deleteDynamic(){
        if(amoyCircleDynamic.getUser().getUserId()==user.getUserId()){
            RequestParams requestParams = new RequestParams(NetUtil.url+"DeleteCircleDynamicServlet");
            requestParams.addQueryStringParameter("circleDynamicId",amoyCircleDynamic.getAmoyCirlceDynamicId()+"");
            x.http().get(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(TaoquanDynamicDetailsActivity.this, "删除动态成功~", Toast.LENGTH_SHORT).show();
                    setResult(CreateTaoquanDynamicActivity.ResultCode);
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


}
